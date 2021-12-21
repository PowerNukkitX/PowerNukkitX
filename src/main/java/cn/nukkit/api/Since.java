package cn.nukkit.api;

import java.lang.annotation.*;

/**
 * Indicates which version added the annotated element.
 */
@PowerNukkitOnly
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.TYPE,
        ElementType.FIELD, ElementType.PACKAGE})
@Documented
@Inherited
public @interface Since {
    /**
     * The version which added the element.
     */
    @PowerNukkitOnly
    String value();
}
