package cn.nukkit.level.updater.block;

import cn.nukkit.block.BlockID;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.level.updater.Updater;
import cn.nukkit.level.updater.util.tagupdater.CompoundTagUpdaterContext;

public class BlockStateUpdater_1_21_110 implements Updater {

    public static final Updater INSTANCE = new BlockStateUpdater_1_21_110();
    @Override
    public void registerUpdaters(CompoundTagUpdaterContext ctx) {
        ctx.addUpdater(1, 21, 110)
                .match("name", "minecraft:chain")
                .edit("name", helper -> {
                    helper.replaceWith("name", "minecraft:iron_chain");
                });

        ctx.addUpdater(1, 21, 110)
                .match("name", BlockID.LIGHTNING_ROD)
                .visit("states")
                .tryAdd(CommonBlockProperties.POWERED_BIT.getName(), (byte) 0);

    }
}