package cn.nukkit.plugin.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Schedules a task automatically when the owning plugin is enabled.
 * <p>
 * May be placed on either:
 * <ul>
 *     <li>a <b>class</b> implementing {@link Runnable} (for example a subclass of
 *     {@link cn.nukkit.scheduler.Task}) with an accessible no-argument
 *     constructor — the processor schedules a fresh instance; or</li>
 *     <li>a <b>public static method</b> taking no arguments — the processor
 *     schedules a call to it.</li>
 * </ul>
 * The generated code schedules each as a delayed (optionally repeating) task via
 * the server scheduler. When {@link #period()} is {@code 0} the task runs once
 * after {@link #delay()} ticks; otherwise it repeats every {@code period} ticks.
 * <p>
 * Constraints (enforced by the processor, each violation is a compile error):
 * <ul>
 *     <li>On a class: must be a concrete {@link Runnable} with an accessible
 *     no-argument constructor.</li>
 *     <li>On a method: must be {@code public}, {@code static} and take no
 *     parameters.</li>
 * </ul>
 *
 * @see PluginMeta
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.SOURCE)
public @interface ScheduleTask {

    /**
     * Delay in ticks before the first run. 20 ticks = 1 second.
     */
    int delay() default 0;

    /**
     * Repeat period in ticks. {@code 0} means run once (no repeat).
     */
    int period() default 0;

    /**
     * Whether the task runs on an asynchronous worker thread.
     */
    boolean async() default false;
}
