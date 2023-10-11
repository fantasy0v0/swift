package io.helidon.examples.quickstart.se;

import java.time.Duration;

import io.helidon.logging.common.LogConfig;
import io.helidon.config.Config;
import io.helidon.webserver.WebServer;
import io.helidon.webserver.http.HttpRouting;
import io.helidon.webserver.observe.ObserveFeature;
import io.helidon.health.HealthCheckResponse;
import io.helidon.health.HealthCheckType;
import io.helidon.webserver.observe.health.HealthObserver;
import io.helidon.webserver.observe.ObserveFeature;
import io.helidon.health.checks.DeadlockHealthCheck;
import io.helidon.health.checks.DiskSpaceHealthCheck;
import io.helidon.health.checks.HeapMemoryHealthCheck;
import io.helidon.webserver.observe.health.HealthObserver;



/**
 * The application main class.
 */
public final class Main {

    private static long serverStartTime;

    /**
     * Cannot be instantiated.
     */
    private Main() {
    }

    /**
     * Application main entry point.
     * @param args command line arguments.
     */
    public static void main(final String[] args) {
         // load logging configuration
        LogConfig.configureRuntime();

        serverStartTime = System.currentTimeMillis();

        // initialize global config from default configuration
        Config config = Config.create();
        Config.global(config);

        WebServer server = WebServer.builder()
                .config(Config.global().get("server"))
                .routing(Main::routing)
                .build()
                .start();

        System.out.println("WEB server is up! http://localhost:" + server.port() + "/simple-greet");
    }

    /**
     * Updates HTTP Routing.
     */
    static void routing(HttpRouting.Builder routing) {
        Config config = Config.global();
        
        ObserveFeature observe = ObserveFeature.builder()
                                               .observersDiscoverServices(false)
                                               .addObserver(HealthObserver.builder()
                                               .details(true)
                                               .useSystemServices(false)
                                               .addCheck(() -> HealthCheckResponse.builder()
                                                        .status(HealthCheckResponse.Status.UP)
                                                        .detail("time", System.currentTimeMillis())
                                                        .build(), HealthCheckType.READINESS)
                                               .addCheck(() -> HealthCheckResponse.builder()
                                                        .status(isStarted())
                                                        .detail("time", System.currentTimeMillis())
                                                        .build(), HealthCheckType.STARTUP)
                                               .build())
                                               .build();


        routing
               .register("/greet", new GreetService())
               .addFeature(ObserveFeature.create())
               .addFeature(observe)
               .get("/simple-greet", (req, res) -> res.send("Hello World!")); 
    }


    private static boolean isStarted() {
        return Duration.ofMillis(System.currentTimeMillis() - serverStartTime).getSeconds() >= 8;
    }

}
