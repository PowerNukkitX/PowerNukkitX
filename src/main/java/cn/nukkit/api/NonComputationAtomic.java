package cn.nukkit.api;

import java.lang.annotation.*;

/**
 * Marks that the annotated element is not computation atomic, and its computeXXX methods may not be atomic.
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.TYPE,
        ElementType.FIELD, ElementType.PACKAGE})
@Documented
public @interface NonComputationAtomic {
}
