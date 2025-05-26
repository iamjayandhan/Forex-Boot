package gomobi.io.forex.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gomobi.io.forex.entity.TransactionEntity;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
    //this will return all on one go!
	List<TransactionEntity> findByUserId(Long userId);
	
	//pagination version
	Page<TransactionEntity> findByUserId(Long userId,Pageable pageable);
    
}
