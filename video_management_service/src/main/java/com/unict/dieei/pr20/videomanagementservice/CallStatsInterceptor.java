package com.unict.dieei.pr20.videomanagementservice;

import com.unict.dieei.pr20.videomanagementservice.service.CallStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class CallStatsInterceptor implements HandlerInterceptor {

    @Autowired
    CallStatsService callStatsService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        long arrivalTime = new Date().getTime();
        request.setAttribute("Arrival-Time", arrivalTime);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        callStatsService.addCallStats(request, response);
    }
}
