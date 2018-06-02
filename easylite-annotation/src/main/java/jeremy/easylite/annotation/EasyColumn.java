package jeremy.easylite.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.CLASS)
public @interface EasyColumn {
    String name() default "";//字段名

    boolean unique() default false;//唯一

    boolean notNull() default false;//不为空
}