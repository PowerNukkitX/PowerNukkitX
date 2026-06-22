package cn.nukkit.utils;

import java.lang.ref.Cleaner;

/**
 * Utility class for managing automatic resource cleanup using Java's Cleaner API.
 * Registers a resource for cleanup when the CleanerHandle is garbage collected.
 * @param <RESOURCE> The type of resource to manage (must be AutoCloseable)
 */
public final class CleanerHandle<RESOURCE extends AutoCloseable> {
    private static final Cleaner CLEANER = Cleaner.create();
    private final Cleaner.Cleanable cleanable;
    private final RESOURCE resource;

    private static final class CleanerTask<RESOURCE extends AutoCloseable> implements Runnable {
        private final RESOURCE resource;

        private CleanerTask(RESOURCE resource) {
            this.resource = resource;
        }

        @Override
        public void run() {
            try {
                this.resource.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Constructs a CleanerHandle for the given resource.
     * @param resource The resource to manage
     */
    public CleanerHandle(RESOURCE resource) {
        this.resource = resource;
        this.cleanable = CLEANER.register(this, new CleanerTask<>(resource));
    }

    /**
     * Returns the managed resource.
     * @return The managed resource
     */
    public RESOURCE getResource() {
        return resource;
    }
}
