package ua.com.posel.gatlingproject;

import io.gatling.javaapi.core.CoreDsl;
import io.gatling.javaapi.core.ScenarioBuilder;

public class Scenario {
    private final Step step;
    public Scenario() {
        this.step = new Step();
    }
    public ScenarioBuilder createScenario() {
        return CoreDsl
                .scenario("Scenario")
                .exec(step.getUsers())
                .exec(step.getTasks())
                .exec(step.postUser())
                .exec(step.postTask())
                .exec(step.getUser())
                .exec(step.getTask())
                .exec(step.putUser())
                .exec(step.putTask())
                .exec(step.deleteTasks())
                .exec(step.deleteUsers());
    }
}
