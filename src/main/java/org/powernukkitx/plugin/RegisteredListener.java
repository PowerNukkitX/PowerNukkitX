package org.powernukkitx.plugin;

import org.powernukkitx.event.Cancellable;
import org.powernukkitx.event.Event;
import org.powernukkitx.event.EventPriority;
import org.powernukkitx.event.Listener;
import org.powernukkitx.utils.EventException;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class RegisteredListener {
    public static final RegisteredListener[] EMPTY_ARRAY = new RegisteredListener[0];

    private final Listener listener;

    private final EventPriority priority;

    private final Plugin plugin;

    private EventExecutor executor;

    private final boolean ignoreCancelled;

    public RegisteredListener(Listener listener, EventExecutor executor, EventPriority priority, Plugin plugin, boolean ignoreCancelled) {
        this.listener = listener;
        this.priority = priority;
        this.plugin = plugin;
        this.executor = executor;
        this.ignoreCancelled = ignoreCancelled;
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
        try {
            executor.execute(listener, event);
        } catch (IllegalAccessError | NoSuchMethodError e) { // escape hatch for when the dynamically compiled bytecode call fails
            if (executor instanceof CompiledExecutor compiledExecutor) {
                executor = new MethodEventExecutor(compiledExecutor.getOriginMethod());
                executor.execute(listener, event);
            }
        }
    }

    public boolean isIgnoringCancelled() {
        return ignoreCancelled;
    }
}
