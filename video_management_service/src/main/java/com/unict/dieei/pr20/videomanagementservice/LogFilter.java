package com.unict.dieei.pr20.videomanagementservice;

import com.unict.dieei.pr20.videomanagementservice.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class LogFilter extends OncePerRequestFilter {

    @Autowired
    LogService logService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Cache request and response
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        // Timestamp arrival time
        long arrivalTime = System.currentTimeMillis();

        // Perform request
        filterChain.doFilter(requestWrapper, responseWrapper);

        // Timestamp finish time
        long finishTime = System.currentTimeMillis();

        // Get eventual communication delay
        long responseTime;
        if(responseWrapper.containsHeader("Communication-Delay")) {
            long communicationDelay = Long.parseLong(responseWrapper.getHeader("Communication-Delay"));
            responseTime = finishTime - arrivalTime - communicationDelay;
        }
        else {
            responseTime = finishTime - arrivalTime;
        }

        // Get logs info
        String api = requestWrapper.getMethod() + " " + requestWrapper.getRequestURI();
        long xRequestId = Long.parseLong(requestWrapper.getHeader("X-REQUEST-ID").replace(".", ""));
        int inputPayloadSize = requestWrapper.getContentLength();
        if(inputPayloadSize == -1) {
            inputPayloadSize = requestWrapper.getContentAsByteArray().length;
        }
        int statusCode = responseWrapper.getStatus();
        String contentLength = responseWrapper.getHeader("Content-Length");
        int outputPayloadSize;
        if(contentLength == null) {
            outputPayloadSize = responseWrapper.getContentAsByteArray().length;
            responseWrapper.setContentLength(outputPayloadSize);
        }
        else {
            outputPayloadSize = Integer.parseInt(contentLength);
        }

        // IMPORTANT: Copy content of cached response back into original response
        responseWrapper.copyBodyToResponse();

        // Save logs to DB
        logService.addLog(api, inputPayloadSize, outputPayloadSize, responseTime, statusCode, xRequestId);
    }
}
