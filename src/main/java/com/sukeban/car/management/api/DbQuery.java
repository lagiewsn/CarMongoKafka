package com.sukeban.car.management.api;

import com.mongodb.MongoClient;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

public class DbQuery {

    private final MongodbProducer mongoProducer;
    private final Datastore datastore;

    private static final String DB_NAME = "CAR-MANAGEMENT-DB";
    private static final String TOPIC_PRODUCER = "UPDATE-USER-MANAGEMENT-DB";

    public DbQuery() {

        Morphia morphia = new Morphia();
        MongoClient mongoClient = new MongoClient();

        // create the Datastore connecting to the default port on the local host
        datastore = morphia.createDatastore(mongoClient, DB_NAME);
        datastore.ensureIndexes();
        mongoProducer = new MongodbProducer(TOPIC_PRODUCER);
    }

    public DbQuery(Datastore datastore) {
        this.datastore = datastore;
        mongoProducer = new MongodbProducer(TOPIC_PRODUCER);
    }

    public Datastore getDatastore() {
        return datastore;
    }

    public Car getCar(String carPlate) {

        Car car = null;
        Query<Car> query = datastore.createQuery(Car.class)
                .field("carPlate").contains(carPlate);

        if (query.asList().size() == 1) {
            car = query.asList().get(0);
        }

        return car;

    }

    public Status addCarUser(Car car) {

        String status = "Existing";
        User user = null;

        Query<Car> queryCar = this.datastore.createQuery(Car.class)
                .field("carPlate").contains(car.getCarPlate());

        if (queryCar.asList().isEmpty() && this.getUser(
                car.getUser().getLastName(),
                car.getUser().getFirstName()) != null) {

            this.datastore.save(car);

            Query<User> queryUser = this.datastore.createQuery(User.class).field("lastName").contains(car.getUser().getLastName())
                    .field("firstName").contains(car.getUser().getFirstName());

            UpdateOperations<User> ops = this.datastore.createUpdateOperations(User.class).addToSet("cars", car.getCarPlate());
            this.datastore.update(queryUser, ops);

            user = this.getUser(car.getUser().getLastName(), car.getUser().getFirstName());
            this.mongoProducer.produceOneEvent(user.UserAsString());
            status = "Added";
        } else if (this.getUser(
                car.getUser().getLastName(),
                car.getUser().getFirstName()) != null) {
            status = "User doesn't exist";
        }

        return new Status(car, status);
    }

    public List<Status> addMultipleCar(List<Car> cars) {
        String status = "Existing";

        User user = null;

        ListIterator<Car> it = cars.listIterator();
        List<Status> listStatus = new ArrayList<>();

        while (it.hasNext()) {

            Car car = it.next();
            Query<Car> queryCar = this.datastore.createQuery(Car.class).field("carPlate").contains(car.getCarPlate());
            Query<User> queryUser = this.datastore.createQuery(User.class).field("lastName").contains(car.getUser().getLastName())
                    .field("firstName").contains(car.getUser().getFirstName());

            UpdateOperations<User> ops = this.datastore.createUpdateOperations(User.class).addToSet("cars", car.getCarPlate());
            this.datastore.update(queryUser, ops);

            if (queryCar.asList().isEmpty() && this.getUser(
                    car.getUser().getLastName(),
                    car.getUser().getFirstName()) != null) {

                this.datastore.save(car);

                user = this.getUser(car.getUser().getLastName(), car.getUser().getFirstName());
                this.mongoProducer.produceOneEvent(user.UserAsString());
                status = "Added";
            } else if (this.getUser(
                    car.getUser().getLastName(),
                    car.getUser().getFirstName()) != null) {
                status = "User doesn't exist";
            }

            listStatus.add(new Status(car, status));
        }

        return listStatus;
    }

    public User getUser(String lastName, String firstName) {

        User user = null;
        Query<User> query = this.datastore.createQuery(User.class)
                .field("lastName").contains(lastName)
                .field("firstName").contains(firstName);

        if (query.asList().size() == 1) {
            user = query.asList().get(0);
        }
        return user;
    }

}
