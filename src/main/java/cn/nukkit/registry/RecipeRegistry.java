package cn.nukkit.registry;

import cn.nukkit.Server;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.network.protocol.CraftingDataPacket;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.recipe.*;
import cn.nukkit.recipe.descriptor.ComplexAliasDescriptor;
import cn.nukkit.recipe.descriptor.DefaultDescriptor;
import cn.nukkit.recipe.descriptor.ItemDescriptor;
import cn.nukkit.recipe.descriptor.ItemDescriptorType;
import cn.nukkit.recipe.descriptor.ItemTagDescriptor;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.Identifier;
import cn.nukkit.utils.MinecraftNamespaceComparator;
import cn.nukkit.utils.Utils;
import com.google.common.collect.Maps;
import io.netty.util.collection.CharObjectHashMap;
import io.netty.util.internal.EmptyArrays;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.*;

@Slf4j
@SuppressWarnings("unchecked")
public class RecipeRegistry implements IRegistry<String, Recipe, Recipe> {
    private static int RECIPE_COUNT = 0;
    public static final Comparator<Item> recipeComparator = (i1, i2) -> {
        int i = MinecraftNamespaceComparator.compareFNV(i1.getId(), i2.getId());
        if (i == 0) {
            if (i1.getDamage() > i2.getDamage()) {
                return 1;
            } else if (i1.getDamage() < i2.getDamage()) {
                return -1;
            } else return Integer.compare(i1.getCount(), i2.getCount());
        } else return i;
    };
    /**
     * 缓存着配方数据包
     */
    private static DataPacket packet = null;
    private final VanillaRecipeParser vanillaRecipeParser = new VanillaRecipeParser(this);
    private final EnumMap<RecipeType, Map<String, Recipe>> recipeMaps = new EnumMap<>(RecipeType.class);
    private final Object2DoubleOpenHashMap<Recipe> recipeXpMap = new Object2DoubleOpenHashMap<>();
    private final List<Recipe> networkIdRecipeList = new ArrayList<>();

    public VanillaRecipeParser getVanillaRecipeParser() {
        return vanillaRecipeParser;
    }

    public List<Recipe> getNetworkIdRecipeList() {
        return networkIdRecipeList;
    }

    public Object2DoubleOpenHashMap<Recipe> getRecipeXpMap() {
        return recipeXpMap;
    }

    public double getRecipeXp(Recipe recipe) {
        return recipeXpMap.getOrDefault(recipe, 0.0);
    }

    public void setRecipeXp(Recipe recipe, double xp) {
        recipeXpMap.put(recipe, xp);
    }

    public Map<String, ShapedRecipe> getShapedRecipeMap() {
        Map<String, Recipe> map = recipeMaps.get(RecipeType.SHAPED);
        return Maps.transformValues(map, v -> (ShapedRecipe) v);
    }

    public Map<String, FurnaceRecipe> getFurnaceRecipesMap() {
        Map<String, Recipe> map = recipeMaps.get(RecipeType.FURNACE);
        Map<String, Recipe> stringRecipeMap2 = recipeMaps.get(RecipeType.FURNACE_DATA);
        map.putAll(stringRecipeMap2);
        return Maps.transformValues(map, v -> (FurnaceRecipe) v);
    }

    public Map<String, BlastFurnaceRecipe> getBlastFurnaceRecipeMap() {
        Map<String, Recipe> map = recipeMaps.get(RecipeType.BLAST_FURNACE);
        Map<String, Recipe> stringRecipeMap2 = recipeMaps.get(RecipeType.BLAST_FURNACE_DATA);
        map.putAll(stringRecipeMap2);
        return Maps.transformValues(map, v -> (BlastFurnaceRecipe) v);
    }

    public Map<String, SmokerRecipe> getSmokerRecipeMap() {
        Map<String, Recipe> map = recipeMaps.get(RecipeType.SMOKER);
        Map<String, Recipe> stringRecipeMap2 = recipeMaps.get(RecipeType.SMOKER_DATA);
        map.putAll(stringRecipeMap2);
        return Maps.transformValues(map, v -> (SmokerRecipe) v);
    }

    public Map<String, CampfireRecipe> getCampfireRecipeMap() {
        Map<String, Recipe> map = recipeMaps.get(RecipeType.CAMPFIRE);
        Map<String, Recipe> stringRecipeMap2 = recipeMaps.get(RecipeType.CAMPFIRE_DATA);
        map.putAll(stringRecipeMap2);
        return Maps.transformValues(map, v -> (CampfireRecipe) v);
    }

    public Map<String, MultiRecipe> getMultiRecipeMap() {
        Map<String, Recipe> map = recipeMaps.get(RecipeType.MULTI);
        return Maps.transformValues(map, v -> (MultiRecipe) v);
    }

    public Map<String, StonecutterRecipe> getStonecutterRecipeMap() {
        Map<String, Recipe> map = recipeMaps.get(RecipeType.STONECUTTER);
        return Maps.transformValues(map, v -> (StonecutterRecipe) v);
    }

    public Map<String, ShapelessRecipe> getShapelessRecipeMap() {
        Map<String, Recipe> map = recipeMaps.get(RecipeType.SHAPELESS);
        return Maps.transformValues(map, v -> (ShapelessRecipe) v);
    }

    public Map<String, CartographyRecipe> getCartographyRecipeMap() {
        Map<String, Recipe> map = recipeMaps.get(RecipeType.CARTOGRAPHY);
        return Maps.transformValues(map, v -> (CartographyRecipe) v);
    }

    public Map<String, SmithingTransformRecipe> getSmithingTransformRecipeMap() {
        Map<String, Recipe> map = recipeMaps.get(RecipeType.SMITHING_TRANSFORM);
        return Maps.transformValues(map, v -> (SmithingTransformRecipe) v);
    }

    public Map<String, BrewingRecipe> getBrewingRecipeMap() {
        Map<String, Recipe> map = recipeMaps.get(RecipeType.BREWING);
        return Maps.transformValues(map, v -> (BrewingRecipe) v);
    }

    public Map<String, ContainerRecipe> getContainerRecipeMap() {
        Map<String, Recipe> map = recipeMaps.get(RecipeType.CONTAINER);
        return Maps.transformValues(map, v -> (ContainerRecipe) v);
    }

    public Map<String, ModProcessRecipe> getModProcessRecipeMap() {
        Map<String, Recipe> map = recipeMaps.get(RecipeType.MOD_PROCESS);
        return Maps.transformValues(map, v -> (ModProcessRecipe) v);
    }

    public DataPacket getCraftingPacket() {
        return packet;
    }

    public String computeRecipeIdWithItem(Collection<Item> results, Collection<Item> inputs, RecipeType type) {
        List<Item> inputs1 = new ArrayList<>(inputs);
        return computeRecipeId(results, inputs1.stream().map(DefaultDescriptor::new).toList(), type);
    }

    public String computeRecipeId(Collection<Item> results, Collection<? extends ItemDescriptor> inputs, RecipeType type) {
        StringBuilder builder = new StringBuilder("pnx:");
        Optional<Item> first = results.stream().findFirst();
        first.ifPresent(item -> builder.append(new Identifier(item.getId()).getPath()).append("from"));
        int limit = 3;
        for (var des : inputs) {
            if ((limit--) == 0) {
                if (des instanceof ItemTagDescriptor tag) {
                    builder.append("tag_").append(tag.getItemTag()).append("_and_");
                } else if (des instanceof DefaultDescriptor def) {
                    builder.append(new Identifier(def.getItem().getId()).getPath()).append("_and_");
                }
            }
        }
        String r = builder.toString();
        return r.substring(0, r.lastIndexOf("_and_")) + type.name();
    }

    public int getRecipeCount() {
        return RECIPE_COUNT;
    }

    public Recipe getRecipeByNetworkId(int networkId) {
        return networkIdRecipeList.get(networkId - 1);
    }

    public static void setCraftingPacket(DataPacket craftingPacket) {
        packet = craftingPacket;
    }

    @Override
    public void init() {
        log.info("Loading recipes...");
        this.loadRecipes();
        this.rebuildPacket();
        log.info("Loaded {} recipes.", RECIPE_COUNT);
    }

    @Override
    public Recipe get(String key) {
        for (var m : recipeMaps.values()) {
            if (m.containsKey(key)) return m.get(key);
        }
        return null;
    }

    @Override
    public void trim() {
        recipeXpMap.trim();
    }

    @Override
    public void register(String key, Recipe recipe) throws RegisterException {
        if (recipe instanceof CraftingRecipe craftingRecipe) {
            Item item = recipe.getResults().get(0);
            UUID id = Utils.dataToUUID(String.valueOf(RECIPE_COUNT), String.valueOf(item.getId()), String.valueOf(item.getDamage()), String.valueOf(item.getCount()), Arrays.toString(item.getCompoundTag()));
            if (craftingRecipe.getUUID() == null) craftingRecipe.setUUID(id);
        }
        Map<String, Recipe> recipeMap = recipeMaps.computeIfAbsent(recipe.getType(), t -> new HashMap<>());
        if (recipeMap.putIfAbsent(recipe.getRecipeId(), recipe) != null) {
            throw new RegisterException("Duplicate recipe %s type %s".formatted(recipe.getRecipeId(), recipe.getType()));
        }
        ++RECIPE_COUNT;
        switch (recipe.getType()) {
            case STONECUTTER, SHAPELESS, CARTOGRAPHY, SHULKER_BOX, SMITHING_TRANSFORM, SHAPED, MULTI ->
                    this.networkIdRecipeList.add(recipe);
        }
    }

    public void register(Recipe recipe) {
        try {
            this.register(recipe.getRecipeId(), recipe);
        } catch (RegisterException e) {
            throw new RuntimeException(e);
        }
    }

    public void cleanAllRecipes() {
        recipeXpMap.clear();
        networkIdRecipeList.clear();
        recipeMaps.values().forEach(Map::clear);
        rebuildPacket();
    }

    public void rebuildPacket() {
        CraftingDataPacket pk = new CraftingDataPacket();
        pk.cleanRecipes = true;

        pk.addNetworkIdRecipe(networkIdRecipeList);

        for (FurnaceRecipe recipe : getFurnaceRecipesMap().values()) {
            pk.addFurnaceRecipe(recipe);
        }
        for (SmokerRecipe recipe : getSmokerRecipeMap().values()) {
            pk.addSmokerRecipe(recipe);
        }
        for (BlastFurnaceRecipe recipe : getBlastFurnaceRecipeMap().values()) {
            pk.addBlastFurnaceRecipe(recipe);
        }
        for (CampfireRecipe recipe : getCampfireRecipeMap().values()) {
            pk.addCampfireRecipeRecipe(recipe);
        }
        for (BrewingRecipe recipe : getBrewingRecipeMap().values()) {
            pk.addBrewingRecipe(recipe);
        }
        for (ContainerRecipe recipe : getContainerRecipeMap().values()) {
            pk.addContainerRecipe(recipe);
        }
        pk.tryEncode();
        packet = pk;
    }

    @SneakyThrows
    private void loadRecipes() {
        //load xp config
        var furnaceXpConfig = new Config(Config.JSON);
        try (var r = Server.class.getClassLoader().getResourceAsStream("furnace_xp.json")) {
            furnaceXpConfig.load(r);
        } catch (IOException e) {
            log.warn("Failed to load furnace xp config");
        }

        var recipeConfig = new Config(Config.JSON);
        try (var r = Server.class.getClassLoader().getResourceAsStream("recipes.json")) {
            recipeConfig.load(r);

            //load potionMixes
            List<Map<String, Object>> potionMixes = (List<Map<String, Object>>) recipeConfig.getList("potionMixes");
            for (Map<String, Object> recipe : potionMixes) {
                String inputId = recipe.get("inputId").toString();
                int inputMeta = Utils.toInt(recipe.get("inputMeta"));
                String reagentId = recipe.get("reagentId").toString();
                int reagentMeta = Utils.toInt(recipe.get("reagentMeta"));
                String outputId = recipe.get("outputId").toString();
                int outputMeta = Utils.toInt(recipe.get("outputMeta"));
                Item inputItem = Item.get(inputId, inputMeta);
                Item reagentItem = Item.get(reagentId, reagentMeta);
                Item outputItem = Item.get(outputId, outputMeta);
                if (inputItem.isNull() || reagentItem.isNull() || outputItem.isNull()) {
                    continue;
                }
                register(new BrewingRecipe(
                        inputItem,
                        reagentItem,
                        outputItem
                ));
            }

            //load containerMixes
            List<Map<String, Object>> containerMixes = (List<Map<String, Object>>) recipeConfig.getList("containerMixes");
            for (Map<String, Object> containerMix : containerMixes) {
                String fromItemId = containerMix.get("inputId").toString();
                String ingredient = containerMix.get("reagentId").toString();
                String toItemId = containerMix.get("outputId").toString();
                register(new ContainerRecipe(Item.get(fromItemId), Item.get(ingredient), Item.get(toItemId)));
            }

            //load recipes
            List<Map<String, Object>> recipes = (List<Map<String, Object>>) recipeConfig.getList("recipes");
            for (Map<String, Object> recipe : recipes) {
                int type = Utils.toInt(recipe.get("type"));
                Recipe re = switch (type) {
                    case 9 -> {
                        //todo trim smithing recipe
                        yield null;
                    }
                    case 4 -> {
                        UUID uuid = UUID.fromString(recipe.get("uuid").toString());
                        yield new MultiRecipe(uuid);
                    }
                    case 0, 5, 8 -> {
                        String block = recipe.get("block").toString();
                        yield parseShapelessRecipe(recipe, block);
                    }
                    case 1 -> parseShapeRecipe(recipe);
                    case 3 -> {
                        String craftingBlock = (String) recipe.get("block");
                        Map<String, Object> resultMap = (Map<String, Object>) recipe.get("output");
                        ItemDescriptor resultItem = parseRecipeItem(resultMap);
                        if (resultItem == null) {
                            yield null;
                        }
                        Map<String, Object> inputMap = (Map<String, Object>) recipe.get("input");
                        ItemDescriptor inputItem = parseRecipeItem(inputMap);
                        if (inputItem == null) {
                            yield null;
                        }
                        Item result = resultItem.toItem();
                        Item input = inputItem.toItem();
                        Recipe furnaceRecipe = switch (craftingBlock) {
                            case "furnace" -> new FurnaceRecipe(result, input);
                            case "blast_furnace" -> new BlastFurnaceRecipe(result, input);
                            case "smoker" -> new SmokerRecipe(result, input);
                            case "campfire", "soul_campfire" -> new CampfireRecipe(result, input);
                            default -> throw new IllegalStateException("Unexpected value: " + craftingBlock);
                        };
                        var xp = furnaceXpConfig.getDouble(input.getId() + ":" + input.getDamage());
                        if (xp != 0) {
                            this.setRecipeXp(furnaceRecipe, xp);
                        }
                        yield furnaceRecipe;
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + recipe);
                };
                if (re == null) {
                    if (type != 9) {//todo trim smithing recipe
                        log.warn("Load recipe {} with null!", recipe.toString().substring(0, 60));
                    }
                    continue;
                }
                this.register(re);
            }
        } catch (IOException e) {
            log.warn("Failed to load recipes config");
        }

        // Allow to rename without crafting
        register(new CartographyRecipe(Item.get(ItemID.EMPTY_MAP), Collections.singletonList(Item.get(ItemID.EMPTY_MAP))));
        register(new CartographyRecipe(Item.get(ItemID.EMPTY_MAP, 2), Collections.singletonList(Item.get(ItemID.EMPTY_MAP, 2))));
        register(new CartographyRecipe(Item.get(ItemID.FILLED_MAP), Collections.singletonList(Item.get(ItemID.FILLED_MAP))));
        register(new CartographyRecipe(Item.get(ItemID.FILLED_MAP, 3), Collections.singletonList(Item.get(ItemID.FILLED_MAP, 3))));
        register(new CartographyRecipe(Item.get(ItemID.FILLED_MAP, 4), Collections.singletonList(Item.get(ItemID.FILLED_MAP, 4))));
        register(new CartographyRecipe(Item.get(ItemID.FILLED_MAP, 5), Collections.singletonList(Item.get(ItemID.FILLED_MAP, 5))));
    }

    private Recipe parseShapelessRecipe(Map<String, Object> recipeObject, String craftingBlock) {
        String id = recipeObject.get("id").toString();
        if (craftingBlock.equals("smithing_table")) {
            Map<String, Object> base = (Map<String, Object>) recipeObject.get("base");
            ItemDescriptor baseItem = parseRecipeItem(base);
            Map<String, Object> addition = (Map<String, Object>) recipeObject.get("addition");
            ItemDescriptor additionItem = parseRecipeItem(addition);
            Map<String, Object> template = (Map<String, Object>) recipeObject.get("template");
            ItemDescriptor templateItem = parseRecipeItem(template);
            Map<String, Object> output = (Map<String, Object>) recipeObject.get("result");
            ItemDescriptor outputItem = parseRecipeItem(output);
            if (additionItem == null || baseItem == null || outputItem == null || templateItem == null) {
                return null;
            }
            return new SmithingTransformRecipe(id, outputItem.toItem(), baseItem, additionItem, templateItem);
        }
        UUID uuid = UUID.fromString(recipeObject.get("uuid").toString());
        List<ItemDescriptor> itemDescriptors = new ArrayList<>();
        List<Map<String, Object>> inputs = ((List<Map<String, Object>>) recipeObject.get("input"));
        List<Map<String, Object>> outputs = ((List<Map<String, Object>>) recipeObject.get("output"));
        if (outputs.size() > 1) {
            return null;
        }
        Map<String, Object> first = outputs.get(0);

        int priority = recipeObject.containsKey("priority") ? Utils.toInt(recipeObject.get("priority")) : 0;

        ItemDescriptor result = parseRecipeItem(first);
        if (result == null) {
            return null;
        }
        Item resultItem = result.toItem();
        for (Map<String, Object> ingredient : inputs) {
            ItemDescriptor recipeItem = parseRecipeItem(ingredient);
            if (recipeItem == null) {
                return null;
            }
            itemDescriptors.add(recipeItem);
        }
        return switch (craftingBlock) {
            case "crafting_table", "deprecated" -> new ShapelessRecipe(id, uuid, priority, resultItem, itemDescriptors);
            case "shulker_box" -> new ShulkerBoxRecipe(id, uuid, priority, resultItem, itemDescriptors);
            case "stonecutter" ->
                    new StonecutterRecipe(id, uuid, priority, resultItem, itemDescriptors.get(0).toItem());
            case "cartography_table" -> new CartographyRecipe(id, uuid, priority, resultItem, itemDescriptors);
            default -> null;
        };
    }

    private Recipe parseShapeRecipe(Map<String, Object> recipeObject) {
        String id = recipeObject.get("id").toString();
        UUID uuid = UUID.fromString(recipeObject.get("uuid").toString());
        List<Map<String, Object>> outputs = (List<Map<String, Object>>) recipeObject.get("output");

        Map<String, Object> first = outputs.remove(0);
        String[] shape = ((List<String>) recipeObject.get("shape")).toArray(EmptyArrays.EMPTY_STRINGS);
        Map<Character, ItemDescriptor> ingredients = new CharObjectHashMap<>();

        int priority = Utils.toInt(recipeObject.get("priority"));
        ItemDescriptor primaryResult = parseRecipeItem(first);
        if (primaryResult == null) return null;

        List<Item> extraResults = new ArrayList<>();
        for (Map<String, Object> data : outputs) {
            ItemDescriptor output = parseRecipeItem(data);
            if (output == null) return null;
            extraResults.add(output.toItem());
        }

        Map<String, Map<String, Object>> input = (Map<String, Map<String, Object>>) recipeObject.get("input");
        for (Map.Entry<String, Map<String, Object>> ingredientEntry : input.entrySet()) {
            char ingredientChar = ingredientEntry.getKey().charAt(0);
            var ingredient = ingredientEntry.getValue();
            ItemDescriptor itemDescriptor = parseRecipeItem(ingredient);
            if (itemDescriptor == null) return null;
            ingredients.put(ingredientChar, itemDescriptor);
        }
        return new ShapedRecipe(id, uuid, priority, primaryResult.toItem(), shape, ingredients, extraResults);
    }

    private ItemDescriptor parseRecipeItem(Map<String, Object> data) {
        String type = data.containsKey("type") ? data.get("type").toString() : "default";
        ItemDescriptorType itemDescriptorType = ItemDescriptorType.valueOf(type.toUpperCase(Locale.ENGLISH));
        return switch (itemDescriptorType) {
            case DEFAULT -> {
                Item item;
                String name = null;
                if (data.containsKey("id")) {
                    name = data.get("id").toString();
                } else if (data.containsKey("itemId")) {
                    name = data.get("itemId").toString();
                }
                if (name == null) yield null;

                int count = data.containsKey("count") ? ((Number) data.get("count")).intValue() : 1;

                String nbt = (String) data.get("nbt");
                byte[] nbtBytes = nbt != null ? Base64.getDecoder().decode(nbt) : EmptyArrays.EMPTY_BYTES;

                Integer meta = null;
                if (data.containsKey("damage")) {
                    meta = Utils.toInt(data.get("damage"));
                } else if (data.containsKey("auxValue")) {
                    meta = Utils.toInt(data.get("auxValue"));
                }
                if (meta != null) {
                    if (meta == Short.MAX_VALUE) {
                        item = Item.get(name).createFuzzyCraftingRecipe();
                    } else {
                        item = Item.get(name, meta);
                    }
                } else {
                    item = Item.get(name);
                }

                item.setCount(count);
                item.setCompoundTag(nbtBytes);
                yield new DefaultDescriptor(item);
            }
            case COMPLEX_ALIAS -> {
                String name = data.get("name").toString();
                yield new ComplexAliasDescriptor(name);
            }
            case ITEM_TAG -> {
                var itemTag = data.get("itemTag").toString();
                int count = data.containsKey("count") ? ((Number) data.get("count")).intValue() : 1;
                yield new ItemTagDescriptor(itemTag, count);
            }
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }

    public static class Entry {
        final int resultItemId;
        final int resultMeta;
        final int ingredientItemId;
        final int ingredientMeta;
        final String recipeShape;
        final int resultAmount;

        public Entry(int resultItemId, int resultMeta, int ingredientItemId, int ingredientMeta, String recipeShape, int resultAmount) {
            this.resultItemId = resultItemId;
            this.resultMeta = resultMeta;
            this.ingredientItemId = ingredientItemId;
            this.ingredientMeta = ingredientMeta;
            this.recipeShape = recipeShape;
            this.resultAmount = resultAmount;
        }
    }
}
