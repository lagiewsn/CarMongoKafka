package com.sukeban.car.management.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Status {

    @JsonProperty
    private Car car;
    @JsonProperty
    private String status;

    public Status() {
    }

    public Status(Car car, String status) {
        this.car = car;
        this.status = status;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    
}
