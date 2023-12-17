package cn.nukkit.api;

import java.lang.annotation.*;

/**
 * 这代表一个仅应该被使用于PNX内部的接口，插件在不知道实现的情况下不应该随意使用
 * <p>
 * This represents an interface that should only be used internally in PNX, Plugins should not be used without knowing the implementation
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.TYPE,
        ElementType.FIELD, ElementType.PACKAGE})
@PowerNukkitXInternal

@Inherited
@Documented
public @interface PowerNukkitXInternal {
}
