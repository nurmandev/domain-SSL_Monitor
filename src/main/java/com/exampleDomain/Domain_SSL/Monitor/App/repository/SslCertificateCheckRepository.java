package com.exampleDomain.Domain_SSL.Monitor.App.repository;


import com.exampleDomain.Domain_SSL.Monitor.App.enums.CertificateStatus;
import com.exampleDomain.Domain_SSL.Monitor.App.model.SslCertificateCheck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SslCertificateCheckRepository extends JpaRepository<SslCertificateCheck, Long> {

    List<SslCertificateCheck> findByDomainOrderByCheckTimestampDesc(String domain);

    Optional<SslCertificateCheck> findFirstByDomainOrderByCheckTimestampDesc(String domain);

    List<SslCertificateCheck> findByStatusIn(List<CertificateStatus> statuses);

    @Query("SELECT s FROM SslCertificateCheck s WHERE s.checkTimestamp >= :since ORDER BY s.checkTimestamp DESC")
    List<SslCertificateCheck> findRecentChecks(@Param("since") LocalDateTime since);

    @Query("SELECT s FROM SslCertificateCheck s WHERE s.daysUntilExpiry <= :days AND s.status != 'ERROR'")
    List<SslCertificateCheck> findExpiringWithinDays(@Param("days") Integer days);
}
