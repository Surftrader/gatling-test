package ua.com.posel.gatlingproject;

import com.github.javafaker.Faker;
import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.CoreDsl;
import io.gatling.javaapi.http.HttpDsl;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.jsonPath;

public class Step {
    private final Iterator<Map<String, Object>> emailMap;
    private final Iterator<Map<String, Object>> taskMap;

    public Step() {
        Faker faker = new Faker();
        this.emailMap = generateEmail(faker);
        this.taskMap = generateTask();
    }

    private Iterator<Map<String, Object>> generateTask() {
        return Stream.generate((Supplier<Map<String, Object>>)
                () -> Collections.singletonMap("customName", RandomStringUtils.randomAlphabetic(15,30))
        ).iterator();
    }

    private Iterator<Map<String, Object>> generateEmail(Faker faker) {
        return Stream.generate((Supplier<Map<String, Object>>)
                () -> Collections.singletonMap("customEmail", faker.internet().emailAddress())
        ).iterator();
    }

    public ChainBuilder getTasks() {
        return CoreDsl.exec(
                HttpDsl
                        .http("Get tasks")
                        .get("/tasks")
                        .check(HttpDsl.status().is(200))
        ).pause(2);
    }

    public ChainBuilder getTask() {
        return CoreDsl.exec(
                HttpDsl
                        .http("Get task")
                        .get("/tasks/#{taskId}")
                        .check(HttpDsl.status().is(200))
        ).pause(2);
    }

    public ChainBuilder postTask() {
        return CoreDsl
                .feed(taskMap)
                .exec(
                        HttpDsl
                                .http("Create task")
                                .post("/tasks")
                                .body(StringBody("{\"name\": \"#{customName}\"}"))
                                .asJson()
                                .check(HttpDsl.status().is(201),
                                        jsonPath("$.name").is(session -> session.get("customName")))
                                .check(jsonPath("$.id").saveAs("taskId"))
                ).pause(2);
    }

    public ChainBuilder getUsers() {
        return CoreDsl.exec(
                HttpDsl
                        .http("Get users")
                        .get("/users")
                        .check(HttpDsl.status().is(200))
        ).pause(2);
    }

    public ChainBuilder getUser() {
        return CoreDsl.exec(
                HttpDsl
                        .http("Get user")
                        .get("/users/#{userId}")
                        .check(HttpDsl.status().is(200))
        ).pause(2);
    }

    public ChainBuilder postUser() {
        return CoreDsl
                .feed(emailMap)
                .exec(
                        HttpDsl
                                .http("Create user")
                                .post("/users")
                                .body(StringBody("{\"email\": \"#{customEmail}\",\"password\": \"user\"}"))
                                .asJson()
                                .check(HttpDsl.status().is(201),
                                        jsonPath("$.email").is(session -> session.get("customEmail")))
                                .check(jsonPath("$.id").saveAs("userId"))
                ).pause(2);
    }

    public ChainBuilder putUser() {
        return CoreDsl
                .feed(emailMap)
                .exec(
                        HttpDsl
                                .http("Edit user")
                                .put("/users/#{userId}")
                                .body(StringBody("{\"id\": #{userId},\n" +
                                        "    \"email\": \"#{customEmail}\",\n" +
                                        "    \"password\": \"user\",\n" +
                                        "    \"enabled\": true,\n" +
                                        "    \"locked\": false}"))
                                .asJson()
                                .check(HttpDsl.status().is(200))
                ).pause(2);
    }

    public ChainBuilder putTask() {
        return CoreDsl
                .feed(taskMap)
                .exec(
                        HttpDsl
                                .http("Edit task")
                                .put("/tasks/#{taskId}")
                                .body(StringBody("{\"id\": #{taskId},\n" +
                                        "    \"name\": \"#{customName}\",\n" +
                                        "    \"taskStatus\": \"PLANNED\",\n" +
                                        "    \"userId\": #{userId}\n}"))
                                .asJson()
                                .check(HttpDsl.status().is(200))
                ).pause(2);
    }

    public ChainBuilder deleteTasks() {
        return CoreDsl.exec(
                HttpDsl
                        .http("Delete task")
                        .delete("/tasks/#{taskId}")
                        .check(HttpDsl.status().is(204))
        ).pause(2);
    }


    public ChainBuilder deleteUsers() {
        return CoreDsl.exec(
                HttpDsl
                        .http("Delete user")
                        .delete("/users/#{userId}")
                        .check(HttpDsl.status().is(204))
        ).pause(2);
    }
}
