package com.exampleDomain.Domain_SSL.Monitor.App.service.impl;


import com.exampleDomain.Domain_SSL.Monitor.App.dto.SslCheckRequestDto;
import com.exampleDomain.Domain_SSL.Monitor.App.dto.SslCheckResponseDto;
import com.exampleDomain.Domain_SSL.Monitor.App.enums.CertificateStatus;
import com.exampleDomain.Domain_SSL.Monitor.App.model.SslCertificateCheck;
import com.exampleDomain.Domain_SSL.Monitor.App.repository.SslCertificateCheckRepository;
import com.exampleDomain.Domain_SSL.Monitor.App.service.SslMonitorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class SslMonitorServiceImpl implements SslMonitorService {

    private static final Logger logger = LoggerFactory.getLogger(SslMonitorServiceImpl.class);

    @Autowired
    private SslCertificateCheckRepository repository;

    @Override
    @Async("sslCheckExecutor")
    public CompletableFuture<List<SslCheckResponseDto>> checkDomainsAsync(SslCheckRequestDto request) {
        logger.info("Starting async SSL check for {} domains", request.getDomains().size());

        List<CompletableFuture<SslCheckResponseDto>> futures = request.getDomains().stream()
                .map(domain -> CompletableFuture.supplyAsync(() ->
                        checkSingleDomain(domain, request.getWarningThresholdDays(), request.getCriticalThresholdDays())))
                .collect(Collectors.toList());

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList()));
    }

    @Override
    public SslCheckResponseDto checkSingleDomain(String domain, Integer warningThresholdDays, Integer criticalThresholdDays) {
        long startTime = System.currentTimeMillis();
        LocalDateTime checkTime = LocalDateTime.now();

        logger.info("Checking SSL certificate for domain: {}", domain);

        SslCertificateCheck check = new SslCertificateCheck(domain, CertificateStatus.ERROR, checkTime);

        try {
            SSLContext sslContext = SSLContext.getDefault();
            SSLSocketFactory factory = sslContext.getSocketFactory();

            try (SSLSocket socket = (SSLSocket) factory.createSocket(domain, 443)) {
                socket.setSoTimeout(10000); // 10 second timeout
                socket.startHandshake();

                Certificate[] certificates = socket.getSession().getPeerCertificates();
                if (certificates.length > 0 && certificates[0] instanceof X509Certificate) {
                    X509Certificate cert = (X509Certificate) certificates[0];

                    LocalDateTime validFrom = cert.getNotBefore().toInstant()
                            .atZone(ZoneId.systemDefault()).toLocalDateTime();
                    LocalDateTime validUntil = cert.getNotAfter().toInstant()
                            .atZone(ZoneId.systemDefault()).toLocalDateTime();

                    long daysUntilExpiry = ChronoUnit.DAYS.between(checkTime, validUntil);

                    check.setCertificateIssuer(cert.getIssuerDN().getName());
                    check.setCertificateSubject(cert.getSubjectDN().getName());
                    check.setValidFrom(validFrom);
                    check.setValidUntil(validUntil);
                    check.setDaysUntilExpiry((int) daysUntilExpiry);

                    // Determine status
                    if (daysUntilExpiry < 0) {
                        check.setStatus(CertificateStatus.EXPIRED);
                    } else if (daysUntilExpiry <= criticalThresholdDays) {
                        check.setStatus(CertificateStatus.EXPIRING_SOON);
                    } else if (daysUntilExpiry <= warningThresholdDays) {
                        check.setStatus(CertificateStatus.EXPIRING_SOON);
                    } else {
                        check.setStatus(CertificateStatus.VALID);
                    }

                    logger.info("SSL certificate for {} expires in {} days", domain, daysUntilExpiry);
                }
            }
        } catch (Exception e) {
            logger.error("Error checking SSL certificate for domain {}: {}", domain, e.getMessage());
            check.setErrorMessage(e.getMessage());
            check.setStatus(CertificateStatus.ERROR);
        }

        long responseTime = System.currentTimeMillis() - startTime;
        check.setResponseTimeMs(responseTime);

        // Save to database
        repository.save(check);

        return convertToResponseDto(check);
    }

    @Override
    public List<SslCertificateCheck> getDomainHistory(String domain) {
        return repository.findByDomainOrderByCheckTimestampDesc(domain);
    }

    @Override
    public List<SslCertificateCheck> getExpiringCertificates(Integer days) {
        return repository.findExpiringWithinDays(days);
    }

    @Override
    public List<SslCertificateCheck> getCertificatesByStatus(List<CertificateStatus> statuses) {
        return repository.findByStatusIn(statuses);
    }

    @Override
    public List<SslCertificateCheck> getRecentChecks(Integer hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return repository.findRecentChecks(since);
    }

    private SslCheckResponseDto convertToResponseDto(SslCertificateCheck check) {
        SslCheckResponseDto dto = new SslCheckResponseDto();
        dto.setDomain(check.getDomain());
        dto.setCertificateIssuer(check.getCertificateIssuer());
        dto.setCertificateSubject(check.getCertificateSubject());
        dto.setValidFrom(check.getValidFrom());
        dto.setValidUntil(check.getValidUntil());
        dto.setDaysUntilExpiry(check.getDaysUntilExpiry());
        dto.setStatus(check.getStatus());
        dto.setErrorMessage(check.getErrorMessage());
        dto.setCheckTimestamp(check.getCheckTimestamp());
        dto.setResponseTimeMs(check.getResponseTimeMs());
        return dto;
    }
}
