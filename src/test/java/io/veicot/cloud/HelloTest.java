package io.veicot.cloud;

import static io.restassured.RestAssured.given;

import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class HelloTest {

    @Test
    public void testSuccessfulRequest() {
        given()
            .when().get("/")
            .then()
            .statusCode(200);
    }
}
