package cn.nukkit.api;

import java.lang.annotation.*;

/**
 * Indicates that the annotated element is used by reflection.
 * <p>
 * This annotation is used to mark elements that are accessed or manipulated through reflection.
 * It serves as a warning to developers that the annotated elements are intended to be used by reflection mechanisms.
 * </p>
 */
@Documented
public @interface UsedByReflection {
}