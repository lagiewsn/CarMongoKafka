/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sukeban.car.management.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

@Entity(value = "CAR", noClassnameStored=true)
public class Car {

    @Id
    @JsonIgnore
    private ObjectId id;
    @JsonProperty
    String carPlate;
    @JsonProperty
    @Reference
    private User user;

    public Car() {

    }

    public Car(String carPlate, User user) {
        super();
        this.carPlate = carPlate;
        this.user = user;
    }

    public ObjectId getId() {
        return id;
    }

    public String getCarPlate() {
        return carPlate;
    }

    public void setCarPlate(String carPlate) {
        this.carPlate = carPlate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    

}
