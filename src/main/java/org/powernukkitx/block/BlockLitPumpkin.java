package org.powernukkitx.block;

import org.powernukkitx.Player;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.entity.mob.EntityIronGolem;
import org.powernukkitx.item.Item;
import org.powernukkitx.math.BlockFace;
import org.jetbrains.annotations.NotNull;

public class BlockLitPumpkin extends BlockPumpkin {
    public static final BlockProperties PROPERTIES = new BlockProperties(LIT_PUMPKIN, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLitPumpkin() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLitPumpkin(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Jack o'Lantern";
    }

    @Override
    public int getLightLevel() {
        return 15;
    }

    @Override
    public boolean canBeActivated() {
        return false;
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        return false;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        if(!super.place(item, block, target, face, fx, fy, fz, player)) return false;
        EntityIronGolem.checkAndSpawnGolem(this, player);
        return true;
    }

    @Override
    public boolean canBePickedUp() {
        return false;
    }
}