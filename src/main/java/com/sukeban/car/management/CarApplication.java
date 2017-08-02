/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sukeban.car.management;

import com.mongodb.MongoClient;
import com.sukeban.car.management.health.CarHealthCheck;
import com.sukeban.car.management.resources.CarResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class CarApplication extends Application<CarConfiguration> {
    
    private final MongoClient mongoClient = new MongoClient();

    public static void main(String[] args) throws Exception {
        new CarApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<CarConfiguration> bootstrap) {
        // nothing to do yet
    }

    @Override
    public void run(CarConfiguration configuration,
            Environment environment) {


        environment.healthChecks().register("User", new CarHealthCheck(mongoClient));

        final CarResource resource = new CarResource();

        environment.jersey().register(resource);

    }
}
