/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sukeban.car.management.resources;

import com.sukeban.car.management.api.Car;
import com.sukeban.car.management.api.CarStatus;
import com.sukeban.car.management.api.DbQuery;
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

    private DbQuery dbQuery;
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
    @Path("/{carPlate}/{lastName}/{firstName}")
    @Produces(MediaType.APPLICATION_JSON)
    public CarStatus addCarUser(@PathParam("carPlate") String carPlate,
            @PathParam("lastName") String lastName,
            @PathParam("firstName") String firstName) {

        return this.dbQuery.addCarUser(carPlate, lastName, firstName);
    }

}
