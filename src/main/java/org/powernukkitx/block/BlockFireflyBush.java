package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.Player;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.BlockFace;
import org.jetbrains.annotations.NotNull;

public class BlockFireflyBush extends BlockFlowable implements Supportable {

    public static final BlockProperties PROPERTIES = new BlockProperties(FIREFLY_BUSH);
    public static final BlockDefinition DEFINITION = FLOWABLE.toBuilder()
            .lightEmission(2)
            .build();

    public BlockFireflyBush() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockFireflyBush(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return "Firefly Bush";
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        if (isSupportDirt(down())) {
            this.getLevel().setBlock(block, this, true);
            return true;
        }
        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!isSupportDirt(down())) {
                this.getLevel().useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        }
        return super.onUpdate(type);
    }

    
    @Override
    public int getSnowloggingLevel() {
        return 1;
    }
}
