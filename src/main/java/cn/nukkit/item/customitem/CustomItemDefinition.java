package cn.nukkit.item.customitem;

import cn.nukkit.block.Block;
import cn.nukkit.item.RuntimeItems;
import cn.nukkit.item.customitem.data.ItemCreativeCategory;
import cn.nukkit.item.customitem.data.RenderOffsets;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import com.google.gson.Gson;
import lombok.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * CustomBlockDefinition用于获得发送给客户端的物品行为包数据。{@link CustomItemDefinition#Builder}中提供的方法都是控制发送给客户端数据，如果需要控制服务端部分行为，请覆写{@link cn.nukkit.item.Item Item}中的方法。
 * <p>
 * CustomBlockDefinition is used to get the data of the item behavior_pack sent to the client. The methods provided in {@link CustomItemDefinition#Builder} control the data sent to the client, if you need to control some of the server-side behavior, please override the methods in {@link cn.nukkit.item.Item Item}.
 */
public record CustomItemDefinition(String identifier, CompoundTag nbt) {
    private static final ConcurrentHashMap<String, Integer> INTERNAL_ALLOCATION_ID_MAP = new ConcurrentHashMap<>();
    private static final AtomicInteger nextRuntimeId = new AtomicInteger(10000);

    /**
     * 自定义工具的定义构造器
     * <p>
     * Definition builder for custom tools
     *
     * @param item             the item
     * @param creativeCategory the creative category
     */
    public static CustomItemDefinition.ToolBuilder toolBuilder(ItemCustomTool item, ItemCreativeCategory creativeCategory) {
        return new CustomItemDefinition.ToolBuilder(item, creativeCategory);
    }

    /**
     * 自定义盔甲的定义构造器
     * <p>
     * Definition builder for custom armor
     *
     * @param item             the item
     * @param creativeCategory the creative category
     */
    public static CustomItemDefinition.ArmorBuilder armorBuilder(ItemCustomArmor item, ItemCreativeCategory creativeCategory) {
        return new CustomItemDefinition.ArmorBuilder(item, creativeCategory);
    }

    /**
     * 自定义食物(药水)的定义构造器
     * <p>
     * Definition builder for custom food or potion
     *
     * @param item             the item
     * @param creativeCategory the creative category
     */
    public static CustomItemDefinition.EdibleBuilder edibleBuilder(ItemCustomEdible item, ItemCreativeCategory creativeCategory) {
        return new CustomItemDefinition.EdibleBuilder(item, creativeCategory);
    }

    public String getDisplayName() {
        return this.nbt.getCompound("components").getCompound("minecraft:display_name").getString("value");
    }

    public String getTexture() {
        return this.nbt.getCompound("components").getCompound("item_properties").getCompound("minecraft:icon").getString("texture");
    }

    public int getRuntimeId() {
        return CustomItemDefinition.INTERNAL_ALLOCATION_ID_MAP.get(identifier);
    }

    public static int getRuntimeId(String identifier) {
        return CustomItemDefinition.INTERNAL_ALLOCATION_ID_MAP.get(identifier);
    }

    public static abstract class Builder {
        protected final String identifier;
        protected final CompoundTag nbt = new CompoundTag()
                .putCompound("components", new CompoundTag()
                        .putCompound("minecraft:display_name", new CompoundTag())
                        .putCompound("item_properties", new CompoundTag()
                                .putCompound("minecraft:icon", new CompoundTag())));


        protected Builder(ItemCustom item, ItemCreativeCategory creativeCategory) {
            this.identifier = item.getNamespaceId();
            //定义材质
            this.nbt.getCompound("components")
                    .getCompound("item_properties")
                    .getCompound("minecraft:icon")
                    .putString("texture", item.getTextureName());
            //定义显示名
            this.nbt.getCompound("components").getCompound("minecraft:display_name").putString("value", item.getName());
            //定义最大堆叠数量
            this.nbt.getCompound("components")
                    .getCompound("item_properties")
                    .putInt("max_stack_size", item.getMaxStackSize());
            //定义在创造栏的大分类
            this.nbt.getCompound("components")
                    .getCompound("item_properties")//1 none
                    .putInt("creative_category", creativeCategory.ordinal() + 1);
        }

        /**
         * 是否允许副手持有
         * <p>
         * Whether to allow the offHand to have
         */
        public Builder allowOffHand(boolean allowOffHand) {
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
        public Builder handEquipped(boolean handEquipped) {
            this.nbt.getCompound("components")
                    .getCompound("item_properties")
                    .putBoolean("hand_equipped", handEquipped);
            return this;
        }

        public Builder foil(boolean foil) {
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
        public Builder creativeGroup(String creativeGroup) {
            if (creativeGroup.isBlank()) {
                System.out.println("displayName has an invalid value!");
                return this;
            }
            this.nbt.getCompound("components")
                    .getCompound("item_properties")
                    .putString("creative_group", creativeGroup);
            return this;
        }

        /**
         * 控制自定义物品在不同视角下的渲染偏移
         * <p>
         * Control rendering offsets of custom items at different viewpoints
         */
        public Builder renderOffsets(@NonNull RenderOffsets renderOffsets) {
            this.nbt.getCompound("components")
                    .putCompound("minecraft:render_offsets", renderOffsets.nbt);
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

        public abstract CustomItemDefinition build();

        protected CustomItemDefinition calculateID() {
            var result = new CustomItemDefinition(identifier, nbt);
            if (!INTERNAL_ALLOCATION_ID_MAP.containsKey(result.identifier())) {
                while (RuntimeItems.getRuntimeMapping().getNamespacedIdByNetworkId(nextRuntimeId.incrementAndGet()) != null)
                    ;
                INTERNAL_ALLOCATION_ID_MAP.put(result.identifier(), nextRuntimeId.get());
            }
            return result;
        }
    }

    public static class ToolBuilder extends Builder {
        private final ItemCustomTool item;
        private Integer speed = null;
        private final Map<String, Integer> blockTags = new HashMap<>();
        private final CompoundTag diggerRoot = new CompoundTag()
                .putBoolean("use_efficiency", true)
                .putList(new ListTag<>("destroy_speeds"));

        private ToolBuilder(ItemCustomTool item, ItemCreativeCategory creativeCategory) {
            super(item, creativeCategory);
            this.item = item;
            this.nbt.getCompound("components")
                    .getCompound("item_properties")
                    .putInt("enchantable_value", item.getEnchantAbility());

            this.nbt.getCompound("components")
                    .getCompound("item_properties")
                    .putFloat("mining_speed", 1f);
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
            this.speed = speed;
            return this;
        }

        public ToolBuilder addExtraBlockTag(@NonNull Block block, int speed) {
            if (speed < 0) {
                System.out.println("speed has an invalid value!");
                return this;
            }
            blockTags.put(block.getPersistenceName(), speed);
            return this;
        }

        public ToolBuilder addExtraBlockTags(@NonNull Map<Block, Integer> blockTags) {
            blockTags.forEach((block, speed) -> {
                if (speed < 0) {
                    System.out.println("speed has an invalid value!");
                    return;
                }
                this.blockTags.put(block.getPersistenceName(), speed);
            });
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
            if (item.isShears() || item.isPickaxe() || item.isHoe() || item.isAxe() || item.isShovel()) {
                var cmp = new CompoundTag();
                var gson = new Gson();

                if (item.isPickaxe()) {
                    cmp.putCompound("block", new CompoundTag()
                                    .putString("tags", "q.any_tag('stone', 'metal', 'diamond_pick_diggable', 'mob_spawner', 'rail', 'slab_block', 'stair_block', 'smooth stone slab', 'sandstone slab', 'cobblestone slab', 'brick slab', 'stone bricks slab', 'quartz slab', 'nether brick slab')")
                            )
                            .putInt("speed", speed);

                    this.diggerRoot.getList("destroy_speeds", CompoundTag.class).add(cmp);

                    //附加附魔信息
                    this.nbt.getCompound("components").getCompound("item_properties")
                            .putString("enchantable_slot", "pickaxe");
                    //附加挖掘速度和挖掘tags
                    this.nbt.getCompound("components")
                            .putCompound("minecraft:digger", this.diggerRoot);
                    for (var name : gson.fromJson("""
                            ["minecraft:ice","minecraft:undyed_shulker_box","minecraft:shulker_box","minecraft:prismarine","minecraft:stone_slab4","minecraft:prismarine_bricks_stairs","minecraft:prismarine_stairs","minecraft:dark_prismarine_stairs","minecraft:anvil","minecraft:bone_block","minecraft:iron_trapdoor","minecraft:nether_brick_fence","minecraft:crying_obsidian","minecraft:magma","minecraft:smoker","minecraft:lit_smoker","minecraft:hopper","minecraft:redstone_block","minecraft:mob_spawner","minecraft:netherite_block","minecraft:smooth_stone","minecraft:diamond_block","minecraft:lapis_block","minecraft:emerald_block","minecraft:enchanting_table","minecraft:end_bricks","minecraft:cracked_polished_blackstone_bricks","minecraft:nether_brick","minecraft:cracked_nether_bricks","minecraft:purpur_block","minecraft:purpur_stairs","minecraft:end_brick_stairs","minecraft:stone_slab","minecraft:stone_slab2","minecraft:stone_slab3","minecraft:stone_brick_stairs","minecraft:mossy_stone_brick_stairs","minecraft:polished_blackstone_bricks","minecraft:polished_blackstone_stairs","minecraft:blackstone_wall","minecraft:blackstone_wall","minecraft:polished_blackstone_wall","minecraft:sandstone","minecraft:grindstone","minecraft:smooth_stone","minecraft:brewing_stand","minecraft:chain","minecraft:lantern","minecraft:soul_lantern","minecraft:ancient_debris","minecraft:quartz_ore","minecraft:netherrack","minecraft:basalt","minecraft:polished_basalt","minecraft:stonebrick","minecraft:warped_nylium","minecraft:crimson_nylium","minecraft:end_stone","minecraft:ender_chest","minecraft:quartz_block","minecraft:quartz_stairs","minecraft:quartz_bricks","minecraft:quartz_stairs","minecraft:nether_gold_ore","minecraft:furnace","minecraft:blast_furnace","minecraft:lit_furnace","minecraft:blast_furnace","minecraft:blackstone","minecraft:concrete","minecraft:deepslate_copper_ore","minecraft:deepslate_lapis_ore","minecraft:chiseled_deepslate","minecraft:cobbled_deepslate","minecraft:cobbled_deepslate_double_slab","minecraft:cobbled_deepslate_slab","minecraft:cobbled_deepslate_stairs","minecraft:cobbled_deepslate_wall","minecraft:cracked_deepslate_bricks","minecraft:cracked_deepslate_tiles","minecraft:deepslate","minecraft:deepslate_brick_double_slab","minecraft:deepslate_brick_slab","minecraft:deepslate_brick_stairs","minecraft:deepslate_brick_wall","minecraft:deepslate_bricks","minecraft:deepslate_tile_double_slab","minecraft:deepslate_tile_slab","minecraft:deepslate_tile_stairs","minecraft:deepslate_tile_wall","minecraft:deepslate_tiles","minecraft:infested_deepslate","minecraft:polished_deepslate","minecraft:polished_deepslate_double_slab","minecraft:polished_deepslate_slab","minecraft:polished_deepslate_stairs","minecraft:polished_deepslate_wall","minecraft:calcite","minecraft:amethyst_block","minecraft:amethyst_cluster","minecraft:budding_amethyst","minecraft:raw_copper_block","minecraft:raw_gold_block","minecraft:raw_iron_block","minecraft:copper_ore","minecraft:copper_block","minecraft:cut_copper","minecraft:cut_copper_slab","minecraft:cut_copper_stairs","minecraft:double_cut_copper_slab","minecraft:exposed_copper","minecraft:exposed_cut_copper","minecraft:exposed_cut_copper_slab","minecraft:exposed_cut_copper_stairs","minecraft:exposed_double_cut_copper_slab","minecraft:oxidized_copper","minecraft:oxidized_cut_copper","minecraft:oxidized_cut_copper_slab","minecraft:oxidized_cut_copper_stairs","minecraft:oxidized_double_cut_copper_slab","minecraft:weathered_copper","minecraft:weathered_cut_copper","minecraft:weathered_cut_copper_slab","minecraft:weathered_cut_copper_stairs","minecraft:weathered_double_cut_copper_slab","minecraft:waxed_copper","minecraft:waxed_cut_copper","minecraft:waxed_cut_copper_slab","minecraft:waxed_cut_copper_stairs","minecraft:waxed_double_cut_copper_slab","minecraft:waxed_exposed_copper","minecraft:waxed_exposed_cut_copper","minecraft:waxed_exposed_cut_copper_slab","minecraft:waxed_exposed_cut_copper_stairs","minecraft:waxed_exposed_double_cut_copper_slab","minecraft:waxed_oxidized_copper","minecraft:waxed_oxidized_cut_copper","minecraft:waxed_oxidized_cut_copper_slab","minecraft:waxed_oxidized_cut_copper_stairs","minecraft:waxed_oxidized_double_cut_copper_slab","minecraft:waxed_weathered_copper","minecraft:waxed_weathered_cut_copper","minecraft:waxed_weathered_cut_copper_slab","minecraft:waxed_weathered_cut_copper_stairs","minecraft:waxed_weathered_double_cut_copper_slab","minecraft:dripstone_block","minecraft:pointed_dripstone","minecraft:lightning_rod","minecraft:basalt","minecraft:tuff","minecraft:double_stone_slab","minecraft:double_stone_slab2","minecraft:double_stone_slab3","minecraft:double_stone_slab4","minecraft:blackstone_double_slab","minecraft:polished_blackstone_brick_double_slab","minecraft:polished_blackstone_double_slab","minecraft:mossy_cobblestone_stairs","minecraft:stonecutter","minecraft:stonecutter_block","minecraft:red_nether_brick","minecraft:red_nether_brick_stairs","minecraft:normal_stone_stairs","minecraft:smooth_basalt","minecraft:stone","minecraft:cobblestone","minecraft:mossy_cobblestone","minecraft:dripstone_block","minecraft:brick_block","minecraft:stone_stairs","minecraft:stone_block_slab","minecraft:stone_block_slab2","minecraft:stone_block_slab3","minecraft:stone_block_slab4","minecraft:cobblestone_wall","minecraft:gold_block","minecraft:iron_block","minecraft:cauldron","minecraft:iron_bars","minecraft:obsidian","minecraft:coal_ore","minecraft:deepslate_coal_ore","minecraft:deepslate_diamond_ore","minecraft:deepslate_emerald_ore","minecraft:deepslate_gold_ore","minecraft:deepslate_iron_ore","minecraft:deepslate_redstone_ore","minecraft:lit_deepslate_redstone_ore","minecraft:diamond_ore","minecraft:emerald_ore","minecraft:gold_ore","minecraft:iron_ore","minecraft:lapis_ore","minecraft:redstone_ore","minecraft:lit_redstone_ore","minecraft:raw_iron_block","minecraft:raw_gold_block","minecraft:raw_copper_block","minecraft:mud_brick_double_slab","minecraft:mud_brick_slab","minecraft:mud_brick_stairs","minecraft:mud_brick_wall","minecraft:mud_bricks","minecraft:hardened_clay","minecraft:stained_hardened_clay","minecraft:polished_diorite_stairs","minecraft:andesite_stairs","minecraft:polished_andesite_stairs","minecraft:granite_stairs","minecraft:polished_granite_stairs","minecraft:polished_blackstone","minecraft:chiseled_polished_blackstone","minecraft:polished_blackstone_brick_stairs","minecraft:blackstone_stairs","minecraft:polished_blackstone_brick_wall","minecraft:gilded_blackstone","minecraft:coal_block"]
                            """, List.class)) {
                        this.blockTags.put((String) name, speed);
                    }
                } else if (item.isAxe()) {
                    cmp.putCompound("block",
                                    new CompoundTag()
                                            .putString("tags", "q.any_tag('wood', 'pumpkin', 'plant')"))
                            .putInt("speed", speed);
                    this.diggerRoot.getList("destroy_speeds", CompoundTag.class).add(cmp);
                    this.nbt.getCompound("components").getCompound("item_properties")
                            .putString("enchantable_slot", "axe");
                    this.nbt.getCompound("components")
                            .putCompound("minecraft:digger", this.diggerRoot);

                    for (var name : gson.fromJson("""
                            ["minecraft:chest","minecraft:bookshelf","minecraft:melon_block","minecraft:warped_stem","minecraft:crimson_stem","minecraft:warped_stem","minecraft:crimson_stem","minecraft:crafting_table","minecraft:crimson_planks","minecraft:warped_planks","minecraft:warped_stairs","minecraft:warped_trapdoor","minecraft:crimson_stairs","minecraft:crimson_trapdoor","minecraft:crimson_door","minecraft:crimson_double_slab","minecraft:warped_door","minecraft:warped_double_slab","minecraft:crafting_table","minecraft:composter","minecraft:cartography_table","minecraft:lectern","minecraft:stripped_crimson_stem","minecraft:stripped_warped_stem","minecraft:trapdoor","minecraft:spruce_trapdoor","minecraft:birch_trapdoor","minecraft:jungle_trapdoor","minecraft:acacia_trapdoor","minecraft:dark_oak_trapdoor","minecraft:wooden_door","minecraft:spruce_door","minecraft:birch_door","minecraft:jungle_door","minecraft:acacia_door","minecraft:dark_oak_door","minecraft:fence","minecraft:fence_gate","minecraft:spruce_fence_gate","minecraft:birch_fence_gate","minecraft:jungle_fence_gate","minecraft:acacia_fence_gate","minecraft:dark_oak_fence_gate","minecraft:log","minecraft:log2","minecraft:wood","minecraft:planks","minecraft:wooden_slab","minecraft:double_wooden_slab","minecraft:oak_stairs","minecraft:spruce_stairs","minecraft:birch_stairs","minecraft:jungle_stairs","minecraft:acacia_stairs","minecraft:dark_oak_stairs","minecraft:wall_sign","minecraft:spruce_wall_sign","minecraft:birch_wall_sign","minecraft:jungle_wall_sign","minecraft:acacia_wall_sign","minecraft:darkoak_wall_sign","minecraft:wooden_pressure_plate","minecraft:spruce_pressure_plate","minecraft:birch_pressure_plate","minecraft:jungle_pressure_plate","minecraft:acacia_pressure_plate","minecraft:dark_oak_pressure_plate","minecraft:smithing_table","minecraft:fletching_table","minecraft:barrel","minecraft:beehive","minecraft:bee_nest","minecraft:ladder","minecraft:pumpkin","minecraft:carved_pumpkin","minecraft:lit_pumpkin","minecraft:mangrove_door","minecraft:mangrove_double_slab","minecraft:mangrove_fence","minecraft:mangrove_fence_gate","minecraft:mangrove_log","minecraft:mangrove_planks","minecraft:mangrove_pressure_plate","minecraft:mangrove_slab","minecraft:mangrove_stairs","minecraft:mangrove_wall_sign","minecraft:mangrove_wood","minecraft:wooden_button","minecraft:spruce_button","minecraft:birch_button","minecraft:jungle_button","minecraft:acacia_button","minecraft:dark_oak_button","minecraft:mangrove_button","minecraft:stripped_oak_wood","minecraft:stripped_spruce_wood","minecraft:stripped_birch_wood","minecraft:stripped_jungle_wood","minecraft:stripped_acacia_wood","minecraft:stripped_dark_oak_wood","minecraft:stripped_mangrove_wood","minecraft:stripped_oak_log","minecraft:stripped_spruce_log","minecraft:stripped_birch_log","minecraft:stripped_jungle_log","minecraft:stripped_acacia_log","minecraft:stripped_dark_oak_log","minecraft:stripped_mangrove_log","minecraft:standing_sign","minecraft:spruce_standing_sign","minecraft:birch_standing_sign","minecraft:jungle_standing_sign","minecraft:acacia_standing_sign","minecraft:darkoak_standing_sign","minecraft:mangrove_standing_sign","minecraft:mangrove_trapdoor","minecraft:warped_standing_sign","minecraft:warped_wall_sign","minecraft:crimson_standing_sign","minecraft:crimson_wall_sign","minecraft:mangrove_roots"]
                            """, List.class)) {
                        this.blockTags.put((String) name, speed);
                    }
                } else if (item.isShovel()) {
                    cmp.putCompound("block",
                                    new CompoundTag()
                                            .putString("tags", "q.any_tag('sand', 'dirt', 'gravel', 'grass', 'snow')"))
                            .putInt("speed", speed);
                    this.diggerRoot.getList("destroy_speeds", CompoundTag.class).add(cmp);
                    this.nbt.getCompound("components").getCompound("item_properties")
                            .putString("enchantable_slot", "shovel");
                    this.nbt.getCompound("components")
                            .putCompound("minecraft:digger", this.diggerRoot);

                    for (var name : gson.fromJson("""
                            ["minecraft:soul_sand","minecraft:soul_soil","minecraft:dirt_with_roots","minecraft:mycelium","minecraft:podzol","minecraft:dirt","minecraft:farmland","minecraft:sand","minecraft:gravel","minecraft:grass","minecraft:grass_path","minecraft:snow","minecraft:mud","minecraft:packed_mud","minecraft:clay"]
                            """, List.class)) {
                        this.blockTags.put((String) name, speed);
                    }
                } else if (item.isHoe()) {
                    this.nbt.getCompound("components").getCompound("item_properties")
                            .putString("enchantable_slot", "hoe");

                    for (var name : gson.fromJson("""
                            ["minecraft:nether_wart_block","minecraft:hay_block","minecraft:target","minecraft:shroomlight","minecraft:leaves","minecraft:leaves2","minecraft:azalea_leaves_flowered","minecraft:azalea_leaves","minecraft:warped_wart_block"]
                            """, List.class)) {
                        this.blockTags.put((String) name, speed);
                    }
                }
                //添加一些额外的挖掘tags
                for (var k : this.blockTags.entrySet()) {
                    this.diggerRoot.getList("destroy_speeds", CompoundTag.class)
                            .add(new CompoundTag()
                                    .putString("block", k.getKey())
                                    .putInt("speed", k.getValue()));
                }
            } else if (item.isSword()) {
                this.nbt.getCompound("components").getCompound("item_properties")
                        .putString("enchantable_slot", "sword");
            }
            return calculateID();
        }
    }

    public static class ArmorBuilder extends Builder {
        private final ItemCustomArmor item;

        private ArmorBuilder(ItemCustomArmor item, ItemCreativeCategory creativeCategory) {
            super(item, creativeCategory);
            this.item = item;
            this.nbt.getCompound("components")
                    .getCompound("item_properties")
                    .putInt("enchantable_value", item.getEnchantAbility());
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
                return null;
            }
            return calculateID();
        }
    }

    public static class EdibleBuilder extends Builder {
        private EdibleBuilder(ItemCustomEdible item, ItemCreativeCategory creativeCategory) {
            super(item, creativeCategory);
            if (this.nbt.getCompound("components").contains("minecraft:food")) {
                this.nbt.getCompound("components").getCompound("minecraft:food").putBoolean("can_always_eat", item.canAlwaysEat());
            } else {
                this.nbt.getCompound("components").putCompound("minecraft:food", new CompoundTag().putBoolean("can_always_eat", item.canAlwaysEat()));
            }

            this.nbt.getCompound("components").getCompound("item_properties")
                    .putInt("use_duration", item.getEatTick());

            this.nbt.getCompound("components").getCompound("item_properties")
                    .putInt("use_animation", item.isDrink() ? 2 : 1);
        }

        @Override
        public CustomItemDefinition build() {
            return calculateID();
        }
    }
}
