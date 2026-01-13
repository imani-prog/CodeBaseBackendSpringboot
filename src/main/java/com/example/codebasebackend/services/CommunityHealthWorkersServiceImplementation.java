package com.example.codebasebackend.services;

import com.example.codebasebackend.Entities.CommunityHealthWorkers;
import com.example.codebasebackend.Entities.Hospital;
import com.example.codebasebackend.dto.CommunityHealthWorkerRequest;
import com.example.codebasebackend.dto.CommunityHealthWorkerResponse;
import com.example.codebasebackend.dto.PerformanceMetricsRequest;
import com.example.codebasebackend.repositories.CommunityHealthWorkersRepository;
import com.example.codebasebackend.repositories.HospitalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
public class CommunityHealthWorkersServiceImplementation implements CommunityHealthWorkersService {

    private final CommunityHealthWorkersRepository chwRepo;
    private final HospitalRepository hospitalRepo;

    @Override
    public CommunityHealthWorkerResponse create(CommunityHealthWorkerRequest r) {
        CommunityHealthWorkers e = new CommunityHealthWorkers();
        // Ensure server-managed identifiers
        e.setId(null);
        e.setCode(null);
        apply(e, r);
        if (r.getHospitalId() != null) {
            Hospital h = hospitalRepo.findById(r.getHospitalId()).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Hospital not found"));
            e.setHospital(h);
        }
        if (e.getStatus() == null) e.setStatus(CommunityHealthWorkers.Status.AVAILABLE);
        CommunityHealthWorkers saved = chwRepo.save(e);
        if (saved.getCode() == null) {
            saved.setCode(String.format("CHW%03d", saved.getId()));
            saved = chwRepo.save(saved);
        }
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CommunityHealthWorkerResponse get(Long id) {
        return chwRepo.findById(id).map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "CHW not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommunityHealthWorkerResponse> list() {
        return chwRepo.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public CommunityHealthWorkerResponse update(Long id, CommunityHealthWorkerRequest r) {
        CommunityHealthWorkers e = chwRepo.findById(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "CHW not found"));

        // Check if status changed
        CommunityHealthWorkers.Status oldStatus = e.getStatus();
        CommunityHealthWorkers.Status newStatus = oldStatus;
        if (r.getStatus() != null) {
            try {
                newStatus = CommunityHealthWorkers.Status.valueOf(r.getStatus().toUpperCase());
            } catch (IllegalArgumentException ex) {
                throw new ResponseStatusException(BAD_REQUEST, "Invalid status: " + r.getStatus());
            }
        }
        boolean statusChanged = !oldStatus.equals(newStatus);

        apply(e, r);
        if (r.getHospitalId() != null) {
            Hospital h = hospitalRepo.findById(r.getHospitalId()).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Hospital not found"));
            e.setHospital(h);
        }

        // Update status and timestamp if changed
        e.setStatus(newStatus);
        if (statusChanged) {
            e.setLastStatusUpdate(OffsetDateTime.now());
        }

        return toResponse(chwRepo.save(e));
    }

    @Override
    public void delete(Long id) {
        if (!chwRepo.existsById(id)) throw new ResponseStatusException(NOT_FOUND, "CHW not found");
        chwRepo.deleteById(id);
    }

    @Override
    public CommunityHealthWorkerResponse updateLocation(Long id, BigDecimal lat, BigDecimal lon) {
        if (lat == null || lon == null) throw new ResponseStatusException(BAD_REQUEST, "lat/lon required");
        CommunityHealthWorkers e = chwRepo.findById(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "CHW not found"));
        e.setLatitude(lat);
        e.setLongitude(lon);
        return toResponse(chwRepo.save(e));
    }

    @Override
    @Transactional(readOnly = true)
    public CommunityHealthWorkerResponse findNearestAvailable(BigDecimal lat, BigDecimal lon, Long hospitalId) {
        if (lat == null || lon == null) throw new ResponseStatusException(BAD_REQUEST, "lat/lon required");
        List<CommunityHealthWorkers> pool = hospitalId != null
                ? chwRepo.findByStatusAndHospitalId(CommunityHealthWorkers.Status.AVAILABLE, hospitalId)
                : chwRepo.findByStatus(CommunityHealthWorkers.Status.AVAILABLE);

        return pool.stream()
                .filter(c -> c.getLatitude() != null && c.getLongitude() != null)
                .min(Comparator.comparingDouble(c -> distanceKm(lat.doubleValue(), lon.doubleValue(),
                        c.getLatitude().doubleValue(), c.getLongitude().doubleValue())))
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "No available CHW with location"));
    }

    @Override
    @Transactional(readOnly = true)
    public CommunityHealthWorkerResponse findNearestAvailable(BigDecimal lat, BigDecimal lon, Long hospitalId, BigDecimal radiusKm) {
        List<CommunityHealthWorkers> pool = hospitalId != null
                ? chwRepo.findByStatusAndHospitalId(CommunityHealthWorkers.Status.AVAILABLE, hospitalId)
                : chwRepo.findByStatus(CommunityHealthWorkers.Status.AVAILABLE);

        CommunityHealthWorkers match = pool.stream()
                .filter(c -> c.getLatitude() != null && c.getLongitude() != null)
                .map(c -> new Object[]{c, distanceKm(lat.doubleValue(), lon.doubleValue(), c.getLatitude().doubleValue(), c.getLongitude().doubleValue())})
                .filter(arr -> {
                    double d = (double) arr[1];
                    return radiusKm == null || d <= radiusKm.doubleValue();
                })
                .min(java.util.Comparator.comparingDouble(arr -> (double) arr[1]))
                .map(arr -> (CommunityHealthWorkers) arr[0])
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "No available CHW within radius"));

        return toResponse(match);
    }

    private void apply(CommunityHealthWorkers e, CommunityHealthWorkerRequest r) {
        if (r.getFirstName() != null) e.setFirstName(r.getFirstName());
        if (r.getMiddleName() != null) e.setMiddleName(r.getMiddleName());
        if (r.getLastName() != null) e.setLastName(r.getLastName());
        if (r.getEmail() != null) e.setEmail(r.getEmail());
        if (r.getPhone() != null) e.setPhone(r.getPhone());
        if (r.getAddressLine1() != null) e.setAddressLine1(r.getAddressLine1());
        if (r.getAddressLine2() != null) e.setAddressLine2(r.getAddressLine2());
        if (r.getCity() != null) e.setCity(r.getCity());
        if (r.getState() != null) e.setState(r.getState());
        if (r.getPostalCode() != null) e.setPostalCode(r.getPostalCode());
        if (r.getCountry() != null) e.setCountry(r.getCountry());
        if (r.getLatitude() != null) e.setLatitude(r.getLatitude());
        if (r.getLongitude() != null) e.setLongitude(r.getLongitude());
        if (r.getSpecialization() != null) e.setSpecialization(r.getSpecialization());

        // New fields
        if (r.getRegion() != null) e.setRegion(r.getRegion());
        if (r.getAssignedPatients() != null) e.setAssignedPatients(r.getAssignedPatients());
        if (r.getStartDate() != null) e.setStartDate(r.getStartDate());
        if (r.getMonthlyVisits() != null) e.setMonthlyVisits(r.getMonthlyVisits());
        if (r.getSuccessRate() != null) e.setSuccessRate(r.getSuccessRate());
        if (r.getResponseTime() != null) e.setResponseTime(r.getResponseTime());
        if (r.getRating() != null) e.setRating(r.getRating());
    }

    private CommunityHealthWorkerResponse toResponse(CommunityHealthWorkers e) {
        CommunityHealthWorkerResponse dto = new CommunityHealthWorkerResponse();
        dto.setId(e.getId());
        dto.setCode(e.getCode());
        dto.setFirstName(e.getFirstName());
        dto.setMiddleName(e.getMiddleName());
        dto.setLastName(e.getLastName());
        dto.setEmail(e.getEmail());
        dto.setPhone(e.getPhone());
        dto.setCity(e.getCity());
        dto.setState(e.getState());
        dto.setCountry(e.getCountry());
        dto.setLatitude(e.getLatitude());
        dto.setLongitude(e.getLongitude());
        dto.setHospitalId(e.getHospital() != null ? e.getHospital().getId() : null);
        dto.setStatus(e.getStatus() != null ? e.getStatus().name() : null);
        dto.setSpecialization(e.getSpecialization());
        dto.setCreatedAt(e.getCreatedAt());
        dto.setUpdatedAt(e.getUpdatedAt());

        // New fields
        dto.setRegion(e.getRegion());
        dto.setAssignedPatients(e.getAssignedPatients());
        dto.setStartDate(e.getStartDate());
        dto.setLastStatusUpdate(e.getLastStatusUpdate());
        dto.setMonthlyVisits(e.getMonthlyVisits());
        dto.setSuccessRate(e.getSuccessRate());
        dto.setResponseTime(e.getResponseTime());
        dto.setRating(e.getRating());

        // Computed fields
        dto.setFullName(buildFullName(e.getFirstName(), e.getMiddleName(), e.getLastName()));
        dto.setAvatar(buildAvatar(e.getFirstName(), e.getLastName()));

        return dto;
    }

    private String buildFullName(String firstName, String middleName, String lastName) {
        StringBuilder fullName = new StringBuilder();
        if (firstName != null && !firstName.isEmpty()) {
            fullName.append(firstName);
        }
        if (middleName != null && !middleName.isEmpty()) {
            if (fullName.length() > 0) fullName.append(" ");
            fullName.append(middleName);
        }
        if (lastName != null && !lastName.isEmpty()) {
            if (fullName.length() > 0) fullName.append(" ");
            fullName.append(lastName);
        }
        return fullName.toString();
    }

    private String buildAvatar(String firstName, String lastName) {
        String firstInitial = firstName != null && !firstName.isEmpty()
            ? firstName.substring(0, 1) : "";
        String lastInitial = lastName != null && !lastName.isEmpty()
            ? lastName.substring(0, 1) : "";
        return (firstInitial + lastInitial).toUpperCase();
    }

    // New service methods

    @Override
    public CommunityHealthWorkerResponse updatePerformanceMetrics(Long id, PerformanceMetricsRequest request) {
        CommunityHealthWorkers chw = chwRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "CHW not found with id: " + id));

        if (request.getMonthlyVisits() != null) {
            chw.setMonthlyVisits(request.getMonthlyVisits());
        }
        if (request.getSuccessRate() != null) {
            chw.setSuccessRate(request.getSuccessRate());
        }
        if (request.getResponseTime() != null) {
            chw.setResponseTime(request.getResponseTime());
        }
        if (request.getRating() != null) {
            chw.setRating(request.getRating());
        }

        CommunityHealthWorkers updated = chwRepo.save(chw);
        return toResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommunityHealthWorkerResponse> findByRegion(String region) {
        return chwRepo.findByRegion(region).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommunityHealthWorkerResponse> findByStatus(CommunityHealthWorkers.Status status) {
        return chwRepo.findByStatus(status).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommunityHealthWorkerResponse> search(String region,
                                                       CommunityHealthWorkers.Status status,
                                                       String city,
                                                       int page, int size,
                                                       String sortBy, String sortDirection) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("DESC")
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<CommunityHealthWorkers> chwPage = chwRepo.searchWithFilters(region, status, city, pageable);

        return chwPage.map(this::toResponse);
    }

    // Haversine distance in KM
    private double distanceKm(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371.0; // km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }
}
