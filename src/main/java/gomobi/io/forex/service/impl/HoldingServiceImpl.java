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
	 public Page<HoldingEntity> getPaginatedHoldingsByUserId(Long userId, int page, int size) {
		     Pageable pageable = PageRequest.of(page, size);
		     return holdingRepository.findByUserId(userId, pageable);
	 }
	 
	 //admin only!
	 @Override 
	 public Page<HoldingEntity> getPaginatedHoldings(int page,int size){
		 Pageable pageable = PageRequest.of(page,size);
		 return holdingRepository.findAll(pageable);
	 }
}
