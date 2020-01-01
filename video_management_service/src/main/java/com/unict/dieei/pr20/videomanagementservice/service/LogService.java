package com.unict.dieei.pr20.videomanagementservice.service;

import com.unict.dieei.pr20.videomanagementservice.entity.Log;
import com.unict.dieei.pr20.videomanagementservice.repository.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class LogService {

    @Autowired
    LogRepository logRepository;

    public void addLog(String api, int inputPayloadSize, int outputPayloadSize, long responseTime, int statusCode, long requestId) {
        String componentName = System.getenv("HOST_NAME");
        Optional<Log> optionalLog = logRepository.findByRequestIdAndComponentName(requestId, componentName);
        Log log;
        if(optionalLog.isPresent()) {
            log = optionalLog.get();
            Long oldResponseTime = log.getResponseTime();
            log.setResponseTime(oldResponseTime + responseTime);
            log.setOutputPayloadSize(outputPayloadSize);
        }
        else {
            log = new Log(api, inputPayloadSize, outputPayloadSize, responseTime, statusCode, requestId, componentName);
        }
        logRepository.save(log);
    }
}
