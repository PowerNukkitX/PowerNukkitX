package org.powernukkitx;

/**
 * An interface to describe a thread that can be interrupted.
 * When a Nukkit server is stopping, Nukkit finds all threads implements {@code InterruptibleThread},
 * and interrupt them one by one.</p>
 *
 * @author MagicDroidX(code) @ Nukkit Project
 * @author Fenxie Dama (javadoc) @ Nukkit Project
 * @see org.powernukkitx.scheduler.AsyncWorker
 * @since Nukkit 1.0 | Nukkit API 1.0.0
 */
public interface InterruptibleThread {
}
