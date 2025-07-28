package com.exampleDomain.Domain_SSL.Monitor.App.repository;


import com.exampleDomain.Domain_SSL.Monitor.App.dto.SslCheckRequestDto;
import com.exampleDomain.Domain_SSL.Monitor.App.dto.SslCheckResponseDto;
import com.exampleDomain.Domain_SSL.Monitor.App.enums.CertificateStatus;
import com.exampleDomain.Domain_SSL.Monitor.App.model.SslCertificateCheck;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface SslMonitorService {

    CompletableFuture<List<SslCheckResponseDto>> checkDomainsAsync(SslCheckRequestDto request);

    SslCheckResponseDto checkSingleDomain(String domain, Integer warningThresholdDays, Integer criticalThresholdDays);

    List<SslCertificateCheck> getDomainHistory(String domain);

    List<SslCertificateCheck> getExpiringCertificates(Integer days);

    List<SslCertificateCheck> getCertificatesByStatus(List<CertificateStatus> statuses);

    List<SslCertificateCheck> getRecentChecks(Integer hours);
}
