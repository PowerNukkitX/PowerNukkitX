package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.Player;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.entity.mob.EntityIronGolem;
import org.powernukkitx.item.Item;
import org.powernukkitx.math.BlockFace;
import org.jetbrains.annotations.NotNull;


public class BlockCarvedPumpkin extends BlockPumpkin {
    public static final BlockProperties PROPERTIES = new BlockProperties(CARVED_PUMPKIN, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION);
    public static final BlockDefinition DEFINITION = BlockPumpkin.DEFINITION.toBuilder()
            .canBeActivated(false)
            .build();

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCarvedPumpkin() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCarvedPumpkin(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Carved Pumpkin";
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
}
