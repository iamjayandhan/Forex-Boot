package gomobi.io.forex.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import gomobi.io.forex.entity.HoldingEntity;
import gomobi.io.forex.repository.HoldingRepository;
import gomobi.io.forex.service.HoldingService;

@Service
public class HoldingServiceImpl implements HoldingService{
	
	 private final HoldingRepository holdingRepository;
	 
	 @Autowired
	 public HoldingServiceImpl(HoldingRepository holdingRepository) {
		 this.holdingRepository = holdingRepository;
	 }
	 
	 @Override
	 public Page<HoldingEntity> getHoldingsByUserId(Long userId, Pageable pageable) {
		     return holdingRepository.findByUserId(userId, pageable);
	 }

}
