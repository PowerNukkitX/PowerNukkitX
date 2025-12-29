package cn.nukkit.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Marks methods that exploit unintended client behavior. These behaviors may be fixed by Mojang in the future.
 */
@Documented
@Target({ElementType.METHOD})
public @interface UnintendedClientBehaviour {
}
