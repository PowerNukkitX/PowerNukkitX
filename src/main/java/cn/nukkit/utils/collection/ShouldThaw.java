package cn.nukkit.utils.collection;

import java.lang.annotation.*;

/**
 * 声明此操作将会解冻可冻结数组
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD})
@Documented


public @interface ShouldThaw {
}
