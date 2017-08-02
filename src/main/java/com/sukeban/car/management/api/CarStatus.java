/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sukeban.car.management.api;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author nicolas
 */
public class CarStatus {

    @JsonProperty
    private Car car;
    @JsonProperty
    private String status;

    public CarStatus() {
    }

    public CarStatus(Car car, String status) {
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
