package gomobi.io.forex.repository;

import java.sql.Timestamp;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import gomobi.io.forex.entity.OtpEntity;

public interface OtpRepository extends JpaRepository<OtpEntity, Long> {
    Optional<OtpEntity> findTopByEmailOrderByTimestampDesc(String email);
    void deleteByExpiresAtBefore(Timestamp timestamp);
    void deleteByEmail(String email);
}
