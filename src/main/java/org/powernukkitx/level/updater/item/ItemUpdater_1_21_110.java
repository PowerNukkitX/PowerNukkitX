package org.powernukkitx.level.updater.item;

import org.powernukkitx.block.BlockID;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.level.updater.Updater;
import org.powernukkitx.level.updater.util.tagupdater.CompoundTagUpdaterContext;

public class ItemUpdater_1_21_110 implements Updater {

    public static final Updater INSTANCE = new ItemUpdater_1_21_110();

    @Override
    public void registerUpdaters(CompoundTagUpdaterContext context) {
        context.addUpdater(1, 21, 110, false, false)
                .match("Name", BlockID.LIGHTNING_ROD)
                .visit("Block")
                .visit("states")
                .tryAdd(CommonBlockProperties.POWERED_BIT.getName(), (byte) 0);

        context.addUpdater(1, 21, 110)
                .match("Name", "minecraft:chain")
                .edit("Name", helper -> helper.replaceWith("Name", "minecraft:iron_chain"));

    }
}
