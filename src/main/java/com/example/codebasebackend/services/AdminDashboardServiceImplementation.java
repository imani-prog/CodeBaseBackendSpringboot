package com.example.codebasebackend.services;

import com.example.codebasebackend.Entities.*;
import com.example.codebasebackend.dto.AdminDashboardResponse;
import com.example.codebasebackend.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImplementation implements AdminDashboardService {

    private final PatientRepository patientRepository;
    private final CommunityHealthWorkersRepository communityHealthWorkersRepository;
    private final AppointmentRepository appointmentRepository;
    private final InsuranceClaimRepository insuranceClaimRepository;
    private final TelemedicineSessionRepository telemedicineSessionRepository;
    private final AmbulanceRepository ambulanceRepository;
    private final HomeVisitRepository homeVisitRepository;
    private final AmbulanceDispatchRepository ambulanceDispatchRepository;
    private final CommunityHealthWorkerAssignmentRepository communityHealthWorkerAssignmentRepository;

    @Override
    public AdminDashboardResponse getDashboard() {
        OffsetDateTime now = OffsetDateTime.now();

        long activePatients = patientRepository.countByStatus(Patient.PatientStatus.ACTIVE);
        long availableChw = safeCount(communityHealthWorkersRepository.countByStatus(CommunityHealthWorkers.Status.AVAILABLE));
        long busyChw = safeCount(communityHealthWorkersRepository.countByStatus(CommunityHealthWorkers.Status.BUSY));
        long activeChw = availableChw + busyChw;

        long liveAppointments = appointmentRepository.countByStatusIn(List.of(
            Appointment.AppointmentStatus.CHECKED_IN,
            Appointment.AppointmentStatus.IN_PROGRESS
        ));

        long openClaims = insuranceClaimRepository.countByStatusIn(List.of(InsuranceClaim.ClaimStatus.PENDING));
        long pendingReviews = insuranceClaimRepository.countByStatusIn(List.of(InsuranceClaim.ClaimStatus.SUBMITTED));

        Integer activeTelemedicine = telemedicineSessionRepository.countByStatus(SessionStatus.ACTIVE);
        long telemedicine = activeTelemedicine != null ? activeTelemedicine : 0;

        long ambulanceActive = ambulanceRepository.countBusy();
        long criticalAlerts = 0L;

        List<YearMonth> last12Months = lastMonths(12, now);
        List<String> monthLabels = last12Months.stream()
            .map(month -> month.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH))
            .toList();
        List<Integer> patientTrend = new ArrayList<>();
        List<Integer> appointmentSlaTrend = new ArrayList<>();

        for (YearMonth month : last12Months) {
            OffsetDateTime start = month.atDay(1).atStartOfDay(now.getOffset()).toOffsetDateTime();
            OffsetDateTime end = start.plusMonths(1);
            long monthlyPatients = patientRepository.countByCreatedAtGreaterThanEqualAndCreatedAtLessThan(start, end);
            patientTrend.add((int) monthlyPatients);

            long totalAppointments = appointmentRepository.countByScheduledStartGreaterThanEqualAndScheduledStartLessThan(start, end);
            long onTimeAppointments = appointmentRepository.countOnTimeCheckIns(start, end);
            int sla = totalAppointments == 0 ? 0 : (int) Math.round((onTimeAppointments * 100.0) / totalAppointments);
            appointmentSlaTrend.add(sla);
        }

        OffsetDateTime mixStart = now.minusDays(30);
        long homeVisits = homeVisitRepository.countByScheduledAtGreaterThanEqualAndScheduledAtLessThan(mixStart, now);
        long chwOnlineAppointments = appointmentRepository
            .countByProviderRoleAndTypeInAndScheduledStartGreaterThanEqualAndScheduledStartLessThan(
                Appointment.ProviderRole.CHW,
                List.of(Appointment.AppointmentType.TELEHEALTH, Appointment.AppointmentType.TELEMEDICINE),
                mixStart,
                now
            );
        long telemedicineSessions = telemedicineSessionRepository
            .countByStartTimeGreaterThanEqualAndStartTimeLessThan(mixStart, now);
        long ambulanceEmergencies = ambulanceDispatchRepository.countDispatchesBetween(mixStart, now);
        long serviceTotal = homeVisits + chwOnlineAppointments + telemedicineSessions + ambulanceEmergencies;

        List<AdminDashboardResponse.ServiceMixItem> serviceMix = List.of(
            buildServiceMixItem("CHW Home Visits", homeVisits, serviceTotal, "#2563eb"),
            buildServiceMixItem("CHW online Appointments", chwOnlineAppointments, serviceTotal, "#059669"),
            buildServiceMixItem("Telemedicine", telemedicineSessions, serviceTotal, "#f59e0b"),
            buildServiceMixItem("Ambulance Emergency", ambulanceEmergencies, serviceTotal, "#dc2626")
        );

        long pendingRegistrations = patientRepository.countByStatus(Patient.PatientStatus.INACTIVE);
        long patientOnboardings = patientRepository.countByCreatedAtGreaterThanEqualAndCreatedAtLessThan(mixStart, now);
        long chwWorkItems = communityHealthWorkerAssignmentRepository.countByStatusIn(List.of(
            CommunityHealthWorkerAssignment.Status.ASSIGNED,
            CommunityHealthWorkerAssignment.Status.IN_PROGRESS
        ));
        long confirmedAppointments = appointmentRepository.countByStatus(Appointment.AppointmentStatus.CONFIRMED);
        long claimsUnderReview = insuranceClaimRepository.countByStatusIn(List.of(
            InsuranceClaim.ClaimStatus.PENDING,
            InsuranceClaim.ClaimStatus.SUBMITTED
        ));

        List<AdminDashboardResponse.PipelineItem> patientCarePipeline = buildPipeline(
            List.of(
                pipelineItem("Pending Registrations", pendingRegistrations),
                pipelineItem("Patient Onboardings", patientOnboardings),
                pipelineItem("CHW Work Items", chwWorkItems),
                pipelineItem("Confirmed Appointments", confirmedAppointments),
                pipelineItem("Claims Under Review", claimsUnderReview)
            )
        );

        List<AdminDashboardResponse.TopChwItem> topChws = communityHealthWorkersRepository
            .findTop5ByOrderByRatingDescSuccessRateDescMonthlyVisitsDesc()
            .stream()
            .map(this::mapToTopChw)
            .toList();

        List<AdminDashboardResponse.KpiItem> kpis = List.of(
            buildKpi("Active Patients", formatCount(activePatients), "0.0%", "Users", "text-blue-700"),
            buildKpi("Active CHWs", formatCount(activeChw), "0.0%", "UserCheck", "text-blue-700"),
            buildKpi("Live Appointments", formatCount(liveAppointments), "0.0%", "CalendarCheck2", "text-blue-700"),
            buildKpi("Open Claims", formatCount(openClaims), "0.0%", "CreditCard", "text-blue-700"),
            buildKpi("Telemedicine", formatCount(telemedicine), "0.0%", "Stethoscope", "text-blue-700"),
            buildKpi("Ambulance Active", formatCount(ambulanceActive), "0.0%", "Ambulance", "text-blue-700"),
            buildKpi("Pending Reviews", formatCount(pendingReviews), "0.0%", "ShieldCheck", "text-blue-700"),
            buildKpi("Critical Alerts", formatCount(criticalAlerts), "0", "Siren", "text-blue-700")
        );

        return AdminDashboardResponse.builder()
            .kpis(kpis)
            .patientTrend(patientTrend)
            .appointmentSlaTrend(appointmentSlaTrend)
            .monthLabels(monthLabels)
            .serviceMix(serviceMix)
            .patientCarePipeline(patientCarePipeline)
            .overdueQueues(List.of(
                AdminDashboardResponse.OverdueQueueItem.builder().queue("Follow-up appointments overdue").count(14).severity("high").build(),
                AdminDashboardResponse.OverdueQueueItem.builder().queue("Insurance claims pending > 72h").count(22).severity("medium").build(),
                AdminDashboardResponse.OverdueQueueItem.builder().queue("Unclosed ambulance incidents").count(5).severity("high").build(),
                AdminDashboardResponse.OverdueQueueItem.builder().queue("Unreviewed CHW task completions").count(9).severity("low").build()
            ))
            .insurancePayerMix(List.of(
                AdminDashboardResponse.InsuranceMixItem.builder().label("SHA").value(38).color("#1d4ed8").build(),
                AdminDashboardResponse.InsuranceMixItem.builder().label("NHIF").value(29).color("#0f766e").build(),
                AdminDashboardResponse.InsuranceMixItem.builder().label("Private Insurance").value(21).color("#f59e0b").build(),
                AdminDashboardResponse.InsuranceMixItem.builder().label("Corporate Plans").value(12).color("#dc2626").build()
            ))
            .financeBasics(List.of(
                AdminDashboardResponse.FinanceBasicItem.builder().label("Monthly Revenue").value("Ksh 2.45M").tone("").build(),
                AdminDashboardResponse.FinanceBasicItem.builder().label("Monthly Expenses").value("Ksh 1.68M").tone("").build(),
                AdminDashboardResponse.FinanceBasicItem.builder().label("Pending Payments").value("Ksh 125K").tone("").build(),
                AdminDashboardResponse.FinanceBasicItem.builder().label("Claims Value").value("Ksh 567K").tone("").build()
            ))
            .financeMonthly(List.of(
                AdminDashboardResponse.FinanceMonthlyItem.builder().month("Jan").revenue(2.1).expenses(1.5).build(),
                AdminDashboardResponse.FinanceMonthlyItem.builder().month("Feb").revenue(2.2).expenses(1.6).build(),
                AdminDashboardResponse.FinanceMonthlyItem.builder().month("Mar").revenue(2.4).expenses(1.8).build(),
                AdminDashboardResponse.FinanceMonthlyItem.builder().month("Apr").revenue(2.6).expenses(1.9).build(),
                AdminDashboardResponse.FinanceMonthlyItem.builder().month("May").revenue(2.8).expenses(2.0).build(),
                AdminDashboardResponse.FinanceMonthlyItem.builder().month("Jun").revenue(3.0).expenses(2.2).build(),
                AdminDashboardResponse.FinanceMonthlyItem.builder().month("Jul").revenue(2.9).expenses(2.1).build(),
                AdminDashboardResponse.FinanceMonthlyItem.builder().month("Aug").revenue(4.1).expenses(2.3).build(),
                AdminDashboardResponse.FinanceMonthlyItem.builder().month("Sep").revenue(3.3).expenses(2.4).build(),
                AdminDashboardResponse.FinanceMonthlyItem.builder().month("Oct").revenue(5.5).expenses(2.5).build(),
                AdminDashboardResponse.FinanceMonthlyItem.builder().month("Nov").revenue(3.7).expenses(2.6).build(),
                AdminDashboardResponse.FinanceMonthlyItem.builder().month("Dec").revenue(3.0).expenses(2.4).build()
            ))
            .insuranceClaimStatus(List.of(
                AdminDashboardResponse.InsuranceClaimStatusItem.builder().label("Approved").value(64).color("#16a34a").build(),
                AdminDashboardResponse.InsuranceClaimStatusItem.builder().label("Pending").value(23).color("#f59e0b").build(),
                AdminDashboardResponse.InsuranceClaimStatusItem.builder().label("Rejected").value(13).color("#dc2626").build()
            ))
            .quickActions(List.of(
                "Review Critical Alerts",
                "Approve Pending CHW Requests",
                "Check Overdue Follow-ups",
                "Dispatch Ambulance Queue",
                "Run Daily Ops Report",
                "Audit Telemedicine Sessions"
            ))
            .liveAlerts(List.of(
                "Critical: Ambulance dispatch lag in Nairobi East zone",
                "Warning: 22 claims pending review beyond SLA",
                "Notice: CHW onboarding batch awaiting final approval",
                "Warning: Telemedicine session drop-rate above threshold",
                "Info: New partner hospital synced successfully"
            ))
            .systemHealthSnapshot(List.of(
                AdminDashboardResponse.SystemHealthItem.builder().label("API Uptime").value("99.94%").ok(true).build(),
                AdminDashboardResponse.SystemHealthItem.builder().label("Queue Throughput").value("1.8k/hr").ok(true).build(),
                AdminDashboardResponse.SystemHealthItem.builder().label("DB Load").value("68%").ok(true).build(),
                AdminDashboardResponse.SystemHealthItem.builder().label("Failed Jobs").value("12").ok(false).build(),
                AdminDashboardResponse.SystemHealthItem.builder().label("Notification Delay").value("42s").ok(true).build(),
                AdminDashboardResponse.SystemHealthItem.builder().label("Incident Tickets").value("6").ok(false).build()
            ))
            .topPerformingChws(topChws)
            .build();
    }

    private AdminDashboardResponse.KpiItem buildKpi(String label, String value, String delta, String icon, String tone) {
        return AdminDashboardResponse.KpiItem.builder()
            .label(label)
            .value(value)
            .delta(delta)
            .icon(icon)
            .tone(tone)
            .build();
    }

    private long safeCount(Long count) {
        return count != null ? count : 0L;
    }

    private String formatCount(long value) {
        return NumberFormat.getInstance(Locale.US).format(value);
    }

    private List<YearMonth> lastMonths(int totalMonths, OffsetDateTime now) {
        return IntStream.rangeClosed(0, totalMonths - 1)
            .mapToObj(i -> YearMonth.from(now).minusMonths(totalMonths - 1L - i))
            .toList();
    }

    private AdminDashboardResponse.ServiceMixItem buildServiceMixItem(
        String label,
        long value,
        long total,
        String color
    ) {
        int percent = total == 0 ? 0 : (int) Math.round((value * 100.0) / total);
        return AdminDashboardResponse.ServiceMixItem.builder()
            .label(label)
            .value(percent)
            .color(color)
            .build();
    }

    private AdminDashboardResponse.PipelineItem pipelineItem(String stage, long count) {
        return AdminDashboardResponse.PipelineItem.builder()
            .stage(stage)
            .count((int) count)
            .progress(0)
            .tone("bg-blue-600")
            .build();
    }

    private List<AdminDashboardResponse.PipelineItem> buildPipeline(
        List<AdminDashboardResponse.PipelineItem> items
    ) {
        int maxCount = items.stream().mapToInt(AdminDashboardResponse.PipelineItem::getCount).max().orElse(0);
        return items.stream()
            .map(item -> AdminDashboardResponse.PipelineItem.builder()
                .stage(item.getStage())
                .count(item.getCount())
                .progress(maxCount == 0 ? 0 : (int) Math.round((item.getCount() * 100.0) / maxCount))
                .tone(item.getTone())
                .build())
            .toList();
    }

    private AdminDashboardResponse.TopChwItem mapToTopChw(CommunityHealthWorkers chw) {
        String name = String.format("%s %s", chw.getFirstName(), chw.getLastName()).trim();
        int patientCount = chw.getAssignedPatients() != null ? chw.getAssignedPatients() : 0;
        return AdminDashboardResponse.TopChwItem.builder()
            .id(chw.getId())
            .name(name)
            .region(chw.getRegion())
            .monthlyVisits(patientCount)
            .successRate(chw.getSuccessRate() != null ? chw.getSuccessRate().toPlainString() : "0")
            .rating(chw.getRating() != null ? chw.getRating().toPlainString() : "0")
            .status(chw.getStatus() != null ? chw.getStatus().name() : null)
            .build();
    }
}

