package cn.nukkit.utils;

import java.lang.ref.Cleaner;

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

    public CleanerHandle(RESOURCE resource) {
        this.resource = resource;
        this.cleanable = CLEANER.register(this, new CleanerTask<>(resource));
    }

    public RESOURCE getResource() {
        return resource;
    }
}
