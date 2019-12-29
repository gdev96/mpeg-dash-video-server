package com.unict.dieei.pr20.videomanagementservice.service;

import com.unict.dieei.pr20.videomanagementservice.repository.CallStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
@Transactional
public class CallStatsService {

    @Autowired
    CallStatsRepository callStatsRepository;


    public void addCallStats(HttpServletRequest request, HttpServletResponse response) {
        String api = request.getMethod() + " " + request.getRequestURI();
        String inputPayloadSize = request.getHeader("Content-Length");
        String xRequestId = request.getHeader("X-REQUEST-ID");
        String componentName = "video_management_service";
/*
        System.out.println("--- REQUEST OBJECT ---");
        String api = request.getMethod() + " " + request.getRequestURI();
        System.out.println(api);
        String inputPayloadSize = request.getHeader("Content-Length");
        System.out.println(inputPayloadSize);
        String xRequestId = request.getHeader("X-REQUEST-ID");
        System.out.println(xRequestId);
        String componentName = "video_management_service";
        Enumeration<String> headerNames = request.getHeaderNames();
        while(headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            System.out.print(headerName + ": ");
            Enumeration<String> headerValues = request.getHeaders(headerName);
            while(headerValues.hasMoreElements()) {
                String headerValue = headerValues.nextElement();
                System.out.print(headerValue + ", ");
            }
            System.out.println();
        }
        System.out.println("--- RESPONSE OBJECT ---");
        System.out.println(response.getHeaderNames());
        System.out.println(response);
*/
    }
}
