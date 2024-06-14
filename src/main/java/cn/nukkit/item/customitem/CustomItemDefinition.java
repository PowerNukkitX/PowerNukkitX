package cn.nukkit.item.customitem;

import cn.nukkit.block.BlockID;
import cn.nukkit.item.Item;
import cn.nukkit.item.customitem.data.CreativeCategory;
import cn.nukkit.item.customitem.data.CreativeGroup;
import cn.nukkit.item.customitem.data.DigProperty;
import cn.nukkit.item.customitem.data.RenderOffsets;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.tags.ItemTags;
import cn.nukkit.utils.Identifier;
import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * CustomBlockDefinition用于获得发送给客户端的物品行为包数据。{@link CustomItemDefinition.SimpleBuilder}中提供的方法都是控制发送给客户端数据，如果需要控制服务端部分行为，请覆写{@link cn.nukkit.item.Item Item}中的方法。
 * <p>
 * CustomBlockDefinition is used to get the data of the item behavior_pack sent to the client. The methods provided in {@link CustomItemDefinition.SimpleBuilder} control the data sent to the client, if you need to control some of the server-side behavior, please override the methods in {@link cn.nukkit.item.Item Item}.
 */
@Slf4j
public record CustomItemDefinition(String identifier, CompoundTag nbt) implements BlockID {
    private static final Object2IntOpenHashMap<String> INTERNAL_ALLOCATION_ID_MAP = new Object2IntOpenHashMap<>();
    private static final AtomicInteger nextRuntimeId = new AtomicInteger(10000);

    /**
     * 自定义物品的定义构造器
     * <p>
     * Definition builder for custom simple item
     *
     * @param item the item
     * @return the custom item definition . simple builder
     */
    public static CustomItemDefinition.SimpleBuilder customBuilder(CustomItem item) {
        return new CustomItemDefinition.SimpleBuilder(item);
    }

    /**
     * 简单物品的定义构造器
     * <p>
     * Definition builder for custom simple item
     *
     * @param item the item
     */
    public static CustomItemDefinition.SimpleBuilder simpleBuilder(ItemCustom item) {
        return new CustomItemDefinition.SimpleBuilder(item);
    }

    /**
     * 自定义工具的定义构造器
     * <p>
     * Definition builder for custom tools
     *
     * @param item the item
     */
    public static CustomItemDefinition.ToolBuilder toolBuilder(ItemCustomTool item) {
        return new CustomItemDefinition.ToolBuilder(item);
    }

    /**
     * 自定义盔甲的定义构造器
     * <p>
     * Definition builder for custom armor
     *
     * @param item the item
     */
    public static CustomItemDefinition.ArmorBuilder armorBuilder(ItemCustomArmor item) {
        return new CustomItemDefinition.ArmorBuilder(item);
    }

    /**
     * 自定义食物(药水)的定义构造器
     * <p>
     * Definition builder for custom food or potion
     *
     * @param item the item
     */
    public static CustomItemDefinition.EdibleBuilder edibleBuilder(ItemCustomFood item) {
        return new CustomItemDefinition.EdibleBuilder(item);
    }

    public @Nullable String getDisplayName() {
        if (!this.nbt.getCompound("components").contains("minecraft:display_name")) return null;
        return this.nbt.getCompound("components").getCompound("minecraft:display_name").getString("value");
    }

    public String getTexture() {
        return this.nbt.getCompound("components")
                .getCompound("item_properties")
                .getCompound("minecraft:icon")
                .getCompound("textures")
                .getString("default");
    }

    public int getRuntimeId() {
        return CustomItemDefinition.INTERNAL_ALLOCATION_ID_MAP.getInt(identifier);
    }

    public static int getRuntimeId(String identifier) {
        return CustomItemDefinition.INTERNAL_ALLOCATION_ID_MAP.getInt(identifier);
    }

    public static class SimpleBuilder {
        protected final String identifier;
        protected final CompoundTag nbt = new CompoundTag()
                .putCompound("components", new CompoundTag()
                        .putCompound("item_properties", new CompoundTag()
                                .putCompound("minecraft:icon", new CompoundTag())));
        private final Item item;
        protected String texture;
        protected String name;

        protected SimpleBuilder(CustomItem customItem) {
            this.item = (Item) customItem;
            this.identifier = ((Item) customItem).getId();
            //定义最大堆叠数量
            this.nbt.getCompound("components")
                    .getCompound("item_properties")
                    .putInt("max_stack_size", item.getMaxStackSize());
            //定义在创造栏的分类
            this.nbt.getCompound("components")
                    .getCompound("item_properties")//1 none
                    .putInt("creative_category", CreativeCategory.NONE.ordinal() + 1)
                    .putString("creative_group", CreativeGroup.NONE.getGroupName());
        }

        public SimpleBuilder texture(String texture) {
            Preconditions.checkArgument(!texture.isBlank(), "texture name is blank");
            this.texture = texture;
            return this;
        }

        public SimpleBuilder name(String name) {
            Preconditions.checkArgument(!name.isBlank(), "name is blank");
            this.name = name;
            return this;
        }


        /**
         * 是否允许副手持有
         * <p>
         * Whether to allow the offHand to have
         */
        public SimpleBuilder allowOffHand(boolean allowOffHand) {
            this.nbt.getCompound("components")
                    .getCompound("item_properties")
                    .putBoolean("allow_off_hand", allowOffHand);
            return this;
        }

        /**
         * 控制第三人称手持物品的显示方式
         * <p>
         * Control how third-person handheld items are displayed
         */
        public SimpleBuilder handEquipped(boolean handEquipped) {
            this.nbt.getCompound("components")
                    .getCompound("item_properties")
                    .putBoolean("hand_equipped", handEquipped);
            return this;
        }

        /**
         * @param foil 自定义物品是否带有附魔光辉效果<br>whether or not the item has an enchanted light effect
         */
        public SimpleBuilder foil(boolean foil) {
            this.nbt.getCompound("components")
                    .getCompound("item_properties")
                    .putBoolean("foil", foil);
            return this;
        }

        /**
         * 控制自定义物品在创造栏的分组,例如所有的附魔书是一组
         * <p>
         * Control the grouping of custom items in the creation inventory, e.g. all enchantment books are grouped together
         *
         * @see <a href="https://wiki.bedrock.dev/documentation/creative-categories.html#list-of-creative-categories">bedrock wiki</a>
         */
        public SimpleBuilder creativeGroup(String creativeGroup) {
            if (creativeGroup.isBlank()) {
                log.error("creativeGroup has an invalid value!");
                return this;
            }
            this.nbt.getCompound("components")
                    .getCompound("item_properties")
                    .putString("creative_group", creativeGroup);
            return this;
        }

        /**
         * 控制自定义物品在创造栏的分组,例如所有的附魔书是一组
         * <p>
         * Control the grouping of custom items in the creation inventory, e.g. all enchantment books are grouped together
         *
         * @see <a href="https://wiki.bedrock.dev/documentation/creative-categories.html#list-of-creative-categories">bedrock wiki</a>
         */
        public SimpleBuilder creativeGroup(CreativeGroup creativeGroup) {
            this.nbt.getCompound("components")
                    .getCompound("item_properties")
                    .putString("creative_group", creativeGroup.getGroupName());
            return this;
        }

        public SimpleBuilder creativeCategory(CreativeCategory creativeCategory) {
            this.nbt.getCompound("components")
                    .getCompound("item_properties")
                    .putInt("creative_category", creativeCategory.ordinal() + 1);
            return this;
        }

        /**
         * 控制自定义物品在不同视角下的渲染偏移
         * <p>
         * Control rendering offsets of custom items at different viewpoints
         */
        public SimpleBuilder renderOffsets(@NotNull RenderOffsets renderOffsets) {
            this.nbt.getCompound("components")
                    .putCompound("minecraft:render_offsets", renderOffsets.nbt);
            return this;
        }

        /**
         * 向自定义物品添加一个tag，通常用于合成等
         * <p>
         * Add a tag to a custom item, usually used for crafting, etc.
         *
         * @param tags the tags
         * @return the simple builder
         */
        public SimpleBuilder tag(String... tags) {
            Arrays.stream(tags).forEach(Identifier::assertValid);
            var list = this.nbt.getCompound("components").getList("item_tags", StringTag.class);
            if (list == null) {
                this.nbt.getCompound("components").putList("item_tags", new ListTag<>());
                return this;
            }
            for (var s : tags) {
                list.add(new StringTag(s));
            }
            this.nbt.getCompound("components").putList("item_tags", list);
            return this;
        }

        /**
         * 控制拿该物品的玩家是否可以在创造模式挖掘方块
         * <p>
         * Control whether the player with the item can dig the block in creation mode
         *
         * @param value the value
         * @return the simple builder
         */
        public SimpleBuilder canDestroyInCreative(boolean value) {
            this.nbt.getCompound("components")
                    .getCompound("item_properties")
                    .putBoolean("can_destroy_in_creative", value);
            return this;
        }

        /**
         * 对要发送给客户端的物品ComponentNBT进行自定义处理，这里包含了所有对自定义物品的定义。在符合条件的情况下，你可以任意修改。
         * <p>
         * Custom processing of the item to be sent to the client ComponentNBT, which contains all definitions for custom item. You can modify them as much as you want, under the right conditions.
         */
        public CustomItemDefinition customBuild(Consumer<CompoundTag> nbt) {
            var def = this.build();
            nbt.accept(def.nbt);
            return def;
        }

        public CustomItemDefinition build() {
            return calculateID();
        }

        protected CustomItemDefinition calculateID() {
            Preconditions.checkNotNull(texture, "You must define the texture through SimpleBuilder#texture method!");
            //定义材质
            this.nbt.getCompound("components")
                    .getCompound("item_properties")
                    .getCompound("minecraft:icon")
                    .putCompound("textures", new CompoundTag()
                            .putString("default", texture)
                    );

            if (name != null) {
                //定义显示名
                this.nbt.getCompound("components")
                        .putCompound("minecraft:display_name", new CompoundTag().putString("value", name));
            }

            var result = new CustomItemDefinition(identifier, nbt);
            int id;
            if (!INTERNAL_ALLOCATION_ID_MAP.containsKey(result.identifier())) {
                while (INTERNAL_ALLOCATION_ID_MAP.containsValue(id = nextRuntimeId.getAndIncrement())) {
                }
                INTERNAL_ALLOCATION_ID_MAP.put(result.identifier(), id);
            } else {
                id = INTERNAL_ALLOCATION_ID_MAP.getInt(result.identifier());
            }
            result.nbt.putString("name", result.identifier());
            result.nbt.putInt("id", id);
            return result;
        }

        /**
         * 添加一个可修理该物品的物品
         * <p>
         * Add an item that can repair the item
         *
         * @param repairItemNames the repair item names
         * @param molang          the molang
         * @return the simple builder
         */

        protected SimpleBuilder addRepairs(@NotNull List<String> repairItemNames, String molang) {
            if (molang.isBlank()) {
                log.error("repairAmount has an invalid value!");
                return this;
            }

            if (this.nbt.getCompound("components").contains("minecraft:repairable")) {
                var repair_items = this.nbt
                        .getCompound("components")
                        .getCompound("minecraft:repairable")
                        .getList("repair_items", CompoundTag.class);

                var items = new ListTag<CompoundTag>();
                for (var name : repairItemNames) {
                    items.add(new CompoundTag().putString("name", name));
                }

                repair_items.add(new CompoundTag()
                        .putList("items", items)
                        .putCompound("repair_amount", new CompoundTag()
                                .putString("expression", molang)
                                .putInt("version", 1)));
            } else {
                var repair_items = new ListTag<CompoundTag>();
                var items = new ListTag<CompoundTag>();
                for (var name : repairItemNames) {
                    items.add(new CompoundTag().putString("name", name));
                }
                repair_items.add(new CompoundTag()
                        .putList("items", items)
                        .putCompound("repair_amount", new CompoundTag()
                                .putString("expression", molang)
                                .putInt("version", 1)));
                this.nbt.getCompound("components")
                        .putCompound("minecraft:repairable", new CompoundTag()
                                .putList("repair_items", repair_items));
            }
            return this;
        }
    }

    public static class ToolBuilder extends SimpleBuilder {
        private final ItemCustomTool item;
        private final List<CompoundTag> blocks = new ArrayList<>();
        private final List<String> blockTags = new ArrayList<>();
        private final CompoundTag diggerRoot = new CompoundTag()
                .putBoolean("use_efficiency", true)
                .putList("destroy_speeds", new ListTag<>(Tag.TAG_Compound));
        private Integer speed = null;

        public static Map<String, Map<String, DigProperty>> toolBlocks = new HashMap<>();

        static {
            var pickaxeBlocks = new Object2ObjectOpenHashMap<String, DigProperty>();
            var axeBlocks = new Object2ObjectOpenHashMap<String, DigProperty>();
            var shovelBlocks = new Object2ObjectOpenHashMap<String, DigProperty>();
            var hoeBlocks = new Object2ObjectOpenHashMap<String, DigProperty>();
            var swordBlocks = new Object2ObjectOpenHashMap<String, DigProperty>();
            for (var name : List.of(ACACIA_SLAB, BAMBOO_MOSAIC_SLAB, BAMBOO_SLAB, BIRCH_SLAB, BLACKSTONE_SLAB, BRICK_SLAB, CHERRY_SLAB, COBBLED_DEEPSLATE_SLAB, COBBLESTONE_SLAB, CRIMSON_SLAB, CUT_COPPER_SLAB, DARK_OAK_SLAB, DEEPSLATE_BRICK_SLAB, DEEPSLATE_TILE_SLAB, EXPOSED_CUT_COPPER_SLAB, JUNGLE_SLAB, MANGROVE_SLAB, MUD_BRICK_SLAB, NETHER_BRICK_SLAB, OAK_SLAB, OXIDIZED_CUT_COPPER_SLAB, PETRIFIED_OAK_SLAB, POLISHED_BLACKSTONE_BRICK_SLAB, POLISHED_BLACKSTONE_SLAB, POLISHED_DEEPSLATE_SLAB, POLISHED_TUFF_SLAB, QUARTZ_SLAB, SANDSTONE_SLAB, SMOOTH_STONE_SLAB, SPRUCE_SLAB, STONE_BRICK_SLAB, TUFF_BRICK_SLAB, TUFF_SLAB, WARPED_SLAB, WAXED_CUT_COPPER_SLAB, WAXED_EXPOSED_CUT_COPPER_SLAB, WAXED_OXIDIZED_CUT_COPPER_SLAB, WAXED_WEATHERED_CUT_COPPER_SLAB, WEATHERED_CUT_COPPER_SLAB,
                    ICE, BLUE_ICE, UNDYED_SHULKER_BOX, BLUE_SHULKER_BOX, RED_SHULKER_BOX, BLACK_SHULKER_BOX, CYAN_SHULKER_BOX, BROWN_SHULKER_BOX, LIME_SHULKER_BOX, GRAY_SHULKER_BOX, GREEN_SHULKER_BOX, LIGHT_BLUE_SHULKER_BOX, LIGHT_GRAY_SHULKER_BOX, MAGENTA_SHULKER_BOX, ORANGE_SHULKER_BOX, WHITE_SHULKER_BOX, YELLOW_SHULKER_BOX, PINK_SHULKER_BOX, PURPLE_SHULKER_BOX, PRISMARINE, PRISMARINE_BRICKS_STAIRS, PRISMARINE_STAIRS, STONE_BLOCK_SLAB4, DARK_PRISMARINE_STAIRS, ANVIL, BONE_BLOCK, IRON_TRAPDOOR, NETHER_BRICK_FENCE, CRYING_OBSIDIAN, MAGMA, SMOKER, LIT_SMOKER, HOPPER, REDSTONE_BLOCK, MOB_SPAWNER, NETHERITE_BLOCK, SMOOTH_STONE, DIAMOND_BLOCK, LAPIS_BLOCK, EMERALD_BLOCK, ENCHANTING_TABLE, END_BRICKS, CRACKED_POLISHED_BLACKSTONE_BRICKS, NETHER_BRICK, CRACKED_NETHER_BRICKS, PURPUR_BLOCK, PURPUR_STAIRS, END_BRICK_STAIRS, STONE_BLOCK_SLAB2, STONE_BLOCK_SLAB3, STONE_BRICK_STAIRS, MOSSY_STONE_BRICK_STAIRS, POLISHED_BLACKSTONE_BRICKS, POLISHED_BLACKSTONE_STAIRS, BLACKSTONE_WALL, BLACKSTONE_WALL, POLISHED_BLACKSTONE_WALL, SANDSTONE, GRINDSTONE, SMOOTH_STONE, BREWING_STAND, CHAIN, LANTERN, SOUL_LANTERN, ANCIENT_DEBRIS, QUARTZ_ORE, NETHERRACK, BASALT, POLISHED_BASALT, STONEBRICK, WARPED_NYLIUM, CRIMSON_NYLIUM, END_STONE, ENDER_CHEST, QUARTZ_BLOCK, QUARTZ_STAIRS, QUARTZ_BRICKS, QUARTZ_STAIRS, NETHER_GOLD_ORE, FURNACE, BLAST_FURNACE, LIT_FURNACE, BLAST_FURNACE, BLACKSTONE, BLACK_CONCRETE, BLUE_CONCRETE, BROWN_CONCRETE, CYAN_CONCRETE, GRAY_CONCRETE, GREEN_CONCRETE, LIGHT_BLUE_CONCRETE, LIME_CONCRETE, MAGENTA_CONCRETE, ORANGE_CONCRETE, PINK_CONCRETE, PURPLE_CONCRETE, RED_CONCRETE, LIGHT_GRAY_CONCRETE, WHITE_CONCRETE, YELLOW_CONCRETE, DEEPSLATE_COPPER_ORE, DEEPSLATE_LAPIS_ORE, CHISELED_DEEPSLATE, COBBLED_DEEPSLATE, COBBLED_DEEPSLATE_DOUBLE_SLAB, COBBLED_DEEPSLATE_SLAB, COBBLED_DEEPSLATE_STAIRS, COBBLED_DEEPSLATE_WALL, CRACKED_DEEPSLATE_BRICKS, CRACKED_DEEPSLATE_TILES, DEEPSLATE, DEEPSLATE_BRICK_DOUBLE_SLAB, DEEPSLATE_BRICK_SLAB, DEEPSLATE_BRICK_STAIRS, DEEPSLATE_BRICK_WALL, DEEPSLATE_BRICKS, DEEPSLATE_TILE_DOUBLE_SLAB, DEEPSLATE_TILE_SLAB, DEEPSLATE_TILE_STAIRS, DEEPSLATE_TILE_WALL, DEEPSLATE_TILES, INFESTED_DEEPSLATE, POLISHED_DEEPSLATE, POLISHED_DEEPSLATE_DOUBLE_SLAB, POLISHED_DEEPSLATE_SLAB, POLISHED_DEEPSLATE_STAIRS, POLISHED_DEEPSLATE_WALL, CALCITE, AMETHYST_BLOCK, AMETHYST_CLUSTER, BUDDING_AMETHYST, RAW_COPPER_BLOCK, RAW_GOLD_BLOCK, RAW_IRON_BLOCK, COPPER_ORE, COPPER_BLOCK, CUT_COPPER, CUT_COPPER_SLAB, CUT_COPPER_STAIRS, DOUBLE_CUT_COPPER_SLAB, EXPOSED_COPPER, EXPOSED_CUT_COPPER, EXPOSED_CUT_COPPER_SLAB, EXPOSED_CUT_COPPER_STAIRS, EXPOSED_DOUBLE_CUT_COPPER_SLAB, OXIDIZED_COPPER, OXIDIZED_CUT_COPPER, OXIDIZED_CUT_COPPER_SLAB, OXIDIZED_CUT_COPPER_STAIRS, OXIDIZED_DOUBLE_CUT_COPPER_SLAB, WEATHERED_COPPER, WEATHERED_CUT_COPPER, WEATHERED_CUT_COPPER_SLAB, WEATHERED_CUT_COPPER_STAIRS, WEATHERED_DOUBLE_CUT_COPPER_SLAB, WAXED_COPPER, WAXED_CUT_COPPER, WAXED_CUT_COPPER_SLAB, WAXED_CUT_COPPER_STAIRS, WAXED_DOUBLE_CUT_COPPER_SLAB, WAXED_EXPOSED_COPPER, WAXED_EXPOSED_CUT_COPPER, WAXED_EXPOSED_CUT_COPPER_SLAB, WAXED_EXPOSED_CUT_COPPER_STAIRS, WAXED_EXPOSED_DOUBLE_CUT_COPPER_SLAB, WAXED_OXIDIZED_COPPER, WAXED_OXIDIZED_CUT_COPPER, WAXED_OXIDIZED_CUT_COPPER_SLAB, WAXED_OXIDIZED_CUT_COPPER_STAIRS, WAXED_OXIDIZED_DOUBLE_CUT_COPPER_SLAB, WAXED_WEATHERED_COPPER, WAXED_WEATHERED_CUT_COPPER, WAXED_WEATHERED_CUT_COPPER_SLAB, WAXED_WEATHERED_CUT_COPPER_STAIRS, WAXED_WEATHERED_DOUBLE_CUT_COPPER_SLAB, DRIPSTONE_BLOCK, POINTED_DRIPSTONE, LIGHTNING_ROD, BASALT, TUFF, DOUBLE_STONE_BLOCK_SLAB, DOUBLE_STONE_BLOCK_SLAB2, DOUBLE_STONE_BLOCK_SLAB3, DOUBLE_STONE_BLOCK_SLAB4, BLACKSTONE_DOUBLE_SLAB, POLISHED_BLACKSTONE_BRICK_DOUBLE_SLAB, POLISHED_BLACKSTONE_DOUBLE_SLAB, MOSSY_COBBLESTONE_STAIRS, STONECUTTER, STONECUTTER_BLOCK, RED_NETHER_BRICK, RED_NETHER_BRICK_STAIRS, NORMAL_STONE_STAIRS, SMOOTH_BASALT, STONE, COBBLESTONE, MOSSY_COBBLESTONE, DRIPSTONE_BLOCK, BRICK_BLOCK, STONE_STAIRS, STONE_BLOCK_SLAB2, STONE_BLOCK_SLAB3, STONE_BLOCK_SLAB4, COBBLESTONE_WALL, GOLD_BLOCK, IRON_BLOCK, CAULDRON, IRON_BARS, OBSIDIAN, COAL_ORE, DEEPSLATE_COAL_ORE, DEEPSLATE_DIAMOND_ORE, DEEPSLATE_EMERALD_ORE, DEEPSLATE_GOLD_ORE, DEEPSLATE_IRON_ORE, DEEPSLATE_REDSTONE_ORE, LIT_DEEPSLATE_REDSTONE_ORE, DIAMOND_ORE, EMERALD_ORE, GOLD_ORE, IRON_ORE, LAPIS_ORE, REDSTONE_ORE, LIT_REDSTONE_ORE, RAW_IRON_BLOCK, RAW_GOLD_BLOCK, RAW_COPPER_BLOCK, MUD_BRICK_DOUBLE_SLAB, MUD_BRICK_SLAB, MUD_BRICK_STAIRS, MUD_BRICK_WALL, MUD_BRICKS, HARDENED_CLAY, BLACK_TERRACOTTA, BLUE_TERRACOTTA, BROWN_TERRACOTTA, CYAN_TERRACOTTA, GRAY_TERRACOTTA, GREEN_TERRACOTTA, LIGHT_BLUE_TERRACOTTA, LIME_TERRACOTTA, MAGENTA_TERRACOTTA, ORANGE_TERRACOTTA, PINK_TERRACOTTA, PURPLE_TERRACOTTA, RED_TERRACOTTA, LIGHT_GRAY_TERRACOTTA, WHITE_TERRACOTTA, YELLOW_TERRACOTTA, POLISHED_DIORITE_STAIRS, ANDESITE_STAIRS, POLISHED_ANDESITE_STAIRS, GRANITE_STAIRS, POLISHED_GRANITE_STAIRS, POLISHED_BLACKSTONE, CHISELED_POLISHED_BLACKSTONE, POLISHED_BLACKSTONE_BRICK_STAIRS, BLACKSTONE_STAIRS, POLISHED_BLACKSTONE_BRICK_WALL, GILDED_BLACKSTONE, COAL_BLOCK)) {
                pickaxeBlocks.put(name, new DigProperty());
            }
            toolBlocks.put(ItemTags.IS_PICKAXE, pickaxeBlocks);

            for (var name : List.of(CHEST, BOOKSHELF, MELON_BLOCK, WARPED_STEM, CRIMSON_STEM, WARPED_STEM, CRIMSON_STEM, CRAFTING_TABLE, CRIMSON_PLANKS, WARPED_PLANKS, WARPED_STAIRS, WARPED_TRAPDOOR, CRIMSON_STAIRS, CRIMSON_TRAPDOOR, CRIMSON_DOOR, CRIMSON_DOUBLE_SLAB, WARPED_DOOR, WARPED_DOUBLE_SLAB, CRAFTING_TABLE, COMPOSTER, CARTOGRAPHY_TABLE, LECTERN, STRIPPED_CRIMSON_STEM, STRIPPED_WARPED_STEM, TRAPDOOR, SPRUCE_TRAPDOOR, BIRCH_TRAPDOOR, JUNGLE_TRAPDOOR, ACACIA_TRAPDOOR, DARK_OAK_TRAPDOOR, WOODEN_DOOR, SPRUCE_DOOR, BIRCH_DOOR, JUNGLE_DOOR, ACACIA_DOOR, DARK_OAK_DOOR, ACACIA_FENCE, DARK_OAK_FENCE, BAMBOO_FENCE, MANGROVE_FENCE, NETHER_BRICK_FENCE, OAK_FENCE, CRIMSON_FENCE, JUNGLE_FENCE, CHERRY_FENCE, BIRCH_FENCE, WARPED_FENCE, SPRUCE_FENCE, FENCE_GATE, SPRUCE_FENCE_GATE, BIRCH_FENCE_GATE, JUNGLE_FENCE_GATE, ACACIA_FENCE_GATE, DARK_OAK_FENCE_GATE, MANGROVE_LOG, OAK_LOG, JUNGLE_LOG, SPRUCE_LOG, DARK_OAK_LOG, CHERRY_LOG, ACACIA_LOG, BIRCH_LOG, ACACIA_PLANKS, BAMBOO_PLANKS, CRIMSON_PLANKS, BIRCH_PLANKS, DARK_OAK_PLANKS, CHERRY_PLANKS, WARPED_PLANKS, OAK_PLANKS, SPRUCE_PLANKS, MANGROVE_PLANKS, JUNGLE_PLANKS, ACACIA_DOUBLE_SLAB, ACACIA_SLAB, BAMBOO_DOUBLE_SLAB, BAMBOO_MOSAIC_SLAB, BIRCH_DOUBLE_SLAB
                    , BIRCH_SLAB, OAK_STAIRS, SPRUCE_STAIRS, BIRCH_STAIRS, JUNGLE_STAIRS, ACACIA_STAIRS, DARK_OAK_STAIRS, WALL_SIGN, SPRUCE_WALL_SIGN, BIRCH_WALL_SIGN, JUNGLE_WALL_SIGN, ACACIA_WALL_SIGN, DARKOAK_WALL_SIGN, WOODEN_PRESSURE_PLATE, SPRUCE_PRESSURE_PLATE, BIRCH_PRESSURE_PLATE, JUNGLE_PRESSURE_PLATE, ACACIA_PRESSURE_PLATE, DARK_OAK_PRESSURE_PLATE, SMITHING_TABLE, FLETCHING_TABLE, BARREL, BEEHIVE, BEE_NEST, LADDER, PUMPKIN, CARVED_PUMPKIN, LIT_PUMPKIN, MANGROVE_DOOR, MANGROVE_DOUBLE_SLAB, MANGROVE_FENCE, MANGROVE_FENCE_GATE, MANGROVE_LOG, MANGROVE_PLANKS, MANGROVE_PRESSURE_PLATE, MANGROVE_SLAB, MANGROVE_STAIRS, MANGROVE_WALL_SIGN, MANGROVE_WOOD, WOODEN_BUTTON, SPRUCE_BUTTON, BIRCH_BUTTON, JUNGLE_BUTTON, ACACIA_BUTTON, DARK_OAK_BUTTON, MANGROVE_BUTTON, STRIPPED_OAK_LOG, STRIPPED_SPRUCE_LOG, STRIPPED_BIRCH_LOG, STRIPPED_JUNGLE_LOG, STRIPPED_ACACIA_LOG, STRIPPED_DARK_OAK_LOG, STRIPPED_MANGROVE_WOOD, STRIPPED_OAK_LOG, STRIPPED_SPRUCE_LOG, STRIPPED_BIRCH_LOG, STRIPPED_JUNGLE_LOG, STRIPPED_ACACIA_LOG, STRIPPED_DARK_OAK_LOG, STRIPPED_MANGROVE_LOG, STANDING_SIGN, SPRUCE_STANDING_SIGN, BIRCH_STANDING_SIGN, JUNGLE_STANDING_SIGN, ACACIA_STANDING_SIGN, DARKOAK_STANDING_SIGN, MANGROVE_STANDING_SIGN, MANGROVE_TRAPDOOR, WARPED_STANDING_SIGN, WARPED_WALL_SIGN, CRIMSON_STANDING_SIGN, CRIMSON_WALL_SIGN, MANGROVE_ROOTS)) {
                axeBlocks.put(name, new DigProperty());
            }
            toolBlocks.put(ItemTags.IS_AXE, axeBlocks);

            for (var name : List.of(SOUL_SAND, SOUL_SOIL, DIRT_WITH_ROOTS, MYCELIUM, PODZOL, DIRT, FARMLAND, SAND, GRAVEL, GRASS_BLOCK, GRASS_PATH, SNOW, MUD, PACKED_MUD, CLAY)) {
                shovelBlocks.put(name, new DigProperty());
            }
            toolBlocks.put(ItemTags.IS_SHOVEL, shovelBlocks);

            for (var name : List.of(NETHER_WART_BLOCK, HAY_BLOCK, TARGET, SHROOMLIGHT, BlockID.ACACIA_LEAVES,
                    BlockID.AZALEA_LEAVES,
                    BlockID.BIRCH_LEAVES,
                    BlockID.AZALEA_LEAVES_FLOWERED,
                    BlockID.CHERRY_LEAVES,
                    BlockID.DARK_OAK_LEAVES,
                    BlockID.JUNGLE_LEAVES,
                    BlockID.MANGROVE_LEAVES,
                    BlockID.OAK_LEAVES,
                    BlockID.SPRUCE_LEAVES, AZALEA_LEAVES_FLOWERED, AZALEA_LEAVES, WARPED_WART_BLOCK)) {
                hoeBlocks.put(name, new DigProperty());
            }
            toolBlocks.put(ItemTags.IS_HOE, hoeBlocks);

            for (var name : List.of(WEB, BAMBOO)) {
                swordBlocks.put(name, new DigProperty());
            }
            toolBlocks.put(ItemTags.IS_SWORD, swordBlocks);
        }

        private ToolBuilder(ItemCustomTool item) {
            super(item);
            this.item = item;
            this.nbt.getCompound("components")
                    .getCompound("item_properties")
                    .putInt("enchantable_value", item.getEnchantAbility());

            this.nbt.getCompound("components")
                    .getCompound("item_properties")
                    .putFloat("mining_speed", 1f)
                    .putBoolean("can_destroy_in_creative", true);
        }

        public ToolBuilder addRepairItemName(@NotNull String repairItemName, String molang) {
            super.addRepairs(List.of(repairItemName), molang);
            return this;
        }

        public ToolBuilder addRepairItemName(@NotNull String repairItemName, int repairAmount) {
            super.addRepairs(List.of(repairItemName), String.valueOf(repairAmount));
            return this;
        }

        public ToolBuilder addRepairItems(@NotNull List<Item> repairItems, String molang) {
            super.addRepairs(repairItems.stream().map(Item::getId).toList(), molang);
            return this;
        }

        public ToolBuilder addRepairItems(@NotNull List<Item> repairItems, int repairAmount) {
            super.addRepairs(repairItems.stream().map(Item::getId).toList(), String.valueOf(repairAmount));
            return this;
        }

        /**
         * 控制采集类工具的挖掘速度
         *
         * @param speed 挖掘速度
         */
        public ToolBuilder speed(int speed) {
            if (speed < 0) {
                log.error("speed has an invalid value!");
                return this;
            }
            if (item.isPickaxe() || item.isShovel() || item.isHoe() || item.isAxe() || item.isShears()) {
                this.speed = speed;
            }
            return this;
        }

        /**
         * 给工具添加可挖掘的方块，及挖掘它的速度
         * <p>
         * Add a diggable block to the tool and define dig speed
         *
         * @param blockName the block name
         * @param speed     挖掘速度
         * @return the tool builder
         */

        public ToolBuilder addExtraBlock(@NotNull String blockName, int speed) {
            if (speed < 0) {
                log.error("speed has an invalid value!");
                return this;
            }
            this.blocks.add(new CompoundTag()
                    .putCompound("block", new CompoundTag()
                            .putString("name", blockName)
                            .putCompound("states", new CompoundTag())
                            .putString("tags", "")
                    )
                    .putInt("speed", speed));
            return this;
        }

        /**
         * 给工具添加可挖掘的方块，及挖掘它的速度
         * <p>
         * Add a diggable block to the tool and define dig speed
         *
         * @param blocks the blocks
         * @return the tool builder
         */

        public ToolBuilder addExtraBlocks(@NotNull Map<String, Integer> blocks) {
            blocks.forEach((blockName, speed) -> {
                if (speed < 0) {
                    log.error("speed has an invalid value!");
                    return;
                }
                this.blocks.add(new CompoundTag()
                        .putCompound("block", new CompoundTag()
                                .putString("name", blockName)
                                .putCompound("states", new CompoundTag())
                                .putString("tags", "")
                        )
                        .putInt("speed", speed));
            });
            return this;
        }

        /**
         * 给工具添加可挖掘的方块，及挖掘它的速度
         * <p>
         * Add a diggable block to the tool and define dig speed
         *
         * @param blockName the block name
         * @param property  the property
         * @return the tool builder
         */

        public ToolBuilder addExtraBlocks(@NotNull String blockName, DigProperty property) {
            Integer propertySpeed;
            if ((propertySpeed = property.getSpeed()) != null && propertySpeed < 0) {
                log.error("speed has an invalid value!");
                return this;
            }
            this.blocks.add(new CompoundTag()
                    .putCompound("block", new CompoundTag()
                            .putString("name", blockName)
                            .putCompound("states", property.getStates())
                            .putString("tags", "")
                    )
                    .putInt("speed", propertySpeed));
            return this;
        }

        /**
         * 给工具添加可挖掘的一类方块，用blockTag描述，挖掘它们的速度为{@link #speed(int)}的速度，如果没定义则为工具TIER对应的速度
         * <p>
         * Add a class of block to the tool that can be mined, described by blockTag, and the speed to mine them is the speed of {@link #speed(int)}, or the speed corresponding to the tool TIER if it is not defined
         *
         * @param blockTags 挖掘速度
         * @return the tool builder
         */

        public ToolBuilder addExtraBlockTags(@NotNull List<String> blockTags) {
            if (!blockTags.isEmpty()) {
                this.blockTags.addAll(blockTags);
            }
            return this;
        }

        @Override
        public CustomItemDefinition build() {
            //附加耐久 攻击伤害信息
            this.nbt.getCompound("components")
                    .putCompound("minecraft:durability", new CompoundTag().putInt("max_durability", item.getMaxDurability()))
                    .getCompound("item_properties")
                    .putInt("damage", item.getAttackDamage());

            if (speed == null) {
                speed = switch (item.getTier()) {
                    case 6 -> 7;
                    case 5 -> 6;
                    case 4 -> 5;
                    case 3 -> 4;
                    case 2 -> 3;
                    case 1 -> 2;
                    default -> 1;
                };
            }
            String type = null;
            if (item.isPickaxe()) {
                //添加可挖掘方块Tags
                this.blockTags.addAll(List.of("'stone'", "'metal'", "'diamond_pick_diggable'", "'mob_spawner'", "'rail'", "'slab_block'", "'stair_block'", "'smooth stone slab'", "'sandstone slab'", "'cobblestone slab'", "'brick slab'", "'stone bricks slab'", "'quartz slab'", "'nether brick slab'"));
                //添加可挖掘方块
                type = ItemTags.IS_PICKAXE;
                //附加附魔信息
                this.nbt.getCompound("components").getCompound("item_properties")
                        .putString("enchantable_slot", "pickaxe");
                this.tag("minecraft:is_pickaxe");
            } else if (item.isAxe()) {
                this.blockTags.addAll(List.of("'wood'", "'pumpkin'", "'plant'"));
                type = ItemTags.IS_AXE;
                this.nbt.getCompound("components").getCompound("item_properties")
                        .putString("enchantable_slot", "axe");
                this.tag("minecraft:is_axe");
            } else if (item.isShovel()) {
                this.blockTags.addAll(List.of("'sand'", "'dirt'", "'gravel'", "'grass'", "'snow'"));
                type = ItemTags.IS_SHOVEL;
                this.nbt.getCompound("components").getCompound("item_properties")
                        .putString("enchantable_slot", "shovel");
                this.tag("minecraft:is_shovel");
            } else if (item.isHoe()) {
                this.nbt.getCompound("components").getCompound("item_properties")
                        .putString("enchantable_slot", "hoe");
                type = ItemTags.IS_HOE;
                this.tag("minecraft:is_hoe");
            } else if (item.isSword()) {
                this.nbt.getCompound("components").getCompound("item_properties")
                        .putString("enchantable_slot", "sword");
                type = ItemTags.IS_SWORD;
            } else {
                if (this.nbt.getCompound("components").contains("item_tags")) {
                    var list = this.nbt.getCompound("components").getList("item_tags", StringTag.class).getAll();
                    for (var tag : list) {
                        var id = tag.parseValue();
                        if (toolBlocks.containsKey(id)) {
                            type = id;
                            break;
                        }
                    }
                }
            }
            if (type != null) {
                toolBlocks.get(type).forEach(
                        (k, v) -> {
                            if (v.getSpeed() == null) v.setSpeed(speed);
                            blocks.add(new CompoundTag()
                                    .putCompound("block", new CompoundTag()
                                            .putString("name", k)
                                            .putCompound("states", v.getStates())
                                            .putString("tags", "")
                                    )
                                    .putInt("speed", v.getSpeed()));
                        }
                );
            }
            //添加可挖掘的方块tags
            if (!this.blockTags.isEmpty()) {
                var cmp = new CompoundTag();
                cmp.putCompound("block", new CompoundTag()
                                .putString("name", "")
                                .putCompound("states", new CompoundTag())
                                .putString("tags", "q.any_tag(" + String.join(", ", this.blockTags) + ")")
                        )
                        .putInt("speed", speed);
                this.diggerRoot.getList("destroy_speeds", CompoundTag.class).add(cmp);
                this.nbt.getCompound("components")
                        .putCompound("minecraft:digger", this.diggerRoot);
            }
            if (!this.blocks.isEmpty()) {
                //添加可挖掘的方块
                for (var k : this.blocks) {
                    this.diggerRoot.getList("destroy_speeds", CompoundTag.class).add(k);
                }
                if (!this.nbt.getCompound("components").containsCompound("minecraft:digger")) {
                    this.nbt.getCompound("components")
                            .putCompound("minecraft:digger", this.diggerRoot);
                }
            }
            return calculateID();
        }
    }

    public static class ArmorBuilder extends SimpleBuilder {
        private final ItemCustomArmor item;

        private ArmorBuilder(ItemCustomArmor item) {
            super(item);
            this.item = item;
            this.nbt.getCompound("components")
                    .getCompound("item_properties")
                    .putInt("enchantable_value", item.getEnchantAbility())
                    .putBoolean("can_destroy_in_creative", true);
        }

        public ArmorBuilder addRepairItemName(@NotNull String repairItemName, String molang) {
            super.addRepairs(List.of(repairItemName), molang);
            return this;
        }

        public ArmorBuilder addRepairItemName(@NotNull String repairItemName, int repairAmount) {
            super.addRepairs(List.of(repairItemName), String.valueOf(repairAmount));
            return this;
        }

        public ArmorBuilder addRepairItems(@NotNull List<Item> repairItems, String molang) {
            super.addRepairs(repairItems.stream().map(Item::getId).toList(), molang);
            return this;
        }

        public ArmorBuilder addRepairItems(@NotNull List<Item> repairItems, int repairAmount) {
            super.addRepairs(repairItems.stream().map(Item::getId).toList(), String.valueOf(repairAmount));
            return this;
        }

        @Override
        public CustomItemDefinition build() {
            this.nbt.getCompound("components")
                    .putCompound("minecraft:armor", new CompoundTag()
                            .putInt("protection", item.getArmorPoints()))
                    .putCompound("minecraft:durability", new CompoundTag()
                            .putInt("max_durability", item.getMaxDurability()));
            if (item.isHelmet()) {
                this.nbt.getCompound("components").getCompound("item_properties")
                        .putString("enchantable_slot", "armor_head");
                this.nbt.getCompound("components")
                        .putCompound("minecraft:wearable", new CompoundTag()
                                .putString("slot", "slot.armor.head"));
            } else if (item.isChestplate()) {
                this.nbt.getCompound("components").getCompound("item_properties")
                        .putString("enchantable_slot", "armor_torso");
                this.nbt.getCompound("components")
                        .putCompound("minecraft:wearable", new CompoundTag()
                                .putString("slot", "slot.armor.chest"));
            } else if (item.isLeggings()) {
                this.nbt.getCompound("components").getCompound("item_properties")
                        .putString("enchantable_slot", "armor_legs");
                this.nbt.getCompound("components")
                        .putCompound("minecraft:wearable", new CompoundTag()
                                .putString("slot", "slot.armor.legs"));
            } else if (item.isBoots()) {
                this.nbt.getCompound("components").getCompound("item_properties")
                        .putString("enchantable_slot", "armor_feet");
                this.nbt.getCompound("components")
                        .putCompound("minecraft:wearable", new CompoundTag()
                                .putString("slot", "slot.armor.feet"));
            }
            return calculateID();
        }
    }

    public static class EdibleBuilder extends SimpleBuilder {
        private EdibleBuilder(ItemCustomFood item) {
            super(item);

            if (this.nbt.getCompound("components").contains("minecraft:food")) {
                this.nbt.getCompound("components").getCompound("minecraft:food").putBoolean("can_always_eat", !item.isRequiresHunger());
            } else {
                this.nbt.getCompound("components").putCompound("minecraft:food", new CompoundTag().putBoolean("can_always_eat", !item.isRequiresHunger()));
            }

            int eatingtick = item.getEatingTicks();
            this.nbt.getCompound("components")
                    .getCompound("item_properties")
                    .putInt("use_duration", eatingtick)
                    .putInt("use_animation", item.isDrink() ? 2 : 1)
                    .putBoolean("can_destroy_in_creative", true);
        }
    }
}
