package cn.nukkit.education;

import cn.nukkit.block.BlockID;
import cn.nukkit.education.block.*;
import cn.nukkit.education.block.elements.*;
import cn.nukkit.education.block.glass.*;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.network.protocol.types.inventory.creative.CreativeCustomGroups;
import cn.nukkit.network.protocol.types.inventory.creative.CreativeItemCategory;
import cn.nukkit.network.protocol.types.inventory.creative.CreativeItemData;
import cn.nukkit.registry.CreativeGroupsRegistry;
import cn.nukkit.registry.CreativeItemRegistry;
import cn.nukkit.registry.Registries;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStreamReader;
import java.util.*;

@Slf4j
public class Education implements BlockID, ItemID {
    @Getter
    private static boolean enabled = false;

    public static final Set<String> eduBlocks = Set.of(
            "minecraft:element_0",
            "minecraft:element_1",
            "minecraft:element_10",
            "minecraft:element_100",
            "minecraft:element_101",
            "minecraft:element_102",
            "minecraft:element_103",
            "minecraft:element_104",
            "minecraft:element_105",
            "minecraft:element_106",
            "minecraft:element_107",
            "minecraft:element_108",
            "minecraft:element_109",
            "minecraft:element_11",
            "minecraft:element_110",
            "minecraft:element_111",
            "minecraft:element_112",
            "minecraft:element_113",
            "minecraft:element_114",
            "minecraft:element_115",
            "minecraft:element_116",
            "minecraft:element_117",
            "minecraft:element_118",
            "minecraft:element_12",
            "minecraft:element_13",
            "minecraft:element_14",
            "minecraft:element_15",
            "minecraft:element_16",
            "minecraft:element_17",
            "minecraft:element_18",
            "minecraft:element_19",
            "minecraft:element_2",
            "minecraft:element_20",
            "minecraft:element_21",
            "minecraft:element_22",
            "minecraft:element_23",
            "minecraft:element_24",
            "minecraft:element_25",
            "minecraft:element_26",
            "minecraft:element_27",
            "minecraft:element_28",
            "minecraft:element_29",
            "minecraft:element_3",
            "minecraft:element_30",
            "minecraft:element_31",
            "minecraft:element_32",
            "minecraft:element_33",
            "minecraft:element_34",
            "minecraft:element_35",
            "minecraft:element_36",
            "minecraft:element_37",
            "minecraft:element_38",
            "minecraft:element_39",
            "minecraft:element_4",
            "minecraft:element_40",
            "minecraft:element_41",
            "minecraft:element_42",
            "minecraft:element_43",
            "minecraft:element_44",
            "minecraft:element_45",
            "minecraft:element_46",
            "minecraft:element_47",
            "minecraft:element_48",
            "minecraft:element_49",
            "minecraft:element_5",
            "minecraft:element_50",
            "minecraft:element_51",
            "minecraft:element_52",
            "minecraft:element_53",
            "minecraft:element_54",
            "minecraft:element_55",
            "minecraft:element_56",
            "minecraft:element_57",
            "minecraft:element_58",
            "minecraft:element_59",
            "minecraft:element_6",
            "minecraft:element_60",
            "minecraft:element_61",
            "minecraft:element_62",
            "minecraft:element_63",
            "minecraft:element_64",
            "minecraft:element_65",
            "minecraft:element_66",
            "minecraft:element_67",
            "minecraft:element_68",
            "minecraft:element_69",
            "minecraft:element_7",
            "minecraft:element_70",
            "minecraft:element_71",
            "minecraft:element_72",
            "minecraft:element_73",
            "minecraft:element_74",
            "minecraft:element_75",
            "minecraft:element_76",
            "minecraft:element_77",
            "minecraft:element_78",
            "minecraft:element_79",
            "minecraft:element_8",
            "minecraft:element_80",
            "minecraft:element_81",
            "minecraft:element_82",
            "minecraft:element_83",
            "minecraft:element_84",
            "minecraft:element_85",
            "minecraft:element_86",
            "minecraft:element_87",
            "minecraft:element_88",
            "minecraft:element_89",
            "minecraft:element_9",
            "minecraft:element_90",
            "minecraft:element_91",
            "minecraft:element_92",
            "minecraft:element_93",
            "minecraft:element_94",
            "minecraft:element_95",
            "minecraft:element_96",
            "minecraft:element_97",
            "minecraft:element_98",
            "minecraft:element_99",
            "minecraft:hard_black_stained_glass",
            "minecraft:hard_black_stained_glass_pane",
            "minecraft:hard_blue_stained_glass",
            "minecraft:hard_blue_stained_glass_pane",
            "minecraft:hard_brown_stained_glass",
            "minecraft:hard_brown_stained_glass_pane",
            "minecraft:hard_cyan_stained_glass",
            "minecraft:hard_cyan_stained_glass_pane",
            "minecraft:hard_glass",
            "minecraft:hard_glass_pane",
            "minecraft:hard_gray_stained_glass",
            "minecraft:hard_gray_stained_glass_pane",
            "minecraft:hard_green_stained_glass",
            "minecraft:hard_green_stained_glass_pane",
            "minecraft:hard_light_blue_stained_glass",
            "minecraft:hard_light_blue_stained_glass_pane",
            "minecraft:hard_light_gray_stained_glass",
            "minecraft:hard_light_gray_stained_glass_pane",
            "minecraft:hard_lime_stained_glass",
            "minecraft:hard_lime_stained_glass_pane",
            "minecraft:hard_magenta_stained_glass",
            "minecraft:hard_magenta_stained_glass_pane",
            "minecraft:hard_orange_stained_glass",
            "minecraft:hard_orange_stained_glass_pane",
            "minecraft:hard_pink_stained_glass",
            "minecraft:hard_pink_stained_glass_pane",
            "minecraft:hard_purple_stained_glass",
            "minecraft:hard_purple_stained_glass_pane",
            "minecraft:hard_red_stained_glass",
            "minecraft:hard_red_stained_glass_pane",
            "minecraft:hard_white_stained_glass",
            "minecraft:hard_white_stained_glass_pane",
            "minecraft:hard_yellow_stained_glass",
            "minecraft:hard_yellow_stained_glass_pane",
            "minecraft:camera",
            "minecraft:chemical_heat",
            "minecraft:compound_creator",
            "minecraft:element_constructor",
            "minecraft:lab_table",
            "minecraft:material_reducer",
            "minecraft:colored_torch_purple",
            "minecraft:colored_torch_blue",
            "minecraft:colored_torch_red",
            "minecraft:colored_torch_green",
            "minecraft:underwater_torch",
            "minecraft:underwater_tnt",

            //NEED TO BE IMPLEMENTED:
            "minecraft:chalkboard"
    );

    public static void enable() {
        enabled = true;

        try {
            registerBlocks();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void registerBlocks() throws Exception {
        Registries.BLOCK.register(ELEMENT_0, BlockElement0.class);
        Registries.BLOCK.register(ELEMENT_1, BlockElement1.class);
        Registries.BLOCK.register(ELEMENT_2, BlockElement2.class);
        Registries.BLOCK.register(ELEMENT_3, BlockElement3.class);
        Registries.BLOCK.register(ELEMENT_4, BlockElement4.class);
        Registries.BLOCK.register(ELEMENT_5, BlockElement5.class);
        Registries.BLOCK.register(ELEMENT_6, BlockElement6.class);
        Registries.BLOCK.register(ELEMENT_7, BlockElement7.class);
        Registries.BLOCK.register(ELEMENT_8, BlockElement8.class);
        Registries.BLOCK.register(ELEMENT_9, BlockElement9.class);
        Registries.BLOCK.register(ELEMENT_10, BlockElement10.class);
        Registries.BLOCK.register(ELEMENT_11, BlockElement11.class);
        Registries.BLOCK.register(ELEMENT_12, BlockElement12.class);
        Registries.BLOCK.register(ELEMENT_13, BlockElement13.class);
        Registries.BLOCK.register(ELEMENT_14, BlockElement14.class);
        Registries.BLOCK.register(ELEMENT_15, BlockElement15.class);
        Registries.BLOCK.register(ELEMENT_16, BlockElement16.class);
        Registries.BLOCK.register(ELEMENT_17, BlockElement17.class);
        Registries.BLOCK.register(ELEMENT_18, BlockElement18.class);
        Registries.BLOCK.register(ELEMENT_19, BlockElement19.class);
        Registries.BLOCK.register(ELEMENT_20, BlockElement20.class);
        Registries.BLOCK.register(ELEMENT_21, BlockElement21.class);
        Registries.BLOCK.register(ELEMENT_22, BlockElement22.class);
        Registries.BLOCK.register(ELEMENT_23, BlockElement23.class);
        Registries.BLOCK.register(ELEMENT_24, BlockElement24.class);
        Registries.BLOCK.register(ELEMENT_25, BlockElement25.class);
        Registries.BLOCK.register(ELEMENT_26, BlockElement26.class);
        Registries.BLOCK.register(ELEMENT_27, BlockElement27.class);
        Registries.BLOCK.register(ELEMENT_28, BlockElement28.class);
        Registries.BLOCK.register(ELEMENT_29, BlockElement29.class);
        Registries.BLOCK.register(ELEMENT_30, BlockElement30.class);
        Registries.BLOCK.register(ELEMENT_31, BlockElement31.class);
        Registries.BLOCK.register(ELEMENT_32, BlockElement32.class);
        Registries.BLOCK.register(ELEMENT_33, BlockElement33.class);
        Registries.BLOCK.register(ELEMENT_34, BlockElement34.class);
        Registries.BLOCK.register(ELEMENT_35, BlockElement35.class);
        Registries.BLOCK.register(ELEMENT_36, BlockElement36.class);
        Registries.BLOCK.register(ELEMENT_37, BlockElement37.class);
        Registries.BLOCK.register(ELEMENT_38, BlockElement38.class);
        Registries.BLOCK.register(ELEMENT_39, BlockElement39.class);
        Registries.BLOCK.register(ELEMENT_40, BlockElement40.class);
        Registries.BLOCK.register(ELEMENT_41, BlockElement41.class);
        Registries.BLOCK.register(ELEMENT_42, BlockElement42.class);
        Registries.BLOCK.register(ELEMENT_43, BlockElement43.class);
        Registries.BLOCK.register(ELEMENT_44, BlockElement44.class);
        Registries.BLOCK.register(ELEMENT_45, BlockElement45.class);
        Registries.BLOCK.register(ELEMENT_46, BlockElement46.class);
        Registries.BLOCK.register(ELEMENT_47, BlockElement47.class);
        Registries.BLOCK.register(ELEMENT_48, BlockElement48.class);
        Registries.BLOCK.register(ELEMENT_49, BlockElement49.class);
        Registries.BLOCK.register(ELEMENT_50, BlockElement50.class);
        Registries.BLOCK.register(ELEMENT_51, BlockElement51.class);
        Registries.BLOCK.register(ELEMENT_52, BlockElement52.class);
        Registries.BLOCK.register(ELEMENT_53, BlockElement53.class);
        Registries.BLOCK.register(ELEMENT_54, BlockElement54.class);
        Registries.BLOCK.register(ELEMENT_55, BlockElement55.class);
        Registries.BLOCK.register(ELEMENT_56, BlockElement56.class);
        Registries.BLOCK.register(ELEMENT_57, BlockElement57.class);
        Registries.BLOCK.register(ELEMENT_58, BlockElement58.class);
        Registries.BLOCK.register(ELEMENT_59, BlockElement59.class);
        Registries.BLOCK.register(ELEMENT_60, BlockElement60.class);
        Registries.BLOCK.register(ELEMENT_61, BlockElement61.class);
        Registries.BLOCK.register(ELEMENT_62, BlockElement62.class);
        Registries.BLOCK.register(ELEMENT_63, BlockElement63.class);
        Registries.BLOCK.register(ELEMENT_64, BlockElement64.class);
        Registries.BLOCK.register(ELEMENT_65, BlockElement65.class);
        Registries.BLOCK.register(ELEMENT_66, BlockElement66.class);
        Registries.BLOCK.register(ELEMENT_67, BlockElement67.class);
        Registries.BLOCK.register(ELEMENT_68, BlockElement68.class);
        Registries.BLOCK.register(ELEMENT_69, BlockElement69.class);
        Registries.BLOCK.register(ELEMENT_70, BlockElement70.class);
        Registries.BLOCK.register(ELEMENT_71, BlockElement71.class);
        Registries.BLOCK.register(ELEMENT_72, BlockElement72.class);
        Registries.BLOCK.register(ELEMENT_73, BlockElement73.class);
        Registries.BLOCK.register(ELEMENT_74, BlockElement74.class);
        Registries.BLOCK.register(ELEMENT_75, BlockElement75.class);
        Registries.BLOCK.register(ELEMENT_76, BlockElement76.class);
        Registries.BLOCK.register(ELEMENT_77, BlockElement77.class);
        Registries.BLOCK.register(ELEMENT_78, BlockElement78.class);
        Registries.BLOCK.register(ELEMENT_79, BlockElement79.class);
        Registries.BLOCK.register(ELEMENT_80, BlockElement80.class);
        Registries.BLOCK.register(ELEMENT_81, BlockElement81.class);
        Registries.BLOCK.register(ELEMENT_82, BlockElement82.class);
        Registries.BLOCK.register(ELEMENT_83, BlockElement83.class);
        Registries.BLOCK.register(ELEMENT_84, BlockElement84.class);
        Registries.BLOCK.register(ELEMENT_85, BlockElement85.class);
        Registries.BLOCK.register(ELEMENT_86, BlockElement86.class);
        Registries.BLOCK.register(ELEMENT_87, BlockElement87.class);
        Registries.BLOCK.register(ELEMENT_88, BlockElement88.class);
        Registries.BLOCK.register(ELEMENT_89, BlockElement89.class);
        Registries.BLOCK.register(ELEMENT_90, BlockElement90.class);
        Registries.BLOCK.register(ELEMENT_91, BlockElement91.class);
        Registries.BLOCK.register(ELEMENT_92, BlockElement92.class);
        Registries.BLOCK.register(ELEMENT_93, BlockElement93.class);
        Registries.BLOCK.register(ELEMENT_94, BlockElement94.class);
        Registries.BLOCK.register(ELEMENT_95, BlockElement95.class);
        Registries.BLOCK.register(ELEMENT_96, BlockElement96.class);
        Registries.BLOCK.register(ELEMENT_97, BlockElement97.class);
        Registries.BLOCK.register(ELEMENT_98, BlockElement98.class);
        Registries.BLOCK.register(ELEMENT_99, BlockElement99.class);
        Registries.BLOCK.register(ELEMENT_100, BlockElement100.class);
        Registries.BLOCK.register(ELEMENT_101, BlockElement101.class);
        Registries.BLOCK.register(ELEMENT_102, BlockElement102.class);
        Registries.BLOCK.register(ELEMENT_103, BlockElement103.class);
        Registries.BLOCK.register(ELEMENT_104, BlockElement104.class);
        Registries.BLOCK.register(ELEMENT_105, BlockElement105.class);
        Registries.BLOCK.register(ELEMENT_106, BlockElement106.class);
        Registries.BLOCK.register(ELEMENT_107, BlockElement107.class);
        Registries.BLOCK.register(ELEMENT_108, BlockElement108.class);
        Registries.BLOCK.register(ELEMENT_109, BlockElement109.class);
        Registries.BLOCK.register(ELEMENT_110, BlockElement110.class);
        Registries.BLOCK.register(ELEMENT_111, BlockElement111.class);
        Registries.BLOCK.register(ELEMENT_112, BlockElement112.class);
        Registries.BLOCK.register(ELEMENT_113, BlockElement113.class);
        Registries.BLOCK.register(ELEMENT_114, BlockElement114.class);
        Registries.BLOCK.register(ELEMENT_115, BlockElement115.class);
        Registries.BLOCK.register(ELEMENT_116, BlockElement116.class);
        Registries.BLOCK.register(ELEMENT_117, BlockElement117.class);
        Registries.BLOCK.register(ELEMENT_118, BlockElement118.class);
        Registries.BLOCK.register(HARD_GLASS, BlockHardGlass.class);
        Registries.BLOCK.register(HARD_GLASS_PANE, BlockHardGlassPane.class);
        Registries.BLOCK.register(HARD_BLACK_STAINED_GLASS, BlockHardBlackStainedGlass.class);
        Registries.BLOCK.register(HARD_BLACK_STAINED_GLASS_PANE, BlockHardBlackStainedGlassPane.class);
        Registries.BLOCK.register(HARD_BLUE_STAINED_GLASS, BlockHardBlueStainedGlass.class);
        Registries.BLOCK.register(HARD_BLUE_STAINED_GLASS_PANE, BlockHardBlueStainedGlassPane.class);
        Registries.BLOCK.register(HARD_BROWN_STAINED_GLASS, BlockHardBrownStainedGlass.class);
        Registries.BLOCK.register(HARD_BROWN_STAINED_GLASS_PANE, BlockHardBrownStainedGlassPane.class);
        Registries.BLOCK.register(HARD_CYAN_STAINED_GLASS, BlockHardCyanStainedGlass.class);
        Registries.BLOCK.register(HARD_CYAN_STAINED_GLASS_PANE, BlockHardCyanStainedGlassPane.class);
        Registries.BLOCK.register(HARD_GRAY_STAINED_GLASS, BlockHardGrayStainedGlass.class);
        Registries.BLOCK.register(HARD_GRAY_STAINED_GLASS_PANE, BlockHardGrayStainedGlassPane.class);
        Registries.BLOCK.register(HARD_GREEN_STAINED_GLASS, BlockHardGreenStainedGlass.class);
        Registries.BLOCK.register(HARD_GREEN_STAINED_GLASS_PANE, BlockHardGreenStainedGlassPane.class);
        Registries.BLOCK.register(HARD_LIGHT_BLUE_STAINED_GLASS, BlockHardLightBlueStainedGlass.class);
        Registries.BLOCK.register(HARD_LIGHT_BLUE_STAINED_GLASS_PANE, BlockHardLightBlueStainedGlassPane.class);
        Registries.BLOCK.register(HARD_LIGHT_GRAY_STAINED_GLASS, BlockHardLightGrayStainedGlass.class);
        Registries.BLOCK.register(HARD_LIGHT_GRAY_STAINED_GLASS_PANE, BlockHardLightGrayStainedGlassPane.class);
        Registries.BLOCK.register(HARD_LIME_STAINED_GLASS, BlockHardLimeStainedGlass.class);
        Registries.BLOCK.register(HARD_LIME_STAINED_GLASS_PANE, BlockHardLimeStainedGlassPane.class);
        Registries.BLOCK.register(HARD_MAGENTA_STAINED_GLASS, BlockHardMagentaStainedGlass.class);
        Registries.BLOCK.register(HARD_MAGENTA_STAINED_GLASS_PANE, BlockHardMagentaStainedGlassPane.class);
        Registries.BLOCK.register(HARD_ORANGE_STAINED_GLASS, BlockHardOrangeStainedGlass.class);
        Registries.BLOCK.register(HARD_ORANGE_STAINED_GLASS_PANE, BlockHardOrangeStainedGlassPane.class);
        Registries.BLOCK.register(HARD_PINK_STAINED_GLASS, BlockHardPinkStainedGlass.class);
        Registries.BLOCK.register(HARD_PINK_STAINED_GLASS_PANE, BlockHardPinkStainedGlassPane.class);
        Registries.BLOCK.register(HARD_PURPLE_STAINED_GLASS, BlockHardPurpleStainedGlass.class);
        Registries.BLOCK.register(HARD_PURPLE_STAINED_GLASS_PANE, BlockHardPurpleStainedGlassPane.class);
        Registries.BLOCK.register(HARD_RED_STAINED_GLASS, BlockHardRedStainedGlass.class);
        Registries.BLOCK.register(HARD_RED_STAINED_GLASS_PANE, BlockHardRedStainedGlassPane.class);
        Registries.BLOCK.register(HARD_WHITE_STAINED_GLASS, BlockHardWhiteStainedGlass.class);
        Registries.BLOCK.register(HARD_WHITE_STAINED_GLASS_PANE, BlockHardWhiteStainedGlassPane.class);
        Registries.BLOCK.register(HARD_YELLOW_STAINED_GLASS, BlockHardYellowStainedGlass.class);
        Registries.BLOCK.register(HARD_YELLOW_STAINED_GLASS_PANE, BlockHardYellowStainedGlassPane.class);
        Registries.BLOCK.register(CAMERA, BlockCamera.class);
        Registries.BLOCK.register(CHEMICAL_HEAT, BlockChemicalHeat.class);
        Registries.BLOCK.register(COMPOUND_CREATOR, BlockCompoundCreator.class);
        Registries.BLOCK.register(ELEMENT_CONSTRUCTOR, BlockElementConstructor.class);
        Registries.BLOCK.register(LAB_TABLE, BlockLabTable.class);
        Registries.BLOCK.register(MATERIAL_REDUCER, BlockMaterialReducer.class);
        Registries.BLOCK.register(COLORED_TORCH_BLUE, BlockColoredTorchBlue.class);
        Registries.BLOCK.register(COLORED_TORCH_PURPLE, BlockColoredTorchPurple.class);
        Registries.BLOCK.register(COLORED_TORCH_RED, BlockColoredTorchRed.class);
        Registries.BLOCK.register(COLORED_TORCH_GREEN, BlockColoredTorchGreen.class);
        Registries.BLOCK.register(UNDERWATER_TORCH, BlockUnderwaterTorch.class);
        Registries.BLOCK.register(UNDERWATER_TNT, BlockUnderwaterTNT.class);
    }

    private static void addCreativeGroup(String name, String icon) {
        Item item = Item.get(icon, 0, 1, null, false);
        CreativeItemRegistry.ITEM_DATA.add(new CreativeItemData(item, 0));
        CreativeCustomGroups.define(CreativeItemCategory.CONSTRUCTION, name, icon);
    }

    public static void registerCreative() {
        if(!enabled) return;

        Map<String, Integer> groups = new HashMap<>();

        try (var input = Education.class.getClassLoader().getResourceAsStream("gamedata/unknown/creativeitems_edu.json")) {
            if(input == null) return;

            Map<String, Object> data;
            try (InputStreamReader reader = new InputStreamReader(input)) {
                data = new Gson().fromJson(reader, new TypeToken<Map<String, Object>>() {}.getType());
            }
            List<String> tmpGroups = new ArrayList<>();

            List<Map<String, Object>> groupData = asStringObjectMapList(data.get("groups"));
            for (Map<String, Object> tag : groupData) {
                String name = (String) tag.getOrDefault("name", null);
                String icon = (String) tag.getOrDefault("icon", null);

                addCreativeGroup(name, icon);
                tmpGroups.add(name);
            }

            CreativeGroupsRegistry.register();

            for(String group : tmpGroups) {
                groups.put(group, Registries.CREATIVE.resolveGroupIndexFromGroupName(group));
                CreativeCustomGroups.getDefinedGroups().stream().filter(d -> d.getName().equalsIgnoreCase(group)).findFirst().flatMap(def -> CreativeItemRegistry.ITEM_DATA.stream().filter(d -> d.getItem().getId().equalsIgnoreCase(def.getIconId())).findFirst()).ifPresent(entry -> {
                    CreativeItemRegistry.ITEM_DATA.remove(entry);
                    CreativeItemRegistry.ITEM_DATA.add(new CreativeItemData(entry.getItem(), groups.get(group)));
                });
            }

            List<Map<String, Object>> items = asStringObjectMapList(data.get("items"));
            for (Map<String, Object> tag : items) {
                String id = (String) tag.getOrDefault("id", null);
                String group = (String) tag.getOrDefault("group", null);

                Integer groupIndex = groups.get(group);

                Item item = Item.get(id, 0, 1, null, false);

                if (item.isNull() || (item.isBlock() && item.getBlockUnsafe().isAir())) {
                    item = Item.AIR;
                    log.warn("load creative edu item {} is null", id);
                }

                if (groupIndex == null) {
                    Registries.CREATIVE.addCreativeItem(item);
                } else {
                    Registries.CREATIVE.addCreativeItem(item, groupIndex);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private static List<Map<String, Object>> asStringObjectMapList(Object value) {
        if (!(value instanceof List<?> rawList)) {
            return List.of();
        }
        List<Map<String, Object>> result = new ArrayList<>(rawList.size());
        for (Object entry : rawList) {
            if (entry instanceof Map<?, ?> rawMap) {
                Map<String, Object> typed = new HashMap<>();
                for (Map.Entry<?, ?> mapEntry : rawMap.entrySet()) {
                    Object key = mapEntry.getKey();
                    if (key instanceof String name) {
                        typed.put(name, mapEntry.getValue());
                    }
                }
                result.add(typed);
            }
        }
        return result;
    }
}
