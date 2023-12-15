package cn.nukkit.level.blockstateupdater;

import cn.nukkit.level.blockstateupdater.util.OrderedUpdater;
import cn.nukkit.level.blockstateupdater.util.tagupdater.CompoundTagUpdaterContext;

import static cn.nukkit.level.blockstateupdater.util.OrderedUpdater.FACING_TO_CARDINAL;

public class BlockStateUpdater_1_20_40 implements BlockStateUpdater {

    public static final BlockStateUpdater INSTANCE = new BlockStateUpdater_1_20_40();

    @Override
    public void registerUpdaters(CompoundTagUpdaterContext ctx) {
        this.addDirectionUpdater(ctx, "minecraft:chest", FACING_TO_CARDINAL);
        this.addDirectionUpdater(ctx, "minecraft:ender_chest", FACING_TO_CARDINAL);
        this.addDirectionUpdater(ctx, "minecraft:stonecutter_block", FACING_TO_CARDINAL);
        this.addDirectionUpdater(ctx, "minecraft:trapped_chest", FACING_TO_CARDINAL);
    }

    private void addDirectionUpdater(CompoundTagUpdaterContext ctx, String identifier, OrderedUpdater updater) {
        ctx.addUpdater(1, 20, 40)
                .match("name", identifier)
                .visit("states")
                .edit(updater.getOldProperty(), helper -> {
                    int value = (int) helper.getTag();
                    helper.replaceWith(updater.getNewProperty(), updater.translate(value));
                });
    }
}
