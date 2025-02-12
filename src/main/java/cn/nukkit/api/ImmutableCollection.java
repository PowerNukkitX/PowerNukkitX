package cn.nukkit.api;

import java.lang.annotation.*;

/**
 * ImmutableCollection is used to mark a collection as immutable.
 * <p>
 * This annotation is used to indicate that a collection should not be modified after its creation.
 * It serves as a warning to developers to maintain the integrity of the annotated collection.
 * </p>
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
public @interface ImmutableCollection {
}