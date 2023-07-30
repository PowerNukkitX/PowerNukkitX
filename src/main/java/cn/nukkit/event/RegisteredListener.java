package cn.nukkit.event;

import cn.nukkit.plugin.Plugin;
import cn.nukkit.utils.EventException;
import lombok.Getter;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class RegisteredListener {

    public static final RegisteredListener[] EMPTY_ARRAY = new RegisteredListener[0];

    @Getter
    private final Listener listener;

    @Getter
    private final EventPriority priority;

    @Getter
    private final Plugin plugin;

    private EventExecutor executor;

    private final boolean ignoreCancelled;

    public RegisteredListener(
            Listener listener, EventExecutor executor, EventPriority priority, Plugin plugin, boolean ignoreCancelled) {
        this.listener = listener;
        this.priority = priority;
        this.plugin = plugin;
        this.executor = executor;
        this.ignoreCancelled = ignoreCancelled;
    }

    public void callEvent(Event event) throws EventException {
        if (event instanceof Cancellable) {
            if (event.isCancelled() && isIgnoringCancelled()) {
                return;
            }
        }
        try {
            executor.execute(listener, event);
        } catch (IllegalAccessError | NoSuchMethodError e) { // 动态编译的字节码调用失败时的逃生门
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
