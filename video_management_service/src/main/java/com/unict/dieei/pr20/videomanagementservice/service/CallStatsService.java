package com.unict.dieei.pr20.videomanagementservice.service;

import com.unict.dieei.pr20.videomanagementservice.repository.CallStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;

@Service
@Transactional
public class CallStatsService {

    @Autowired
    CallStatsRepository callStatsRepository;

    public void addCallStats(HttpServletRequest request, HttpServletResponse response) {
        long currentTime = new Date().getTime();
        long arrivalTime = (long)request.getAttribute("Arrival-Time");
        long responseTime = currentTime - arrivalTime;
        String api = request.getMethod() + " " + request.getRequestURI();
        String inputPayloadSize = request.getHeader("Content-Length");
        String outputPayloadSize = response.getHeader("Content-Length");
        String xRequestId = request.getHeader("X-REQUEST-ID");
        int statusCode = response.getStatus();
        String componentName = "video_management_service";

        System.out.println("--- CALL STATS ---");
        System.out.println("Response time: " + responseTime + "ms");
        System.out.println("API: " + api);
        System.out.println("Input Payload Size: " + inputPayloadSize);
        System.out.println("Output Payload Size: " + outputPayloadSize);
        System.out.println("X-REQUEST-ID: " + xRequestId);
        System.out.println("Status Code: " + statusCode);
        System.out.println("Component Name: " + componentName);

        System.out.println("--- REQUEST HEADERS ---");
        Enumeration<String> requestHeaderNames = request.getHeaderNames();
        while(requestHeaderNames.hasMoreElements()) {
            String requestHeaderName = requestHeaderNames.nextElement();
            System.out.print(requestHeaderName + ": ");
            Enumeration<String> requestHeaderValues = request.getHeaders(requestHeaderName);
            while(requestHeaderValues.hasMoreElements()) {
                String headerValue = requestHeaderValues.nextElement();
                System.out.print(headerValue + ", ");
            }
            System.out.println();
        }

        System.out.println("--- RESPONSE HEADERS ---");
        Collection<String> responseHeaderNames = response.getHeaderNames();
        for(String responseHeaderName : responseHeaderNames) {
            System.out.print(responseHeaderName + ": ");
            Collection<String> responseHeaderValues = response.getHeaders(responseHeaderName);
            for(String responseHeaderValue : responseHeaderValues) {
                System.out.print(responseHeaderValue + ", ");
            }
            System.out.println();
        }
    }
}
