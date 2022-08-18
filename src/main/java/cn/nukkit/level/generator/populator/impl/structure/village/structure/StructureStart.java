package cn.nukkit.level.generator.populator.impl.structure.village.structure;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.generator.populator.impl.structure.village.math.BoundingBox;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import com.google.common.collect.Lists;

import java.util.List;

public abstract class StructureStart {

    protected final ChunkManager level;
    protected final List<StructurePiece> pieces = Lists.newArrayList();
    protected BoundingBox boundingBox;
    private final int chunkX;
    private final int chunkZ;
    protected final NukkitRandom random;

    public StructureStart(ChunkManager level, int chunkX, int chunkZ) {
        this.level = level;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.random = new NukkitRandom(level.getSeed());
        this.random.setSeed(chunkX * this.random.nextInt() ^ chunkZ * this.random.nextInt() ^ level.getSeed());
        this.boundingBox = BoundingBox.getUnknownBox();
    }

    public abstract void generatePieces(ChunkManager level, int chunkX, int chunkZ);

    public BoundingBox getBoundingBox() {
        return this.boundingBox;
    }

    public List<StructurePiece> getPieces() {
        return this.pieces;
    }

    public void postProcess(ChunkManager level, NukkitRandom random, BoundingBox boundingBox, int chunkX, int chunkZ) {
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
                .put("BB", this.boundingBox.createTag());

        ListTag<CompoundTag> children = new ListTag<>("Children");
        for (StructurePiece piece : this.pieces) {
            children.add(piece.createTag());
        }
        tag.putList(children);

        return tag;
    }

    protected void moveBelowSeaLevel(int max, NukkitRandom random, int min) {
        int range = max - min;
        int y = this.boundingBox.getYSpan() + 1;
        if (y < range) {
            y += random.nextBoundedInt(range - y);
        }

        int offset = y - this.boundingBox.y1;
        this.boundingBox.move(0, offset, 0);

        for (StructurePiece piece : this.pieces) {
            piece.move(0, offset, 0);
        }
    }

    protected void moveInsideHeights(NukkitRandom random, int min, int max) {
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
