package org.powernukkitx.plugin.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks an event listener class for automatic registration.
 * <p>
 * At compile time the PNX annotation processor collects every type carrying this
 * annotation and generates code that registers an instance of each with the
 * server's plugin manager when the owning plugin is enabled. The author no
 * longer writes {@code getServer().getPluginManager().registerEvents(...)}.
 * <p>
 * Constraints (enforced by the processor, each violation is a compile error):
 * <ul>
 *     <li>The annotated type must implement {@link org.powernukkitx.event.Listener}.</li>
 *     <li>The annotated type must be a concrete (non-abstract) class with an
 *     accessible no-argument constructor.</li>
 * </ul>
 *
 * @see PluginMeta
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface EventListener {
}
