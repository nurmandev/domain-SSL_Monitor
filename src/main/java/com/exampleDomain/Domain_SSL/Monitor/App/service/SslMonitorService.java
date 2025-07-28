package com.exampleDomain.Domain_SSL.Monitor.App.service;

import com.exampleDomain.Domain_SSL.Monitor.App.dto.SslCheckRequestDto;
import com.exampleDomain.Domain_SSL.Monitor.App.dto.SslCheckResponseDto;
import com.exampleDomain.Domain_SSL.Monitor.App.enums.CertificateStatus;
import com.exampleDomain.Domain_SSL.Monitor.App.model.SslCertificateCheck;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface SslMonitorService {
    public CompletableFuture<List<SslCheckResponseDto>> checkDomainsAsync(SslCheckRequestDto request);
    public SslCheckResponseDto checkSingleDomain(String domain, Integer warningThresholdDays, Integer criticalThresholdDays);
    public List<SslCertificateCheck> getDomainHistory(String domain);
    public List<SslCertificateCheck> getExpiringCertificates(Integer days);
    public List<SslCertificateCheck> getCertificatesByStatus(List<CertificateStatus> statuses);
    public List<SslCertificateCheck> getRecentChecks(Integer hours);
}
