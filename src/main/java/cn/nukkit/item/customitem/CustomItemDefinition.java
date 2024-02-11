package cn.nukkit.item.customitem;

import cn.nukkit.item.Item;
import cn.nukkit.item.customitem.data.CreativeCategory;
import cn.nukkit.item.customitem.data.CreativeGroup;
import cn.nukkit.item.customitem.data.DigProperty;
import cn.nukkit.item.customitem.data.RenderOffsets;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.registry.Registries;
import cn.nukkit.tags.ItemTags;
import cn.nukkit.utils.Identifier;
import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
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


public record CustomItemDefinition(String identifier, CompoundTag nbt) {
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
                System.out.println("creativeGroup has an invalid value!");
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
                list = new ListTag<>();
                this.nbt.getCompound("components").putList("item_tags", list);
            }
            for (var s : tags) {
                list.add(new StringTag(s));
            }
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
                    .putCompound("textures", new CompoundTag().putString("default", texture));

            if (name != null) {
                //定义显示名
                this.nbt.getCompound("components")
                        .putCompound("minecraft:display_name", new CompoundTag().putString("value", name));
            }

            var result = new CustomItemDefinition(identifier, nbt);
            int id;
            if (!INTERNAL_ALLOCATION_ID_MAP.containsKey(result.identifier())) {
                while (Registries.ITEM_RUNTIMEID.getIdentifier(id = nextRuntimeId.getAndIncrement()) != null) {
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
                System.out.println("repairAmount has an invalid value!");
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
        private final CompoundTag diggerRoot = new CompoundTag().putCompound("minecraft:digger", new CompoundTag()
                .putBoolean("use_efficiency", true)
                .putList("destroy_speeds", new ListTag<>()));
        private Integer speed = null;

        public static Map<String, Map<String, DigProperty>> toolBlocks = new HashMap<>();

        static {
            var pickaxeBlocks = new Object2ObjectOpenHashMap<String, DigProperty>();
            var axeBlocks = new Object2ObjectOpenHashMap<String, DigProperty>();
            var shovelBlocks = new Object2ObjectOpenHashMap<String, DigProperty>();
            var hoeBlocks = new Object2ObjectOpenHashMap<String, DigProperty>();
            var swordBlocks = new Object2ObjectOpenHashMap<String, DigProperty>();
            for (var name : List.of("minecraft:ice", "minecraft:undyed_shulker_box", "minecraft:shulker_box", "minecraft:prismarine", "minecraft:stone_slab4", "minecraft:prismarine_bricks_stairs", "minecraft:prismarine_stairs", "minecraft:dark_prismarine_stairs", "minecraft:anvil", "minecraft:bone_block", "minecraft:iron_trapdoor", "minecraft:nether_brick_fence", "minecraft:crying_obsidian", "minecraft:magma", "minecraft:smoker", "minecraft:lit_smoker", "minecraft:hopper", "minecraft:redstone_block", "minecraft:mob_spawner", "minecraft:netherite_block", "minecraft:smooth_stone", "minecraft:diamond_block", "minecraft:lapis_block", "minecraft:emerald_block", "minecraft:enchanting_table", "minecraft:end_bricks", "minecraft:cracked_polished_blackstone_bricks", "minecraft:nether_brick", "minecraft:cracked_nether_bricks", "minecraft:purpur_block", "minecraft:purpur_stairs", "minecraft:end_brick_stairs", "minecraft:stone_slab", "minecraft:stone_slab2", "minecraft:stone_slab3", "minecraft:stone_brick_stairs", "minecraft:mossy_stone_brick_stairs", "minecraft:polished_blackstone_bricks", "minecraft:polished_blackstone_stairs", "minecraft:blackstone_wall", "minecraft:blackstone_wall", "minecraft:polished_blackstone_wall", "minecraft:sandstone", "minecraft:grindstone", "minecraft:smooth_stone", "minecraft:brewing_stand", "minecraft:chain", "minecraft:lantern", "minecraft:soul_lantern", "minecraft:ancient_debris", "minecraft:quartz_ore", "minecraft:netherrack", "minecraft:basalt", "minecraft:polished_basalt", "minecraft:stonebrick", "minecraft:warped_nylium", "minecraft:crimson_nylium", "minecraft:end_stone", "minecraft:ender_chest", "minecraft:quartz_block", "minecraft:quartz_stairs", "minecraft:quartz_bricks", "minecraft:quartz_stairs", "minecraft:nether_gold_ore", "minecraft:furnace", "minecraft:blast_furnace", "minecraft:lit_furnace", "minecraft:blast_furnace", "minecraft:blackstone", "minecraft:concrete", "minecraft:deepslate_copper_ore", "minecraft:deepslate_lapis_ore", "minecraft:chiseled_deepslate", "minecraft:cobbled_deepslate", "minecraft:cobbled_deepslate_double_slab", "minecraft:cobbled_deepslate_slab", "minecraft:cobbled_deepslate_stairs", "minecraft:cobbled_deepslate_wall", "minecraft:cracked_deepslate_bricks", "minecraft:cracked_deepslate_tiles", "minecraft:deepslate", "minecraft:deepslate_brick_double_slab", "minecraft:deepslate_brick_slab", "minecraft:deepslate_brick_stairs", "minecraft:deepslate_brick_wall", "minecraft:deepslate_bricks", "minecraft:deepslate_tile_double_slab", "minecraft:deepslate_tile_slab", "minecraft:deepslate_tile_stairs", "minecraft:deepslate_tile_wall", "minecraft:deepslate_tiles", "minecraft:infested_deepslate", "minecraft:polished_deepslate", "minecraft:polished_deepslate_double_slab", "minecraft:polished_deepslate_slab", "minecraft:polished_deepslate_stairs", "minecraft:polished_deepslate_wall", "minecraft:calcite", "minecraft:amethyst_block", "minecraft:amethyst_cluster", "minecraft:budding_amethyst", "minecraft:raw_copper_block", "minecraft:raw_gold_block", "minecraft:raw_iron_block", "minecraft:copper_ore", "minecraft:copper_block", "minecraft:cut_copper", "minecraft:cut_copper_slab", "minecraft:cut_copper_stairs", "minecraft:double_cut_copper_slab", "minecraft:exposed_copper", "minecraft:exposed_cut_copper", "minecraft:exposed_cut_copper_slab", "minecraft:exposed_cut_copper_stairs", "minecraft:exposed_double_cut_copper_slab", "minecraft:oxidized_copper", "minecraft:oxidized_cut_copper", "minecraft:oxidized_cut_copper_slab", "minecraft:oxidized_cut_copper_stairs", "minecraft:oxidized_double_cut_copper_slab", "minecraft:weathered_copper", "minecraft:weathered_cut_copper", "minecraft:weathered_cut_copper_slab", "minecraft:weathered_cut_copper_stairs", "minecraft:weathered_double_cut_copper_slab", "minecraft:waxed_copper", "minecraft:waxed_cut_copper", "minecraft:waxed_cut_copper_slab", "minecraft:waxed_cut_copper_stairs", "minecraft:waxed_double_cut_copper_slab", "minecraft:waxed_exposed_copper", "minecraft:waxed_exposed_cut_copper", "minecraft:waxed_exposed_cut_copper_slab", "minecraft:waxed_exposed_cut_copper_stairs", "minecraft:waxed_exposed_double_cut_copper_slab", "minecraft:waxed_oxidized_copper", "minecraft:waxed_oxidized_cut_copper", "minecraft:waxed_oxidized_cut_copper_slab", "minecraft:waxed_oxidized_cut_copper_stairs", "minecraft:waxed_oxidized_double_cut_copper_slab", "minecraft:waxed_weathered_copper", "minecraft:waxed_weathered_cut_copper", "minecraft:waxed_weathered_cut_copper_slab", "minecraft:waxed_weathered_cut_copper_stairs", "minecraft:waxed_weathered_double_cut_copper_slab", "minecraft:dripstone_block", "minecraft:pointed_dripstone", "minecraft:lightning_rod", "minecraft:basalt", "minecraft:tuff", "minecraft:double_stone_slab", "minecraft:double_stone_slab2", "minecraft:double_stone_slab3", "minecraft:double_stone_slab4", "minecraft:blackstone_double_slab", "minecraft:polished_blackstone_brick_double_slab", "minecraft:polished_blackstone_double_slab", "minecraft:mossy_cobblestone_stairs", "minecraft:stonecutter", "minecraft:stonecutter_block", "minecraft:red_nether_brick", "minecraft:red_nether_brick_stairs", "minecraft:normal_stone_stairs", "minecraft:smooth_basalt", "minecraft:stone", "minecraft:cobblestone", "minecraft:mossy_cobblestone", "minecraft:dripstone_block", "minecraft:brick_block", "minecraft:stone_stairs", "minecraft:stone_block_slab", "minecraft:stone_block_slab2", "minecraft:stone_block_slab3", "minecraft:stone_block_slab4", "minecraft:cobblestone_wall", "minecraft:gold_block", "minecraft:iron_block", "minecraft:cauldron", "minecraft:iron_bars", "minecraft:obsidian", "minecraft:coal_ore", "minecraft:deepslate_coal_ore", "minecraft:deepslate_diamond_ore", "minecraft:deepslate_emerald_ore", "minecraft:deepslate_gold_ore", "minecraft:deepslate_iron_ore", "minecraft:deepslate_redstone_ore", "minecraft:lit_deepslate_redstone_ore", "minecraft:diamond_ore", "minecraft:emerald_ore", "minecraft:gold_ore", "minecraft:iron_ore", "minecraft:lapis_ore", "minecraft:redstone_ore", "minecraft:lit_redstone_ore", "minecraft:raw_iron_block", "minecraft:raw_gold_block", "minecraft:raw_copper_block", "minecraft:mud_brick_double_slab", "minecraft:mud_brick_slab", "minecraft:mud_brick_stairs", "minecraft:mud_brick_wall", "minecraft:mud_bricks", "minecraft:hardened_clay", "minecraft:stained_hardened_clay", "minecraft:polished_diorite_stairs", "minecraft:andesite_stairs", "minecraft:polished_andesite_stairs", "minecraft:granite_stairs", "minecraft:polished_granite_stairs", "minecraft:polished_blackstone", "minecraft:chiseled_polished_blackstone", "minecraft:polished_blackstone_brick_stairs", "minecraft:blackstone_stairs", "minecraft:polished_blackstone_brick_wall", "minecraft:gilded_blackstone", "minecraft:coal_block")) {
                pickaxeBlocks.put(name, new DigProperty());
            }
            toolBlocks.put(ItemTags.IS_PICKAXE, pickaxeBlocks);

            for (var name : List.of("minecraft:chest", "minecraft:bookshelf", "minecraft:melon_block", "minecraft:warped_stem", "minecraft:crimson_stem", "minecraft:warped_stem", "minecraft:crimson_stem", "minecraft:crafting_table", "minecraft:crimson_planks", "minecraft:warped_planks", "minecraft:warped_stairs", "minecraft:warped_trapdoor", "minecraft:crimson_stairs", "minecraft:crimson_trapdoor", "minecraft:crimson_door", "minecraft:crimson_double_slab", "minecraft:warped_door", "minecraft:warped_double_slab", "minecraft:crafting_table", "minecraft:composter", "minecraft:cartography_table", "minecraft:lectern", "minecraft:stripped_crimson_stem", "minecraft:stripped_warped_stem", "minecraft:trapdoor", "minecraft:spruce_trapdoor", "minecraft:birch_trapdoor", "minecraft:jungle_trapdoor", "minecraft:acacia_trapdoor", "minecraft:dark_oak_trapdoor", "minecraft:wooden_door", "minecraft:spruce_door", "minecraft:birch_door", "minecraft:jungle_door", "minecraft:acacia_door", "minecraft:dark_oak_door", "minecraft:fence", "minecraft:fence_gate", "minecraft:spruce_fence_gate", "minecraft:birch_fence_gate", "minecraft:jungle_fence_gate", "minecraft:acacia_fence_gate", "minecraft:dark_oak_fence_gate", "minecraft:log", "minecraft:log2", "minecraft:wood", "minecraft:planks", "minecraft:wooden_slab", "minecraft:double_wooden_slab", "minecraft:oak_stairs", "minecraft:spruce_stairs", "minecraft:birch_stairs", "minecraft:jungle_stairs", "minecraft:acacia_stairs", "minecraft:dark_oak_stairs", "minecraft:wall_sign", "minecraft:spruce_wall_sign", "minecraft:birch_wall_sign", "minecraft:jungle_wall_sign", "minecraft:acacia_wall_sign", "minecraft:darkoak_wall_sign", "minecraft:wooden_pressure_plate", "minecraft:spruce_pressure_plate", "minecraft:birch_pressure_plate", "minecraft:jungle_pressure_plate", "minecraft:acacia_pressure_plate", "minecraft:dark_oak_pressure_plate", "minecraft:smithing_table", "minecraft:fletching_table", "minecraft:barrel", "minecraft:beehive", "minecraft:bee_nest", "minecraft:ladder", "minecraft:pumpkin", "minecraft:carved_pumpkin", "minecraft:lit_pumpkin", "minecraft:mangrove_door", "minecraft:mangrove_double_slab", "minecraft:mangrove_fence", "minecraft:mangrove_fence_gate", "minecraft:mangrove_log", "minecraft:mangrove_planks", "minecraft:mangrove_pressure_plate", "minecraft:mangrove_slab", "minecraft:mangrove_stairs", "minecraft:mangrove_wall_sign", "minecraft:mangrove_wood", "minecraft:wooden_button", "minecraft:spruce_button", "minecraft:birch_button", "minecraft:jungle_button", "minecraft:acacia_button", "minecraft:dark_oak_button", "minecraft:mangrove_button", "minecraft:stripped_oak_wood", "minecraft:stripped_spruce_wood", "minecraft:stripped_birch_wood", "minecraft:stripped_jungle_wood", "minecraft:stripped_acacia_wood", "minecraft:stripped_dark_oak_wood", "minecraft:stripped_mangrove_wood", "minecraft:stripped_oak_log", "minecraft:stripped_spruce_log", "minecraft:stripped_birch_log", "minecraft:stripped_jungle_log", "minecraft:stripped_acacia_log", "minecraft:stripped_dark_oak_log", "minecraft:stripped_mangrove_log", "minecraft:standing_sign", "minecraft:spruce_standing_sign", "minecraft:birch_standing_sign", "minecraft:jungle_standing_sign", "minecraft:acacia_standing_sign", "minecraft:darkoak_standing_sign", "minecraft:mangrove_standing_sign", "minecraft:mangrove_trapdoor", "minecraft:warped_standing_sign", "minecraft:warped_wall_sign", "minecraft:crimson_standing_sign", "minecraft:crimson_wall_sign", "minecraft:mangrove_roots")) {
                axeBlocks.put(name, new DigProperty());
            }
            toolBlocks.put(ItemTags.IS_AXE, axeBlocks);

            for (var name : List.of("minecraft:soul_sand", "minecraft:soul_soil", "minecraft:dirt_with_roots", "minecraft:mycelium", "minecraft:podzol", "minecraft:dirt", "minecraft:farmland", "minecraft:sand", "minecraft:gravel", "minecraft:grass", "minecraft:grass_path", "minecraft:snow", "minecraft:mud", "minecraft:packed_mud", "minecraft:clay")) {
                shovelBlocks.put(name, new DigProperty());
            }
            toolBlocks.put(ItemTags.IS_SHOVEL, shovelBlocks);

            for (var name : List.of("minecraft:nether_wart_block", "minecraft:hay_block", "minecraft:target", "minecraft:shroomlight", "minecraft:leaves", "minecraft:leaves2", "minecraft:azalea_leaves_flowered", "minecraft:azalea_leaves", "minecraft:warped_wart_block")) {
                hoeBlocks.put(name, new DigProperty());
            }
            toolBlocks.put(ItemTags.IS_HOE, hoeBlocks);

            for (var name : List.of("minecraft:web", "minecraft:bamboo")) {
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
                System.out.println("speed has an invalid value!");
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
                System.out.println("speed has an invalid value!");
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
                    System.out.println("speed has an invalid value!");
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
            if (property.getSpeed() != null && property.getSpeed() < 0) {
                System.out.println("speed has an invalid value!");
                return this;
            }
            this.blocks.add(new CompoundTag()
                    .putCompound("block", new CompoundTag()
                            .putString("name", blockName)
                            .putCompound("states", property.getStates())
                            .putString("tags", "")
                    )
                    .putInt("speed", property.getSpeed()));
            return this;
        }

        /**
         * 物品的攻击力必须大于0才能生效<p>
         * 标记这个物品是否为武器，如果是，会在物品描述中提示{@code "+X 攻击伤害"}的信息
         * <p>
         * The item's attack damage must be greater than 0<p>
         * define the item is a weapon or not, and if so, it will prompt {@code "+X attack damage"} in the item description
         */

        public ToolBuilder isWeapon() {
            if (this.item.getAttackDamage() > 0 && !this.nbt.getCompound("components").containsCompound("minecraft:weapon")) {
                this.nbt.getCompound("components").putCompound("minecraft:weapon", new CompoundTag());
            }
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
                this.isWeapon();
            } else if (item.isAxe()) {
                this.blockTags.addAll(List.of("'wood'", "'pumpkin'", "'plant'"));
                type = ItemTags.IS_AXE;
                this.nbt.getCompound("components").getCompound("item_properties")
                        .putString("enchantable_slot", "axe");
                this.tag("minecraft:is_axe");
                this.isWeapon();
            } else if (item.isShovel()) {
                this.blockTags.addAll(List.of("'sand'", "'dirt'", "'gravel'", "'grass'", "'snow'"));
                type = ItemTags.IS_SHOVEL;
                this.nbt.getCompound("components").getCompound("item_properties")
                        .putString("enchantable_slot", "shovel");
                this.tag("minecraft:is_shovel");
                this.isWeapon();
            } else if (item.isHoe()) {
                this.nbt.getCompound("components").getCompound("item_properties")
                        .putString("enchantable_slot", "hoe");
                type = ItemTags.IS_HOE;
                this.tag("minecraft:is_hoe");
                this.isWeapon();
            } else if (item.isSword()) {
                this.nbt.getCompound("components").getCompound("item_properties")
                        .putString("enchantable_slot", "sword");
                type = ItemTags.IS_SWORD;
                this.isWeapon();
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

            //添加可挖掘的方块
            for (var k : this.blocks) {
                this.diggerRoot.getList("destroy_speeds", CompoundTag.class).add(k);
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
