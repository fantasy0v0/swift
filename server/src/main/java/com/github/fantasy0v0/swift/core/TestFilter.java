package com.github.fantasy0v0.swift.core;

import io.helidon.webserver.http.Filter;
import io.helidon.webserver.http.FilterChain;
import io.helidon.webserver.http.RoutingRequest;
import io.helidon.webserver.http.RoutingResponse;

public class TestFilter implements Filter {

  @Override
  public void beforeStart() {
    System.out.println("beforeStart");
  }

  @Override
  public void afterStop() {
    System.out.println("afterStop");
  }

  @Override
  public void filter(FilterChain chain, RoutingRequest req, RoutingResponse res) {
    System.out.println("before");
    chain.proceed();
    System.out.println("after");
  }
}
