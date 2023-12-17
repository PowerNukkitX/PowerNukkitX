package cn.nukkit.api;

import java.lang.annotation.*;

/**
 * Indicates that the annotated element works differently in PowerNukkit environment
 * and may cause issues or unexpected behaviour when used in a normal NukkitX server
 * without PowerNukkitX's patches and features.
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.TYPE,
        ElementType.FIELD, ElementType.PACKAGE})


@Inherited
@Documented
@Repeatable(PowerNukkitXDifference.DifferenceList.class)
public @interface PowerNukkitXDifference {


    String info() default "";


    String since() default "";


    Class<?> extendsOnlyInPowerNukkitX() default Void.class;


    Class<?> insteadOf() default Void.class;

    @Retention(RetentionPolicy.CLASS)
    @Target({ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.TYPE,
            ElementType.FIELD, ElementType.PACKAGE})


    @Inherited
    @Documented
    @interface DifferenceList {
        PowerNukkitXDifference[] value();
    }
}
