package com.unict.dieei.pr20.videomanagementservice.repository;

import com.unict.dieei.pr20.videomanagementservice.entity.Log;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface LogRepository extends CrudRepository<Log, Integer> {
    Optional<Log> findByRequestIdAndComponentName(Long xRequestId, String componentName);
}
