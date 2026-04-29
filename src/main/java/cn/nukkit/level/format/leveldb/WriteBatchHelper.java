package cn.nukkit.level.format.leveldb;

import org.iq80.leveldb.WriteBatch;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Buddelbubi (PowerNukkitX)
 * @since 2026/04/29
 * Allows parallel chunk serialization.
 */
public class WriteBatchHelper implements WriteBatch {

    private final Map<byte[], byte[]> DATA = new ConcurrentHashMap<>();

    @Override
    public int getApproximateSize() {
        return DATA.size();
    }

    @Override
    public int size() {
        return DATA.size();
    }

    @Override
    public WriteBatch put(byte[] key, byte[] value) {
        DATA.put(key, value);
        return this;
    }

    @Override
    public WriteBatch delete(byte[] key) {
        DATA.remove(key);
        return this;
    }

    public void write(WriteBatch batch) {
        for(var v : DATA.entrySet()) {
            batch.put(v.getKey(), v.getValue());
        }
    }

    @Override
    public void close() {
    }
}
