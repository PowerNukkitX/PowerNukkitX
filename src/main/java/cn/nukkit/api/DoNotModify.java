package cn.nukkit.api;

import java.lang.annotation.*;

/**
 * Indicates that the return value of a method, variables, etc., should not be modified.
 * <p>
 * This annotation is used to mark methods, fields, or parameters to indicate that their values should not be altered.
 * It serves as a warning to developers to maintain the integrity of the annotated elements.
 * </p>
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
public @interface DoNotModify {
}