package gomobi.io.forex.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import gomobi.io.forex.entity.HoldingEntity;

public interface HoldingService {
	Page<HoldingEntity> getHoldingsByUserId(Long userId, Pageable pageable);
}
