package com.github.fantasy0v0.swift.server;

import io.helidon.http.Method;
import io.helidon.webserver.http.Handler;

import java.util.*;

public class HttpRouteBuilder {

  final Map<Method, Map<String, List<Handler>>> table = new HashMap<>();

  HttpRouteBuilder() {

  }

  private List<Handler> getHandlerList(Method method, String pathPattern) {
    Map<String, List<Handler>> map = table.computeIfAbsent(method, k -> new HashMap<>());
    return map.computeIfAbsent(pathPattern, k -> new ArrayList<>());
  }

  public void get(String pathPattern, Handler... handlers) {
    List<Handler> handlerList = getHandlerList(Method.GET, pathPattern);
    handlerList.addAll(Arrays.asList(handlers));
  }

  public void post(String pathPattern, Handler... handlers) {
    List<Handler> handlerList = getHandlerList(Method.POST, pathPattern);
    handlerList.addAll(Arrays.asList(handlers));
  }

}
