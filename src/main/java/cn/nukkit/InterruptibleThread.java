package cn.nukkit;

/**
 * An interface to describe a thread that can be interrupted.
 * When a Nukkit server is stopping, Nukkit finds all threads implements {@code InterruptibleThread},
 * and interrupt them one by one.</p>
 *
 * @author MagicDroidX(code) @ Nukkit Project
 * @author 粉鞋大妈(javadoc) @ Nukkit Project
 * @see cn.nukkit.scheduler.AsyncWorker
 * @since Nukkit 1.0 | Nukkit API 1.0.0
 */
public interface InterruptibleThread {
}
