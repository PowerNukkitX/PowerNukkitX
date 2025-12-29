package cn.nukkit.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Provides detailed information about the deprecation of an element. This annotation is retained in the class file,
 * allowing tools to access deprecation details even without Javadocs.
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.TYPE,
        ElementType.FIELD, ElementType.PACKAGE})
@Documented
public @interface DeprecationDetails {
    /**
     * Specifies the version in which this element was marked as deprecated.
     *
     * @return the version string
     */
    String since();

    /**
     * Explains the reason for deprecation.
     *
     * @return the reason for deprecation
     */
    String reason();

    /**
     * Suggests an alternative or replacement for the deprecated element.
     *
     * @return the recommended replacement, or an empty string if none
     */
    String replaceWith() default "";

    /**
     * Identifies the maintainer or project that introduced the deprecation (e.g., PowerNukkit, Cloudburst Nukkit).
     *
     * @return the maintainer or project name, or an empty string if unspecified
     */
    String by() default "";
}
