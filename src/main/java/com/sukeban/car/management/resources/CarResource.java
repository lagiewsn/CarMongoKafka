/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sukeban.car.management.resources;

import com.sukeban.car.management.api.Car;
import com.sukeban.car.management.api.DbQuery;
import com.sukeban.car.management.api.Status;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.mongodb.morphia.Datastore;

@Path("sukeban/car/")
@Produces(MediaType.APPLICATION_JSON)
public class CarResource {

    DbQuery dbQuery;
    Datastore datastore;

    public CarResource(Datastore datastore) {

        this.dbQuery = new DbQuery(datastore);

    }

    @GET
    @Path("/{carPlate}")
    @Produces(MediaType.APPLICATION_JSON)
    public Car getCar(@PathParam("carPlate") String carPlate) {

        return this.dbQuery.getCar(carPlate);

    }

    @POST
    @Path("add-one-car")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Status addCarUser(Car car) {

        return this.dbQuery.addCarUser(car);
    }
    
    @POST
    @Path("add-multiple-car")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<Status> addCarUser(List<Car> cars) {

        return this.dbQuery.addMultipleCar(cars);
    }
}
