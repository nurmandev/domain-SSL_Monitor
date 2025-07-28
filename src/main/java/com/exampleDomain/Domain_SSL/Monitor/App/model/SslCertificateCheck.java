package com.exampleDomain.Domain_SSL.Monitor.App.model;


import com.exampleDomain.Domain_SSL.Monitor.App.enums.CertificateStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ssl_certificate_checks")
public class SslCertificateCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String domain;

    @Column(name = "certificate_issuer")
    private String certificateIssuer;

    @Column(name = "certificate_subject")
    private String certificateSubject;

    @Column(name = "valid_from")
    private LocalDateTime validFrom;

    @Column(name = "valid_until")
    private LocalDateTime validUntil;

    @Column(name = "days_until_expiry")
    private Integer daysUntilExpiry;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CertificateStatus status;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "check_timestamp", nullable = false)
    private LocalDateTime checkTimestamp;

    @Column(name = "response_time_ms")
    private Long responseTimeMs;

    // Constructors
    public SslCertificateCheck() {}

    public SslCertificateCheck(String domain, CertificateStatus status, LocalDateTime checkTimestamp) {
        this.domain = domain;
        this.status = status;
        this.checkTimestamp = checkTimestamp;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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
