package cn.nukkit.registry;

/**
 * Exception thrown when a packet cannot be instantiated.
 */
public class PacketInstantiationException extends RuntimeException {
    public PacketInstantiationException(String message, Throwable cause) {
        super(message, cause);
    }
}
