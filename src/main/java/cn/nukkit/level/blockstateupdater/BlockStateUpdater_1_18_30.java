package cn.nukkit.level.blockstateupdater;

import cn.nukkit.level.blockstateupdater.util.tagupdater.CompoundTagUpdaterContext;

public class BlockStateUpdater_1_18_30 implements BlockStateUpdater {

    public static final BlockStateUpdater INSTANCE = new BlockStateUpdater_1_18_30();

    @Override
    public void registerUpdaters(CompoundTagUpdaterContext context) {
        this.renameIdentifier(context, "minecraft:concretePowder", "minecraft:concrete_powder");
        this.renameIdentifier(context, "minecraft:frog_egg", "minecraft:frog_spawn");
        this.renameIdentifier(context, "minecraft:invisibleBedrock", "minecraft:invisible_bedrock");
        this.renameIdentifier(context, "minecraft:movingBlock", "minecraft:moving_block");
        this.renameIdentifier(context, "minecraft:pistonArmCollision", "minecraft:piston_arm_collision");
        this.renameIdentifier(context, "minecraft:seaLantern", "minecraft:sea_lantern");
        this.renameIdentifier(context, "minecraft:stickyPistonArmCollision", "minecraft:sticky_piston_arm_collision");
        this.renameIdentifier(context, "minecraft:tripWire", "minecraft:trip_wire");

        this.addPillarAxis(context, "minecraft:ochre_froglight");
        this.addPillarAxis(context, "minecraft:pearlescent_froglight");
        this.addPillarAxis(context, "minecraft:verdant_froglight");
    }

    private void renameIdentifier(CompoundTagUpdaterContext context, String from, String to) {
        context.addUpdater(1, 18, 10, true) // Here we go again Mojang
                .match("name", from)
                .edit("name", helper -> helper.replaceWith("name", to));
    }

    private void addPillarAxis(CompoundTagUpdaterContext context, String from) {
        context.addUpdater(1, 18, 10, true) // Here we go again Mojang
                .match("name", from)
                .visit("states")
                .tryAdd("pillar_axis", "y");
    }
}
