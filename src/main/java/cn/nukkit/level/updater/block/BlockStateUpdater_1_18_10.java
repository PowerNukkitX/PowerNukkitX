package cn.nukkit.level.updater.block;

import cn.nukkit.level.updater.Updater;
import cn.nukkit.level.updater.util.tagupdater.CompoundTagUpdaterContext;

public class BlockStateUpdater_1_18_10 implements Updater {

    public static final Updater INSTANCE = new BlockStateUpdater_1_18_10();

    @Override
    public void registerUpdaters(CompoundTagUpdaterContext context) {
        context.addUpdater(1, 18, 10)
                .match("name", "minecraft:skull")
                .visit("states")
                .remove("no_drop_bit");


        context.addUpdater(1, 18, 10)
                .match("name", "minecraft:glow_lichen")
                .visit("states")
                .tryEdit("multi_face_direction_bits", helper -> {
                    int bits = (int) helper.getTag();
                    boolean north = (bits & (1 << 2)) != 0;
                    boolean south = (bits & (1 << 3)) != 0;
                    boolean west = (bits & (1 << 4)) != 0;
                    if (north) {
                        bits |= 1 << 4;
                    } else {
                        bits &= ~(1 << 4);
                    }
                    if (south) {
                        bits |= 1 << 2;
                    } else {
                        bits &= ~(1 << 2);
                    }
                    if (west) {
                        bits |= 1 << 3;
                    } else {
                        bits &= ~(1 << 3);
                    }
                    helper.replaceWith("multi_face_direction_bits", bits);
                });
    }
}
