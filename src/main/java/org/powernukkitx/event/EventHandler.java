package org.powernukkitx.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that defines a handler.
 *
 * <p>The importance of a handler is called its <b>priority</b>, handlers with higher priority speaks louder then
 * lower ones. See: {@link #priority()}</p>
 *
 * <p>A handler can choose to ignore a cancelled event or not, that can be defined in {@link #ignoreCancelled()}.</p>
 *
 * @author MagicDroidX(code) @ Nukkit Project
 * @author null(javadoc) @ Nukkit Project
 * @see org.powernukkitx.event.Listener
 * @see org.powernukkitx.event.Event
 * @since Nukkit 1.0 | Nukkit API 1.0.0
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventHandler {
    /**
     * Define the priority of the handler.
     *
     * When Nukkit calls all handlers, ones with lower priority is called earlier,
     * that make handlers with higher priority can replace the decisions made by lower ones.
     * The order that Nukkit call handlers is from the first to the last as:
     * <ol>
     * <li>EventPriority.LOWEST
     * <li>EventPriority.LOW
     * <li>EventPriority.NORMAL
     * <li>EventPriority.HIGH
     * <li>EventPriority.HIGHEST
     * <li>EventPriority.MONITOR
     * </ol>
     *
     * @return The priority of this handler.
     * @see org.powernukkitx.event.EventHandler
     */
    EventPriority priority() default EventPriority.NORMAL;

    /**
     * Define if the handler ignores a cancelled event.
     *
     * <p>If ignoreCancelled is {@code true} and the event is cancelled, the method is
     * not called. Otherwise, the method is always called.</p>
     *
     * @return Whether cancelled events should be ignored.
     * @see org.powernukkitx.event.EventHandler
     */
    boolean ignoreCancelled() default false;
}
