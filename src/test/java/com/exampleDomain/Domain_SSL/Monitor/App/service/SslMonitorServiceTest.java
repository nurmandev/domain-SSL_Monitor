package com.exampleDomain.Domain_SSL.Monitor.App.service;


import com.exampleDomain.Domain_SSL.Monitor.App.dto.SslCheckRequestDto;
import com.exampleDomain.Domain_SSL.Monitor.App.dto.SslCheckResponseDto;
import com.exampleDomain.Domain_SSL.Monitor.App.enums.CertificateStatus;
import com.exampleDomain.Domain_SSL.Monitor.App.model.SslCertificateCheck;
import com.exampleDomain.Domain_SSL.Monitor.App.repository.SslCertificateCheckRepository;
import com.exampleDomain.Domain_SSL.Monitor.App.service.impl.SslMonitorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
class SslMonitorServiceTest {

    @Mock
    private SslCertificateCheckRepository repository;

    @InjectMocks
    private SslMonitorServiceImpl sslMonitorService;

    private SslCertificateCheck mockCheck;
    private SslCheckRequestDto mockRequest;

    @BeforeEach
    void setUp() {
        mockCheck = new SslCertificateCheck();
        mockCheck.setId(1L);
        mockCheck.setDomain("example.com");
        mockCheck.setStatus(CertificateStatus.VALID);
        mockCheck.setCheckTimestamp(LocalDateTime.now());
        mockCheck.setDaysUntilExpiry(90);
        mockCheck.setResponseTimeMs(1000L);

        mockRequest = new SslCheckRequestDto();
        mockRequest.setDomains(Arrays.asList("example.com", "test.com"));
        mockRequest.setWarningThresholdDays(30);
        mockRequest.setCriticalThresholdDays(7);
    }

    @Test
    void testCheckSingleDomain_ValidCertificate() {
        // Given
        when(repository.save(any(SslCertificateCheck.class))).thenReturn(mockCheck);

        // When
        SslCheckResponseDto response = sslMonitorService.checkSingleDomain("google.com", 30, 7);

        // Then
        assertNotNull(response);
        assertEquals("google.com", response.getDomain());
        assertNotNull(response.getStatus());
        verify(repository, times(1)).save(any(SslCertificateCheck.class));
    }

    @Test
    void testCheckSingleDomain_InvalidDomain() {
        // Given
        when(repository.save(any(SslCertificateCheck.class))).thenReturn(mockCheck);

        // When
        SslCheckResponseDto response = sslMonitorService.checkSingleDomain("invalid-domain.invalid", 30, 7);

        // Then
        assertNotNull(response);
        assertEquals("invalid-domain.invalid", response.getDomain());
        assertEquals(CertificateStatus.ERROR, response.getStatus());
        assertNotNull(response.getErrorMessage());
        verify(repository, times(1)).save(any(SslCertificateCheck.class));
    }

    @Test
    void testCheckDomainsAsync() throws ExecutionException, InterruptedException {
        // Given
        when(repository.save(any(SslCertificateCheck.class))).thenReturn(mockCheck);

        // When
        CompletableFuture<List<SslCheckResponseDto>> future = sslMonitorService.checkDomainsAsync(mockRequest);
        List<SslCheckResponseDto> responses = future.get();

        // Then
        assertNotNull(responses);
        assertEquals(2, responses.size());
        verify(repository, times(2)).save(any(SslCertificateCheck.class));
    }

    @Test
    void testGetDomainHistory() {
        // Given
        List<SslCertificateCheck> mockHistory = Arrays.asList(mockCheck);
        when(repository.findByDomainOrderByCheckTimestampDesc(anyString())).thenReturn(mockHistory);

        // When
        List<SslCertificateCheck> history = sslMonitorService.getDomainHistory("example.com");

        // Then
        assertNotNull(history);
        assertEquals(1, history.size());
        assertEquals("example.com", history.get(0).getDomain());
        verify(repository, times(1)).findByDomainOrderByCheckTimestampDesc("example.com");
    }

    @Test
    void testGetExpiringCertificates() {
        // Given
        List<SslCertificateCheck> mockExpiring = Arrays.asList(mockCheck);
        when(repository.findExpiringWithinDays(anyInt())).thenReturn(mockExpiring);

        // When
        List<SslCertificateCheck> expiring = sslMonitorService.getExpiringCertificates(30);

        // Then
        assertNotNull(expiring);
        assertEquals(1, expiring.size());
        verify(repository, times(1)).findExpiringWithinDays(30);
    }

    @Test
    void testGetCertificatesByStatus() {
        // Given
        List<CertificateStatus> statuses = Arrays.asList(CertificateStatus.VALID, CertificateStatus.EXPIRED);
        List<SslCertificateCheck> mockCertificates = Arrays.asList(mockCheck);
        when(repository.findByStatusIn(statuses)).thenReturn(mockCertificates);

        // When
        List<SslCertificateCheck> certificates = sslMonitorService.getCertificatesByStatus(statuses);

        // Then
        assertNotNull(certificates);
        assertEquals(1, certificates.size());
        verify(repository, times(1)).findByStatusIn(statuses);
    }

    @Test
    void testGetRecentChecks() {
        // Given
        List<SslCertificateCheck> mockRecent = Arrays.asList(mockCheck);
        when(repository.findRecentChecks(any(LocalDateTime.class))).thenReturn(mockRecent);

        // When
        List<SslCertificateCheck> recent = sslMonitorService.getRecentChecks(24);

        // Then
        assertNotNull(recent);
        assertEquals(1, recent.size());
        verify(repository, times(1)).findRecentChecks(any(LocalDateTime.class));
    }
}