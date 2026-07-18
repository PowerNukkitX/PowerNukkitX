package org.powernukkitx.scheduler;


import org.powernukkitx.plugin.Plugin;

/**
 * Task that created by a plugin.
 *
 * <p>For plugin developers: Tasks that extend this class, won't be executed when the plugin is disabled.</p>
 *
 * <p>Otherwise, tasks that extend this class can use {@link #getOwner()} to get its owner.</p>
 *
 * An example for plugin create a task:
 * <pre>
 *     public class ExampleTask extends PluginTask&lt;ExamplePlugin&gt;{
 *         public ExampleTask(ExamplePlugin plugin){
 *             super(plugin);
 *         }
 *
 *        {@code @Override}
 *         public void onRun(int currentTick){
 *             getOwner().getLogger().info("Task is executed in tick "+currentTick);
 *         }
 *     }
 * </pre>
 *
 * <p>If you want Nukkit to execute this task with delay or repeat, use {@link ServerScheduler}.</p>
 *
 * @param <T> The plugin that owns this task.
 * @author MagicDroidX(code) @ Nukkit Project
 * @author Fenxie Dama (javadoc) @ Nukkit Project
 * @since Nukkit 1.0 | Nukkit API 1.0.0
 */
public abstract class PluginTask<T extends Plugin> extends Task {

    protected final T owner;

    /**
     * Constructs a plugin-owned task.
     *
     * @param owner The plugin object that owns this task.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    public PluginTask(T owner) {
        this.owner = owner;
    }

    /**
     * Returns the owner of this task.
     *
     * @return The plugin that owns this task.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    public final T getOwner() {
        return this.owner;
    }

}
