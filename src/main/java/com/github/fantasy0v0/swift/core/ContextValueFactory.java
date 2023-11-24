package com.github.fantasy0v0.swift.core;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.graalvm.polyglot.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContextValueFactory implements PooledObjectFactory<Value> {

  private final Logger log = LoggerFactory.getLogger(ContextValueFactory.class);

  private final Engine engine;

  private final Source source;

  public ContextValueFactory(Engine engine, Source source) {
    this.engine = engine;
    this.source = source;
  }

  @Override
  public void activateObject(PooledObject<Value> p) {

  }

  @Override
  public void destroyObject(PooledObject<Value> p) {
    p.getObject().getContext().close();
  }

  @Override
  public PooledObject<Value> makeObject() {
    Context context = createContext();
    return new DefaultPooledObject<>(context.eval(source));
  }

  @Override
  public void passivateObject(PooledObject<Value> p) {

  }

  @Override
  public boolean validateObject(PooledObject<Value> p) {
    return true;
  }

  private Context createContext() {
    return Context.newBuilder("js")
      .engine(engine)
      .allowExperimentalOptions(true)
      .allowHostAccess(HostAccess.ALL)
      .allowHostClassLookup(clazz -> {
        log.debug("ClassLookup:{}", clazz);
        return true;
      })
      // 打印未处理的异常
      .allowExperimentalOptions(true).option("js.unhandled-rejections", "warn")
      .build();
  }

}
