package gomobi.io.forex.repository;

import java.sql.Timestamp;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import gomobi.io.forex.entity.OtpEntity;
import gomobi.io.forex.enums.OtpPurpose;

public interface OtpRepository extends JpaRepository<OtpEntity, Long> {
	//only email
    Optional<OtpEntity> findTopByEmailOrderByTimestampDesc(String email);
    void deleteByExpiresAtBefore(Timestamp timestamp);
    
    //purpose also included
    Optional<OtpEntity> findTopByEmailAndPurposeOrderByTimestampDesc(String email, OtpPurpose purpose);
    void deleteByEmailAndPurpose(String email, OtpPurpose purpose);
    
    void deleteByEmail(String email);
}
