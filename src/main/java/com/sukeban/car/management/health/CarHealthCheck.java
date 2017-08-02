/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sukeban.car.management.health;

import com.codahale.metrics.health.HealthCheck;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientException;

/**
 *
 * @author nicolas
 */
public class CarHealthCheck extends HealthCheck {
        private final MongoClient mongoClient;

    public CarHealthCheck(MongoClient mongoClient) {
        super();
        this.mongoClient = mongoClient;
    }

    /**
     * Checks if the system database, which exists in all MongoDB instances can be reached.
     * @return A Result object
     * @throws Exception
     */
    @Override
    protected Result check() throws Exception {

        try {
            mongoClient.getDB("user-datastore").getStats();
        }catch(MongoClientException ex) {
            return Result.unhealthy(ex.getMessage());
        }


        return Result.healthy();
}
}
