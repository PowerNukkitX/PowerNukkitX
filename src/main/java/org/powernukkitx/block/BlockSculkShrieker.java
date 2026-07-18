package org.powernukkitx.block;

import org.powernukkitx.Player;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.blockentity.BlockEntity;
import org.powernukkitx.blockentity.BlockEntitySculkShrieker;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.level.Sound;
import org.powernukkitx.math.AxisAlignedBB;
import org.jetbrains.annotations.NotNull;

import static org.powernukkitx.block.property.CommonBlockProperties.ACTIVE;


public class BlockSculkShrieker extends BlockFlowable implements BlockEntityHolder<BlockEntitySculkShrieker> {

    public static final BlockProperties PROPERTIES = new BlockProperties(SCULK_SHRIEKER, CommonBlockProperties.ACTIVE, CommonBlockProperties.CAN_SUMMON);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSculkShrieker() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSculkShrieker(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Sculk Shrieker";
    }

    @Override
    public boolean canBePulled() {
        return false;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public double getResistance() {
        return 3;
    }

    @Override
    public double getHardness() {
        return 3.0;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_HOE;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.hasEnchantment(Enchantment.ID_SILK_TOUCH)) {
            return new Item[]{Block.get(SCULK_SHRIEKER).toItem()};
        }
        return Item.EMPTY_ARRAY;
    }

    @Override
    public int getDropExp() {
        return 5;
    }

    @Override
    @NotNull public Class<? extends BlockEntitySculkShrieker> getBlockEntityClass() {
        return BlockEntitySculkShrieker.class;
    }

    @Override
    @NotNull public String getBlockEntityType() {
        return BlockEntity.SCULK_SHRIEKER;
    }

    @Override
    public boolean canPassThrough() {
        return false;
    }

    @Override
    public void onEntityStepOn(Entity entity) {
        if (entity instanceof Player player) {
            getOrCreateBlockEntity().tryShriek(player);
        }
    }

    public boolean isShrieking() {
        return getPropertyValue(ACTIVE);
    }

    public void setShrieking(boolean shrieking) {
        this.setPropertyValue(ACTIVE, shrieking);
        this.level.setBlock(this, this, true, false);
        if (shrieking) {
            this.level.addSound(this.add(0.5, 0.5, 0.5), Sound.SHRIEK_SCULK_SHRIEKER);
        }
    }

    @Override
    public boolean breaksWhenMoved() {
        return false;
    }

    @Override
    public boolean canBeFlowedInto() {
        return false;
    }

    protected AxisAlignedBB recalculateBoundingBox() {
        return this;
    }

    @Override
    public int getWaterloggingLevel() {
        return 1;
    }
}
