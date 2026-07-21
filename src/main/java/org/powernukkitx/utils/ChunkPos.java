package org.powernukkitx.utils;

import org.powernukkitx.math.BlockVector3;
import org.powernukkitx.math.Vector2;
import org.powernukkitx.math.Vector3;

import javax.annotation.Nullable;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public record ChunkPos(int x, int z) {

    public static ChunkPos fromBlock(int blockX, int blockZ) {
        return new ChunkPos(blockX >> 4, blockZ >> 4);
    }

    public static ChunkPos containing(final BlockVector3 pos) {
        return new ChunkPos(pos.getX() >> 4, pos.getZ() >> 4);
    }

    public static ChunkPos containing(final Vector3 vec) {
        return new ChunkPos(vec.getFloorX() >> 4, vec.getFloorZ() >> 4);
    }

    public static ChunkPos containing(final Vector2 vec) {
        return new ChunkPos(vec.getFloorX() >> 4, vec.getFloorY() >> 4);
    }

    public int distanceSquared(int x, int z) {
        int dx = this.x - x;
        int dz = this.z - z;
        return dx * dx + dz * dz;
    }



    public static Stream<ChunkPos> rangeClosed(final ChunkPos center, final int radius) {
        return rangeClosed(new ChunkPos(center.x - radius, center.z - radius), new ChunkPos(center.x + radius, center.z + radius));
    }

    public static Stream<ChunkPos> rangeClosed(final ChunkPos from, final ChunkPos to) {
        int xSize = Math.abs(from.x - to.x) + 1;
        int zSize = Math.abs(from.z - to.z) + 1;
        final int xDiff = from.x < to.x ? 1 : -1;
        final int zDiff = from.z < to.z ? 1 : -1;
        return StreamSupport.stream(new Spliterators.AbstractSpliterator<>((long) xSize * zSize, Spliterator.SIZED) {
            private @Nullable ChunkPos pos;

            public boolean tryAdvance(final Consumer<? super ChunkPos> action) {
                if (this.pos == null) {
                    this.pos = from;
                } else {
                    int x = this.pos.x;
                    int z = this.pos.z;
                    if (x == to.x) {
                        if (z == to.z) {
                            return false;
                        }

                        this.pos = new ChunkPos(from.x, z + zDiff);
                    } else {
                        this.pos = new ChunkPos(x + xDiff, z);
                    }
                }

                action.accept(this.pos);
                return true;
            }
        }, false);
    }
}
