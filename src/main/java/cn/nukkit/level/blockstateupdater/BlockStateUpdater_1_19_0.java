package cn.nukkit.level.blockstateupdater;

import cn.nukkit.level.blockstateupdater.util.tagupdater.CompoundTagUpdaterContext;

public class BlockStateUpdater_1_19_0 implements BlockStateUpdater {

    public static final BlockStateUpdater INSTANCE = new BlockStateUpdater_1_19_0();

    @Override
    public void registerUpdaters(CompoundTagUpdaterContext context) {
        this.renameIdentifier(context, "minecraft:stone_slab", "minecraft:stone_block_slab");
        this.renameIdentifier(context, "minecraft:stone_slab2", "minecraft:stone_block_slab2");
        this.renameIdentifier(context, "minecraft:stone_slab3", "minecraft:stone_block_slab3");
        this.renameIdentifier(context, "minecraft:stone_slab4", "minecraft:stone_block_slab4");
        this.renameIdentifier(context, "minecraft:double_stone_slab", "minecraft:double_stone_block_slab");
        this.renameIdentifier(context, "minecraft:double_stone_slab2", "minecraft:double_stone_block_slab2");
        this.renameIdentifier(context, "minecraft:double_stone_slab3", "minecraft:double_stone_block_slab3");
        this.renameIdentifier(context, "minecraft:double_stone_slab4", "minecraft:double_stone_block_slab4");

        this.addProperty(context, "minecraft:sculk_shrieker", "can_summon", (byte) 0);

    }

    private void renameIdentifier(CompoundTagUpdaterContext context, String from, String to) {
        context.addUpdater(1, 18, 10, true) // Here we go again Mojang
                .match("name", from)
                .edit("name", helper -> helper.replaceWith("name", to));
    }

    private void addProperty(CompoundTagUpdaterContext context, String identifier, String propertyName, Object value) {
        context.addUpdater(1, 18, 10, true) // Here we go again Mojang
                .match("name", identifier)
                .visit("states")
                .tryAdd(propertyName, value);
    }
}
