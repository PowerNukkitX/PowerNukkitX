package cn.nukkit.plugin;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.Event;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.utils.EventException;
import co.aikar.timings.Timing;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class RegisteredListener {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final RegisteredListener[] EMPTY_ARRAY = new RegisteredListener[0];

    private final Listener listener;

    private final EventPriority priority;

    private final Plugin plugin;

    private EventExecutor executor;

    private final boolean ignoreCancelled;

    private final Timing timing;

    public RegisteredListener(Listener listener, EventExecutor executor, EventPriority priority, Plugin plugin, boolean ignoreCancelled, Timing timing) {
        this.listener = listener;
        this.priority = priority;
        this.plugin = plugin;
        this.executor = executor;
        this.ignoreCancelled = ignoreCancelled;
        this.timing = timing;
    }

    public Listener getListener() {
        return listener;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public EventPriority getPriority() {
        return priority;
    }

    public void callEvent(Event event) throws EventException {
        if (event instanceof Cancellable) {
            if (event.isCancelled() && isIgnoringCancelled()) {
                return;
            }
        }
        this.timing.startTiming();
        try {
            executor.execute(listener, event);
        } catch (IllegalAccessError e) { // 动态编译的字节码调用失败时的逃生门
            if (executor instanceof CompiledExecutor compiledExecutor) {
                executor = new MethodEventExecutor(compiledExecutor.getOriginMethod());
                executor.execute(listener, event);
            }
        }

        this.timing.stopTiming();
    }

    public boolean isIgnoringCancelled() {
        return ignoreCancelled;
    }
}
