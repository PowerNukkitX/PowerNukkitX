package cn.nukkit.api;

import java.lang.annotation.*;

@Retention(RetentionPolicy.CLASS)
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.TYPE,
        ElementType.FIELD, ElementType.PACKAGE})


@Inherited
@Documented
public @interface PowerNukkitXOnly {

    String value() default "";
}
