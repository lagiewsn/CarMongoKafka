/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sukeban.car.management;

import com.mongodb.MongoClient;
import com.sukeban.car.management.api.MongodbConsumer;
import com.sukeban.car.management.health.CarHealthCheck;
import com.sukeban.car.management.resources.CarResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

public class CarApplication extends Application<CarConfiguration> {

    private static final String GROUP_ID = "group2";
    private static final String CONSUMER_TOPIC = "UPDATE-CAR-MANAGEMENT-DB";

    private static final String DB_NAME = "CAR-MANAGEMENT-DB";
    private Morphia morphia;
    private MongoClient mongoClient;

    public static void main(String[] args) throws Exception {
        new CarApplication().run(args);
        new MongodbConsumer(CONSUMER_TOPIC, GROUP_ID).start();
    }

    @Override
    public void initialize(Bootstrap<CarConfiguration> bootstrap) {
        // nothing to do yet
    }

    @Override
    public void run(CarConfiguration configuration,
            Environment environment) {

        morphia = new Morphia();
        mongoClient = new MongoClient();

        // create the Datastore connecting to the default port on the local host
        final Datastore datastore = morphia.createDatastore(mongoClient, DB_NAME);
        datastore.ensureIndexes();

        environment.healthChecks().register("User", new CarHealthCheck(mongoClient));

        final CarResource resource = new CarResource(datastore);
        environment.jersey().register(resource);
    }
}
