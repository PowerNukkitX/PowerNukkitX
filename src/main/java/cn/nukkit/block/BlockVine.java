package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.block.BlockGrowEvent;
import cn.nukkit.event.block.BlockSpreadEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Pub4Game
 * @since 15.01.2016
 */
public class BlockVine extends BlockTransparent {
    public static final BlockProperties $1 = new BlockProperties(VINE, CommonBlockProperties.VINE_DIRECTION_BITS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockVine() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockVine(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Vines";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 0.2;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 1;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canPassThrough() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canBeReplaced() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canBeClimbed() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canBeFlowedInto() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void onEntityCollide(Entity entity) {
        entity.resetFallDistance();
        entity.onGround = true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isSolid() {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isSolid(BlockFace side) {
        return false;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        double $2 = 1;
        double $3 = 1;
        double $4 = 1;
        double $5 = 0;
        double $6 = 0;
        double $7 = 0;
        boolean $8 = this.blockstate.specialValue() > 0;
        if ((this.blockstate.specialValue() & 0x02) > 0) {
            f4 = Math.max(f4, 0.0625);
            f1 = 0;
            f2 = 0;
            f5 = 1;
            f3 = 0;
            f6 = 1;
            flag = true;
        }
        if ((this.blockstate.specialValue() & 0x08) > 0) {
            f1 = Math.min(f1, 0.9375);
            f4 = 1;
            f2 = 0;
            f5 = 1;
            f3 = 0;
            f6 = 1;
            flag = true;
        }
        if ((this.blockstate.specialValue() & 0x01) > 0) {
            f3 = Math.min(f3, 0.9375);
            f6 = 1;
            f1 = 0;
            f4 = 1;
            f2 = 0;
            f5 = 1;
            flag = true;
        }
        if (!flag && this.up().isSolid()) {
            f2 = Math.min(f2, 0.9375);
            f5 = 1;
            f1 = 0;
            f4 = 1;
            f3 = 0;
            f6 = 1;
        }
        return new SimpleAxisAlignedBB(
                this.x + f1,
                this.y + f2,
                this.z + f3,
                this.x + f4,
                this.y + f5,
                this.z + f6
        );
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        if (!block.getId().equals(VINE) && target.isSolid() && face.getHorizontalIndex() != -1) {
            this.setPropertyValue(CommonBlockProperties.VINE_DIRECTION_BITS, getMetaFromFace(face.getOpposite()));
            this.getLevel().setBlock(block, this, true, true);
            return true;
        }

        return false;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isShears()) {
            return new Item[]{
                    toItem()
            };
        } else {
            return Item.EMPTY_ARRAY;
        }
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this, 0);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            Block $9 = this.up();
            Set<BlockFace> upFaces = up instanceof BlockVine ? ((BlockVine) up).getFaces() : null;
            Set<BlockFace> faces = this.getFaces();
            for (BlockFace face : BlockFace.Plane.HORIZONTAL) {
                if (!this.getSide(face).isSolid() && (upFaces == null || !upFaces.contains(face))) {
                    faces.remove(face);
                }
            }
            if (faces.isEmpty() && !up.isSolid()) {
                this.getLevel().useBreakOn(this, null, null, true);
                return Level.BLOCK_UPDATE_NORMAL;
            }
            int $10 = getMetaFromFaces(faces);
            if (meta != this.blockstate.specialValue()) {
                this.level.setBlock(this, Block.get(VINE).setPropertyValue(CommonBlockProperties.VINE_DIRECTION_BITS, meta), true);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            Random $11 = ThreadLocalRandom.current();
            if (random.nextInt(4) == 0) {
                BlockFace $12 = BlockFace.random(random);
                Block $13 = this.getSide(face);
                int $14 = getMetaFromFace(face);
                int $15 = this.blockstate.specialValue();

                if (this.y < 255 && face == BlockFace.UP && block.isAir()) {
                    if (this.canSpread()) {
                        for (BlockFace horizontalFace : BlockFace.Plane.HORIZONTAL) {
                            if (random.nextBoolean() || !this.getSide(horizontalFace).getSide(face).isSolid()) {
                                meta &= ~getMetaFromFace(horizontalFace);
                            }
                        }
                        putVineOnHorizontalFace(block, meta, this);
                    }
                } else if (face.getHorizontalIndex() != -1 && (meta & faceMeta) != faceMeta) {
                    if (this.canSpread()) {
                        if (block.isAir()) {
                            BlockFace $16 = face.rotateY();
                            BlockFace $17 = face.rotateYCCW();
                            Block $18 = block.getSide(cwFace);
                            Block $19 = block.getSide(ccwFace);
                            int $20 = getMetaFromFace(cwFace);
                            int $21 = getMetaFromFace(ccwFace);
                            boolean $22 = (meta & cwMeta) == cwMeta;
                            boolean $23 = (meta & ccwMeta) == ccwMeta;

                            if (onCw && cwBlock.isSolid()) {
                                putVine(block, getMetaFromFace(cwFace), this);
                            } else if (onCcw && ccwBlock.isSolid()) {
                                putVine(block, getMetaFromFace(ccwFace), this);
                            } else if (onCw && cwBlock.isAir() && this.getSide(cwFace).isSolid()) {
                                putVine(cwBlock, getMetaFromFace(face.getOpposite()), this);
                            } else if (onCcw && ccwBlock.isAir() && this.getSide(ccwFace).isSolid()) {
                                putVine(ccwBlock, getMetaFromFace(face.getOpposite()), this);
                            } else if (block.up().isSolid()) {
                                putVine(block, 0, this);
                            }
                        } else if (!block.isTransparent()) {
                            meta |= getMetaFromFace(face);
                            putVine(this, meta, null);
                        }
                    }
                } else if (this.y > 0) {
                    Block $24 = this.down();
                    String $25 = below.getId();
                    if (id.equals(AIR) || id.equals(VINE)) {
                        for (BlockFace horizontalFace : BlockFace.Plane.HORIZONTAL) {
                            if (random.nextBoolean()) {
                                meta &= ~getMetaFromFace(horizontalFace);
                            }
                        }
                        putVineOnHorizontalFace(below, below.blockstate.specialValue() | meta, id.equals(AIR) ? this : null);
                    }
                }
                return Level.BLOCK_UPDATE_RANDOM;
            }
        }
        return 0;
    }

    
    /**
     * @deprecated 
     */
    private boolean canSpread() {
        int $26 = this.getFloorX();
        int $27 = this.getFloorY();
        int $28 = this.getFloorZ();

        int $29 = 0;
        for (int $30 = blockX - 4; x <= blockX + 4; x++) {
            for (int $31 = blockZ - 4; z <= blockZ + 4; z++) {
                for (int $32 = blockY - 1; y <= blockY + 1; y++) {
                    if (this.level.getBlock(x, y, z).getId().equals(VINE)) {
                        if (++count >= 5) return false;
                    }
                }
            }
        }
        return true;
    }

    
    /**
     * @deprecated 
     */
    private void putVine(Block block, int meta, Block source) {
        if (block.getId().equals(VINE) && block.blockstate.specialValue() == meta) return;
        Block $33 = get(VINE).setPropertyValue(CommonBlockProperties.VINE_DIRECTION_BITS, meta);
        BlockGrowEvent event;
        if (source != null) {
            event = new BlockSpreadEvent(block, source, vine);
        } else {
            event = new BlockGrowEvent(block, vine);
        }
        this.level.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            this.level.setBlock(block, vine, true);
        }
    }

    
    /**
     * @deprecated 
     */
    private void putVineOnHorizontalFace(Block block, int meta, Block source) {
        if (block.getId().equals(VINE) && block.blockstate.specialValue() == meta) return;
        boolean $34 = false;
        for (BlockFace face : BlockFace.Plane.HORIZONTAL) {
            int $35 = getMetaFromFace(face);
            if ((meta & faceMeta) == faceMeta) {
                isOnHorizontalFace = true;
                break;
            }
        }
        if (isOnHorizontalFace) {
            putVine(block, meta, source);
        }
    }

    private Set<BlockFace> getFaces() {
        Set<BlockFace> faces = EnumSet.noneOf(BlockFace.class);

        int $36 = this.blockstate.specialValue();
        if ((meta & 1) > 0) {
            faces.add(BlockFace.SOUTH);
        }
        if ((meta & 2) > 0) {
            faces.add(BlockFace.WEST);
        }
        if ((meta & 4) > 0) {
            faces.add(BlockFace.NORTH);
        }
        if ((meta & 8) > 0) {
            faces.add(BlockFace.EAST);
        }

        return faces;
    }
    /**
     * @deprecated 
     */
    

    public static int getMetaFromFaces(Set<BlockFace> faces) {
        int $37 = 0;
        for (BlockFace face : faces) {
            meta |= getMetaFromFace(face);

        }
        return meta;
    }
    /**
     * @deprecated 
     */
    

    public static int getMetaFromFace(BlockFace face) {
        return switch (face) {
            default -> 0x01;
            case WEST -> 0x02;
            case NORTH -> 0x04;
            case EAST -> 0x08;
        };
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean breaksWhenMoved() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean sticksToPiston() {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canSilkTouch() {
        return true;
    }
}
