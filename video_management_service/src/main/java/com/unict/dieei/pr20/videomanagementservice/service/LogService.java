package com.unict.dieei.pr20.videomanagementservice.service;

import com.unict.dieei.pr20.videomanagementservice.entity.Log;
import com.unict.dieei.pr20.videomanagementservice.repository.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LogService {

    @Autowired
    LogRepository logRepository;

    public void addLogs(String api, int inputPayloadSize, int outputPayloadSize, long responseTime, int statusCode, long xRequestId) {
        String componentName = System.getenv("HOST_NAME");
        Log log = new Log(api, inputPayloadSize, outputPayloadSize, responseTime, statusCode, xRequestId, componentName);
        logRepository.save(log);
    }
}
