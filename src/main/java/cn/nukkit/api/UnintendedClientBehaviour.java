package cn.nukkit.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * This is used to mark methods which abuse unintended client behavior.
 * Mojang may (probably won't) fix those 
 */
@Documented
@Target({ElementType.METHOD})
public @interface UnintendedClientBehaviour {
}
