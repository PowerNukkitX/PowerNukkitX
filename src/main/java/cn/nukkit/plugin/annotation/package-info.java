/**
 * Compile-time annotations that remove plugin boilerplate.
 * <p>
 * A plugin author annotates their classes and the PNX annotation processor
 * (shipped inside the server jar) does the rest at build time:
 * <ul>
 *     <li>{@link cn.nukkit.plugin.annotation.PluginMeta} on the main class emits
 *     {@code plugin.yml} — no handwritten descriptor.</li>
 *     <li>{@link cn.nukkit.plugin.annotation.EventListener} registers listeners
 *     automatically.</li>
 *     <li>{@link cn.nukkit.plugin.annotation.ScheduleTask} schedules tasks
 *     automatically.</li>
 *     <li>{@link cn.nukkit.plugin.annotation.CommandDefinition} registers commands
 *     automatically, with the name and metadata supplied on the annotation so the
 *     command class needs no constructor.</li>
 * </ul>
 * The generated registrations live in a {@code PNXPluginBootstrap} class that the
 * server invokes on enable, so there is nothing to call from {@code onEnable}.
 * <p>
 * The only build configuration a plugin needs is to put the server jar on the
 * annotation processor path so the processor is discovered, e.g. with Gradle:
 * <pre>
 * dependencies {
 *     compileOnly("org.powernukkitx:server:&lt;version&gt;")
 *     annotationProcessor("org.powernukkitx:server:&lt;version&gt;")
 * }
 * </pre>
 * After that, annotate and build — there is no other glue code.
 */
package cn.nukkit.plugin.annotation;
