package cn.nukkit.level.blockstateupdater;

import cn.nukkit.level.blockstateupdater.util.tagupdater.CompoundTagUpdaterContext;

public class BlockStateUpdater_1_19_80 implements BlockStateUpdater {

    public static final BlockStateUpdater INSTANCE = new BlockStateUpdater_1_19_80();

    private static final String[] WOOD = {
            "birch",
            "oak",
            "jungle",
            "spruce",
            "acacia",
            "dark_oak"
    };

    @Override
    public void registerUpdaters(CompoundTagUpdaterContext ctx) {
        // It could be done the more clever way, but Mojang added 12 updaters, and so we do.
        for (int i = 0; i < WOOD.length; i++) {
            String type = WOOD[i];
            this.addTypeUpdater(ctx, "minecraft:fence", "wood_type", type, "minecraft:" + type + "_fence");
            if (i < 4) {
                this.addTypeUpdater(ctx, "minecraft:log", "old_log_type", type, "minecraft:" + type + "_log");
            } else {
                this.addTypeUpdater(ctx, "minecraft:log2", "new_log_type", type, "minecraft:" + type + "_log");
            }
        }
    }

    private void addTypeUpdater(CompoundTagUpdaterContext context, String identifier, String typeState, String type, String newIdentifier) {
        context.addUpdater(1, 19, 80)
                .match("name", identifier)
                .visit("states")
                .match(typeState, type)
                .edit(typeState, helper -> helper.getRootTag().put("name", newIdentifier))
                .remove(typeState);

    }
}
