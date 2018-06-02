package jeremy.easylite.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Created by JIANGJIAN650 on 2018/5/22.
 */
@Target(TYPE)
@Retention(CLASS)
public @interface EasyTable {
    String name() default "";
}
