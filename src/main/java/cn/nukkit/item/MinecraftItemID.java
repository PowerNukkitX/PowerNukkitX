/*
 * https://PowerNukkit.org - The Nukkit you know but Powerful!
 * Copyright (C) 2020  José Roberto de Araújo Júnior
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import io.netty.util.internal.EmptyArrays;

import javax.annotation.Nullable;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An enum containing all valid vanilla Minecraft items.
 * 添加原版新物品时一定不要忘记在这里补上命名空间的枚举(例如 minecraft:quartz_bricks 这里填写QUARTZ_BRICKS)
 *
 * @author joserobjr
 * @since 2023-06-09 v1.20.0-r1 现在已经弃用，不需要再添加物品名称
 * @since 2020-12-20
 */
@Deprecated


public enum MinecraftItemID {


    //


    //


    //


    //


    INFESTED_DEEPSLATE,
    RAW_GOLD_BLOCK,
    RAW_COPPER_BLOCK,
    RAW_IRON_BLOCK,
    WAXED_OXIDIZED_DOUBLE_CUT_COPPER_SLAB,
    WAXED_OXIDIZED_CUT_COPPER_SLAB,
    WAXED_OXIDIZED_CUT_COPPER_STAIRS,
    WAXED_OXIDIZED_CUT_COPPER,
    WAXED_OXIDIZED_COPPER,
    GLOW_LICHEN,
    CRACKED_DEEPSLATE_BRICKS,
    CRACKED_DEEPSLATE_TILES,
    DEEPSLATE_COPPER_ORE,
    DEEPSLATE_EMERALD_ORE,
    DEEPSLATE_COAL_ORE,
    DEEPSLATE_DIAMOND_ORE,
    LIT_DEEPSLATE_REDSTONE_ORE(false, true),
    DEEPSLATE_REDSTONE_ORE,
    DEEPSLATE_GOLD_ORE,
    DEEPSLATE_IRON_ORE,
    DEEPSLATE_LAPIS_ORE,
    DEEPSLATE_BRICK_DOUBLE_SLAB(false, true),
    DEEPSLATE_TILE_DOUBLE_SLAB(false, true),
    POLISHED_DEEPSLATE_DOUBLE_SLAB(false, true),
    COBBLED_DEEPSLATE_DOUBLE_SLAB(false, true),
    CHISELED_DEEPSLATE,
    DEEPSLATE_BRICK_WALL,
    DEEPSLATE_BRICK_STAIRS,
    DEEPSLATE_BRICK_SLAB,
    DEEPSLATE_BRICKS,
    DEEPSLATE_TILE_WALL,
    DEEPSLATE_TILE_STAIRS,
    DEEPSLATE_TILE_SLAB,
    DEEPSLATE_TILES,
    POLISHED_DEEPSLATE_WALL,
    POLISHED_DEEPSLATE_STAIRS,
    POLISHED_DEEPSLATE_SLAB,
    POLISHED_DEEPSLATE,
    COBBLED_DEEPSLATE_WALL,
    COBBLED_DEEPSLATE_STAIRS,
    COBBLED_DEEPSLATE_SLAB,
    COBBLED_DEEPSLATE,
    DEEPSLATE,
    SMOOTH_BASALT,
    CAVE_VINES_HEAD_WITH_BERRIES(false, true),
    CAVE_VINES_BODY_WITH_BERRIES(false, true),
    WAXED_WEATHERED_DOUBLE_CUT_COPPER_SLAB,
    WAXED_EXPOSED_DOUBLE_CUT_COPPER_SLAB,
    WAXED_DOUBLE_CUT_COPPER_SLAB,
    OXIDIZED_DOUBLE_CUT_COPPER_SLAB,
    WEATHERED_DOUBLE_CUT_COPPER_SLAB,
    EXPOSED_DOUBLE_CUT_COPPER_SLAB,
    DOUBLE_CUT_COPPER_SLAB,
    WAXED_WEATHERED_CUT_COPPER_SLAB,
    WAXED_EXPOSED_CUT_COPPER_SLAB,
    WAXED_CUT_COPPER_SLAB,
    OXIDIZED_CUT_COPPER_SLAB,
    WEATHERED_CUT_COPPER_SLAB,
    EXPOSED_CUT_COPPER_SLAB,
    CUT_COPPER_SLAB,
    WAXED_WEATHERED_CUT_COPPER_STAIRS,
    WAXED_EXPOSED_CUT_COPPER_STAIRS,
    WAXED_CUT_COPPER_STAIRS,
    OXIDIZED_CUT_COPPER_STAIRS,
    WEATHERED_CUT_COPPER_STAIRS,
    EXPOSED_CUT_COPPER_STAIRS,
    CUT_COPPER_STAIRS,
    WAXED_WEATHERED_CUT_COPPER,
    WAXED_EXPOSED_CUT_COPPER,
    WAXED_CUT_COPPER,
    OXIDIZED_CUT_COPPER,
    WEATHERED_CUT_COPPER,
    EXPOSED_CUT_COPPER,
    CUT_COPPER,
    WAXED_WEATHERED_COPPER,
    WAXED_EXPOSED_COPPER,
    WAXED_COPPER,
    OXIDIZED_COPPER,
    WEATHERED_COPPER,
    EXPOSED_COPPER,
    COPPER_BLOCK,
    GLOW_FRAME_BLOCK_FORM(true, true),
    FLOWERING_AZALEA,
    AZALEA,
    SMALL_DRIPLEAF_BLOCK,
    MOSS_CARPET,
    TINTED_GLASS,
    TUFF,
    SMALL_AMETHYST_BUD,
    MEDIUM_AMETHYST_BUD,
    LARGE_AMETHYST_BUD,
    AMETHYST_CLUSTER,
    BUDDING_AMETHYST,
    AMETHYST_BLOCK,
    CALCITE,
    AZALEA_LEAVES_FLOWERED,
    AZALEA_LEAVES,
    BIG_DRIPLEAF,
    CAVE_VINES(false, true),
    SPORE_BLOSSOM,
    MOSS_BLOCK,
    HANGING_ROOTS,
    DIRT_WITH_ROOTS,
    DRIPSTONE_BLOCK,
    LIGHTNING_ROD,
    COPPER_ORE,
    POINTED_DRIPSTONE,
    SCULK_SENSOR(false, true),
    POWDER_SNOW(false, true),
    POWDER_SNOW_BUCKET,
    AXOLOTL_BUCKET,

    AXOLOTL_SPAWN_EGG,
    GOAT_SPAWN_EGG,
    GLOW_SQUID_SPAWN_EGG,


    GLOW_INK_SAC,
    COPPER_INGOT,
    RAW_IRON,
    RAW_GOLD,
    RAW_COPPER,
    GLOW_FRAME(false),

    AMETHYST_SHARD,
    SPYGLASS,
    GLOW_BERRIES,






































































    LIGHT_GRAY_WOOL,


    GRAY_WOOL,


    BLACK_WOOL,


    BROWN_WOOL,


    RED_WOOL,


    ORANGE_WOOL,


    YELLOW_WOOL,


    LIME_WOOL,


    GREEN_WOOL,


    CYAN_WOOL,


    LIGHT_BLUE_WOOL,


    BLUE_WOOL,


    PURPLE_WOOL,


    MAGENTA_WOOL,


    PINK_WOOL,


    OAK_FENCE,


    SPRUCE_FENCE,


    BIRCH_FENCE,


    JUNGLE_FENCE,


    ACACIA_FENCE,


    DARK_OAK_FENCE,


    OAK_LOG,


    SPRUCE_LOG,


    BIRCH_LOG,


    JUNGLE_LOG,


    ACACIA_LOG,


    DARK_OAK_LOG,


    WHITE_CARPET,


    ORANGE_CARPET,


    MAGENTA_CARPET,


    LIGHT_BLUE_CARPET,


    YELLOW_CARPET,


    LIME_CARPET,


    PINK_CARPET,


    GRAY_CARPET,


    LIGHT_GRAY_CARPET,


    CYAN_CARPET,


    PURPLE_CARPET,


    BLUE_CARPET,


    BROWN_CARPET,


    GREEN_CARPET,


    RED_CARPET,


    BLACK_CARPET,


    TUBE_CORAL,


    BRAIN_CORAL,


    BUBBLE_CORAL,


    FIRE_CORAL,


    HORN_CORAL,


    DEAD_TUBE_CORAL,


    DEAD_BRAIN_CORAL,


    DEAD_BUBBLE_CORAL,


    DEAD_FIRE_CORAL,


    DEAD_HORN_CORAL,


    CHERRY_BUTTON,


    CHERRY_DOOR,


    CHERRY_FENCE,


    CHERRY_FENCE_GATE,


    CHERRY_HANGING_SIGN,


    STRIPPED_CHERRY_LOG,


    CHERRY_LOG,


    CHERRY_PLANKS,


    CHERRY_PRESSURE_PLATE,


    CHERRY_SLAB,


    CHERRY_DOUBLE_SLAB,


    CHERRY_STAIRS,


    CHERRY_STANDING_SIGN,


    CHERRY_TRAPDOOR,


    CHERRY_WALL_SIGN,


    STRIPPED_CHERRY_WOOD,


    CHERRY_WOOD,


    CHERRY_SAPLING,


    CHERRY_LEAVES,


    PINK_PETALS;


    private static final Map<String, MinecraftItemID> namespacedIdMap = Arrays.stream(values())
            .flatMap(id ->
                    Stream.concat(Arrays.stream(id.aliases), Stream.of(id.getNamespacedId()))
                            .map(ns -> new AbstractMap.SimpleEntry<>(ns, id)))
            .collect(Collectors.toMap(entry -> entry.getKey().toLowerCase(), AbstractMap.SimpleEntry::getValue));


    @Nullable
    public static MinecraftItemID getByNamespaceId(String namespacedId) {
        return namespacedIdMap.get(namespacedId);
    }

    private final String namespacedId;
    private final String itemFormNamespaceId;
    private final boolean technical;
    private final boolean edu;
    private final String[] aliases;

    MinecraftItemID(String namespacedId, String itemFormNamespaceId, String[] aliases) {
        this.namespacedId = namespacedId;
        this.itemFormNamespaceId = itemFormNamespaceId;
        technical = false;
        edu = false;
        this.aliases = aliases;
    }

    MinecraftItemID() {
        namespacedId = "minecraft:" + name().toLowerCase();
        itemFormNamespaceId = namespacedId;
        technical = false;
        edu = false;
        aliases = EmptyArrays.EMPTY_STRINGS;
    }

    MinecraftItemID(boolean blockForm) {
        this(blockForm, false);
    }

    MinecraftItemID(boolean blockForm, boolean technical) {
        this(blockForm, technical, false);
    }

    MinecraftItemID(boolean blockForm, boolean technical, boolean edu) {
        this.technical = technical;
        this.edu = edu;
        String namespacedId = name().toLowerCase();
        aliases = EmptyArrays.EMPTY_STRINGS;
        itemFormNamespaceId = "minecraft:" + namespacedId;
        if (blockForm) {
            this.namespacedId = "minecraft:item." + namespacedId;
        } else {
            this.namespacedId = itemFormNamespaceId;
        }
    }

    MinecraftItemID(String namespacedId, String itemFormNamespaceId) {
        this(namespacedId, itemFormNamespaceId, false);
    }

    MinecraftItemID(String namespacedId, String itemFormNamespaceId, boolean technical) {
        this(namespacedId, itemFormNamespaceId, technical, false);
    }

    MinecraftItemID(String namespacedId, String itemFormNamespaceId, boolean technical, boolean edu) {
        this.edu = edu;
        this.technical = technical;
        this.namespacedId = namespacedId;
        this.itemFormNamespaceId = itemFormNamespaceId;
        aliases = EmptyArrays.EMPTY_STRINGS;
    }


    public Item get(int amount) {
        return RuntimeItems.getRuntimeMapping().getItemByNamespaceId(getItemFormNamespaceId(), amount);
    }


    public Item get(int amount, byte[] compoundTag) {
        Item item = get(amount);
        item.setCompoundTag(compoundTag != null ? compoundTag.clone() : null);
        return item;
    }


    public String getItemFormNamespaceId() {
        return itemFormNamespaceId;
    }


    public String getNamespacedId() {
        return namespacedId;
    }


    public boolean isTechnical() {
        return technical;
    }


    public boolean isEducationEdition() {
        return edu;
    }


    public String[] getAliases() {
        return aliases.length == 0 ? aliases : aliases.clone();
    }
}
