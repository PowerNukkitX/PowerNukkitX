package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDoor;
import cn.nukkit.block.BlockID;
import cn.nukkit.dispenser.DispenseBehaviorRegister;
import cn.nukkit.entity.Attribute;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.level.biome.EnumBiome;
import cn.nukkit.potion.Effect;
import cn.nukkit.potion.Potion;
import cn.nukkit.utils.Identifier;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class TestLegacyItemIds {
    static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    static Set<Integer> itemBlock = new HashSet<>();
    static Map<String, Integer> specialItem = new HashMap<>();

    private static void init() {
        Block.init();
        Enchantment.init();
        RuntimeItems.init();
        Potion.init();
        Item.init();
        EnumBiome.values(); //load class, this also registers biomes
        Effect.init();
        Attribute.init();
        DispenseBehaviorRegister.init();
        GlobalBlockPalette.getOrCreateRuntimeId(0, 0); //Force it to load

        itemBlock.add(BlockID.GLOW_FRAME);
        itemBlock.add(BlockID.SOUL_CAMPFIRE_BLOCK);
        itemBlock.add(BlockID.CHAIN_BLOCK);
        itemBlock.add(BlockID.NETHER_SPROUTS_BLOCK);
        itemBlock.add(BlockID.CAMPFIRE_BLOCK);
        itemBlock.add(BlockID.BLOCK_KELP);
        itemBlock.add(BlockID.BED_BLOCK);
        itemBlock.add(BlockID.WHEAT_BLOCK);
        itemBlock.add(BlockID.REEDS);
        itemBlock.add(BlockID.CAKE_BLOCK);
        itemBlock.add(BlockID.NETHER_WART_BLOCK);
        itemBlock.add(BlockID.CAULDRON_BLOCK);
        itemBlock.add(BlockID.FLOWER_POT_BLOCK);
        itemBlock.add(BlockID.SKULL_BLOCK);
        itemBlock.add(BlockID.HOPPER_BLOCK);
        itemBlock.add(BlockID.ITEM_FRAME_BLOCK);
        itemBlock.add(BlockID.BEETROOT_BLOCK);

        specialItem.put("minecraft:wool", 35);
        specialItem.put("minecraft:boat", 333);
        specialItem.put("minecraft:dye", 351);
        specialItem.put("minecraft:banner_pattern", 434);
        specialItem.put("minecraft:brewingstandblock", 117);
        specialItem.put("minecraft:reserved6", 255);
        specialItem.put("minecraft:info_update2", 249);
        specialItem.put("minecraft:unknown", -305);
        specialItem.put("minecraft:brewing_stand", 379);
    }

    @Test
    void testLegacyItemIds() throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        init();
        TreeMap<Integer, String> legacyIds = new TreeMap<>(Integer::compare);
        Map<String, Integer> errors = new LinkedHashMap<>();
        for (var b : Block.list) {
            if (b != null) {
                Constructor<? extends Block> declaredConstructor = b.getDeclaredConstructor(null);
                declaredConstructor.setAccessible(true);
                Block block = declaredConstructor.newInstance();

                if (block instanceof BlockDoor || itemBlock.contains(block.getId())) {
                    legacyIds.put(block.getItemId(), Identifier.DEFAULT_NAMESPACE + ":" + "item." + Identifier.tryParse(block.getPersistenceName()).getPath());
                } else {
                    legacyIds.put(block.getItemId(), block.getPersistenceName());
                }
            }
        }
        for (Class<?> c : Item.list) {
            if (c != null) {
                if (c.getSimpleName().startsWith("Block")) {
                    continue;
                }
                Constructor<? extends Item> declaredConstructor = (Constructor<? extends Item>) c.getDeclaredConstructor(null);
                declaredConstructor.setAccessible(true);
                Item item = declaredConstructor.newInstance();
                try {
                    legacyIds.put(item.getId(), item.getNamespaceId());
                } catch (IllegalArgumentException | UnknownNetworkIdException ignore) {
                    errors.put(item.getName(), item.getId());
                }
            }
        }
        if (!errors.isEmpty()) {
            throw new RuntimeException("Legacy Item Ids are incomplete! The Missing item list:\n" + gson.toJson(errors));
        }
        LinkedHashMap<String, Integer> result = new LinkedHashMap<>();
        for (var entry : legacyIds.entrySet()) {
            result.put(entry.getValue(), entry.getKey());
        }
        result.putAll(specialItem);

        System.out.println("RESULTS:");
        System.out.println(gson.toJson(result));
    }
}
