package jeremy.easylite.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.CLASS)
public @interface EasyId {
    String name() default "";//字段名

    boolean autoincrement() default false;//自增长(只有整形生效，非整形设置也不会设置自增长属性)
}