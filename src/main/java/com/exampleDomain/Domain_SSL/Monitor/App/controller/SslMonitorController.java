package com.exampleDomain.Domain_SSL.Monitor.App.controller;


import com.exampleDomain.Domain_SSL.Monitor.App.dto.SslCheckRequestDto;
import com.exampleDomain.Domain_SSL.Monitor.App.dto.SslCheckResponseDto;
import com.exampleDomain.Domain_SSL.Monitor.App.enums.CertificateStatus;
import com.exampleDomain.Domain_SSL.Monitor.App.model.SslCertificateCheck;
import com.exampleDomain.Domain_SSL.Monitor.App.service.SslMonitorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/ssl")
@Tag(name = "SSL Monitor", description = "SSL Certificate monitoring endpoints")
public class SslMonitorController {

    @Autowired
    private SslMonitorService sslMonitorService;

    @PostMapping("/check")
    @Operation(summary = "Check SSL certificates for multiple domains",
            description = "Asynchronously checks SSL certificates for the provided domains")
    public CompletableFuture<ResponseEntity<List<SslCheckResponseDto>>> checkDomains(
            @Valid @RequestBody SslCheckRequestDto request) {
        return sslMonitorService.checkDomainsAsync(request)
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/check/{domain}")
    @Operation(summary = "Check SSL certificate for a single domain")
    public ResponseEntity<SslCheckResponseDto> checkSingleDomain(
            @PathVariable String domain,
            @Parameter(description = "Warning threshold in days") @RequestParam(defaultValue = "30") Integer warningThresholdDays,
            @Parameter(description = "Critical threshold in days") @RequestParam(defaultValue = "7") Integer criticalThresholdDays) {

        SslCheckResponseDto response = sslMonitorService.checkSingleDomain(domain, warningThresholdDays, criticalThresholdDays);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history/{domain}")
    @Operation(summary = "Get SSL check history for a domain")
    public ResponseEntity<List<SslCertificateCheck>> getDomainHistory(@PathVariable String domain) {
        List<SslCertificateCheck> history = sslMonitorService.getDomainHistory(domain);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/expiring")
    @Operation(summary = "Get certificates expiring within specified days")
    public ResponseEntity<List<SslCertificateCheck>> getExpiringCertificates(
            @Parameter(description = "Number of days to check") @RequestParam(defaultValue = "30") Integer days) {
        List<SslCertificateCheck> expiring = sslMonitorService.getExpiringCertificates(days);
        return ResponseEntity.ok(expiring);
    }

    @GetMapping("/status")
    @Operation(summary = "Get certificates by status")
    public ResponseEntity<List<SslCertificateCheck>> getCertificatesByStatus(
            @RequestParam List<CertificateStatus> statuses) {
        List<SslCertificateCheck> certificates = sslMonitorService.getCertificatesByStatus(statuses);
        return ResponseEntity.ok(certificates);
    }

    @GetMapping("/recent")
    @Operation(summary = "Get recent SSL checks")
    public ResponseEntity<List<SslCertificateCheck>> getRecentChecks(
            @Parameter(description = "Hours to look back") @RequestParam(defaultValue = "24") Integer hours) {
        List<SslCertificateCheck> recent = sslMonitorService.getRecentChecks(hours);
        return ResponseEntity.ok(recent);
    }
}
