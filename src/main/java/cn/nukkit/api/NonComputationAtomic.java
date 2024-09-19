package cn.nukkit.api;

import java.lang.annotation.*;

/**
 * Indicates that the annotated element is not computation atomic.
 * <p>
 * This annotation is used to mark elements whose computeXXX methods may not be atomic.
 * It serves as a warning to developers that the annotated methods or elements may not perform atomic computations.
 * </p>
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.TYPE,
        ElementType.FIELD, ElementType.PACKAGE})
@Documented
public @interface NonComputationAtomic {
}