package com.github.fantasy0v0.swift;


import io.helidon.http.Status;
import io.helidon.webserver.testing.junit5.SetUpRoute;
import io.helidon.webclient.http1.Http1Client;
import io.helidon.webclient.http1.Http1ClientResponse;
import io.helidon.webserver.http.HttpRouting;

import org.junit.jupiter.api.Test;
import jakarta.json.JsonObject;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

abstract class AbstractMainTest {
    private final Http1Client client;

    protected AbstractMainTest(Http1Client client) {
        this.client = client;
    }

    @SetUpRoute
    static void routing(HttpRouting.Builder builder) {
        Main.routing(builder);
    }


    @Test
    void testRootRoute() {
        try (Http1ClientResponse response = client.get("/greet")
                .request()) {

            assertThat(response.status(), is(Status.OK_200));
            JsonObject json = response.as(JsonObject.class);
            assertThat(json.getString("message"), is("Hello World!"));
        }
    }


    @Test
    void testMetricsObserver() {
        try (Http1ClientResponse response = client.get("/observe/metrics").request()) {
            assertThat(response.status(), is(Status.OK_200));
        }
    }


    @Test
    void testSimpleGreet() {
        try (Http1ClientResponse response = client.get("/simple-greet").request()) {
            assertThat(response.status(), is(Status.OK_200));
            assertThat(response.as(String.class), is("Hello World!"));
        }
    }


}
