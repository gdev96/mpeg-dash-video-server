package com.unict.dieei.pr20.videomanagementservice.service;

import com.unict.dieei.pr20.videomanagementservice.model.log.LogInfo;
import com.unict.dieei.pr20.videomanagementservice.repository.log.LogInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional("logTransactionManager")
public class LogService {

    @Autowired
    LogInfoRepository logInfoRepository;

    public void addLog(String api, int inputPayloadSize, int outputPayloadSize, long responseTime, int statusCode, long requestId) {
        String componentName = System.getenv("HOST_NAME");
        Optional<LogInfo> optionalLog = logInfoRepository.findByRequestIdAndComponentName(requestId, componentName);
        LogInfo logInfo;
        if(optionalLog.isPresent()) {
            logInfo = optionalLog.get();
            Long oldResponseTime = logInfo.getResponseTime();
            logInfo.setResponseTime(oldResponseTime + responseTime);
            logInfo.setOutputPayloadSize(outputPayloadSize);
        }
        else {
            logInfo = new LogInfo(api, inputPayloadSize, outputPayloadSize, responseTime, statusCode, requestId, componentName);
        }
        logInfoRepository.save(logInfo);
    }
}
