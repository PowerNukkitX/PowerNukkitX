package org.powernukkitx.level.format.leveldb;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBFactory;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.impl.Iq80DBFactory;
import org.powernukkitx.Server;
import org.powernukkitx.leveldb.jni.NativeDBFactory;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

/**
 * Selects the LevelDB backend used for Bedrock world storage.
 *
 * @author Curse
 */
final class LevelDBBackendFactory {
    private static final DBFactory JAVA_FACTORY = new Iq80DBFactory();

    private LevelDBBackendFactory() {
    }

    static DB open(File path, Options options) throws IOException {
        String backend = Server.getInstance()
                .getSettings()
                .levelSettings()
                .levelDBBackend()
                .toLowerCase(Locale.ROOT);

        return switch (backend) {
            case "java" -> JAVA_FACTORY.open(path, options);
            case "jni" -> NativeDBFactory.factory.open(path, options);
            default -> throw new IllegalArgumentException("Unsupported LevelDB backend: " + backend);
        };
    }
}
