package cn.nukkit.api;

import java.lang.annotation.*;

/**
 * DoNotModify is used to indicate that the return value of method, variables, etc. should not be modified
 * <p/>
 * DoNotModify注解用于标明方法的返回值，变量等不应该被修改
 */
@PowerNukkitXOnly
@Since("1.19.50-r4")
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
public @interface DoNotModify {
}
