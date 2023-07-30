package cn.nukkit.event;

import cn.nukkit.Server;
import lombok.extern.log4j.Log4j2;

/**
 * Describes things that happens in the server.<p>
 *
 * Things that happens in the server is called a <b>event</b>. Define a procedure that should be executed
 * when a event happens, this procedure is called a <b>listener</b>.</p>
 *
 * When Nukkit is calling a handler, the event needed to listen is judged by the type of the parameter. </p>
 *
 * For the way to implement a listener, see: {@link cn.nukkit.event.Listener}</p>
 *
 * @author Unknown(code) @ Nukkit Project
 * @author 粉鞋大妈(javadoc) @ Nukkit Project
 * @see cn.nukkit.event.EventHandler
 * @since Nukkit 1.0 | Nukkit API 1.0.0
 */
@Log4j2
public abstract class Event {

    private static final int MAX_EVENT_CALL_DEPTH = 50;

    private static int eventCallDepth = 1;

    protected String eventName = null;

    private boolean isCancelled = false;

    public final String getEventName() {
        return eventName == null ? getClass().getName() : eventName;
    }

    public void call() {
        if (eventCallDepth >= MAX_EVENT_CALL_DEPTH) {
            throw new RuntimeException(
                    "Recursive event call detected (reached max depth of " + MAX_EVENT_CALL_DEPTH + " calls)");
        }

        HandlerList handlerList = HandlerListManager.global().getListFor(getEventName());

        ++eventCallDepth;
        try {
            for (RegisteredListener registration : handlerList.getListenerList()) {
                if (!registration.getPlugin().isEnabled()) {
                    continue;
                }
                try {
                    registration.callEvent(this);
                } catch (Exception e) {
                    log.error(
                            Server.getInstance()
                                    .getLanguage()
                                    .tr(
                                            "nukkit.plugin.eventError",
                                            getEventName(),
                                            registration
                                                    .getPlugin()
                                                    .getDescription()
                                                    .getFullName(),
                                            e.getMessage(),
                                            registration
                                                    .getListener()
                                                    .getClass()
                                                    .getName()),
                            e);
                }
            }
        } finally {
            --eventCallDepth;
        }
    }

    public boolean isCancelled() {
        return this instanceof Cancellable && isCancelled;
    }

    public void cancel() {
        isCancelled = true;
    }

    public void uncancel() {
        isCancelled = false;
    }
}
