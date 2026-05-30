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

import static cn.nukkit.block.property.CommonBlockProperties.STABILITY;
import static cn.nukkit.block.property.CommonBlockProperties.STABILITY_CHECK;

public class BlockScaffolding extends BlockFallable {
    public static final BlockProperties PROPERTIES = new BlockProperties(SCAFFOLDING, STABILITY, STABILITY_CHECK);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockScaffolding() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockScaffolding(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Scaffolding";
    }

    public int getStability() {
        return getPropertyValue(STABILITY);
    }

    public void setStability(int stability) {
        setPropertyValue(STABILITY, stability);
    }

    public boolean getStabilityCheck() {
        return getPropertyValue(STABILITY_CHECK);
    }

    public void setStabilityCheck(boolean check) {
        setPropertyValue(STABILITY_CHECK, check);
    }

    @Override
    public Item toItem() {
        return new ItemBlock(new BlockScaffolding());
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        if (block instanceof BlockFlowingLava) {
            return false;
        }

        Block down = down();
        if (!target.getId().equals(SCAFFOLDING) && !down.getId().equals(SCAFFOLDING) && !down.isAir() && !down.isSolid()) {
            boolean scaffoldOnSide = false;
            for (int i = 0; i < 4; i++) {
                BlockFace sideFace = BlockFace.fromHorizontalIndex(i);
                if (sideFace != face) {
                    Block side = getSide(sideFace);
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
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            Block down = down();
            if (down.isSolid()) {
                if (!isDefaultState()) {
                    setPropertyValues(STABILITY.createValue(0), STABILITY_CHECK.createValue(false));
                    this.getLevel().setBlock(this, this, true, true);
                }
                return type;
            }

            int stability = 7;
            for (BlockFace face : BlockFace.values()) {
                if (face == BlockFace.UP) {
                    continue;
                }

                Block otherBlock = getSide(face);
                if (otherBlock.getId().equals(SCAFFOLDING)) {
                    BlockScaffolding other = (BlockScaffolding) otherBlock;
                    int otherStability = other.getStability();
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
    public double getHardness() {
        return 0.5;
    }

    @Override
    public double getResistance() {
        return 0;
    }

    @Override
    public int getBurnChance() {
        return 60;
    }

    @Override
    public int getBurnAbility() {
        return 60;
    }

    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean canBeClimbed() {
        return true;
    }

    @Override
    public boolean canBeFlowedInto() {
        return false;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return new SimpleAxisAlignedBB(x, y + (2.0 / 16), z, x + 1, y + 1, z + 1);
    }

    @Override
    public void onEntityCollide(Entity entity) {
        entity.resetFallDistance();
    }

    @Override
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
    public double getMinY() {
        return this.y + (14.0 / 16);
    }

    @Override
    protected AxisAlignedBB recalculateCollisionBoundingBox() {
        return this;
    }

    @Override
    public boolean canPassThrough() {
        return false;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean isSolid(BlockFace side) {
        return side == BlockFace.UP;
    }
}
