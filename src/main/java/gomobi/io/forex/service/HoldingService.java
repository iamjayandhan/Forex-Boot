package gomobi.io.forex.service;

import org.springframework.data.domain.Page;

import gomobi.io.forex.entity.HoldingEntity;

public interface HoldingService {
	Page<HoldingEntity> getPaginatedHoldingsByUserId(Long userId, int page, int size);
	
	//for admin only!
	Page<HoldingEntity> getPaginatedHoldings(int page, int size);
}
