package cn.nukkit.api;

import java.lang.annotation.*;

/**
 * ImmutableCollection is used to mark a collection as immutable.
 * <p>
 * ImmutableCollection是用来标记一个集合为不可变的。
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
public @interface ImmutableCollection {
}
