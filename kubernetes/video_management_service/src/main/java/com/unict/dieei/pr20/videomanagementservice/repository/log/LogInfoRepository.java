package com.unict.dieei.pr20.videomanagementservice.repository.log;

import com.unict.dieei.pr20.videomanagementservice.model.log.LogInfo;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface LogInfoRepository extends CrudRepository<LogInfo, Integer> {
    Optional<LogInfo> findByRequestIdAndComponentName(Long requestId, String componentName);
}
