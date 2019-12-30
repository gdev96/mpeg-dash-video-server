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
public class LoggingFilter extends OncePerRequestFilter {

    @Autowired
    LogService logService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Log each request and response with full Request URI, content payload and duration of the request in ms.
        long arrivalTime = System.currentTimeMillis();
        String api = request.getMethod() + " " + request.getRequestURI();
        long xRequestId = Long.parseLong(request.getHeader("X-REQUEST-ID").replace(".", ""));

        // Log request and response payload (body)
        // We CANNOT simply read the request payload here.
        // The InputStream would be consumed and cannot be read again by the actual processing server.
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        // This performs the actual request
        filterChain.doFilter(wrappedRequest, wrappedResponse);

        long responseTime = System.currentTimeMillis() - arrivalTime;

        int inputPayloadSize = request.getContentLength();
        if(inputPayloadSize == -1) {
            // We can only log the info of request's body AFTER the request has been made using ContentCachingRequestWrapper
            inputPayloadSize = wrappedRequest.getContentAsByteArray().length;
        }
        int statusCode = response.getStatus();
        String contentLength = response.getHeader("Content-Length");
        int outputPayloadSize;
        if(contentLength == null) {
            outputPayloadSize = wrappedResponse.getContentAsByteArray().length;
            wrappedResponse.setContentLength(outputPayloadSize);
        }
        else {
            outputPayloadSize = Integer.parseInt(contentLength);
        }

        // IMPORTANT: copy content of cached response back into original response
        wrappedResponse.copyBodyToResponse();

        logService.addCallStats(api, inputPayloadSize, outputPayloadSize, responseTime, statusCode, xRequestId);
    }
}
