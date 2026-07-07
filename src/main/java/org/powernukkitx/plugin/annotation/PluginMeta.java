package org.powernukkitx.plugin.annotation;

import org.powernukkitx.plugin.PluginBase;
import org.powernukkitx.plugin.PluginLoadOrder;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares the metadata of a PowerNukkitX plugin.
 * <p>
 * Place this on the single main plugin class (the one that extends
 * {@link PluginBase}). At compile time the PNX annotation
 * processor reads it and emits a {@code plugin.yml} into the plugin jar, so the
 * author never hand-writes one. The {@code main} entry of the generated
 * descriptor is the fully qualified name of the annotated class.
 * <p>
 * Constraints (enforced by the processor, each violation is a compile error):
 * <ul>
 *     <li>The annotated type must extend {@link PluginBase}.</li>
 *     <li>At most one type per project may carry this annotation.</li>
 * </ul>
 *
 * @see EventListener
 * @see ScheduleTask
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface PluginMeta {

    /**
     * The plugin name. Required.
     */
    String name();

    /**
     * The plugin version. Required.
     */
    String version();

    /**
     * The compatible PowerNukkitX API versions. Required, at least one.
     */
    String[] api();

    /**
     * The plugin authors. Optional.
     */
    String[] authors() default {};

    /**
     * A human-readable description of the plugin. Optional.
     */
    String description() default "";

    /**
     * The plugin website. Optional.
     */
    String website() default "";

    /**
     * The log prefix used by this plugin. Optional.
     */
    String prefix() default "";

    /**
     * Hard dependencies: plugins that must be present and loaded first. Optional.
     */
    String[] depend() default {};

    /**
     * Soft dependencies: plugins loaded first when present, but not required. Optional.
     */
    String[] softDepend() default {};

    /**
     * Plugins that must be loaded after this one. Optional.
     */
    String[] loadBefore() default {};

    /**
     * The load order. Defaults to {@link PluginLoadOrder#POSTWORLD}; the default
     * is omitted from the generated descriptor.
     */
    PluginLoadOrder order() default PluginLoadOrder.POSTWORLD;

    /**
     * Declared feature flags. Optional.
     */
    String[] features() default {};
}
