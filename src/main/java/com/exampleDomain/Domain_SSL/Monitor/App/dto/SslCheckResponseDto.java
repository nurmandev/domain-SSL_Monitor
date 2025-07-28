package com.exampleDomain.Domain_SSL.Monitor.App.dto;


import com.exampleDomain.Domain_SSL.Monitor.App.enums.CertificateStatus;

import java.time.LocalDateTime;

public class SslCheckResponseDto {

    private String domain;
    private String certificateIssuer;
    private String certificateSubject;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private Integer daysUntilExpiry;
    private CertificateStatus status;
    private String errorMessage;
    private LocalDateTime checkTimestamp;
    private Long responseTimeMs;

    // Constructors
    public SslCheckResponseDto() {}

    public SslCheckResponseDto(String domain, CertificateStatus status, LocalDateTime checkTimestamp) {
        this.domain = domain;
        this.status = status;
        this.checkTimestamp = checkTimestamp;
    }

    // Getters and Setters
    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }

    public String getCertificateIssuer() { return certificateIssuer; }
    public void setCertificateIssuer(String certificateIssuer) { this.certificateIssuer = certificateIssuer; }

    public String getCertificateSubject() { return certificateSubject; }
    public void setCertificateSubject(String certificateSubject) { this.certificateSubject = certificateSubject; }

    public LocalDateTime getValidFrom() { return validFrom; }
    public void setValidFrom(LocalDateTime validFrom) { this.validFrom = validFrom; }

    public LocalDateTime getValidUntil() { return validUntil; }
    public void setValidUntil(LocalDateTime validUntil) { this.validUntil = validUntil; }

    public Integer getDaysUntilExpiry() { return daysUntilExpiry; }
    public void setDaysUntilExpiry(Integer daysUntilExpiry) { this.daysUntilExpiry = daysUntilExpiry; }

    public CertificateStatus getStatus() { return status; }
    public void setStatus(CertificateStatus status) { this.status = status; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public LocalDateTime getCheckTimestamp() { return checkTimestamp; }
    public void setCheckTimestamp(LocalDateTime checkTimestamp) { this.checkTimestamp = checkTimestamp; }

    public Long getResponseTimeMs() { return responseTimeMs; }
    public void setResponseTimeMs(Long responseTimeMs) { this.responseTimeMs = responseTimeMs; }
}
