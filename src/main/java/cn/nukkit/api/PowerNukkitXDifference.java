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
@PowerNukkitXOnly
@Since("1.19.20-r3")
@Inherited
@Documented
@Repeatable(PowerNukkitXDifference.DifferenceList.class)
public @interface PowerNukkitXDifference {
    @PowerNukkitXOnly
    @Since("1.19.20-r3")
    String info() default "";

    @PowerNukkitXOnly
    @Since("1.19.20-r3")
    String since() default "";

    @PowerNukkitXOnly
    @Since("1.19.20-r3")
    Class<?> extendsOnlyInPowerNukkitX() default Void.class;

    @PowerNukkitXOnly
    @Since("1.19.20-r3")
    Class<?> insteadOf() default Void.class;

    @Retention(RetentionPolicy.CLASS)
    @Target({ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.TYPE,
            ElementType.FIELD, ElementType.PACKAGE})
    @PowerNukkitXOnly
    @Since("1.19.20-r3")
    @Inherited
    @Documented
    @interface DifferenceList {
        PowerNukkitXDifference[] value();
    }
}
