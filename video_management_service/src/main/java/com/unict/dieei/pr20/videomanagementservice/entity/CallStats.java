package com.unict.dieei.pr20.videomanagementservice.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class CallStats {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @NotNull(message = "The API parameter must not be blank!")
    private String api;

    @NotNull(message = "The Input Payload Size parameter must not be blank!")
    private Integer inputPayloadSize;

    @NotNull(message = "The Output Payload Size parameter must not be blank!")
    private Integer outputPayloadSize;

    @NotNull(message = "The Response Time parameter must not be blank!")
    private Integer responseTime;

    @NotNull(message = "The Status Code parameter must not be blank!")
    private Integer statusCode;

    @NotNull(message = "The X-REQUEST-ID parameter must not be blank!")
    private Float xRequestId;

    @NotNull(message = "The Component Name parameter must not be blank!")
    private Float componentName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public Integer getInputPayloadSize() {
        return inputPayloadSize;
    }

    public void setInputPayloadSize(Integer inputPayloadSize) {
        this.inputPayloadSize = inputPayloadSize;
    }

    public Integer getOutputPayloadSize() {
        return outputPayloadSize;
    }

    public void setOutputPayloadSize(Integer outputPayloadSize) {
        this.outputPayloadSize = outputPayloadSize;
    }

    public Integer getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(Integer responseTime) {
        this.responseTime = responseTime;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public Float getxRequestId() {
        return xRequestId;
    }

    public void setxRequestId(Float xRequestId) {
        this.xRequestId = xRequestId;
    }

    public Float getComponentName() {
        return componentName;
    }

    public void setComponentName(Float componentName) {
        this.componentName = componentName;
    }
}
