package com.example.codebasebackend.services;

import com.example.codebasebackend.Entities.CommunityHealthWorkers;
import com.example.codebasebackend.Entities.Hospital;
import com.example.codebasebackend.dto.CommunityHealthWorkerRequest;
import com.example.codebasebackend.dto.CommunityHealthWorkerResponse;
import com.example.codebasebackend.repositories.CommunityHealthWorkersRepository;
import com.example.codebasebackend.repositories.HospitalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
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
        apply(e, r);
        if (r.getHospitalId() != null) {
            Hospital h = hospitalRepo.findById(r.getHospitalId()).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Hospital not found"));
            e.setHospital(h);
        }
        if (e.getStatus() == null) e.setStatus(CommunityHealthWorkers.Status.AVAILABLE);
        CommunityHealthWorkers saved = chwRepo.save(e);
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
        apply(e, r);
        if (r.getHospitalId() != null) {
            Hospital h = hospitalRepo.findById(r.getHospitalId()).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Hospital not found"));
            e.setHospital(h);
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
    }

    private CommunityHealthWorkerResponse toResponse(CommunityHealthWorkers e) {
        CommunityHealthWorkerResponse dto = new CommunityHealthWorkerResponse();
        dto.setId(e.getId());
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
        return dto;
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

