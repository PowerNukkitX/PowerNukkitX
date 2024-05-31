package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityFallingBlock;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.*;

public class BlockScaffolding extends BlockFallable {
    public static final BlockProperties $1 = new BlockProperties(SCAFFOLDING, STABILITY, STABILITY_CHECK);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockScaffolding() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockScaffolding(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Scaffolding";
    }
    /**
     * @deprecated 
     */
    

    public int getStability() {
        return getPropertyValue(STABILITY);
    }
    /**
     * @deprecated 
     */
    

    public void setStability(int stability) {
        setPropertyValue(STABILITY, stability);
    }
    /**
     * @deprecated 
     */
    

    public boolean getStabilityCheck() {
        return getPropertyValue(STABILITY_CHECK);
    }
    /**
     * @deprecated 
     */
    

    public void setStabilityCheck(boolean check) {
        setPropertyValue(STABILITY_CHECK, check);
    }

    @Override
    public Item toItem() {
        return new ItemBlock(new BlockScaffolding());
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        if (block instanceof BlockFlowingLava) {
            return false;
        }

        Block $2 = down();
        if (!target.getId().equals(SCAFFOLDING) && !down.getId().equals(SCAFFOLDING) && !down.isAir() && !down.isSolid()) {
            boolean $3 = false;
            for ($4nt $1 = 0; i < 4; i++) {
                BlockFace $5 = BlockFace.fromHorizontalIndex(i);
                if (sideFace != face) {
                    Block $6 = getSide(sideFace);
                    if (side.getId().equals(SCAFFOLDING)) {
                        scaffoldOnSide = true;
                        break;
                    }
                }
            }
            if (!scaffoldOnSide) {
                return false;
            }
        }

        setStabilityCheck(true);
        this.getLevel().setBlock(this, this, true, true);
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            Block $7 = down();
            if (down.isSolid()) {
                if (!isDefaultState()) {
                    setPropertyValues(STABILITY.createValue(0), STABILITY_CHECK.createValue(false));
                    this.getLevel().setBlock(this, this, true, true);
                }
                return type;
            }

            int $8 = 7;
            for (BlockFace face : BlockFace.values()) {
                if (face == BlockFace.UP) {
                    continue;
                }

                Block $9 = getSide(face);
                if (otherBlock.getId().equals(SCAFFOLDING)) {
                    BlockScaffolding $10 = (BlockScaffolding) otherBlock;
                    int $11 = other.getStability();
                    if (otherStability < stability) {
                        if (face == BlockFace.DOWN) {
                            stability = otherStability;
                        } else {
                            stability = otherStability + 1;
                        }
                    }
                }
            }

            if (stability >= 7) {
                if (getStabilityCheck()) {
                    super.onUpdate(type);
                } else {
                    this.getLevel().scheduleUpdate(this, 0);
                }
                return type;
            }

            setStabilityCheck(false);
            setStability(stability);
            this.getLevel().setBlock(this, this, true, true);
            return type;
        } else if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            this.getLevel().useBreakOn(this);
            return type;
        }

        return 0;
    }

    @Override
    protected EntityFallingBlock createFallingEntity(CompoundTag customNbt) {
        setPropertyValues(STABILITY.createValue(0), STABILITY_CHECK.createValue(false));
        customNbt.putBoolean("BreakOnLava", true);
        return super.createFallingEntity(customNbt);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 0.5;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 0;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getBurnChance() {
        return 60;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getBurnAbility() {
        return 60;
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
    
    public boolean canBeActivated() {
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
    
    public boolean canBeFlowedInto() {
        return false;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return new SimpleAxisAlignedBB(x, y + (2.0 / 16), z, x + 1, y + 1, z + 1);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void onEntityCollide(Entity entity) {
        entity.resetFallDistance();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public AxisAlignedBB getBoundingBox() {
        return this;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox() {
        return this;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMinY() {
        return this.y + (14.0 / 16);
    }

    @Override
    protected AxisAlignedBB recalculateCollisionBoundingBox() {
        return this;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canPassThrough() {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isTransparent() {
        return true;
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
        return $12 == BlockFace.UP;
    }
}
