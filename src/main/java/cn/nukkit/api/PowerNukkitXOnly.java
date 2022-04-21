package cn.nukkit.api;

import java.lang.annotation.*;

@Retention(RetentionPolicy.CLASS)
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.TYPE,
        ElementType.FIELD, ElementType.PACKAGE})
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
@Inherited
@Documented
public @interface PowerNukkitXOnly {
    @PowerNukkitXOnly
    String value() default "";
}
