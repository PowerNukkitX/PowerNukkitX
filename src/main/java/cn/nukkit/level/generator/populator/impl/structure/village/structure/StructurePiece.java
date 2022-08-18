package cn.nukkit.level.generator.populator.impl.structure.village.structure;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.generator.populator.impl.structure.village.block.BlockTypes;
import cn.nukkit.level.generator.populator.impl.structure.village.block.state.*;
import cn.nukkit.level.generator.populator.impl.structure.village.math.BoundingBox;
import cn.nukkit.level.generator.populator.impl.structure.village.math.Rotation;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.nbt.tag.CompoundTag;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;

public abstract class StructurePiece {

    protected ChunkManager level;
    protected BoundingBox boundingBox;
    @Nullable
    private BlockFace orientation;
    private Rotation rotation = Rotation.NONE;
    protected int genDepth;

    protected StructurePiece(int genDepth) {
        this.genDepth = genDepth;
    }

    public StructurePiece(CompoundTag tag) {
        this(tag.getInt("GD"));
        if (tag.contains("BB")) {
            this.boundingBox = new BoundingBox(tag.getIntArray("BB"));
        }
        int orientation = tag.getInt("O");
        this.setOrientation(orientation == -1 ? null : BlockFace.fromHorizontalIndex(orientation));
    }

    public final CompoundTag createTag() {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", this.getType());
        tag.put("BB", this.boundingBox.createTag());
        BlockFace orientation = this.getOrientation();
        tag.putInt("O", orientation == null ? -1 : orientation.getHorizontalIndex());
        tag.putInt("GD", this.genDepth);
        this.addAdditionalSaveData(tag);
        return tag;
    }

    protected abstract void addAdditionalSaveData(CompoundTag tag);

    public void addChildren(StructurePiece piece, List<StructurePiece> pieces, NukkitRandom random) {
        //NOOP
    }

    public abstract boolean postProcess(ChunkManager level, NukkitRandom random, BoundingBox boundingBox, int chunkX, int chunkZ);

    public BoundingBox getBoundingBox() {
        return this.boundingBox;
    }

    public int getGenDepth() {
        return this.genDepth;
    }

    public static StructurePiece findCollisionPiece(List<StructurePiece> pieces, BoundingBox boundingBox) {
        Iterator<StructurePiece> iterator = pieces.iterator();

        StructurePiece piece;
        do {
            if (!iterator.hasNext()) {
                return null;
            }

            piece = iterator.next();
        } while (piece.getBoundingBox() == null || !piece.getBoundingBox().intersects(boundingBox));

        return piece;
    }

    protected boolean edgesLiquid(ChunkManager level, final BoundingBox boundingBox) {
        int x0 = Math.max(this.boundingBox.x0 - 1, boundingBox.x0);
        int y0 = Math.max(this.boundingBox.y0 - 1, boundingBox.y0);
        int z0 = Math.max(this.boundingBox.z0 - 1, boundingBox.z0);
        int x1 = Math.min(this.boundingBox.x1 + 1, boundingBox.x1);
        int y1 = Math.min(this.boundingBox.y1 + 1, boundingBox.y1);
        int z1 = Math.min(this.boundingBox.z1 + 1, boundingBox.z1);

        for (int x = x0; x <= x1; ++x) {
            for (int z = z0; z <= z1; ++z) {
                if (BlockTypes.isLiquid(level.getBlockIdAt(x, y0, z)) || BlockTypes.isLiquid(level.getBlockIdAt(x, y1, z))) {
                    return true;
                }
            }
        }
        for (int x = x0; x <= x1; ++x) {
            for (int y = y0; y <= y1; ++y) {
                if (BlockTypes.isLiquid(level.getBlockIdAt(x, y, z0)) || BlockTypes.isLiquid(level.getBlockIdAt(x, y, z1))) {
                    return true;
                }
            }
        }
        for (int z = z0; z <= z1; ++z) {
            for (int y = y0; y <= y1; ++y) {
                if (BlockTypes.isLiquid(level.getBlockIdAt(x0, y, z)) || BlockTypes.isLiquid(level.getBlockIdAt(x1, y, z))) {
                    return true;
                }
            }
        }

        return false;
    }

    protected int getWorldX(int x, int z) {
        BlockFace orientation = this.getOrientation();
        if (orientation == null) {
            return x;
        } else {
            switch (orientation) {
                case NORTH:
                case SOUTH:
                    return this.boundingBox.x0 + x;
                case WEST:
                    return this.boundingBox.x1 - z;
                case EAST:
                    return this.boundingBox.x0 + z;
                default:
                    return x;
            }
        }
    }

    protected int getWorldY(int y) {
        return this.getOrientation() == null ? y : y + this.boundingBox.y0;
    }

    protected int getWorldZ(int x, int z) {
        BlockFace orientation = this.getOrientation();
        if (orientation == null) {
            return z;
        } else {
            switch (orientation) {
                case NORTH:
                    return this.boundingBox.z1 - z;
                case SOUTH:
                    return this.boundingBox.z0 + z;
                case WEST:
                case EAST:
                    return this.boundingBox.z0 + x;
                default:
                    return z;
            }
        }
    }

    protected void placeBlock(ChunkManager level, BlockState block, int x, int y, int z, BoundingBox boundingBox) {
        BlockVector3 vec = new BlockVector3(this.getWorldX(x, z), this.getWorldY(y), this.getWorldZ(x, z));
        if (boundingBox.isInside(vec)) {
            if (this.rotation != Rotation.NONE) {
                switch (block.getId()) {
                    case Block.TORCH:
                        if (this.rotation == Rotation.CLOCKWISE_90) {
                            block = block.rotate(this.rotation);
                        } else if (this.rotation == Rotation.CLOCKWISE_180) {
                            switch (block.getMeta()) {
                                case TorchFacingDirection.EAST:
                                case TorchFacingDirection.WEST:
                                    break;
                                case TorchFacingDirection.SOUTH:
                                case TorchFacingDirection.NORTH:
                                    block = block.rotate(this.rotation);
                                    break;
                            }
                        } else if (this.rotation == Rotation.COUNTERCLOCKWISE_90) {
                            switch (block.getMeta()) {
                                case TorchFacingDirection.EAST:
                                case TorchFacingDirection.WEST:
                                    block = block.rotate(Rotation.CLOCKWISE_90);
                                    break;
                                case TorchFacingDirection.SOUTH:
                                case TorchFacingDirection.NORTH:
                                    block = block.rotate(this.rotation);
                                    break;
                            }
                        }
                        break;
                    case Block.LADDER:
                        if (this.rotation == Rotation.CLOCKWISE_90) {
                            block = block.rotate(this.rotation);
                        } else if (this.rotation == Rotation.CLOCKWISE_180) {
                            switch (block.getMeta()) {
                                case FacingDirection.EAST:
                                case FacingDirection.WEST:
                                    break;
                                case FacingDirection.SOUTH:
                                case FacingDirection.NORTH:
                                    block = block.rotate(this.rotation);
                                    break;
                            }
                        } else if (this.rotation == Rotation.COUNTERCLOCKWISE_90) {
                            switch (block.getMeta()) {
                                case FacingDirection.EAST:
                                case FacingDirection.WEST:
                                    block = block.rotate(Rotation.CLOCKWISE_90);
                                    break;
                                case FacingDirection.SOUTH:
                                case FacingDirection.NORTH:
                                    block = block.rotate(this.rotation);
                                    break;
                            }
                        }
                        break;
                    case Block.WOOD_STAIRS:
                    case Block.SPRUCE_WOOD_STAIRS:
                    case Block.ACACIA_WOOD_STAIRS:
                    case Block.COBBLESTONE_STAIRS:
                    case Block.SANDSTONE_STAIRS:
                        if (this.rotation == Rotation.CLOCKWISE_90) {
                            block = block.rotate(this.rotation);
                        } else if (this.rotation == Rotation.CLOCKWISE_180) {
                            switch (block.getMeta()) {
                                case WeirdoDirection.EAST:
                                case WeirdoDirection.WEST:
                                    break;
                                case WeirdoDirection.SOUTH:
                                case WeirdoDirection.NORTH:
                                    block = block.rotate(this.rotation);
                                    break;
                            }
                        } else if (this.rotation == Rotation.COUNTERCLOCKWISE_90) {
                            switch (block.getMeta()) {
                                case WeirdoDirection.EAST:
                                case WeirdoDirection.WEST:
                                    block = block.rotate(Rotation.CLOCKWISE_90);
                                    break;
                                case WeirdoDirection.SOUTH:
                                case WeirdoDirection.NORTH:
                                    block = block.rotate(this.rotation);
                                    break;
                            }
                        }
                        break;
                    default:
                        block = block.rotate(this.rotation);
                        break;
                }
            }

            level.setBlockAt(vec.x, vec.y, vec.z, block.getId(), block.getMeta());
        }
    }

    protected BlockState getBlock(ChunkManager level, int x, int y, int z, BoundingBox boundingBox) {
        BlockVector3 vec = new BlockVector3(this.getWorldX(x, z), this.getWorldY(y), this.getWorldZ(x, z));
        return !boundingBox.isInside(vec) ? BlockState.AIR : new BlockState(level.getBlockIdAt(vec.x, vec.y, vec.z), level.getBlockDataAt(vec.x, vec.y, vec.z));
    }

    protected boolean isInterior(ChunkManager level, int x, int y, int z, BoundingBox boundingBox) {
        int worldX = this.getWorldX(x, z);
        int worldY = this.getWorldY(y + 1);
        int worldZ = this.getWorldZ(x, z);
        if (!boundingBox.isInside(new BlockVector3(worldX, worldY, worldZ))) {
            return false;
        } else {
            BaseFullChunk chunk = level.getChunk(worldX >> 4, worldZ >> 4);
            if (chunk == null) {
                return false;
            }
            return worldY < chunk.getHighestBlockAt(worldX & 0xf, worldZ & 0xf);
        }
    }

    protected void generateAirBox(ChunkManager level, BoundingBox boundingBox, int x1, int y1, int z1, int x2, int y2, int z2) {
        for (int y = y1; y <= y2; ++y) {
            for (int x = x1; x <= x2; ++x) {
                for (int z = z1; z <= z2; ++z) {
                    this.placeBlock(level, BlockState.AIR, x, y, z, boundingBox);
                }
            }
        }
    }

    protected void generateBox(ChunkManager level, BoundingBox boundingBox, int x1, int y1, int z1, int x2, int y2, int z2, BlockState outsideBlock, BlockState insideBlock, boolean skipAir) {
        for (int y = y1; y <= y2; ++y) {
            for (int x = x1; x <= x2; ++x) {
                for (int z = z1; z <= z2; ++z) {
                    if (!skipAir || !this.getBlock(level, x, y, z, boundingBox).equals(BlockState.AIR)) {
                        if (y != y1 && y != y2 && x != x1 && x != x2 && z != z1 && z != z2) {
                            this.placeBlock(level, insideBlock, x, y, z, boundingBox);
                        } else {
                            this.placeBlock(level, outsideBlock, x, y, z, boundingBox);
                        }
                    }
                }
            }
        }
    }

    protected void generateBox(ChunkManager level, BoundingBox boundingBox, int x1, int y1, int z1, int x2, int y2, int z2, boolean skipAir, NukkitRandom random, BlockSelector selector) {
        for (int y = y1; y <= y2; ++y) {
            for (int x = x1; x <= x2; ++x) {
                for (int z = z1; z <= z2; ++z) {
                    if (!skipAir || !this.getBlock(level, x, y, z, boundingBox).equals(BlockState.AIR)) {
                        selector.next(random, x, y, z, y == y1 || y == y2 || x == x1 || x == x2 || z == z1 || z == z2);
                        this.placeBlock(level, selector.getNext(), x, y, z, boundingBox);
                    }
                }
            }
        }
    }

    protected void generateMaybeBox(ChunkManager level, BoundingBox boundingBox, NukkitRandom random, int prob, int x1, int y1, int z1, int x2, int y2, int z2, BlockState outsideBlock, BlockState insideBlock, boolean skipAir, boolean checkInterior) {
        for (int y = y1; y <= y2; ++y) {
            for (int x = x1; x <= x2; ++x) {
                for (int z = z1; z <= z2; ++z) {
                    if (random.nextBoundedInt(100) <= prob && (!skipAir || !this.getBlock(level, x, y, z, boundingBox).equals(BlockState.AIR)) && (!checkInterior || this.isInterior(level, x, y, z, boundingBox))) {
                        if (y != y1 && y != y2 && x != x1 && x != x2 && z != z1 && z != z2) {
                            this.placeBlock(level, insideBlock, x, y, z, boundingBox);
                        } else {
                            this.placeBlock(level, outsideBlock, x, y, z, boundingBox);
                        }
                    }
                }
            }
        }
    }

    protected void maybeGenerateBlock(ChunkManager level, BoundingBox boundingBox, NukkitRandom random, int prob, int x, int y, int z, BlockState block) {
        if (random.nextBoundedInt(100) < prob) {
            this.placeBlock(level, block, x, y, z, boundingBox);
        }
    }

    protected void generateUpperHalfSphere(ChunkManager level, BoundingBox boundingBox, int x1, int y1, int z1, int x2, int y2, int z2, BlockState block, boolean skipAir) {
        float xLen = x2 - x1 + 1;
        float yLen = y2 - y1 + 1;
        float zLen = z2 - z1 + 1;
        float xHalf = x1 + xLen / 2f;
        float zHalf = z1 + zLen / 2f;

        for (int y = y1; y <= y2; ++y) {
            float dy = (float) (y - y1) / yLen;
            for (int x = x1; x <= x2; ++x) {
                float dx = ((float) x - xHalf) / (xLen * .5f);
                for (int z = z1; z <= z2; ++z) {
                    float dz = ((float) z - zHalf) / (zLen * .5f);
                    if (!skipAir || !this.getBlock(level, x, y, z, boundingBox).equals(BlockState.AIR)) {
                        float d = dx * dx + dy * dy + dz * dz;
                        if (d <= 1.05f) {
                            this.placeBlock(level, block, x, y, z, boundingBox);
                        }
                    }
                }
            }
        }
    }

    protected void fillAirColumnUp(ChunkManager level, int x, int y, int z, BoundingBox boundingBox) {
        BlockVector3 vec = new BlockVector3(this.getWorldX(x, z), this.getWorldY(y), this.getWorldZ(x, z));
        if (boundingBox.isInside(vec)) {
            while (level.getBlockIdAt(vec.x, vec.y, vec.z) != Block.AIR && vec.getY() < 255) {
                level.setBlockAt(vec.x, vec.y, vec.z, Block.AIR);
                vec = vec.up();
            }
        }
    }

    protected void fillColumnDown(ChunkManager level, BlockState block, int x, int y, int z, BoundingBox boundingBox) {
        int worldX = this.getWorldX(x, z);
        int worldY = this.getWorldY(y);
        int worldZ = this.getWorldZ(x, z);
        if (boundingBox.isInside(new BlockVector3(worldX, worldY, worldZ))) {
            BaseFullChunk chunk = level.getChunk(worldX >> 4, worldZ >> 4);
            int cx = worldX & 0xf;
            int cz = worldZ & 0xf;
            int blockId = chunk.getBlockId(cx, worldY, cz);
            while ((blockId == Block.AIR || blockId == Block.WATER || blockId == Block.STILL_WATER || blockId == Block.LAVA || blockId == Block.STILL_LAVA) && worldY > 1) {
                chunk.setBlock(cx, worldY, cz, block.getId(), block.getMeta());
                blockId = chunk.getBlockId(cx, --worldY, cz);
            }
        }
    }

    protected void generateDoor(ChunkManager level, BoundingBox boundingBox, NukkitRandom random, int x, int y, int z, BlockFace orientation, BlockState door)  {
        switch (orientation) {
            case NORTH:
            default:
                this.placeBlock(level, new BlockState(door.getId(), Direction.NORTH), x, y, z, boundingBox);
            case SOUTH:
                this.placeBlock(level, new BlockState(door.getId(), Direction.SOUTH), x, y, z, boundingBox);
            case EAST:
                this.placeBlock(level, new BlockState(door.getId(), Direction.EAST), x, y, z, boundingBox);
            case WEST:
                this.placeBlock(level, new BlockState(door.getId(), Direction.WEST), x, y, z, boundingBox);
        }
        this.placeBlock(level, new BlockState(door.getId(), UpperBlockBit.UPPER), x, y + 1, z, boundingBox);
    }

    public void move(int x, int y, int z) {
        this.boundingBox.move(x, y, z);
    }

    @Nullable
    public BlockFace getOrientation() {
        return this.orientation;
    }

    public void setOrientation(@Nullable BlockFace orientation) {
        this.orientation = orientation;
        if (orientation == null) {
            this.rotation = Rotation.NONE;
        } else {
            switch (orientation) {
                case SOUTH:
                    this.rotation = Rotation.CLOCKWISE_180;
                    break;
                case WEST:
                    this.rotation = Rotation.COUNTERCLOCKWISE_90;
                    break;
                case EAST:
                    this.rotation = Rotation.CLOCKWISE_90;
                    break;
                case NORTH:
                default:
                    this.rotation = Rotation.NONE;
            }
        }
    }

    public Rotation getRotation() {
        return this.rotation;
    }

    public abstract String getType();

    public abstract static class BlockSelector {

        protected BlockState next;

        protected BlockSelector() {
            this.next = BlockState.AIR;
        }

        public abstract void next(NukkitRandom random, int x, int y, int z, boolean hasNext);

        public BlockState getNext() {
            return this.next;
        }
    }
}
