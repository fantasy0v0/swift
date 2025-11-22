package test.container;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 仅允许哪些数据库运行, 如果为空则运行所有数据库
 *
 * @author fan 2025/8/28
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Allowed {

  Db[] value();

}
