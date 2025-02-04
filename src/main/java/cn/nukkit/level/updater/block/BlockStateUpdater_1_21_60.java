package cn.nukkit.level.updater.block;

import cn.nukkit.level.updater.Updater;
import cn.nukkit.level.updater.util.OrderedUpdater;
import cn.nukkit.level.updater.util.tagupdater.CompoundTagUpdaterContext;

import java.util.Map;

import static cn.nukkit.level.updater.util.OrderedUpdater.DIRECTION_TO_CARDINAL;

public class BlockStateUpdater_1_21_60 implements Updater {

    public static final Updater INSTANCE = new BlockStateUpdater_1_21_60();
    @Override
    public void registerUpdaters(CompoundTagUpdaterContext ctx) {
        this.addDirectionUpdater(ctx, "minecraft:acacia_door", DIRECTION_TO_CARDINAL);
        this.addDirectionUpdater(ctx, "minecraft:acacia_fence_gate", DIRECTION_TO_CARDINAL);
        this.addDirectionUpdater(ctx, "minecraft:bamboo_door", DIRECTION_TO_CARDINAL);
        this.addDirectionUpdater(ctx, "minecraft:bamboo_fence_gate", DIRECTION_TO_CARDINAL);
        this.addDirectionUpdater(ctx, "minecraft:birch_door", DIRECTION_TO_CARDINAL);
        this.addDirectionUpdater(ctx, "minecraft:birch_fence_gate", DIRECTION_TO_CARDINAL);
        this.addDirectionUpdater(ctx, "minecraft:cherry_door", DIRECTION_TO_CARDINAL);
        this.addDirectionUpdater(ctx, "minecraft:cherry_fence_gate", DIRECTION_TO_CARDINAL);
        this.addDirectionUpdater(ctx, "minecraft:copper_door", DIRECTION_TO_CARDINAL);
        this.addDirectionUpdater(ctx, "minecraft:crimson_door", DIRECTION_TO_CARDINAL);
        this.addDirectionUpdater(ctx, "minecraft:crimson_fence_gate", DIRECTION_TO_CARDINAL);
        this.addDirectionUpdater(ctx, "minecraft:dark_oak_door", DIRECTION_TO_CARDINAL);
        this.addDirectionUpdater(ctx, "minecraft:dark_oak_fence_gate", DIRECTION_TO_CARDINAL);
        this.addDirectionUpdater(ctx, "minecraft:exposed_copper_door", DIRECTION_TO_CARDINAL);
        this.addDirectionUpdater(ctx, "minecraft:fence_gate", DIRECTION_TO_CARDINAL);
        this.addDirectionUpdater(ctx, "minecraft:iron_door", DIRECTION_TO_CARDINAL);
        this.addDirectionUpdater(ctx, "minecraft:jungle_door", DIRECTION_TO_CARDINAL);
        this.addDirectionUpdater(ctx, "minecraft:jungle_fence_gate", DIRECTION_TO_CARDINAL);
        this.addDirectionUpdater(ctx, "minecraft:mangrove_door", DIRECTION_TO_CARDINAL);
        this.addDirectionUpdater(ctx, "minecraft:mangrove_fence_gate", DIRECTION_TO_CARDINAL);
        this.addDirectionUpdater(ctx, "minecraft:oxidized_copper_door", DIRECTION_TO_CARDINAL);
        this.addDirectionUpdater(ctx, "minecraft:pale_oak_door", DIRECTION_TO_CARDINAL);
        this.addDirectionUpdater(ctx, "minecraft:pale_oak_fence_gate", DIRECTION_TO_CARDINAL);
        this.addDirectionUpdater(ctx, "minecraft:spruce_door", DIRECTION_TO_CARDINAL);
        this.addDirectionUpdater(ctx, "minecraft:spruce_fence_gate", DIRECTION_TO_CARDINAL);
        this.addDirectionUpdater(ctx, "minecraft:warped_door", DIRECTION_TO_CARDINAL);
        this.addDirectionUpdater(ctx, "minecraft:warped_fence_gate", DIRECTION_TO_CARDINAL);
        this.addDirectionUpdater(ctx, "minecraft:waxed_copper_door", DIRECTION_TO_CARDINAL);
        this.addDirectionUpdater(ctx, "minecraft:waxed_exposed_copper_door", DIRECTION_TO_CARDINAL);
        this.addDirectionUpdater(ctx, "minecraft:waxed_oxidized_copper_door", DIRECTION_TO_CARDINAL);
        this.addDirectionUpdater(ctx, "minecraft:waxed_weathered_copper_door", DIRECTION_TO_CARDINAL);
        this.addDirectionUpdater(ctx, "minecraft:weathered_copper_door", DIRECTION_TO_CARDINAL);
        this.addDirectionUpdater(ctx, "minecraft:wooden_door", DIRECTION_TO_CARDINAL);
        ctx.addUpdater(1, 21, 60)
                .match("name", "minecraft:creaking_heart")
                .edit("states", helper -> {
                    Map<String, Object> states = helper.getCompoundTag();
                    Object bit = states.remove("active");
                    boolean active = bit instanceof Byte && (byte) bit == 1 || bit instanceof Boolean && (boolean) bit;
                    helper.replaceWith("creaking_heart_state", active ? "awake" : "uprooted");
                });
    }
    private void addDirectionUpdater(CompoundTagUpdaterContext ctx, String identifier, OrderedUpdater updater) {
        ctx.addUpdater(1, 21, 60)
                .match("name", identifier)
                .visit("states")
                .edit(updater.getOldProperty(), helper -> {
                    int value = (int) helper.getTag();
                    helper.replaceWith(updater.getNewProperty(), updater.translate(value));
                });
    }
}