package org.powernukkitx.block;

import org.powernukkitx.Player;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.block.property.CommonPropertyMap;
import org.powernukkitx.entity.mob.EntityCopperGolem;
import org.powernukkitx.entity.mob.EntitySnowGolem;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemBlock;
import org.powernukkitx.item.ItemID;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.utils.Faceable;
import org.jetbrains.annotations.NotNull;

public class BlockPumpkin extends BlockSolid implements Faceable, Natural {


    public static final BlockProperties PROPERTIES = new BlockProperties(PUMPKIN, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION);

    public BlockPumpkin() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockPumpkin(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Pumpkin";
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public double getHardness() {
        return 1;
    }

    @Override
    public double getResistance() {
        return 1;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this, 0);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (item.isShears()) {
            BlockCarvedPumpkin carvedPumpkin = new BlockCarvedPumpkin();
            // TODO: Use the activated block face not the player direction
            if (player == null) {
                carvedPumpkin.setBlockFace(BlockFace.SOUTH);
            } else {
                carvedPumpkin.setBlockFace(player.getDirection().getOpposite());
            }
            item.useOn(this);
            this.level.setBlock(this, carvedPumpkin, true, true);
            this.getLevel().dropItem(add(0.5, 0.5, 0.5), Item.get(ItemID.PUMPKIN_SEEDS)); // TODO: Get correct drop item position
            return true;
        }
        return false;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        if (player == null) {
            setBlockFace(BlockFace.SOUTH);
        } else {
            setBlockFace(player.getDirection().getOpposite());
        }
        this.level.setBlock(block, this, true, true);
        EntitySnowGolem.checkAndSpawnGolem(this);
        EntityCopperGolem.checkAndSpawnGolem(this);
        return true;
    }

    @Override
    public boolean breaksWhenMoved() {
        return true;
    }

    @Override
    public BlockFace getBlockFace() {
        return CommonPropertyMap.CARDINAL_BLOCKFACE.get(getPropertyValue(CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION));
    }

    @Override
    public void setBlockFace(BlockFace face) {
        this.setPropertyValue(CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION, CommonPropertyMap.CARDINAL_BLOCKFACE.inverse().get(face));
    }
}
