package cn.nukkit.utils.collection;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

import java.lang.annotation.*;

/**
 * 声明此操作将会解冻可冻结数组
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD})
@Documented
@PowerNukkitXOnly
@Since("1.19.50-r1")
public @interface ShouldThaw {
}
