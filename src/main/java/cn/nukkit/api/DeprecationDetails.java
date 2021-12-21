package cn.nukkit.api;

import java.lang.annotation.*;

/**
 * Describe the deprecation with more details. This is persisted to the class file, so it can be read without javadocs.
 */
@PowerNukkitOnly
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.TYPE,
        ElementType.FIELD, ElementType.PACKAGE})
@Documented
public @interface DeprecationDetails {
    /**
     * The version which marked this element as deprecated.
     */
    @PowerNukkitOnly
    String since();

    /**
     * Why it is deprecated.
     */
    @PowerNukkitOnly
    String reason();

    /**
     * What should be used or do instead.
     */
    @PowerNukkitOnly
    String replaceWith() default "";

    /**
     * When the annotated element will be removed or have it's signature changed.
     */
    @PowerNukkitOnly
    String toBeRemovedAt() default "";

    /**
     * The maintainer party that has added this depreciation. For example: PowerNukkit, Cloudburst Nukkit, and Nukkit
     */
    @PowerNukkitOnly
    String by() default "";
}
