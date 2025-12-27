package cn.nukkit.utils;

/**
 * Exception thrown when a chunk-related error occurs in the world or level system.
 */
public class ChunkException extends RuntimeException {

    public ChunkException(String message) {
        super(message);
    }

    public ChunkException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChunkException(Throwable cause) {
        super(cause);
    }

}
