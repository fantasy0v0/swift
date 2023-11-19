package com.github.fantasy0v0.swift.core.server;

import com.github.fantasy0v0.swift.core.server.SwiftServer;
import io.helidon.http.Method;
import io.helidon.webserver.WebServer;
import io.helidon.webserver.http.Handler;
import io.helidon.webserver.http.HttpRouting;
import org.graalvm.polyglot.Context;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class SwiftServerBuilder {

  private int backlog = 1024;

  private int maxConcurrentRequests = -1;

  private int maxTcpConnections = -1;

  private InetAddress address;

  private int port = 0;

  private long maxPayloadSize = -1L;

  private Consumer<HttpRouteBuilder> routingConsumer;

  SwiftServerBuilder() {

  }

  public SwiftServerBuilder backlog(int backlog) {
    this.backlog = backlog;
    return this;
  }

  public SwiftServerBuilder maxConcurrentRequests(int maxConcurrentRequests) {
    this.maxConcurrentRequests = maxConcurrentRequests;
    return this;
  }

  public SwiftServerBuilder maxTcpConnections(int maxTcpConnections) {
    this.maxTcpConnections = maxTcpConnections;
    return this;
  }

  public SwiftServerBuilder host(String host) {
    try {
      this.address = InetAddress.getByName(host);
    } catch (UnknownHostException e) {
      throw new IllegalArgumentException("Failed to get address from host: " + host, e);
    }
    return this;
  }

  public SwiftServerBuilder port(int port) {
    this.port = port;
    return this;
  }

  public SwiftServerBuilder maxPayloadSize(long maxPayloadSize) {
    this.maxPayloadSize = maxPayloadSize;
    return this;
  }

  public SwiftServerBuilder routing(Consumer<HttpRouteBuilder> consumer) {
    this.routingConsumer = consumer;
    return this;
  }

  public SwiftServer build(Context context) {
    Consumer<HttpRouting.Builder> builderConsumer = routing -> {
      if (null == routingConsumer) {
        return;
      }
      HttpRouteBuilder routeBuilder = new HttpRouteBuilder();
      routingConsumer.accept(routeBuilder);
      for (Method method : routeBuilder.table.keySet()) {
        Map<String, List<Handler>> pathPatternMap = routeBuilder.table.get(method);
        for (String pathPattern : pathPatternMap.keySet()) {
          List<Handler> handlers = pathPatternMap.get(pathPattern);
          for (Handler handler : handlers) {
            Handler handlerWrap = (req, res) -> {
              context.enter();
              try {
                handler.handle(req, res);
              } finally {
                context.leave();
              }
            };
            if (null == pathPattern) {
              routing.route(method, handlerWrap);
            } else {
              routing.route(method, pathPattern, handlerWrap);
            }
          }
        }

      }
    };

    WebServer webServer = WebServer.create(builder -> {
      builder
        .backlog(backlog)
        .maxConcurrentRequests(maxConcurrentRequests)
        .maxTcpConnections(maxTcpConnections)
        .address(address).port(port)
        .maxPayloadSize(maxPayloadSize)
        .routing(builderConsumer);
    });
    return new SwiftServer(webServer);
  }

}
