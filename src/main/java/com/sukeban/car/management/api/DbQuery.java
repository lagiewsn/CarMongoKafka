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

    public MongodbProducer getMongoProducer() {
        return mongoProducer;
    }

    public Datastore getDatastore() {
        return datastore;
    }

    public static String getDB_NAME() {
        return DB_NAME;
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

    public CarStatus addCarUser(String carPlate,
            String lastName,
            String firstName) {

        Car car = null;
        CarStatus carStatus = null;
        User user = null;
        String status = "Existing";
        Query<Car> queryCar = null;
        Query<User> queryUser = null;
        DbQuery dbQuery = new DbQuery();
        UpdateOperations<User> ops = null;

        queryCar = datastore.createQuery(Car.class)
                .field("carPlate").contains(carPlate);
        if (queryCar.asList().isEmpty()) {
            user = dbQuery.getUser(lastName, firstName);
            if (user == null) {
                dbQuery.addUser(lastName, firstName);
                user = dbQuery.getUser(lastName, firstName);
            }
            car = new Car(carPlate, user);
            datastore.save(car);

            queryUser = datastore.createQuery(User.class);
            ops = datastore
                    .createUpdateOperations(User.class)
                    .add("cars", car.getCarPlate());
            datastore.update(queryUser, ops);
            mongoProducer.produceOneEvent(dbQuery.getUser(lastName, firstName).UserAsString());
            status = "Added";
        }
        if (queryCar.asList().size() == 1) {
            car = dbQuery.getCar(carPlate);
        }
        return carStatus = new CarStatus(car, status);
    }

    public User getUser(String lastName, String firstName) {

        User user = null;
        Query<User> query = null;

        query = datastore.createQuery(User.class).field("lastName").contains(lastName).field("firstName").contains(firstName);
        if (query.asList().size() == 1) {
            user = query.asList().get(0);
        }
        return user;
    }

    public User addUser(String lastName, String firstName) {

        User user = null;
        Query<User> query = null;
        DbQuery dbQuery = new DbQuery();

        query = datastore.createQuery(User.class).field("lastName")
                .contains(lastName).field("firstName").contains(firstName);

        if (query.asList().isEmpty()) {
            user = new User(lastName, firstName);
            datastore.save(user);

        } else {
            user = dbQuery.getUser(lastName, firstName);

        }

        return user;
    }
}
