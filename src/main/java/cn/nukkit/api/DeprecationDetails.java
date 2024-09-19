package cn.nukkit.api;

import java.lang.annotation.*;

/**
 * Describes the deprecation with more details. This is persisted to the class file, so it can be read without javadocs.
 * <p>
 * This annotation can be used to provide additional information about why a particular element is deprecated,
 * since which version it is deprecated, and what should be used instead. It also allows specifying the maintainer
 * who marked the element as deprecated.
 * </p>
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.TYPE,
        ElementType.FIELD, ElementType.PACKAGE})
@Documented
public @interface DeprecationDetails {
    /**
     * The version which marked this element as deprecated.
     *
     * @return The version string.
     */
    String since();

    /**
     * Provides the reason why the element is deprecated.
     *
     * @return The reason for deprecation.
     */
    String reason();

    /**
     * Suggests what should be used or done instead of the deprecated element.
     *
     * @return The replacement suggestion.
     */
    String replaceWith() default "";

    /**
     * Specifies the maintainer party that has added this deprecation.
     * For example: PowerNukkit, Cloudburst Nukkit, and Nukkit.
     *
     * @return The maintainer party.
     */
    String by() default "";
}