package cn.nukkit.level.generator.object.structures.utils;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockProperties;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.property.enums.MinecraftCardinalDirection;
import cn.nukkit.block.property.enums.TorchFacingDirection;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.types.Rotation;
import cn.nukkit.utils.random.RandomSourceProvider;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;

import static cn.nukkit.block.property.CommonBlockProperties.FACING_DIRECTION;
import static cn.nukkit.block.property.CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION;
import static cn.nukkit.block.property.CommonBlockProperties.TORCH_FACING_DIRECTION;
import static cn.nukkit.block.property.CommonBlockProperties.UPPER_BLOCK_BIT;
import static cn.nukkit.block.property.CommonBlockProperties.WEIRDO_DIRECTION;

public abstract class StructurePiece {

    protected BlockManager level;
    protected BoundingBox boundingBox;
    protected int genDepth;
    @Nullable
    private BlockFace orientation;
    private Rotation rotation = Rotation.NONE;

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

    public void addChildren(StructurePiece piece, List<StructurePiece> pieces, RandomSourceProvider random) {
        //NOOP
    }

    public abstract boolean postProcess(BlockManager level, RandomSourceProvider random, BoundingBox boundingBox, int chunkX, int chunkZ);

    public BoundingBox getBoundingBox() {
        return this.boundingBox;
    }

    public int getGenDepth() {
        return this.genDepth;
    }

    protected boolean isLiquid(String id) {
        return id == Block.FLOWING_WATER || id == Block.WATER || id == Block.FLOWING_LAVA || id == Block.LAVA;
    }

    protected boolean edgesLiquid(BlockManager level, final BoundingBox boundingBox) {
        int x0 = Math.max(this.boundingBox.x0 - 1, boundingBox.x0);
        int y0 = Math.max(this.boundingBox.y0 - 1, boundingBox.y0);
        int z0 = Math.max(this.boundingBox.z0 - 1, boundingBox.z0);
        int x1 = Math.min(this.boundingBox.x1 + 1, boundingBox.x1);
        int y1 = Math.min(this.boundingBox.y1 + 1, boundingBox.y1);
        int z1 = Math.min(this.boundingBox.z1 + 1, boundingBox.z1);

        for (int x = x0; x <= x1; ++x) {
            for (int z = z0; z <= z1; ++z) {
                if (isLiquid(level.getBlockIdAt(x, y0, z)) || isLiquid(level.getBlockIdAt(x, y1, z))) {
                    return true;
                }
            }
        }
        for (int x = x0; x <= x1; ++x) {
            for (int y = y0; y <= y1; ++y) {
                if (isLiquid(level.getBlockIdAt(x, y, z0)) || isLiquid(level.getBlockIdAt(x, y, z1))) {
                    return true;
                }
            }
        }
        for (int z = z0; z <= z1; ++z) {
            for (int y = y0; y <= y1; ++y) {
                if (isLiquid(level.getBlockIdAt(x0, y, z)) || isLiquid(level.getBlockIdAt(x1, y, z))) {
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

    protected void placeBlock(BlockManager level, BlockState block, int x, int y, int z, BoundingBox boundingBox) {
        BlockVector3 vec = new BlockVector3(this.getWorldX(x, z), this.getWorldY(y), this.getWorldZ(x, z));
        if (boundingBox.isInside(vec)) {
            if (this.rotation != Rotation.NONE) {
                switch (block.getIdentifier()) {
                    case Block.TORCH:
                        if (this.rotation == Rotation.ROTATE_90) {
                            block = Rotation.clockwise90(block);
                        } else if (this.rotation == Rotation.ROTATE_180) {
                            switch (block.getPropertyValue(TORCH_FACING_DIRECTION)) {
                                case TorchFacingDirection.EAST:
                                case TorchFacingDirection.WEST:
                                    break;
                                case TorchFacingDirection.SOUTH:
                                case TorchFacingDirection.NORTH:
                                    block = Rotation.clockwise180(block);
                                    break;
                            }
                        } else if (this.rotation == Rotation.ROTATE_270) {
                            switch (block.getPropertyValue(TORCH_FACING_DIRECTION)) {
                                case TorchFacingDirection.EAST:
                                case TorchFacingDirection.WEST:
                                    block = Rotation.clockwise90(block);
                                    break;
                                case TorchFacingDirection.SOUTH:
                                case TorchFacingDirection.NORTH:
                                    block = Rotation.counterclockwise90(block);
                                    break;
                            }
                        }
                        break;
                    case Block.LADDER:
                        if (this.rotation == Rotation.ROTATE_90) {
                            block = Rotation.clockwise90(block);
                        } else if (this.rotation == Rotation.ROTATE_180) {
                            switch (block.getPropertyValue(FACING_DIRECTION)) {
                                case 5:
                                case 4:
                                    break;
                                case 2:
                                case 3:
                                    block = Rotation.clockwise90(block);
                                    break;
                            }
                        } else if (this.rotation == Rotation.ROTATE_270) {
                            switch (block.getPropertyValue(FACING_DIRECTION)) {
                                case 5:
                                case 4:
                                    block = Rotation.clockwise90(block);
                                    break;
                                case 2:
                                case 3:
                                    block = Rotation.counterclockwise90(block);
                                    break;
                            }
                        }
                        break;
                    case Block.OAK_STAIRS:
                    case Block.SPRUCE_STAIRS:
                    case Block.ACACIA_STAIRS:
                    case Block.STONE_STAIRS:
                    case Block.SANDSTONE_STAIRS:
                        if (this.rotation == Rotation.ROTATE_90) {
                            block = Rotation.clockwise90(block);
                        } else if (this.rotation == Rotation.ROTATE_180) {
                            switch (block.getPropertyValue(WEIRDO_DIRECTION)) {
                                case 0:
                                case 1:
                                    break;
                                case 2:
                                case 3:
                                    block = Rotation.clockwise90(block);
                                    break;
                            }
                        } else if (this.rotation == Rotation.ROTATE_270) {
                            switch (block.getPropertyValue(WEIRDO_DIRECTION)) {
                                case 5:
                                case 4:
                                    block = Rotation.clockwise90(block);
                                    break;
                                case 2:
                                case 3:
                                    block = Rotation.counterclockwise90(block);
                                    break;
                            }
                        }
                        break;
                    default:
                        switch (this.rotation) {
                            case ROTATE_90 -> block = Rotation.clockwise90(block);
                            case ROTATE_180 -> block = Rotation.clockwise180(block);
                            case ROTATE_270 -> block = Rotation.counterclockwise90(block);
                        }
                        break;
                }
            }
            level.setBlockStateAt(vec.x, vec.y, vec.z, block);
        }
    }

    protected BlockState getBlock(BlockManager level, int x, int y, int z, BoundingBox boundingBox) {
        BlockVector3 vec = new BlockVector3(this.getWorldX(x, z), this.getWorldY(y), this.getWorldZ(x, z));
        return !boundingBox.isInside(vec) ? BlockAir.STATE : level.getBlockAt(vec.x, vec.y, vec.z).getBlockState();
    }

    protected boolean isInterior(BlockManager level, int x, int y, int z, BoundingBox boundingBox) {
        int worldX = this.getWorldX(x, z);
        int worldY = this.getWorldY(y + 1);
        int worldZ = this.getWorldZ(x, z);
        if (!boundingBox.isInside(new BlockVector3(worldX, worldY, worldZ))) {
            return false;
        } else {
            IChunk chunk = level.getChunk(worldX >> 4, worldZ >> 4);
            if (chunk == null) {
                return false;
            }
            return worldY < chunk.getHeightMap(worldX & 0xf, worldZ & 0xf);
        }
    }

    protected void generateAirBox(BlockManager level, BoundingBox boundingBox, int x1, int y1, int z1, int x2, int y2, int z2) {
        for (int y = y1; y <= y2; ++y) {
            for (int x = x1; x <= x2; ++x) {
                for (int z = z1; z <= z2; ++z) {
                    this.placeBlock(level, BlockAir.STATE, x, y, z, boundingBox);
                }
            }
        }
    }

    protected void generateBox(BlockManager level, BoundingBox boundingBox, int x1, int y1, int z1, int x2, int y2, int z2, BlockState outsideBlock, BlockState insideBlock, boolean skipAir) {
        for (int y = y1; y <= y2; ++y) {
            for (int x = x1; x <= x2; ++x) {
                for (int z = z1; z <= z2; ++z) {
                    if (!skipAir || (!this.getBlock(level, x, y, z, boundingBox).getIdentifier().equals(Block.AIR) && !this.getBlock(level, x, y, z, boundingBox).getIdentifier().equals(Block.WATER))) {
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

    protected void generateBox(BlockManager level, BoundingBox boundingBox, int x1, int y1, int z1, int x2, int y2, int z2, boolean skipAir, RandomSourceProvider random, BlockSelector selector) {
        for (int y = y1; y <= y2; ++y) {
            for (int x = x1; x <= x2; ++x) {
                for (int z = z1; z <= z2; ++z) {
                    if (!skipAir || !this.getBlock(level, x, y, z, boundingBox).equals(BlockAir.STATE)) {
                        selector.next(random, x, y, z, y == y1 || y == y2 || x == x1 || x == x2 || z == z1 || z == z2);
                        this.placeBlock(level, selector.getNext(), x, y, z, boundingBox);
                    }
                }
            }
        }
    }

    protected void generateMaybeBox(BlockManager level, BoundingBox boundingBox, RandomSourceProvider random, int prob, int x1, int y1, int z1, int x2, int y2, int z2, BlockState outsideBlock, BlockState insideBlock, boolean skipAir, boolean checkInterior) {
        for (int y = y1; y <= y2; ++y) {
            for (int x = x1; x <= x2; ++x) {
                for (int z = z1; z <= z2; ++z) {
                    if (random.nextBoundedInt(100) <= prob && (!skipAir || !this.getBlock(level, x, y, z, boundingBox).equals(BlockAir.STATE)) && (!checkInterior || this.isInterior(level, x, y, z, boundingBox))) {
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

    protected void maybeGenerateBlock(BlockManager level, BoundingBox boundingBox, RandomSourceProvider random, int prob, int x, int y, int z, BlockState block) {
        if (random.nextBoundedInt(100) < prob) {
            this.placeBlock(level, block, x, y, z, boundingBox);
        }
    }

    protected void generateUpperHalfSphere(BlockManager level, BoundingBox boundingBox, int x1, int y1, int z1, int x2, int y2, int z2, BlockState block, boolean skipAir) {
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
                    if (!skipAir || !this.getBlock(level, x, y, z, boundingBox).equals(BlockAir.STATE)) {
                        float d = dx * dx + dy * dy + dz * dz;
                        if (d <= 1.05f) {
                            this.placeBlock(level, block, x, y, z, boundingBox);
                        }
                    }
                }
            }
        }
    }

    protected void fillAirColumnUp(BlockManager level, int x, int y, int z, BoundingBox boundingBox) {
        BlockVector3 vec = new BlockVector3(this.getWorldX(x, z), this.getWorldY(y), this.getWorldZ(x, z));
        if (boundingBox.isInside(vec)) {
            while (level.getBlockIdAt(vec.x, vec.y, vec.z) != Block.AIR && vec.getY() < level.getMaxHeight()) {
                level.setBlockStateAt(vec.x, vec.y, vec.z, Block.AIR);
                vec = vec.up();
            }
        }
    }

    protected void fillColumnDown(BlockManager level, BlockState block, int x, int y, int z, BoundingBox boundingBox) {
        int worldX = this.getWorldX(x, z);
        int worldY = this.getWorldY(y);
        int worldZ = this.getWorldZ(x, z);
        if (boundingBox.isInside(new BlockVector3(worldX, worldY, worldZ))) {
            IChunk chunk = level.getChunk(worldX >> 4, worldZ >> 4);
            int cx = worldX & 0xf;
            int cz = worldZ & 0xf;
            String blockId = chunk.getBlockState(cx, worldY, cz).getIdentifier();
            while ((blockId == Block.AIR || blockId == Block.WATER || blockId == Block.FLOWING_WATER || blockId == Block.LAVA || blockId == Block.FLOWING_LAVA) && worldY > 1) {
                level.setBlockStateAt(worldX, worldY, worldZ, block);
                blockId = level.getBlockIdAt(worldX, --worldY, worldZ);
            }
        }
    }

    protected void generateDoor(BlockManager level, BoundingBox boundingBox, RandomSourceProvider random, int x, int y, int z, MinecraftCardinalDirection orientation, BlockProperties properties) {
        this.placeBlock(level, properties.getBlockState(UPPER_BLOCK_BIT.createValue(false), MINECRAFT_CARDINAL_DIRECTION.createValue(orientation)), x, y, z, boundingBox);
        this.placeBlock(level, properties.getBlockState(UPPER_BLOCK_BIT.createValue(true), MINECRAFT_CARDINAL_DIRECTION.createValue(orientation)), x, y + 1, z, boundingBox);
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
                    this.rotation = Rotation.ROTATE_180;
                    break;
                case WEST:
                    this.rotation = Rotation.ROTATE_270;
                    break;
                case EAST:
                    this.rotation = Rotation.ROTATE_90;
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
            this.next = BlockAir.STATE;
        }

        public abstract void next(RandomSourceProvider random, int x, int y, int z, boolean hasNext);

        public BlockState getNext() {
            return this.next;
        }
    }
}
