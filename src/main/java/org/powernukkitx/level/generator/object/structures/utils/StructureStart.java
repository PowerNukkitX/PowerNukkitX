package org.powernukkitx.level.generator.object.structures.utils;

import org.powernukkitx.level.generator.object.BlockManager;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.utils.random.RandomSourceProvider;
import org.powernukkitx.utils.random.Xoroshiro128;
import com.google.common.collect.Lists;

import java.util.List;

import static org.powernukkitx.level.generator.stages.normal.NormalTerrainStage.SEA_LEVEL;

public abstract class StructureStart {

    protected final BlockManager level;
    protected final List<StructurePiece> pieces = Lists.newArrayList();
    protected final RandomSourceProvider random;
    private final int chunkX;
    private final int chunkZ;
    protected BoundingBox boundingBox;

    public StructureStart(BlockManager level, int chunkX, int chunkZ) {
        this.level = level;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.random = new Xoroshiro128(level.getSeed());
        this.random.setSeed((long) chunkX * this.random.nextInt() ^ (long) chunkZ * this.random.nextInt() ^ level.getSeed());
        this.boundingBox = BoundingBox.getUnknownBox();
    }

    public abstract void generatePieces(BlockManager level, int chunkX, int chunkZ);

    public BoundingBox getBoundingBox() {
        return this.boundingBox;
    }

    public List<StructurePiece> getPieces() {
        return this.pieces;
    }

    public void postProcess(BlockManager level, RandomSourceProvider random, BoundingBox boundingBox, int chunkX, int chunkZ) {
        synchronized (this.pieces) {
            this.pieces.removeIf(piece -> piece.getBoundingBox().intersects(boundingBox) && !piece.postProcess(level, random, boundingBox, chunkX, chunkZ));
            this.calculateBoundingBox();
        }
    }

    protected void calculateBoundingBox() {
        this.boundingBox = BoundingBox.getUnknownBox();
        for (StructurePiece piece : this.pieces) {
            this.boundingBox.expand(piece.getBoundingBox());
        }
    }

    public final CompoundTag createTag() {
        CompoundTag tag = new CompoundTag()
                .putString("id", this.getType())
                .putInt("ChunkX", this.chunkX)
                .putInt("ChunkZ", this.chunkZ)
                .putIntArray("BB", this.boundingBox.createTag());

        ListTag<CompoundTag> children = new ListTag<>();
        for (StructurePiece piece : this.pieces) {
            children.add(piece.createTag());
        }
        tag.putList("Children", children);

        return tag;
    }

    protected void moveBelowSeaLevel(RandomSourceProvider random, int min) {
        int range = SEA_LEVEL - min;
        int y = this.boundingBox.getYSpan() - 64 + 1;
        if (y < range) {
            y += random.nextBoundedInt(range - y);
        }

        int offset = y - this.boundingBox.y1;
        this.boundingBox.move(0, offset, 0);

        for (StructurePiece piece : this.pieces) {
            piece.move(0, offset, 0);
        }
    }

    protected void moveInsideHeights(RandomSourceProvider random, int min, int max) {
        int range = max - min + 1 - this.boundingBox.getYSpan();
        int y;
        if (range > 1) {
            y = min + random.nextBoundedInt(range);
        } else {
            y = min;
        }

        int offset = y - this.boundingBox.y0;
        this.boundingBox.move(0, offset, 0);

        for (StructurePiece piece : this.pieces) {
            piece.move(0, offset, 0);
        }
    }

    public boolean isValid() {
        return !this.pieces.isEmpty();
    }

    public int getChunkX() {
        return this.chunkX;
    }

    public int getChunkZ() {
        return this.chunkZ;
    }

    public abstract String getType();
}
