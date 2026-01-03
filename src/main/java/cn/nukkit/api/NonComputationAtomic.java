package cn.nukkit.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated element is not computation atomic. Its computeXXX methods may not be atomic.
 * Use with caution in concurrent environments.
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.ANNOTATION_TYPE,
        ElementType.TYPE, ElementType.FIELD, ElementType.PACKAGE})
@Documented
public @interface NonComputationAtomic {
}
