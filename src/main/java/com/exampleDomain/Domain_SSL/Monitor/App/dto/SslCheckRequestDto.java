package com.exampleDomain.Domain_SSL.Monitor.App.dto;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class SslCheckRequestDto {

    @NotNull(message = "Domains list cannot be null")
    @NotEmpty(message = "Domains list cannot be empty")
    private List<String> domains;

    private Integer warningThresholdDays = 30;
    private Integer criticalThresholdDays = 7;

    public SslCheckRequestDto() {}

    public SslCheckRequestDto(List<String> domains) {
        this.domains = domains;
    }

    // Getters and Setters
    public List<String> getDomains() { return domains; }
    public void setDomains(List<String> domains) { this.domains = domains; }

    public Integer getWarningThresholdDays() { return warningThresholdDays; }
    public void setWarningThresholdDays(Integer warningThresholdDays) { this.warningThresholdDays = warningThresholdDays; }

    public Integer getCriticalThresholdDays() { return criticalThresholdDays; }
    public void setCriticalThresholdDays(Integer criticalThresholdDays) { this.criticalThresholdDays = criticalThresholdDays; }
}
