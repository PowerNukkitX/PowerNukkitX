package cn.nukkit.level.format.leveldb;

import org.iq80.leveldb.WriteBatch;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Buddelbubi (PowerNukkitX)
 * @since 2026/04/29
 * Allows parallel chunk serialization.
 */
public class WriteBatchHelper implements WriteBatch {

    private final Map<byte[], byte[]> PUT = new ConcurrentHashMap<>();
    private final Set<byte[]> DELETE = new HashSet<>();

    @Override
    public int getApproximateSize() {
        return PUT.size();
    }

    @Override
    public int size() {
        return PUT.size();
    }

    @Override
    public WriteBatch put(byte[] key, byte[] value) {
        PUT.put(key, value);
        return this;
    }

    @Override
    public WriteBatch delete(byte[] key) {
        DELETE.add(key);
        return this;
    }

    public void write(WriteBatch batch) {
        for(var v : PUT.entrySet()) batch.put(v.getKey(), v.getValue());
        for(var v : DELETE) batch.delete(v);
    }

    @Override
    public void close() {
    }
}
