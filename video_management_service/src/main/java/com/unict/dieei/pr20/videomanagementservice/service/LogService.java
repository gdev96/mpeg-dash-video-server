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

    public void addCallStats(String api, int inputPayloadSize, int outputPayloadSize, long responseTime, int statusCode, long xRequestId) {
        System.out.println("---- LOGS ----");
        System.out.println("API: " + api);
        System.out.println("Input Payload Size: " + inputPayloadSize);
        System.out.println("Output Payload Size: " + outputPayloadSize);
        System.out.println("Response time: " + responseTime + "ms");
        System.out.println("Status Code: " + statusCode);
        System.out.println("X-REQUEST-ID: " + xRequestId);
        String componentName = System.getenv("HOST_NAME");
        System.out.println("Component Name: " + componentName);

        Log log = new Log(api, inputPayloadSize, outputPayloadSize, responseTime, statusCode, xRequestId, componentName);
        logRepository.save(log);
    }
}
