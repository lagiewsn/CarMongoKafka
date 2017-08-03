/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sukeban.car.management.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity(value = "USER", noClassnameStored = true)
public class User {

    @Id
    @JsonIgnore
    private ObjectId id;
    @JsonProperty
    String lastName;
    @JsonProperty
    String firstName;
    @JsonIgnore
    List<String> cars;

    public User() {

    }

    public User(String lastName, String firstName) {
        this.lastName = lastName;
        this.firstName = firstName;
    }


    public ObjectId getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<String> getCars() {
        return cars;
    }

    public void setCars(List<String> cars) {
        this.cars = cars;
    }
    
    public String UserAsString() {
        Document userDocument = new Document()
                .append("lastName", lastName)
                .append("firstName", firstName)
                .append("cars",cars);
        
        return userDocument.toJson();
                
    }

}
