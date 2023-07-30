package cn.nukkit.event;

/**
 * @author Nukkit Team.
 */
public interface Cancellable {

    boolean isCancelled();

    void cancel();

    void uncancel();
}
