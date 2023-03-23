package cn.nukkit.inventory;

import cn.nukkit.Server;
import cn.nukkit.api.*;
import cn.nukkit.inventory.recipe.DefaultDescriptor;
import cn.nukkit.inventory.recipe.ItemDescriptor;
import cn.nukkit.inventory.recipe.ItemTagDescriptor;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.StringItem;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.CraftingDataPacket;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.utils.BinaryStream;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.Utils;
import com.google.gson.GsonBuilder;
import io.netty.util.collection.CharObjectHashMap;
import io.netty.util.internal.EmptyArrays;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteOrder;
import java.util.*;
import java.util.zip.Deflater;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@Log4j2
public class CraftingManager {

    public static final Comparator<Item> recipeComparator = (i1, i2) -> {
        if (i1.getId() > i2.getId()) {
            return 1;
        } else if (i1.getId() < i2.getId()) {
            return -1;
        } else if (i1.getDamage() > i2.getDamage()) {
            return 1;
        } else if (i1.getDamage() < i2.getDamage()) {
            return -1;
        } else return Integer.compare(i1.getCount(), i2.getCount());
    };

    //<editor-fold desc="deprecated fields" defaultstate="collapsed">
    /**
     * 缓存着配方数据包
     */
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE", reason = "Direct access to fields are not future-proof.",
            replaceWith = "getPacket()")
    @Since("1.5.0.0-PN")
    public static DataPacket packet = null;
    @PowerNukkitXDifference(info = "Now it is the count of all recipes", since = "1.19.50-r3")
    private static int RECIPE_COUNT = 0;
    private final Int2ObjectMap<Map<UUID, ShapedRecipe>> shapedRecipeMap = new Int2ObjectOpenHashMap<>();
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE", reason = "Direct access to fields are not future-proof.")
    protected final Map<Integer, Map<UUID, ShapedRecipe>> shapedRecipes = shapedRecipeMap;
    private final Int2ObjectMap<FurnaceRecipe> furnaceRecipeMap = new Int2ObjectOpenHashMap<>();
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE", reason = "Direct access to fields are not future-proof.")
    public final Map<Integer, FurnaceRecipe> furnaceRecipes = furnaceRecipeMap;
    private final Int2ObjectMap<BlastFurnaceRecipe> blastFurnaceRecipeMap = new Int2ObjectOpenHashMap<>();
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE", reason = "Direct access to fields are not future-proof.")
    @PowerNukkitOnly
    public final Map<Integer, BlastFurnaceRecipe> blastFurnaceRecipes = blastFurnaceRecipeMap;
    private final Int2ObjectMap<CampfireRecipe> campfireRecipeMap = new Int2ObjectOpenHashMap<>();
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE", reason = "Direct access to fields are not future-proof.")
    @PowerNukkitOnly
    public final Map<Integer, CampfireRecipe> campfireRecipes = campfireRecipeMap;
    private final Int2ObjectMap<SmokerRecipe> smokerRecipeMap = new Int2ObjectOpenHashMap<>();
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE", reason = "Direct access to fields are not future-proof.")
    @PowerNukkitOnly
    public final Map<Integer, SmokerRecipe> smokerRecipes = smokerRecipeMap;
    private final Map<UUID, MultiRecipe> multiRecipeMap = new HashMap<>();
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE", reason = "Direct access to fields are not future-proof.")
    @Since("1.4.0.0-PN")
    public final Map<UUID, MultiRecipe> multiRecipes = multiRecipeMap;
    private final Int2ObjectMap<BrewingRecipe> brewingRecipeMap = new Int2ObjectOpenHashMap<>();
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE", reason = "Direct access to fields are not future-proof.")
    public final Map<Integer, BrewingRecipe> brewingRecipes = brewingRecipeMap;
    private final Int2ObjectMap<ContainerRecipe> containerRecipeMap = new Int2ObjectOpenHashMap<>();
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE", reason = "Direct access to fields are not future-proof.")
    public final Map<Integer, ContainerRecipe> containerRecipes = containerRecipeMap;
    private final Int2ObjectMap<StonecutterRecipe> stonecutterRecipeMap = new Int2ObjectOpenHashMap<>();
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE", reason = "Direct access to fields are not future-proof.")
    @PowerNukkitOnly
    public final Map<Integer, StonecutterRecipe> stonecutterRecipes = stonecutterRecipeMap;
    private final Int2ObjectMap<Map<UUID, ShapelessRecipe>> shapelessRecipeMap = new Int2ObjectOpenHashMap<>();
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE", reason = "Direct access to fields are not future-proof.")
    protected final Map<Integer, Map<UUID, ShapelessRecipe>> shapelessRecipes = shapelessRecipeMap;
    private final Int2ObjectMap<Map<UUID, CartographyRecipe>> cartographyRecipeMap = new Int2ObjectOpenHashMap<>();
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE", reason = "Direct access to fields are not future-proof.")
    @PowerNukkitOnly
    protected final Map<Integer, Map<UUID, CartographyRecipe>> cartographyRecipes = cartographyRecipeMap;
    private final Int2ObjectOpenHashMap<Map<UUID, SmithingRecipe>> smithingRecipeMap = new Int2ObjectOpenHashMap<>();
    @Since("1.19.50-r3")
    @PowerNukkitXOnly
    private final Map<String, Map<UUID, ModProcessRecipe>> modProcessRecipeMap = new HashMap<>();
    @Since("1.19.50-r3")
    @PowerNukkitXOnly
    private final Object2DoubleOpenHashMap<Recipe> recipeXpMap = new Object2DoubleOpenHashMap<>();
    private final Deque<Recipe> recipeList = new ArrayDeque<>();

    /**
     * 一个包含全部种类配方的双端队列集合
     */
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE", reason = "Direct access to fields are not future-proof.")
    @PowerNukkitXDifference(info = "Now it is contain all type recipes", since = "1.19.50-r3")
    public final Collection<Recipe> recipes = recipeList;
    //</editor-fold>

    //<editor-fold desc="constructors and setup" defaultstate="collapsed">
    public CraftingManager() {
        log.info("Loading recipes...");
        this.loadRecipes();
        this.rebuildPacket();
        log.info("Loaded {} recipes.", this.recipes.size());
    }

    private CraftingManager(Boolean Test) {
    }

    @SneakyThrows
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void loadRecipes() {
        var gson = new GsonBuilder().create();

        //load xp config
        var furnaceXpConfig = new Config(Config.JSON);
        try {
            furnaceXpConfig.load(Server.class.getModule().getResourceAsStream("vanilla_recipes/furnace_xp.json"));
        } catch (IOException e) {
            log.warn("Failed to load furnace xp config");
        }

        //load potion
        try (var r = Server.class.getModule().getResourceAsStream("vanilla_recipes/potion_type.json")) {
            if (r == null) {
                throw new AssertionError("Unable to find potion_type.json");
            }
            List<Map<String, Object>> map = gson.fromJson(new InputStreamReader(r), List.class);
            for (Map<String, Object> recipe : map) {
                Map<String, Object> ingredient = (Map<String, Object>) recipe.get("ingredient");
                Map<String, Object> input = (Map<String, Object>) recipe.get("input");
                Map<String, Object> output = (Map<String, Object>) recipe.get("output");
                Item inputItem = parseRecipeItem(input);
                Item ingredientItem = parseRecipeItem(ingredient);
                Item outputItem = parseRecipeItem(output);
                if (inputItem.isNull() || ingredientItem.isNull() || outputItem.isNull()) {
                    continue;
                }
                registerBrewingRecipe(new BrewingRecipe(
                        inputItem,
                        ingredientItem,
                        outputItem
                ));
            }
        }

        //load shaped recipe
        try (var r = Server.class.getModule().getResourceAsStream("vanilla_recipes/shaped_crafting.json")) {
            if (r == null) {
                throw new AssertionError("Unable to find shaped_crafting.json");
            }
            List<Map<String, Object>> map = gson.fromJson(new InputStreamReader(r), List.class);
            for (Map<String, Object> recipe : map) {
                String craftingBlock = (String) recipe.get("block");
                if (!"crafting_table".equals(craftingBlock)) {
                    continue;
                }
                var reg = parseShapeRecipe(recipe);
                if (reg == null) {
                    continue;
                }
                this.registerRecipe(reg);
            }
        }

        //load shapeless recipe
        {
            var shapelessCrafting = Server.class.getModule().getResourceAsStream("vanilla_recipes/shapeless_crafting.json");
            if (shapelessCrafting == null) {
                throw new AssertionError("Unable to find shapeless_crafting.json");
            }

            var smithing = Server.class.getModule().getResourceAsStream("vanilla_recipes/smithing.json");
            if (smithing == null) {
                throw new AssertionError("Unable to find smithing.json");
            }
            List<Map<String, Object>> smithingRecipes = gson.fromJson(new InputStreamReader(smithing), List.class);
            smithing.close();

            List<Map<String, Object>> shapelessRecipes = new ArrayList<>(gson.fromJson(new InputStreamReader(shapelessCrafting), List.class));
            shapelessRecipes.addAll(smithingRecipes);
            shapelessCrafting.close();

            for (Map<String, Object> recipe : shapelessRecipes) {
                String craftingBlock = (String) recipe.get("block");
                var reg = parseShapelessRecipe(recipe, craftingBlock);
                if (reg == null) {
                    continue;
                }
                this.registerRecipe(reg);
            }

            var shapelessShulkerBox = Server.class.getModule().getResourceAsStream("vanilla_recipes/shapeless_shulker_box.json");
            if (shapelessShulkerBox == null) {
                throw new AssertionError("Unable to find shapeless_shulker_box.json");
            }
            List<Map<String, Object>> shulkerBoxRecipes = gson.fromJson(new InputStreamReader(shapelessShulkerBox), List.class);
            for (Map<String, Object> recipe : shulkerBoxRecipes) {
                String craftingBlock = "shulker_box";
                var reg = parseShapelessRecipe(recipe, craftingBlock);
                if (reg == null) {
                    continue;
                }
                this.registerRecipe(reg);
            }
            shapelessShulkerBox.close();
        }

        //load furnace recipe
        try (var r = Server.class.getModule().getResourceAsStream("vanilla_recipes/smelting.json")) {
            if (r == null) {
                throw new AssertionError("Unable to find smelting.json");
            }
            List<Map<String, Object>> map = gson.fromJson(new InputStreamReader(r), List.class);
            for (Map<String, Object> recipe : map) {
                String craftingBlock = (String) recipe.get("block");
                if (!"furnace".equals(craftingBlock) && !"blast_furnace".equals(craftingBlock)
                        && !"smoker".equals(craftingBlock) && !"campfire".equals(craftingBlock)) {
                    // Ignore other recipes than furnaces, blast furnaces, smokers and campfire
                    continue;
                }
                Map<String, Object> resultMap = (Map) recipe.get("output");
                Item resultItem = parseRecipeItem(resultMap);
                if (resultItem.isNull()) {
                    continue;
                }
                Item inputItem;
                try {
                    Map<String, Object> inputMap = (Map) recipe.get("input");
                    inputItem = parseRecipeItem(inputMap);
                } catch (Exception old) {
                    inputItem = Item.get(Utils.toInt(recipe.get("inputId")), recipe.containsKey("inputDamage") ? Utils.toInt(recipe.get("inputDamage")) : -1, 1);
                }
                if (inputItem.isNull()) {
                    continue;
                }
                Recipe furnaceRecipe = null;
                switch (craftingBlock) {
                    case "furnace" -> this.registerRecipe(furnaceRecipe = new FurnaceRecipe(resultItem, inputItem));
                    case "blast_furnace" ->
                            this.registerRecipe(furnaceRecipe = new BlastFurnaceRecipe(resultItem, inputItem));
                    case "smoker" -> this.registerRecipe(furnaceRecipe = new SmokerRecipe(resultItem, inputItem));
                    case "campfire" -> this.registerRecipe(furnaceRecipe = new CampfireRecipe(resultItem, inputItem));
                }
                var xp = furnaceXpConfig.getDouble(inputItem.getNamespaceId() + ":" + inputItem.getDamage());
                if (xp != 0) {
                    this.setRecipeXp(furnaceRecipe, xp);
                }
            }
        }

        //load multi recipe
        try (var r = Server.class.getModule().getResourceAsStream("vanilla_recipes/special_hardcoded.json")) {
            if (r == null) {
                throw new AssertionError("Unable to find special_hardcoded.json");
            }
            List<String> uuids = gson.fromJson(new InputStreamReader(r), List.class);
            for (String uuid : uuids) {
                this.registerRecipe(new MultiRecipe(UUID.fromString(uuid)));
            }
        }

        //load multi recipe
        try (var r = Server.class.getModule().getResourceAsStream("vanilla_recipes/special_hardcoded.json")) {
            if (r == null) {
                throw new AssertionError("Unable to find special_hardcoded.json");
            }
            List<String> uuids = gson.fromJson(new InputStreamReader(r), List.class);
            for (String uuid : uuids) {
                this.registerRecipe(new MultiRecipe(UUID.fromString(uuid)));
            }
        }

        //load container mixes
        try (var r = Server.class.getModule().getResourceAsStream("vanilla_recipes/container_mixes.json")) {
            if (r == null) {
                throw new AssertionError("Unable to find container_mixes.json");
            }
            List<Map<String, Object>> containerMixes = gson.fromJson(new InputStreamReader(r), List.class);
            for (Map containerMix : containerMixes) {
                String fromItemId = containerMix.get("inputId").toString();
                String ingredient = containerMix.get("reagentId").toString();
                String toItemId = containerMix.get("outputId").toString();
                registerContainerRecipe(new ContainerRecipe(Item.fromString(fromItemId), Item.fromString(ingredient), Item.fromString(toItemId)));
            }
        }

        // Allow to rename without crafting
        registerCartographyRecipe(new CartographyRecipe(Item.get(ItemID.EMPTY_MAP), Collections.singletonList(Item.get(ItemID.EMPTY_MAP))));
        registerCartographyRecipe(new CartographyRecipe(Item.get(ItemID.EMPTY_MAP, 2), Collections.singletonList(Item.get(ItemID.EMPTY_MAP, 2))));
        registerCartographyRecipe(new CartographyRecipe(Item.get(ItemID.MAP), Collections.singletonList(Item.get(ItemID.MAP))));
        registerCartographyRecipe(new CartographyRecipe(Item.get(ItemID.MAP, 3), Collections.singletonList(Item.get(ItemID.MAP, 3))));
        registerCartographyRecipe(new CartographyRecipe(Item.get(ItemID.MAP, 4), Collections.singletonList(Item.get(ItemID.MAP, 4))));
        registerCartographyRecipe(new CartographyRecipe(Item.get(ItemID.MAP, 5), Collections.singletonList(Item.get(ItemID.MAP, 5))));
    }

    @PowerNukkitXOnly
    @Since("1.19.50-r2")
    @SuppressWarnings("unchecked")
    private Recipe parseShapelessRecipe(Map<String, Object> recipeObject, String craftingBlock) {
        String id = recipeObject.containsKey("id") ? recipeObject.get("id").toString() : null;
        if (craftingBlock.equals("smithing_table")) {
            List<Item> items = new ArrayList<>();
            Map<String, Object> addition = (Map<String, Object>) recipeObject.get("addition");
            Item additionItem = parseRecipeItem(addition);
            Map<String, Object> input = (Map<String, Object>) recipeObject.get("input");
            Item inputItem = parseRecipeItem(input);
            Map<String, Object> output = (Map<String, Object>) recipeObject.get("output");
            Item outputItem = parseRecipeItem(output);
            if (additionItem.isNull() || inputItem.isNull() || outputItem.isNull()) {
                return null;
            }
            items.add(inputItem);
            items.add(additionItem);
            return new SmithingRecipe(null, 0, items, outputItem);
        }

        List<ItemDescriptor> itemDescriptors = new ArrayList<>();
        List<Map<String, Object>> outputs = ((List<Map<String, Object>>) recipeObject.get("output"));
        List<Map<String, Object>> inputs = ((List<Map<String, Object>>) recipeObject.get("input"));
        // TODO: handle multiple result items
        if (outputs.size() > 1) {
            return null;
        }
        Map<String, Object> first = outputs.get(0);

        int priority = recipeObject.containsKey("priority") ? Utils.toInt(recipeObject.get("priority")) : 0;

        Item result = parseRecipeItem(first);
        if (result.isNull()) {
            return null;
        }

        for (Map<String, Object> ingredient : inputs) {
            if (ingredient.containsKey("tag")) {
                var itemTag = ingredient.get("tag").toString();
                int count = ingredient.containsKey("count") ? ((Number) ingredient.get("count")).intValue() : 1;
                itemDescriptors.add(new ItemTagDescriptor(itemTag, count));
            } else {
                Item recipeItem = parseRecipeItem(ingredient);
                if (recipeItem.isNull()) {
                    return null;
                }
                itemDescriptors.add(new DefaultDescriptor(recipeItem));
            }
        }

        return switch (craftingBlock) {
            case "crafting_table" -> new ShapelessRecipe(id, priority, result, itemDescriptors);
            case "shulker_box" -> new ShulkerBoxRecipe(id, priority, result, itemDescriptors);
            case "stonecutter" -> new StonecutterRecipe(id, priority, result, itemDescriptors.get(0).toItem());
            case "cartography_table" -> new CartographyRecipe(id, priority, result, itemDescriptors);
            default -> null;
        };
    }

    @PowerNukkitXOnly
    @Since("1.19.50-r2")
    @SuppressWarnings("unchecked")
    private Recipe parseShapeRecipe(Map<String, Object> recipeObject) {
        String id = recipeObject.containsKey("id") ? recipeObject.get("id").toString() : null;
        List<Map<String, Object>> outputs = (List<Map<String, Object>>) recipeObject.get("output");

        Map<String, Object> first = outputs.remove(0);
        String[] shape = ((List<String>) recipeObject.get("shape")).toArray(EmptyArrays.EMPTY_STRINGS);
        Map<Character, ItemDescriptor> ingredients = new CharObjectHashMap<>();
        List<Item> extraResults = new ArrayList<>();

        int priority = Utils.toInt(recipeObject.get("priority"));
        Item primaryResult = parseRecipeItem(first);
        if (primaryResult.isNull()) {
            return null;
        }

        for (Map<String, Object> data : outputs) {
            Item output = parseRecipeItem(data);
            if (output.isNull()) {
                return null;
            }
            extraResults.add(output);
        }

        Map<String, Map<String, Object>> input = (Map<String, Map<String, Object>>) recipeObject.get("input");
        for (Map.Entry<String, Map<String, Object>> ingredientEntry : input.entrySet()) {
            char ingredientChar = ingredientEntry.getKey().charAt(0);
            var ingredient = ingredientEntry.getValue();

            if (ingredient.containsKey("tag")) {
                var tag = ingredient.get("tag").toString();
                int count = ingredient.containsKey("count") ? ((Number) ingredient.get("count")).intValue() : 1;
                ingredients.put(ingredientChar, new ItemTagDescriptor(tag, count));
            } else {
                Item recipeItem = parseRecipeItem(ingredient);
                if (recipeItem.isNull()) {
                    return null;
                }
                ingredients.put(ingredientChar, new DefaultDescriptor(recipeItem));
            }
        }
        return new ShapedRecipe(id, priority, primaryResult, shape, ingredients, extraResults);
    }

    @PowerNukkitXDifference(info = "Recipe formats exported from proxypass before 1.19.40 are no longer supported", since = "1.19.50-r1")
    private Item parseRecipeItem(Map<String, Object> data) {
        Item item;

        String name = data.get("name").toString();
        int count = data.containsKey("count") ? ((Number) data.get("count")).intValue() : 1;

        //block item
        if (data.containsKey("block_states")) {
            try {
                item = NBTIO.getBlockHelper(new CompoundTag()
                        .putString("name", name)
                        .putCompound("states", NBTIO.read(Base64.getDecoder().decode(data.get("block_states").toString()), ByteOrder.LITTLE_ENDIAN))
                ).toItem();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            item.setCount(count);
            return item;
        }

        String nbt = (String) data.get("nbt");
        byte[] nbtBytes = nbt != null ? Base64.getDecoder().decode(nbt) : EmptyArrays.EMPTY_BYTES;

        Integer meta = null;
        if (data.containsKey("meta")) {
            meta = Utils.toInt(data.get("meta"));
        }
        if (meta != null) {
            if (meta == Short.MAX_VALUE) {
                item = Item.fromString(name).createFuzzyCraftingRecipe();
            } else {
                item = Item.fromString(name + ":" + meta);
            }
        } else {
            item = Item.fromString(name);
        }

        item.setCount(count);
        item.setCompoundTag(nbtBytes);
        return item;
    }
    //</editor-fold>

    //<editor-fold desc="getters" defaultstate="collapsed">
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static DataPacket getCraftingPacket() {
        return packet;
    }

    @PowerNukkitOnly("Public only in PowerNukkit")
    public static UUID getMultiItemHash(Collection<Item> items) {
        BinaryStream stream = new BinaryStream();
        for (Item item : items) {
            stream.putVarInt(getFullItemHash(item));
        }
        return UUID.nameUUIDFromBytes(stream.getBuffer());
    }

    @PowerNukkitXOnly
    @Since("1.19.50-r2")
    public static UUID getItemWithItemDescriptorsHash(Collection<Item> items, Collection<ItemDescriptor> itemDescriptors) {
        BinaryStream stream = new BinaryStream();
        for (Item item : items) {
            stream.putVarInt(getFullItemHash(item));
        }
        for (var des : itemDescriptors) {
            if (des instanceof ItemTagDescriptor) {
                stream.putVarInt(des.hashCode());
            }
        }
        return UUID.nameUUIDFromBytes(stream.getBuffer());
    }

    @Since("1.19.50-r3")
    @PowerNukkitXOnly
    public static UUID getShapelessItemDescriptorHash(Collection<ItemDescriptor> itemDescriptors) {
        var stream = new BinaryStream();
        itemDescriptors.stream().mapToInt(Objects::hashCode).sorted().forEachOrdered(stream::putVarInt);
        return UUID.nameUUIDFromBytes(stream.getBuffer());
    }

    @Since("1.19.50-r3")
    @PowerNukkitXOnly
    public static UUID getShapelessMultiItemHash(Collection<Item> items) {
        var stream = new BinaryStream();
        items.stream().mapToInt(CraftingManager::getFullItemHash).sorted().forEachOrdered(stream::putVarInt);
        return UUID.nameUUIDFromBytes(stream.getBuffer());
    }

    @PowerNukkitOnly("Public only in PowerNukkit")
    @Since("FUTURE")
    public static int getFullItemHash(Item item) {
        return 31 * getItemHash(item) + item.getCount();
    }

    @PowerNukkitOnly("Public only in PowerNukkit")
    @Since("FUTURE")
    public static int getItemHash(Item item) {
        return getItemHash(item, item.getDamage());
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public static int getItemHash(Item item, int meta) {
        int id = item.getId();
        int hash = 31 + (id << 8 | meta & 0xFF);
        hash *= 31 + (id == ItemID.STRING_IDENTIFIED_ITEM && item instanceof StringItem ?
                item.getNamespaceId().hashCode()
                : 0);
        return hash;
    }

    @PowerNukkitOnly("Public only in PowerNukkit")
    public static int getPotionHash(Item ingredient, Item potion) {
        int ingredientId = ingredient.getId();
        int potionId = potion.getId();
        int hash = 17;
        hash *= 31 + ingredientId;
        hash *= 31 + (ingredientId == ItemID.STRING_IDENTIFIED_ITEM ? ingredient.getNamespaceId().hashCode() : 0);
        hash *= 31 + potion.getDamage();
        hash *= 31 + potionId;
        hash *= 31 + (potionId == ItemID.STRING_IDENTIFIED_ITEM ? potion.getNamespaceId().hashCode() : 0);
        hash *= 31 + potion.getDamage();
        return hash;
    }

    @PowerNukkitOnly
    public static int getContainerHash(@NotNull Item ingredient, @NotNull Item container) {
        int ingredientId = ingredient.getId();
        int containerId = container.getId();
        int hash = 17;
        hash *= 31 + ingredientId;
        hash *= 31 + (ingredientId == ItemID.STRING_IDENTIFIED_ITEM ? ingredient.getNamespaceId().hashCode() : 0);
        hash *= 31 + containerId;
        hash *= 31 + (containerId == ItemID.STRING_IDENTIFIED_ITEM ? container.getNamespaceId().hashCode() : 0);
        return hash;
    }

    @PowerNukkitOnly
    public Int2ObjectMap<Map<UUID, ShapedRecipe>> getShapedRecipeMap() {
        return shapedRecipeMap;
    }

    @PowerNukkitOnly
    public Int2ObjectMap<FurnaceRecipe> getFurnaceRecipesMap() {
        return furnaceRecipeMap;
    }

    @PowerNukkitOnly
    public Int2ObjectMap<BlastFurnaceRecipe> getBlastFurnaceRecipeMap() {
        return blastFurnaceRecipeMap;
    }

    @PowerNukkitOnly
    public Int2ObjectMap<SmokerRecipe> getSmokerRecipeMap() {
        return smokerRecipeMap;
    }

    @PowerNukkitOnly
    public Int2ObjectMap<CampfireRecipe> getCampfireRecipeMap() {
        return campfireRecipeMap;
    }


    @PowerNukkitOnly
    public Map<UUID, MultiRecipe> getMultiRecipeMap() {
        return multiRecipeMap;
    }

    @PowerNukkitOnly
    public Int2ObjectMap<BrewingRecipe> getBrewingRecipeMap() {
        return brewingRecipeMap;
    }

    @PowerNukkitOnly
    public Int2ObjectMap<ContainerRecipe> getContainerRecipeMap() {
        return containerRecipeMap;
    }

    @PowerNukkitOnly
    public Int2ObjectMap<StonecutterRecipe> getStonecutterRecipeMap() {
        return stonecutterRecipeMap;
    }

    @PowerNukkitOnly
    protected Int2ObjectMap<Map<UUID, ShapelessRecipe>> getShapelessRecipeMap() {
        return shapelessRecipeMap;
    }

    @PowerNukkitOnly
    public Int2ObjectMap<Map<UUID, CartographyRecipe>> getCartographyRecipeMap() {
        return cartographyRecipeMap;
    }

    @PowerNukkitOnly
    public Int2ObjectMap<Map<UUID, SmithingRecipe>> getSmithingRecipeMap() {
        return smithingRecipeMap;
    }

    @Since("1.19.50-r3")
    @PowerNukkitXOnly
    public Object2DoubleOpenHashMap<Recipe> getRecipeXpMap() {
        return recipeXpMap;
    }

    // Get Mod-Processing Recipes
    @Since("1.19.50-r3")
    @PowerNukkitXOnly
    public Map<String, Map<UUID, ModProcessRecipe>> getModProcessRecipeMap() {
        return modProcessRecipeMap;
    }

    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE", replaceWith = "getFurnaceRecipeMap()",
            reason = "The other provides a specialized map which performs better")
    public Map<Integer, FurnaceRecipe> getFurnaceRecipes() {
        return furnaceRecipes;
    }

    public Collection<Recipe> getRecipes() {
        return recipeList;
    }

    public static int getRecipeCount() {
        return RECIPE_COUNT;
    }

    //</editor-fold>

    public void rebuildPacket() {
        CraftingDataPacket pk = new CraftingDataPacket();
        pk.cleanRecipes = true;

        for (Map<UUID, ShapedRecipe> map : getShapedRecipeMap().values()) {
            for (ShapedRecipe recipe : map.values()) {
                pk.addShapedRecipe(recipe);
            }
        }

        for (Map<UUID, ShapelessRecipe> map : getShapelessRecipeMap().values()) {
            for (ShapelessRecipe recipe : map.values()) {
                pk.addShapelessRecipe(recipe);
            }
        }

        for (Map<UUID, CartographyRecipe> map : getCartographyRecipeMap().values()) {
            for (CartographyRecipe recipe : map.values()) {
                pk.addCartographyRecipe(recipe);
            }
        }

        for (FurnaceRecipe recipe : getFurnaceRecipesMap().values()) {
            pk.addFurnaceRecipe(recipe);
        }

        for (MultiRecipe recipe : getMultiRecipeMap().values()) {
            pk.addMultiRecipe(recipe);
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

        for (StonecutterRecipe recipe : getStonecutterRecipeMap().values()) {
            pk.addStonecutterRecipe(recipe);
        }

        pk.tryEncode();
        //TODO: find out whats wrong with compression
        packet = pk.compress(Deflater.BEST_COMPRESSION);
    }

    public FurnaceRecipe matchFurnaceRecipe(Item input) {
        if (input.isNull()) {
            return null;
        }
        FurnaceRecipe recipe = getFurnaceRecipesMap().get(getItemHash(input));
        if (recipe == null) recipe = getFurnaceRecipesMap().get(getItemHash(input, 0));
        return recipe;
    }

    @PowerNukkitOnly
    public CampfireRecipe matchCampfireRecipe(Item input) {
        if (input.isNull()) {
            return null;
        }
        CampfireRecipe recipe = getCampfireRecipeMap().get(getItemHash(input));
        if (recipe == null) recipe = getCampfireRecipeMap().get(getItemHash(input, 0));
        return recipe;
    }

    @PowerNukkitOnly
    public BlastFurnaceRecipe matchBlastFurnaceRecipe(Item input) {
        if (input.isNull()) {
            return null;
        }
        BlastFurnaceRecipe recipe = getBlastFurnaceRecipeMap().get(getItemHash(input));
        if (recipe == null) recipe = getBlastFurnaceRecipeMap().get(getItemHash(input, 0));
        return recipe;
    }

    @PowerNukkitOnly
    public SmokerRecipe matchSmokerRecipe(Item input) {
        if (input.isNull()) {
            return null;
        }
        SmokerRecipe recipe = getSmokerRecipeMap().get(getItemHash(input));
        if (recipe == null) recipe = getSmokerRecipeMap().get(getItemHash(input, 0));
        return recipe;
    }

    public void registerRecipe(Recipe recipe) {
        recipe.registerToCraftingManager(this);
    }

    @PowerNukkitOnly
    public void registerCartographyRecipe(CartographyRecipe recipe) {
        this.registerShapelessRecipe(recipe);
    }

    public void registerShapedRecipe(ShapedRecipe recipe) {
        this.addRecipe(recipe);
        int resultHash = getItemHash(recipe.getResult());
        Map<UUID, ShapedRecipe> map = getShapedRecipeMap().computeIfAbsent(resultHash, k -> new HashMap<>());
        List<Item> list1 = new LinkedList<>(recipe.getIngredientsAggregate());
        var list2 = recipe.getNewIngredientList();
        map.put(getItemWithItemDescriptorsHash(list1, list2), recipe);
    }

    public void registerShapelessRecipe(ShapelessRecipe recipe) {
        this.addRecipe(recipe);
        List<Item> list1 = recipe.getIngredientsAggregate();
        List<ItemDescriptor> list2 = recipe.getNewIngredients();

        UUID hash = getItemWithItemDescriptorsHash(list1, list2);

        int resultHash = getItemHash(recipe.getResult());
        Map<UUID, ShapelessRecipe> map = getShapelessRecipeMap().computeIfAbsent(resultHash, k -> new HashMap<>());

        map.put(hash, recipe);
    }

    @PowerNukkitOnly
    public void registerStonecutterRecipe(StonecutterRecipe recipe) {
        this.addRecipe(recipe);
        getStonecutterRecipeMap().put(getItemHash(recipe.getResult()), recipe);
    }

    public void registerFurnaceRecipe(FurnaceRecipe recipe) {
        this.addRecipe(recipe);
        Item input = recipe.getInput();
        getFurnaceRecipesMap().put(getItemHash(input), recipe);
    }

    @PowerNukkitOnly
    public void registerBlastFurnaceRecipe(BlastFurnaceRecipe recipe) {
        this.addRecipe(recipe);
        Item input = recipe.getInput();
        getBlastFurnaceRecipeMap().put(getItemHash(input), recipe);
    }

    @PowerNukkitOnly
    public void registerSmokerRecipe(SmokerRecipe recipe) {
        this.addRecipe(recipe);
        Item input = recipe.getInput();
        getSmokerRecipeMap().put(getItemHash(input), recipe);
    }

    @PowerNukkitOnly
    public void registerCampfireRecipe(CampfireRecipe recipe) {
        this.addRecipe(recipe);
        Item input = recipe.getInput();
        getCampfireRecipeMap().put(getItemHash(input), recipe);
    }

    @Since("1.19.50-r3")
    @PowerNukkitXOnly
    public void registerModProcessRecipe(@NotNull ModProcessRecipe recipe) {
        this.addRecipe(recipe);
        var map = getModProcessRecipeMap().computeIfAbsent(recipe.getCategory(), k -> new HashMap<>());
        var inputHash = getShapelessItemDescriptorHash(recipe.getIngredients());
        map.put(inputHash, recipe);
    }


    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void registerSmithingRecipe(@NotNull SmithingRecipe recipe) {
        this.addRecipe(recipe);
        List<Item> list1 = recipe.getIngredientsAggregate();
        List<ItemDescriptor> list2 = recipe.getNewIngredients();

        UUID hash = getItemWithItemDescriptorsHash(list1, list2);

        int resultHash = getItemHash(recipe.getResult());
        Map<UUID, ShapelessRecipe> map1 = getShapelessRecipeMap().computeIfAbsent(resultHash, k -> new HashMap<>());
        Map<UUID, SmithingRecipe> map2 = getSmithingRecipeMap().computeIfAbsent(resultHash, k -> new HashMap<>());
        map1.put(hash, recipe);
        map2.put(hash, recipe);
    }

    public void registerBrewingRecipe(BrewingRecipe recipe) {
        this.addRecipe(recipe);
        Item input = recipe.getIngredient();
        Item potion = recipe.getInput();

        int potionHash = getPotionHash(input, potion);
        var brewingRecipes = getBrewingRecipeMap();
        if (brewingRecipes.containsKey(potionHash)) {
            log.warn("The brewing recipe {} is being replaced by {}", brewingRecipes.get(potionHash), recipe);
        }
        brewingRecipes.put(potionHash, recipe);
    }

    public void registerContainerRecipe(ContainerRecipe recipe) {
        this.addRecipe(recipe);
        Item input = recipe.getIngredient();
        Item potion = recipe.getInput();

        getContainerRecipeMap().put(getContainerHash(input, potion), recipe);
    }

    @Since("1.4.0.0-PN")
    public void registerMultiRecipe(MultiRecipe recipe) {
        this.addRecipe(recipe);
        getMultiRecipeMap().put(recipe.getId(), recipe);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nullable
    public SmithingRecipe matchSmithingRecipe(Item equipment, Item ingredient) {
        List<Item> inputList = new ArrayList<>(2);
        inputList.add(equipment.decrement(equipment.count - 1));
        inputList.add(ingredient.decrement(ingredient.count - 1));
        return matchSmithingRecipe(inputList);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nullable
    public SmithingRecipe matchSmithingRecipe(@NotNull List<Item> inputList) {
        inputList.sort(recipeComparator);
        UUID inputHash = getMultiItemHash(inputList);

        return getSmithingRecipeMap().values().stream().flatMap(map -> map.entrySet().stream())
                .filter(entry -> entry.getKey().equals(inputHash))
                .map(Map.Entry::getValue)
                .findFirst().orElseGet(() ->
                        getSmithingRecipeMap().values().stream().flatMap(map -> map.values().stream())
                                .filter(recipe -> recipe.matchItems(inputList))
                                .findFirst().orElse(null)
                );
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nullable
    public SmithingRecipe matchSmithingRecipe(@NotNull Item equipment, @NotNull Item ingredient, @NotNull Item primaryOutput) {
        List<Item> inputList = Arrays.asList(equipment, ingredient);
        return matchSmithingRecipe(inputList, primaryOutput);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public SmithingRecipe matchSmithingRecipe(@NotNull List<Item> inputList, @NotNull Item primaryOutput) {
        int outputHash = getItemHash(primaryOutput);
        if (!getSmithingRecipeMap().containsKey(outputHash)) {
            return null;
        }

        inputList.sort(recipeComparator);

        UUID inputHash = getMultiItemHash(inputList);

        Map<UUID, SmithingRecipe> recipeMap = getSmithingRecipeMap().get(outputHash);

        if (recipeMap != null) {
            SmithingRecipe recipe = recipeMap.get(inputHash);

            if (recipe != null && (recipe.matchItems(inputList) || matchItemsAccumulation(recipe, inputList, primaryOutput))) {
                return recipe;
            }

            for (SmithingRecipe smithingRecipe : recipeMap.values()) {
                if (smithingRecipe.matchItems(inputList) || matchItemsAccumulation(smithingRecipe, inputList, primaryOutput)) {
                    return smithingRecipe;
                }
            }
        }

        return null;
    }

    public BrewingRecipe matchBrewingRecipe(Item input, Item potion) {
        return getBrewingRecipeMap().get(getPotionHash(input, potion));
    }

    public ContainerRecipe matchContainerRecipe(Item input, Item potion) {
        return getContainerRecipeMap().get(getContainerHash(input, potion));
    }

    @PowerNukkitOnly
    public StonecutterRecipe matchStonecutterRecipe(Item output) {
        return getStonecutterRecipeMap().get(getItemHash(output));
    }

    @PowerNukkitOnly
    public CartographyRecipe matchCartographyRecipe(List<Item> inputList, Item primaryOutput, List<Item> extraOutputList) {
        int outputHash = getItemHash(primaryOutput);

        if (getCartographyRecipeMap().containsKey(outputHash)) {
            inputList.sort(recipeComparator);

            UUID inputHash = getMultiItemHash(inputList);

            Map<UUID, CartographyRecipe> recipes = getCartographyRecipeMap().get(outputHash);

            if (recipes == null) {
                return null;
            }

            CartographyRecipe recipe = recipes.get(inputHash);

            if (recipe != null && (recipe.matchItems(inputList, extraOutputList) || matchItemsAccumulation(recipe, inputList, primaryOutput, extraOutputList))) {
                return recipe;
            }

            for (CartographyRecipe cartographyRecipe : recipes.values()) {
                if (cartographyRecipe.matchItems(inputList, extraOutputList) || matchItemsAccumulation(cartographyRecipe, inputList, primaryOutput, extraOutputList)) {
                    return cartographyRecipe;
                }
            }
        }

        return null;
    }

    public CraftingRecipe matchRecipe(List<Item> inputList, Item primaryOutput, List<Item> extraOutputList) {
        //TODO: try to match special recipes before anything else (first they need to be implemented!)

        int outputHash = getItemHash(primaryOutput);
        if (getShapedRecipeMap().containsKey(outputHash)) {
            inputList.sort(recipeComparator);

            UUID inputHash = getMultiItemHash(inputList);

            Map<UUID, ShapedRecipe> recipeMap = getShapedRecipeMap().get(outputHash);

            if (recipeMap != null) {
                ShapedRecipe recipe = recipeMap.get(inputHash);

                if (recipe != null && (recipe.matchItems(inputList, extraOutputList) || matchItemsAccumulation(recipe, inputList, primaryOutput, extraOutputList))) {
                    return recipe;
                }

                for (ShapedRecipe shapedRecipe : recipeMap.values()) {
                    if (shapedRecipe.matchItems(inputList, extraOutputList) || matchItemsAccumulation(shapedRecipe, inputList, primaryOutput, extraOutputList)) {
                        return shapedRecipe;
                    }
                }
            }
        }

        if (getShapelessRecipeMap().containsKey(outputHash)) {
            inputList.sort(recipeComparator);

            UUID inputHash = getMultiItemHash(inputList);

            Map<UUID, ShapelessRecipe> recipes = getShapelessRecipeMap().get(outputHash);

            if (recipes == null) {
                return null;
            }

            ShapelessRecipe recipe = recipes.get(inputHash);

            if (recipe != null && (recipe.matchItems(inputList, extraOutputList) || matchItemsAccumulation(recipe, inputList, primaryOutput, extraOutputList))) {
                return recipe;
            }

            for (ShapelessRecipe shapelessRecipe : recipes.values()) {
                if (shapelessRecipe.matchItems(inputList, extraOutputList) || matchItemsAccumulation(shapelessRecipe, inputList, primaryOutput, extraOutputList)) {
                    return shapelessRecipe;
                }
            }
        }

        return null;
    }

    private boolean matchItemsAccumulation(SmithingRecipe recipe, List<Item> inputList, Item primaryOutput) {
        Item recipeResult = recipe.getResult();
        if (primaryOutput.equals(recipeResult, recipeResult.hasMeta(), recipeResult.hasCompoundTag()) && primaryOutput.getCount() % recipeResult.getCount() == 0) {
            int multiplier = primaryOutput.getCount() / recipeResult.getCount();
            return recipe.matchItems(inputList, multiplier);
        }
        return false;
    }

    private boolean matchItemsAccumulation(CraftingRecipe recipe, List<Item> inputList, Item primaryOutput, List<Item> extraOutputList) {
        Item recipeResult = recipe.getResult();
        if (primaryOutput.equals(recipeResult, recipeResult.hasMeta(), recipeResult.hasCompoundTag()) && primaryOutput.getCount() % recipeResult.getCount() == 0) {
            int multiplier = primaryOutput.getCount() / recipeResult.getCount();
            return recipe.matchItems(inputList, extraOutputList, multiplier);
        }
        return false;
    }

    @Since("1.19.50-r3")
    @PowerNukkitXOnly
    @Nullable
    public ModProcessRecipe matchModProcessRecipe(@NotNull String category, @NotNull List<Item> inputList) {
        var recipeMap = getModProcessRecipeMap();
        var subMap = recipeMap.get(category);
        if (subMap != null) {
            var uuid = getMultiItemHash(inputList);
            var recipe = subMap.get(uuid);
            if (recipe != null) return recipe;
            for (var modProcessRecipe : subMap.values()) {
                if (modProcessRecipe.matchItems(Collections.unmodifiableList(inputList))) {
                    return modProcessRecipe;
                }
            }
        }
        return null;
    }

    @Since("1.19.50-r3")
    @PowerNukkitXOnly
    public void setRecipeXp(Recipe recipe, double xp) {
        recipeXpMap.put(recipe, xp);
    }

    @PowerNukkitXOnly
    @Since("1.19.50-r3")
    public static void setCraftingPacket(DataPacket craftingPacket) {
        CraftingManager.packet = craftingPacket;
    }

    @Since("1.19.50-r3")
    @PowerNukkitXOnly
    public double getRecipeXp(Recipe recipe) {
        return recipeXpMap.getOrDefault(recipe, 0.0);
    }

    private void addRecipe(Recipe recipe) {
        ++RECIPE_COUNT;
        if (recipe instanceof CraftingRecipe || recipe instanceof StonecutterRecipe) {
            UUID id = Utils.dataToUUID(String.valueOf(RECIPE_COUNT), String.valueOf(recipe.getResult().getId()), String.valueOf(recipe.getResult().getDamage()), String.valueOf(recipe.getResult().getCount()), Arrays.toString(recipe.getResult().getCompoundTag()));
            if (recipe instanceof CraftingRecipe) {
                ((CraftingRecipe) recipe).setId(id);
            } else {
                ((StonecutterRecipe) recipe).setId(id);
            }
        }
        this.recipeList.add(recipe);
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
