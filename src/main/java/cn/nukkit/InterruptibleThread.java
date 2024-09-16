package cn.nukkit;

/**
 * An interface to describe a thread that can be interrupted.
 *
 * <p>When a Nukkit server is stopping, Nukkit finds all threads that implement {@code InterruptibleThread},
 * and interrupts them one by one.</p>
 *
 * @see cn.nukkit.scheduler.AsyncWorker
 * @since Nukkit 1.0 | Nukkit API 1.0.0
 */
public interface InterruptibleThread {
}