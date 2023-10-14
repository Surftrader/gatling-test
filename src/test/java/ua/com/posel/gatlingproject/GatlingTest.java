package ua.com.posel.gatlingproject;

import io.gatling.javaapi.core.CoreDsl;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpDsl;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;

public class GatlingTest extends Simulation {
    public static final Logger logger = LoggerFactory.getLogger(GatlingTest.class);
    private static final String USERNAME = "admin@gmail.com";
    private static final String PASSWORD = "admin";
    private static final String PROPERTY_FILE = "application.properties";

    public GatlingTest() {
        Properties properties = getProperties();
        HttpProtocolBuilder httpProtocolBuilder =
                HttpDsl.http
                        .baseUrl(properties.getProperty("baseUrl"))
                        .basicAuth(USERNAME, PASSWORD)
                        .header("Content-Type", "application/json");
        Scenario scenario = new Scenario();
        this.setUp(
                scenario.createScenario().injectOpen(
                        CoreDsl.constantUsersPerSec(Integer.parseInt(properties.getProperty("users")))
                                .during(Integer.parseInt(properties.getProperty("during")))
                )
        ).protocols(httpProtocolBuilder);
    }

    private Properties getProperties() {
        Properties properties = new Properties();
        try (InputStream is = GatlingTest.class.getClassLoader().getResourceAsStream(PROPERTY_FILE)) {
            Reader reader = new InputStreamReader(Objects.requireNonNull(is), StandardCharsets.UTF_8);
            properties.load(reader);
        } catch (IOException e) {
            logger.error("Error loading properties from internal file", e);
        }
        return properties;
    }
}
