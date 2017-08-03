package com.sukeban.car.management.api;

import com.mongodb.MongoClient;
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
        // tell Morphia where to find your classes
        // can be called multiple times with different packages or classes
        morphia.mapPackage("com.sukeban.user.management.api");

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
        Query<Car> query = null;
        query = datastore.createQuery(Car.class)
                .field("carPlate").contains(carPlate);

        if (query.asList().size() == 1) {
            car = query.asList().get(0);
        }

        return car;

    }

    public CarStatus addCarUser(Car car) {

        String status = "Existing";
        Query<User> queryUser = datastore.createQuery(User.class);
        User user = null;

        UpdateOperations<User> ops = datastore.createUpdateOperations(User.class);

        Query<Car> queryCar = datastore.createQuery(Car.class)
                .field("carPlate").contains(car.getCarPlate());

        if (queryCar.asList().isEmpty() && this.getUser(
                car.getUser().getLastName(),
                car.getUser().getFirstName()) != null) {

            datastore.save(car);

            queryUser = queryUser.field("lastName").contains(car.getUser().getLastName())
                    .field("firstName").contains(car.getUser().getFirstName());

            ops = ops.addToSet("cars", car.getCarPlate());
            datastore.update(queryUser, ops);

            user = this.getUser(car.getUser().getLastName(), car.getUser().getFirstName());
            mongoProducer.produceOneEvent(user.UserAsString());
            status = "Added";
        }
        else {
            status = "User doesn't exist";
        }

        return new CarStatus(car, status);
    }

    public User getUser(String lastName, String firstName) {

        User user = null;
        Query<User> query = datastore.createQuery(User.class)
                .field("lastName").contains(lastName)
                .field("firstName").contains(firstName);

        if (query.asList().size() == 1) {
            user = query.asList().get(0);
        }
        return user;
    }

    public void addUser(User user) {

        Query<User> query = this.datastore.createQuery(User.class)
                .field("lastName").contains(user.getLastName())
                .field("firstName").contains(user.getFirstName());

        if (query.asList().isEmpty()) {
            this.datastore.save(user);
            this.mongoProducer.produceOneEvent(user.UserAsString());
        }
    }

}
