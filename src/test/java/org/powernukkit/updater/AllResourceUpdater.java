package org.powernukkit.updater;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockUnknown;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.blockstate.BlockStateRegistry;
import cn.nukkit.item.Item;
import cn.nukkit.item.RuntimeItems;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import io.netty.util.internal.EmptyArrays;
import lombok.SneakyThrows;
import lombok.var;
import org.powernukkit.dumps.ItemIdDumper;
import org.powernukkit.dumps.RuntimeBlockStateDumper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.*;

/**
 * @author joserobjr
 * @since 2021-10-23
 */
public class AllResourceUpdater {
    public static void main(String[] args) {
        /*
        Pre-requisites:
        - Run src/test/java/org/powernukkit/updater/AllResourcesDownloader.java
        - Run mvn clean package
        - Run src/test/java/org/powernukkit/updater/RuntimeItemIdUpdater.java
        - Run mvn clean package
         */
        try {
            new AllResourceUpdater().execute();
        } catch (Throwable e) {
            e.printStackTrace();
            System.exit(1);
        } finally {
            System.exit(0);
        }
    }

    public static final Gson GSON = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting()
            .registerTypeAdapter(Double.class, (JsonSerializer<Double>) (src, typeOfSrc, context) -> {
                if (src == src.longValue())
                    return new JsonPrimitive(src.longValue());
                return new JsonPrimitive(src);
            }).create();

    @SneakyThrows
    private void execute() {
        ItemIdDumper.main(EmptyArrays.EMPTY_STRINGS);
        RuntimeBlockStateDumper.main(EmptyArrays.EMPTY_STRINGS);
        init();
        updateRecipes();
        updateCreativeItems();
        System.exit(0);
    }

    @SuppressWarnings("unchecked")
    private void updateRecipes() {
        Config config = new Config(Config.JSON);
        try(InputStream recipesStream = getClass().getClassLoader()
                .getResourceAsStream("org/powernukkit/updater/dumps/proxypass/recipes.json")
        ) {
            if (recipesStream == null) {
                throw new AssertionError("Unable to find recipes.json");
            }
            config.loadAsJson(recipesStream, GSON);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        var newRecipes = config.getMapList("recipes");
        var recipesIterator = newRecipes.listIterator();
        while (recipesIterator.hasNext()) {
            var recipe = new LinkedHashMap<String, Object>(recipesIterator.next());
            var type = Utils.toInt(recipe.get("type"));
            Object inputObject = recipe.get("input");
            if (inputObject != null) {
                if (type == 3) {
                    inputObject = updateItemEntry((Map<String, Object>) inputObject);
                } else {
                    if (inputObject instanceof Map) {
                        var inputs = (Map<String, Map<String, Object>>) inputObject;
                        inputs = new LinkedHashMap<>(inputs);
                        for (var itemEntry : inputs.entrySet()) {
                            itemEntry.setValue(updateItemEntry(itemEntry.getValue()));
                        }
                        inputObject = inputs;
                    } else if (inputObject instanceof List) {
                        var inputs = (List<Map<String, Object>>) inputObject;
                        inputObject = updateItemEntryList(inputs);
                    }
                }
                recipe.put("input", inputObject);
            }

            Object outputObject = recipe.get("output");
            if (outputObject != null) {
                if (type == 3) {
                    outputObject = updateItemEntry((Map<String, Object>) outputObject);
                } else {
                    var outputList = (List<Map<String, Object>>) outputObject;
                    outputObject = updateItemEntryList(outputList);
                }
                recipe.put("output", outputObject);
            }

            recipesIterator.set(recipe);
        }
        config.set("recipes", newRecipes);

        config.saveAsJson(new File("src/main/resources/recipes.json"), false, GSON);
    }

    @SuppressWarnings("unchecked")
    private void updateCreativeItems() {
        Config config = new Config(Config.JSON);
        try(InputStream recipesStream = getClass().getClassLoader()
                .getResourceAsStream("org/powernukkit/updater/dumps/proxypass/creativeitems.json")
        ) {
            if (recipesStream == null) {
                throw new AssertionError("Unable to findcreativeitems.json");
            }
            config.loadAsJson(recipesStream, GSON);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        var newItems = (List<Map<String, Object>>)(Object)config.getMapList("items");
        newItems = updateItemEntryList(newItems);
        config.set("items", newItems);

        config.saveAsJson(new File("src/main/resources/creativeitems.json"), false, GSON);
    }

    private List<Map<String, Object>> updateItemEntryList(List<Map<String, Object>> inputs) {
        inputs = new ArrayList<>(inputs);
        var inputIterator = inputs.listIterator();
        while (inputIterator.hasNext()) {
            inputIterator.set(updateItemEntry(inputIterator.next()));
        }
        return inputs;
    }

    private Map<String, Object> updateItemEntry(Map<String, Object> itemEntry) {
        var result = updateItemEntry0(itemEntry);
        if ("minecraft:air".equals(result.get("blockState"))) {
            throw new NoSuchElementException("State not found for: "+itemEntry);
        }
        return result;
    }

    private Map<String, Object> updateItemEntry0(Map<String, Object> itemEntry) {
        itemEntry = new LinkedHashMap<>(itemEntry);
        Integer damage = itemEntry.containsKey("damage")? Utils.toInt(itemEntry.get("damage")) : null;
        boolean fuzzy = damage != null && (damage.equals((int)Short.MAX_VALUE) || damage.equals(-1));
        if (itemEntry.containsKey("blockState")) {
            itemEntry.remove("legacyId");
            itemEntry.remove("blockRuntimeId");
            itemEntry.remove("id");
            itemEntry.remove("damage");
            return itemEntry;
        }

        if (itemEntry.containsKey("blockRuntimeId")) {
            int blockRuntimeId = Utils.toInt(itemEntry.get("blockRuntimeId"));
            BlockState state;
            String stateId;
            boolean unknown = false;
            try {
                state = BlockStateRegistry.getBlockStateByRuntimeId(blockRuntimeId);
                if (state == null || state.equals(BlockState.AIR)) {
                    throw new NoSuchElementException("State not found for blockRuntimeId: "+blockRuntimeId);
                }
                if (state.getProperties().equals(BlockUnknown.PROPERTIES)) {
                    unknown = true;
                    stateId = BlockStateRegistry.getKnownBlockStateIdByRuntimeId(blockRuntimeId);
                } else {
                    stateId = state.getMinimalistStateId();
                }
            } catch (Exception e) {
                try {
                    int blockId = BlockStateRegistry.getBlockIdByRuntimeId(blockRuntimeId);
                    BlockState baseState = BlockState.of(blockId);
                    if (baseState.equals(BlockState.AIR)) {
                        throw new NoSuchElementException("State not found for blockRuntimeId: "+blockRuntimeId);
                    }
                    if (baseState.getProperties().equals(BlockUnknown.PROPERTIES)) {
                        unknown = true;
                        stateId = BlockStateRegistry.getKnownBlockStateIdByRuntimeId(blockRuntimeId);
                    } else {
                        throw e;
                    }
                }  catch (Exception e2) {
                    e2.addSuppressed(e);
                    throw e2;
                }
            }
            if (unknown) {
                if (stateId != null) {
                    System.out.println("State of unimplemented block found for blockRuntimeId: " + blockRuntimeId + ", using " + stateId);
                } else {
                    throw new NoSuchElementException("State unknown for blockRuntimeId: " + blockRuntimeId);
                }
            }
            itemEntry.remove("legacyId");
            itemEntry.remove("blockRuntimeId");
            itemEntry.remove("id");
            itemEntry.remove("damage");
            itemEntry.put("blockState", stateId);
            if (fuzzy) {
                itemEntry.put("fuzzy", true);
            }
            return itemEntry;
        }

        if (itemEntry.containsKey("legacyId")) {
            int legacyId = Utils.toInt(itemEntry.get("legacyId"));
            if (legacyId > 255) {
                int fullId;
                try {
                    fullId = RuntimeItems.getRuntimeMapping().getLegacyFullId(legacyId);
                } catch (Exception e) {
                    System.out.println("Could not update " + legacyId + " " + itemEntry.get("id") + " : " + itemEntry.get("damage"));
                    return itemEntry;
                }
                int itemId = RuntimeItems.getId(fullId);
                Integer meta = null;
                if (RuntimeItems.hasData(fullId)) {
                    meta = RuntimeItems.getData(fullId);
                }

                if (itemEntry.containsKey("damage")) {
                    int damage2 = Utils.toInt(itemEntry.get("damage"));
                    if (damage2 == Short.MAX_VALUE) {
                        fuzzy = true;
                    } else if (meta == null) {
                        meta = damage;
                    }
                }

                Item item = Item.get(itemId, meta == null ? 0 : meta);
                itemEntry.remove("legacyId");
                itemEntry.remove("blockRuntimeId");
                itemEntry.remove("damage");
                itemEntry.remove("blockState");
                itemEntry.put("id", item.getNamespaceId());
                if (fuzzy) {
                    itemEntry.put("fuzzy", true);
                } else if (item.getDamage() != 0) {
                    itemEntry.put("damage", item.getDamage());
                }
                return itemEntry;
            }
        }

        String id = itemEntry.get("id").toString();
        Item item = Item.fromString(id);
        if (item.getId() > 255) {
            if (damage != null && !fuzzy && damage != 0) {
                item = Item.fromString(id+":"+damage);
            }
            itemEntry.remove("legacyId");
            itemEntry.remove("blockRuntimeId");
            itemEntry.remove("damage");
            itemEntry.remove("blockState");
            itemEntry.put("id", item.getNamespaceId());
            if (fuzzy) {
                itemEntry.put("fuzzy", true);
            } else if (damage != null && damage != 0) {
                itemEntry.put("damage", damage);
            }
            return itemEntry;
        }

        Integer blockId = BlockStateRegistry.getBlockId(id);
        if (blockId == null) {
            System.out.println("Block id not found for id: " + itemEntry.get("id") + " : " + damage);
            return itemEntry;
        }

        String namespacedId = BlockStateRegistry.getPersistenceName(blockId);
        String stateId;
        if (damage == null || damage == 0 || damage == Short.MAX_VALUE || damage == -1) {
            stateId = namespacedId;
        } else {
            item = Item.getBlock(blockId, damage);
            if (item.getBlock().getId() == BlockID.AIR) {
                throw new NoSuchElementException("State not found for id: " + itemEntry.get("id") + " : " + damage);
            }
            stateId = item.getBlock().getMinimalistStateId();
        }
        if (damage != null && damage == Short.MAX_VALUE) {
            fuzzy = true;
        }
        if (fuzzy) {
            itemEntry.put("fuzzy", true);
        }
        itemEntry.remove("damage");
        itemEntry.remove("legacyId");
        itemEntry.remove("blockRuntimeId");
        itemEntry.remove("id");
        itemEntry.put("blockState", stateId);
        return itemEntry;
    }

    private void init() {
        Block.init();
        Enchantment.init();
        Item.init();
    }
}
