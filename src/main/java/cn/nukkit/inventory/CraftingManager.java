package cn.nukkit.inventory;

import cn.nukkit.Server;
import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXDifference;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockID;
import cn.nukkit.item.*;
import cn.nukkit.network.protocol.CraftingDataPacket;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.utils.BinaryStream;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.Utils;
import io.netty.util.collection.CharObjectHashMap;
import io.netty.util.internal.EmptyArrays;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.zip.Deflater;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@Log4j2
public class CraftingManager {

    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE", reason = "Direct access to fields are not future-proof.",
            replaceWith = "getPacket()")
    @Since("1.5.0.0-PN")
    public static DataPacket packet = null;

    private static int RECIPE_COUNT = 0;
    private final Int2ObjectMap<Map<UUID, ShapedRecipe>> shapedRecipeMap = new Int2ObjectOpenHashMap<>();
    private final Int2ObjectMap<FurnaceRecipe> furnaceRecipeMap = new Int2ObjectOpenHashMap<>();
    private final Int2ObjectMap<BlastFurnaceRecipe> blastFurnaceRecipeMap = new Int2ObjectOpenHashMap<>();
    private final Int2ObjectMap<CampfireRecipe> campfireRecipeMap = new Int2ObjectOpenHashMap<>();
    private final Int2ObjectMap<SmokerRecipe> smokerRecipeMap = new Int2ObjectOpenHashMap<>();
    private final Map<UUID, MultiRecipe> multiRecipeMap = new HashMap<>();
    private final Int2ObjectMap<BrewingRecipe> brewingRecipeMap = new Int2ObjectOpenHashMap<>();
    private final Int2ObjectMap<ContainerRecipe> containerRecipeMap = new Int2ObjectOpenHashMap<>();
    private final Int2ObjectMap<StonecutterRecipe> stonecutterRecipeMap = new Int2ObjectOpenHashMap<>();
    private final Int2ObjectMap<Map<UUID, ShapelessRecipe>> shapelessRecipeMap = new Int2ObjectOpenHashMap<>();
    private final Int2ObjectMap<Map<UUID, CartographyRecipe>> cartographyRecipeMap = new Int2ObjectOpenHashMap<>();
    private final Int2ObjectOpenHashMap<Map<UUID, SmithingRecipe>> smithingRecipeMap = new Int2ObjectOpenHashMap<>();
    private final Deque<Recipe> recipeList = new ArrayDeque<>();
    private final ItemTags itemTags = new ItemTags();

    //<editor-fold desc="deprecated fields" defaultstate="collapsed">
    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE", reason = "Direct access to fields are not future-proof.")
    public final Collection<Recipe> recipes = recipeList;

    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE", reason = "Direct access to fields are not future-proof.")
    protected final Map<Integer, Map<UUID, ShapedRecipe>> shapedRecipes = shapedRecipeMap;

    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE", reason = "Direct access to fields are not future-proof.")
    public final Map<Integer, FurnaceRecipe> furnaceRecipes = furnaceRecipeMap;

    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE", reason = "Direct access to fields are not future-proof.")
    @PowerNukkitOnly
    public final Map<Integer, BlastFurnaceRecipe> blastFurnaceRecipes = blastFurnaceRecipeMap;

    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE", reason = "Direct access to fields are not future-proof.")
    @PowerNukkitOnly
    public final Map<Integer, SmokerRecipe> smokerRecipes = smokerRecipeMap;

    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE", reason = "Direct access to fields are not future-proof.")
    @PowerNukkitOnly
    public final Map<Integer, CampfireRecipe> campfireRecipes = campfireRecipeMap;

    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE", reason = "Direct access to fields are not future-proof.")
    @Since("1.4.0.0-PN")
    public final Map<UUID, MultiRecipe> multiRecipes = multiRecipeMap;

    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE", reason = "Direct access to fields are not future-proof.")
    public final Map<Integer, BrewingRecipe> brewingRecipes = brewingRecipeMap;

    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE", reason = "Direct access to fields are not future-proof.")
    public final Map<Integer, ContainerRecipe> containerRecipes = containerRecipeMap;

    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE", reason = "Direct access to fields are not future-proof.")
    @PowerNukkitOnly
    public final Map<Integer, StonecutterRecipe> stonecutterRecipes = stonecutterRecipeMap;

    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE", reason = "Direct access to fields are not future-proof.")
    protected final Map<Integer, Map<UUID, ShapelessRecipe>> shapelessRecipes = shapelessRecipeMap;

    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE", reason = "Direct access to fields are not future-proof.")
    @PowerNukkitOnly
    protected final Map<Integer, Map<UUID, CartographyRecipe>> cartographyRecipes = cartographyRecipeMap;
    //</editor-fold>

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

    //<editor-fold desc="getters" defaultstate="collapsed">
    @PowerNukkitOnly
    @Since("FUTURE")
    public Int2ObjectMap<Map<UUID, ShapedRecipe>> getShapedRecipeMap() {
        return shapedRecipeMap;
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public Int2ObjectMap<FurnaceRecipe> getFurnaceRecipesMap() {
        return furnaceRecipeMap;
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public Int2ObjectMap<BlastFurnaceRecipe> getBlastFurnaceRecipeMap() {
        return blastFurnaceRecipeMap;
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public Int2ObjectMap<SmokerRecipe> getSmokerRecipeMap() {
        return smokerRecipeMap;
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public Int2ObjectMap<CampfireRecipe> getCampfireRecipeMap() {
        return campfireRecipeMap;
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public Map<UUID, MultiRecipe> getMultiRecipeMap() {
        return multiRecipeMap;
    }


    @PowerNukkitOnly
    @Since("FUTURE")
    public Int2ObjectMap<BrewingRecipe> getBrewingRecipeMap() {
        return brewingRecipeMap;
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public Int2ObjectMap<ContainerRecipe> getContainerRecipeMap() {
        return containerRecipeMap;
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public Int2ObjectMap<StonecutterRecipe> getStonecutterRecipeMap() {
        return stonecutterRecipeMap;
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    protected Int2ObjectMap<Map<UUID, ShapelessRecipe>> getShapelessRecipeMap() {
        return shapelessRecipeMap;
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public Int2ObjectMap<Map<UUID, CartographyRecipe>> getCartographyRecipeMap() {
        return cartographyRecipeMap;
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public Int2ObjectMap<Map<UUID, SmithingRecipe>> getSmithingRecipeMap() {
        return smithingRecipeMap;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static DataPacket getCraftingPacket() {
        return packet;
    }

    public Collection<Recipe> getRecipes() {
        return recipes;
    }

    @Deprecated
    @DeprecationDetails(by = "PowerNukkit", since = "FUTURE", replaceWith = "getFurnaceRecipeMap()",
            reason = "The other provides a specialized map which performs better")
    public Map<Integer, FurnaceRecipe> getFurnaceRecipes() {
        return furnaceRecipes;
    }
    //</editor-fold>

    //<editor-fold desc="constructors and setup" defaultstate="collapsed">
    public CraftingManager() {
        InputStream recipesStream = null;
        try {
            recipesStream = Server.class.getModule().getResourceAsStream("recipes.json");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (recipesStream == null) {
            throw new AssertionError("Unable to find recipes.json");
        }

        Config recipesConfig = new Config(Config.JSON);
        recipesConfig.load(recipesStream);
        this.loadRecipes(recipesConfig);

        String path = Server.getInstance().getDataPath() + "custom_recipes.json";
        File filePath = new File(path);

        if (filePath.exists()) {
            Config customRecipes = new Config(filePath, Config.JSON);
            this.loadRecipes(customRecipes);
        }
        this.rebuildPacket();

        log.info("Loaded {} recipes.", this.recipes.size());
    }

    private CraftingManager(Boolean Test) {
    }

    @SuppressWarnings("unchecked")
    private void loadRecipes(Config config) {
        List<Map> recipes = config.getMapList("recipes");
        log.info("Loading recipes...");
        toNextRecipe:
        for (Map<String, Object> recipe : recipes) {
            try {
                int type = Utils.toInt(recipe.get("type"));
                switch (type) {
                    case 0:
                    case 5:
                        String craftingBlock = (String) recipe.get("block");
                        if (type == 5) {
                            craftingBlock = "shulker_box";
                        }
                        if (!"crafting_table".equals(craftingBlock) && !"stonecutter".equals(craftingBlock)
                                && !"cartography_table".equalsIgnoreCase(craftingBlock) && !"shulker_box".equalsIgnoreCase(craftingBlock)
                                && !"smithing_table".equalsIgnoreCase(craftingBlock)) {
                            // Ignore other recipes than crafting table, stonecutter, smithing_table and cartography table
                            continue;
                        }
                        var register = parseUnShapeRecipe(recipe, craftingBlock);
                        if (register == null) continue toNextRecipe;
                        for (var reg : register) {
                            this.registerRecipe(reg);
                        }
                        break;
                    case 1:
                        craftingBlock = (String) recipe.get("block");
                        if (!"crafting_table".equals(craftingBlock)) {
                            // Ignore other recipes than crafting table ones
                            continue;
                        }
                        register = parseShapeRecipe(recipe);
                        if (register == null) continue toNextRecipe;
                        for (var reg : register) {
                            this.registerRecipe(reg);
                        }
                        break;
                    case 2:
                    case 3:
                        craftingBlock = (String) recipe.get("block");
                        if (!"furnace".equals(craftingBlock) && !"blast_furnace".equals(craftingBlock)
                                && !"smoker".equals(craftingBlock) && !"campfire".equals(craftingBlock)) {
                            // Ignore other recipes than furnaces, blast furnaces, smokers and campfire
                            continue;
                        }
                        Map<String, Object> resultMap = (Map) recipe.get("output");
                        Item resultItem = parseRecipeItem(resultMap);
                        if (resultItem.isNull()) {
                            continue toNextRecipe;
                        }
                        Item inputItem;
                        try {
                            Map<String, Object> inputMap = (Map) recipe.get("input");
                            inputItem = parseRecipeItem(inputMap);
                        } catch (Exception old) {
                            inputItem = Item.get(Utils.toInt(recipe.get("inputId")), recipe.containsKey("inputDamage") ? Utils.toInt(recipe.get("inputDamage")) : -1, 1);
                        }
                        if (inputItem.isNull()) {
                            continue toNextRecipe;
                        }
                        switch (craftingBlock) {
                            case "furnace":
                                this.registerRecipe(new FurnaceRecipe(resultItem, inputItem));
                                break;
                            case "blast_furnace":
                                this.registerRecipe(new BlastFurnaceRecipe(resultItem, inputItem));
                                break;
                            case "smoker":
                                this.registerRecipe(new SmokerRecipe(resultItem, inputItem));
                                break;
                            case "campfire":
                                this.registerRecipe(new CampfireRecipe(resultItem, inputItem));
                                break;
                        }
                        break;
                    case 4:
                        this.registerRecipe(new MultiRecipe(UUID.fromString((String) recipe.get("uuid"))));
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                log.error("Exception during registering recipe", e);
            }
        }

        // Load brewing recipes
        List<Map> potionMixes = config.getMapList("potionMixes");

        RuntimeItemMapping runtimeMapping = RuntimeItems.getRuntimeMapping();
        for (Map potionMix : potionMixes) {
            String fromPotionId = potionMix.get("inputId").toString();
            int fromPotionMeta = ((Number) potionMix.get("inputMeta")).intValue();
            String ingredient = potionMix.get("reagentId").toString();
            int ingredientMeta = ((Number) potionMix.get("reagentMeta")).intValue();
            String toPotionId = potionMix.get("outputId").toString();
            int toPotionMeta = ((Number) potionMix.get("outputMeta")).intValue();

            registerBrewingRecipe(new BrewingRecipe(
                    Item.fromString(fromPotionId + ":" + fromPotionMeta),
                    Item.fromString(ingredient + ":" + ingredientMeta),
                    Item.fromString(toPotionId + ":" + toPotionMeta)
            ));
        }

        List<Map> containerMixes = config.getMapList("containerMixes");

        for (Map containerMix : containerMixes) {
            String fromItemId = containerMix.get("inputId").toString();
            String ingredient = containerMix.get("reagentId").toString();
            String toItemId = containerMix.get("outputId").toString();

            registerContainerRecipe(new ContainerRecipe(Item.fromString(fromItemId), Item.fromString(ingredient), Item.fromString(toItemId)));
        }

        // Allow to rename without crafting 
        registerCartographyRecipe(new CartographyRecipe(Item.get(ItemID.EMPTY_MAP), Collections.singletonList(Item.get(ItemID.EMPTY_MAP))));
        registerCartographyRecipe(new CartographyRecipe(Item.get(ItemID.EMPTY_MAP, 2), Collections.singletonList(Item.get(ItemID.EMPTY_MAP, 2))));
        registerCartographyRecipe(new CartographyRecipe(Item.get(ItemID.MAP), Collections.singletonList(Item.get(ItemID.MAP))));
        registerCartographyRecipe(new CartographyRecipe(Item.get(ItemID.MAP, 3), Collections.singletonList(Item.get(ItemID.MAP, 3))));
        registerCartographyRecipe(new CartographyRecipe(Item.get(ItemID.MAP, 4), Collections.singletonList(Item.get(ItemID.MAP, 4))));
        registerCartographyRecipe(new CartographyRecipe(Item.get(ItemID.MAP, 5), Collections.singletonList(Item.get(ItemID.MAP, 5))));
    }

    @SuppressWarnings("unchecked")
    public List<Recipe> parseUnShapeRecipe(Map<String, Object> recipeObject, String craftingBlock) {
        List<Map> outputs = ((List<Map>) recipeObject.get("output"));
        List<Map> inputs = ((List<Map>) recipeObject.get("input"));
        // TODO: handle multiple result items
        if (outputs.size() > 1) {
            return null;
        }
        Map<String, Object> first = outputs.get(0);
        List<Item> sorted = new ArrayList<>();
        List<List<Item>> tagsItem = new ArrayList<>();

        for (Map<String, Object> ingredient : inputs) {
            String inputType = ingredient.containsKey("type") ? ingredient.get("type").toString() : "default";
            if (inputType.equals("default")) {
                Item recipeItem = parseRecipeItem(ingredient);
                if (recipeItem.isNull()) {
                    return null;
                }
                sorted.add(recipeItem);
            } else if (inputType.equals("item_tag")) {
                var items = itemTags.fromTags(ingredient.get("itemTag").toString());
                int count = ingredient.containsKey("count") ? ((Number) ingredient.get("count")).intValue() : 1;
                items.forEach(i -> i.setCount(count));
                tagsItem.add(items);
            }
        }

        // Bake sorted list
        sorted.sort(recipeComparator);

        String recipeId = (String) recipeObject.get("id");
        int priority = Utils.toInt(recipeObject.get("priority"));

        Item result = parseRecipeItem(first);
        if (result.isNull()) {
            return null;
        }

        if (tagsItem.size() == 0) {
            switch (craftingBlock) {
                case "crafting_table":
                    return List.of(new ShapelessRecipe(recipeId, priority, result, sorted));
                case "shulker_box":
                    return List.of(new ShulkerBoxRecipe(recipeId, priority, result, sorted));
                case "stonecutter":
                    return List.of(new StonecutterRecipe(recipeId, priority, result, sorted.get(0)));
                case "cartography_table":
                    return List.of(new CartographyRecipe(recipeId, priority, result, sorted));
                case "smithing_table":
                    return List.of(new SmithingRecipe(recipeId, priority, sorted, result));
            }
        } else {
            //item_tag 类型只可能存在于crafting_table合成
            List<Recipe> recipes = new ArrayList<>();
            var list = new ArrayList<List<Item>>();
            tagsItem.add(sorted);
            tagsCombine(0, new ArrayList<>(), tagsItem, list);
            for (var sort : list) {
                recipes.add(new ShapelessRecipe(recipeId, priority, result, sort));
            }
            return recipes;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public List<Recipe> parseShapeRecipe(Map<String, Object> recipeObject) {
        List<Map> outputs = (List<Map>) recipeObject.get("output");

        Map<String, Object> first = outputs.remove(0);
        String[] shape = ((List<String>) recipeObject.get("shape")).toArray(EmptyArrays.EMPTY_STRINGS);
        Map<Character, List<Item>> ingredients = new CharObjectHashMap<>();
        List<Item> extraResults = new ArrayList<>();

        Map<String, Map<String, Object>> input = (Map) recipeObject.get("input");
        for (Map.Entry<String, Map<String, Object>> ingredientEntry : input.entrySet()) {
            char ingredientChar = ingredientEntry.getKey().charAt(0);
            var ingredient = ingredientEntry.getValue();

            String inputType = ingredient.containsKey("type") ? ingredient.get("type").toString() : "default";
            if (inputType.equals("default")) {
                Item recipeItem = parseRecipeItem(ingredient);
                if (recipeItem.isNull()) {
                    return null;
                }
                ingredients.put(ingredientChar, List.of(recipeItem));
            } else if (inputType.equals("item_tag")) {
                var items = itemTags.fromTags(ingredient.get("itemTag").toString());
                int count = ingredient.containsKey("count") ? ((Number) ingredient.get("count")).intValue() : 1;
                items.forEach(i -> i.setCount(count));
                ingredients.put(ingredientChar, items);
            }
        }

        for (Map<String, Object> data : outputs) {
            Item output = parseRecipeItem(data);
            if (output.isNull()) {
                return null;
            }
            extraResults.add(output);
        }

        String recipeId = (String) recipeObject.get("id");
        int priority = Utils.toInt(recipeObject.get("priority"));

        Item primaryResult = parseRecipeItem(first);
        if (primaryResult.isNull()) {
            return null;
        }
        var recipes = new ArrayList<Recipe>();
        var result = new ArrayList<Map<Character, Item>>();
        tagsCombine(0, new HashMap<>(), ingredients, result);

        for (var r : result) {
            recipes.add(new ShapedRecipe(recipeId, priority, primaryResult, shape, r, extraResults));
        }
        return recipes;
    }

    private void tagsCombine(int i, Map<Character, Item> last, Map<Character, List<Item>> input, List<Map<Character, Item>> result) {
        if (i == input.size()) {
            result.add(new HashMap<>(last));
            return;
        }
        var entry = input.entrySet().stream().toList().get(i);
        for (var v : entry.getValue()) {
            last.put(entry.getKey(), v);
            tagsCombine(i + 1, last, input, result);
            last.remove(entry.getKey());
        }
    }

    private void tagsCombine(int i, List<Item> last, List<List<Item>> input, List<List<Item>> result) {
        if (i == input.size()) {
            result.add(new ArrayList<>(last));
            return;
        }
        for (var v : input.get(i)) {
            last.add(v);
            tagsCombine(i + 1, last, input, result);
            last.remove(i);
        }
    }

    @PowerNukkitXDifference(info = "Recipe formats exported from proxypass before 1.19.40 are no longer supported", since = "1.19.50-r1")
    private Item parseRecipeItem(Map<String, Object> data) {
        String nbt = (String) data.get("nbt_b64");
        byte[] nbtBytes = nbt != null ? Base64.getDecoder().decode(nbt) : EmptyArrays.EMPTY_BYTES;

        int count = data.containsKey("count") ? ((Number) data.get("count")).intValue() : 1;

        String id = null;
        if (data.containsKey("id")) {
            id = data.get("id").toString();
        }

        Integer damage = null;
        if (data.containsKey("damage")) {
            damage = Utils.toInt(data.get("damage"));
        } else if (data.containsKey("auxValue")) {
            damage = Utils.toInt(data.get("auxValue"));
        }

        Item item;
        Integer legacyId = null;
        if (data.containsKey("legacyId")) {
            legacyId = Utils.toInt(data.get("legacyId"));
        } else if (data.containsKey("itemId")) {
            legacyId = Utils.toInt(data.get("itemId"));
        }

        if (legacyId != null) {
            try {
                int fullId = RuntimeItems.getRuntimeMapping().getLegacyFullId(legacyId);
                int itemId = RuntimeItems.getId(fullId);
                Integer meta = null;
                if (RuntimeItems.hasData(fullId)) {
                    meta = RuntimeItems.getData(fullId);
                }

                boolean fuzzy = false;
                if (damage != null) {
                    if (damage == Short.MAX_VALUE) {
                        fuzzy = true;
                    } else if (meta == null) {
                        meta = damage;
                    }
                }

                item = Item.get(itemId, meta == null ? 0 : meta, count);
                if (fuzzy) {
                    item = item.createFuzzyCraftingRecipe();
                }

                item.setCompoundTag(nbtBytes);
                return item;
            } catch (IllegalArgumentException e) {
                log.debug("Failed to load a crafting recipe item, attempting to load by string id", e);
            }
        }

        if (id != null) {
            if (damage != null) {
                if (damage == Short.MAX_VALUE) {
                    item = Item.fromString(id).createFuzzyCraftingRecipe();
                } else {
                    item = Item.fromString(id + ":" + damage);
                }
            } else {
                item = Item.fromString(id);
            }
            item.setCount(count);
            item.setCompoundTag(nbtBytes);
            return item;
        }
        return Item.get(BlockID.AIR);
    }

    public void rebuildPacket() {
        CraftingDataPacket pk = new CraftingDataPacket();
        pk.cleanRecipes = true;

        for (Recipe recipe : getRecipes()) {
            if (recipe instanceof ShapedRecipe) {
                pk.addShapedRecipe((ShapedRecipe) recipe);
            } else if (recipe instanceof ShapelessRecipe) {
                pk.addShapelessRecipe((ShapelessRecipe) recipe);
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
        // TODO: find out whats wrong with compression
        packet = pk.compress(Deflater.BEST_COMPRESSION);
    }
    //</editor-fold>

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

    @PowerNukkitOnly("Public only in PowerNukkit")
    @Since("FUTURE")
    public static UUID getMultiItemHash(Collection<Item> items) {
        BinaryStream stream = new BinaryStream();
        for (Item item : items) {
            stream.putVarInt(getFullItemHash(item));
        }
        return UUID.nameUUIDFromBytes(stream.getBuffer());
    }

    @PowerNukkitOnly("Public only in PowerNukkit")
    @Since("FUTURE")
    public static int getFullItemHash(Item item) {
        return 31 * getItemHash(item) + item.getCount();
    }

    @PowerNukkitOnly
    public void registerStonecutterRecipe(StonecutterRecipe recipe) {
        getStonecutterRecipeMap().put(getItemHash(recipe.getResult()), recipe);
    }

    public void registerFurnaceRecipe(FurnaceRecipe recipe) {
        Item input = recipe.getInput();
        getFurnaceRecipesMap().put(getItemHash(input), recipe);
    }


    @PowerNukkitOnly
    public void registerBlastFurnaceRecipe(BlastFurnaceRecipe recipe) {
        Item input = recipe.getInput();
        getBlastFurnaceRecipeMap().put(getItemHash(input), recipe);
    }

    @PowerNukkitOnly
    public void registerSmokerRecipe(SmokerRecipe recipe) {
        Item input = recipe.getInput();
        getSmokerRecipeMap().put(getItemHash(input), recipe);
    }

    @PowerNukkitOnly
    public void registerCampfireRecipe(CampfireRecipe recipe) {
        Item input = recipe.getInput();
        getCampfireRecipeMap().put(getItemHash(input), recipe);
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

    public void registerShapedRecipe(ShapedRecipe recipe) {
        int resultHash = getItemHash(recipe.getResult());
        Map<UUID, ShapedRecipe> map = getShapedRecipeMap().computeIfAbsent(resultHash, k -> new HashMap<>());
        List<Item> inputList = new LinkedList<>(recipe.getIngredientsAggregate());
        map.put(getMultiItemHash(inputList), recipe);
    }


    public void registerRecipe(Recipe recipe) {
        UUID id = null;
        if (recipe instanceof CraftingRecipe || recipe instanceof StonecutterRecipe) {
            id = Utils.dataToUUID(String.valueOf(++RECIPE_COUNT), String.valueOf(recipe.getResult().getId()), String.valueOf(recipe.getResult().getDamage()), String.valueOf(recipe.getResult().getCount()), Arrays.toString(recipe.getResult().getCompoundTag()));
        }
        if (recipe instanceof CraftingRecipe) {
            ((CraftingRecipe) recipe).setId(id);
            getRecipes().add(recipe);
        } else if (recipe instanceof StonecutterRecipe) {
            ((StonecutterRecipe) recipe).setId(id);
        }

        recipe.registerToCraftingManager(this);
    }

    @PowerNukkitOnly
    public void registerCartographyRecipe(CartographyRecipe recipe) {
        List<Item> list = recipe.getIngredientList();
        list.sort(recipeComparator);

        UUID hash = getMultiItemHash(list);

        int resultHash = getItemHash(recipe.getResult());
        Map<UUID, CartographyRecipe> map = getCartographyRecipeMap().computeIfAbsent(resultHash, k -> new HashMap<>());

        map.put(hash, recipe);
    }

    public void registerShapelessRecipe(ShapelessRecipe recipe) {
        List<Item> list = recipe.getIngredientsAggregate();

        UUID hash = getMultiItemHash(list);

        int resultHash = getItemHash(recipe.getResult());
        Map<UUID, ShapelessRecipe> map = getShapelessRecipeMap().computeIfAbsent(resultHash, k -> new HashMap<>());

        map.put(hash, recipe);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void registerSmithingRecipe(@Nonnull SmithingRecipe recipe) {
        List<Item> list = recipe.getIngredientsAggregate();

        UUID hash = getMultiItemHash(list);

        int resultHash = getItemHash(recipe.getResult());
        Map<UUID, SmithingRecipe> map = getSmithingRecipeMap().computeIfAbsent(resultHash, k -> new HashMap<>());

        map.put(hash, recipe);
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
    public SmithingRecipe matchSmithingRecipe(@Nonnull List<Item> inputList) {
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
    public SmithingRecipe matchSmithingRecipe(@Nonnull Item equipment, @Nonnull Item ingredient, @Nonnull Item primaryOutput) {
        List<Item> inputList = Arrays.asList(equipment, ingredient);
        return matchSmithingRecipe(inputList, primaryOutput);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public SmithingRecipe matchSmithingRecipe(@Nonnull List<Item> inputList, @Nonnull Item primaryOutput) {
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

    @PowerNukkitOnly("Public only in PowerNukkit")
    @Since("FUTURE")
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
    @Since("FUTURE")
    public static int getContainerHash(@Nonnull Item ingredient, @Nonnull Item container) {
        int ingredientId = ingredient.getId();
        int containerId = container.getId();
        int hash = 17;
        hash *= 31 + ingredientId;
        hash *= 31 + (ingredientId == ItemID.STRING_IDENTIFIED_ITEM ? ingredient.getNamespaceId().hashCode() : 0);
        hash *= 31 + containerId;
        hash *= 31 + (containerId == ItemID.STRING_IDENTIFIED_ITEM ? container.getNamespaceId().hashCode() : 0);
        return hash;
    }

    public void registerBrewingRecipe(BrewingRecipe recipe) {
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
        Item input = recipe.getIngredient();
        Item potion = recipe.getInput();

        getContainerRecipeMap().put(getContainerHash(input, potion), recipe);
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

    @Since("1.4.0.0-PN")
    public void registerMultiRecipe(MultiRecipe recipe) {
        getMultiRecipeMap().put(recipe.getId(), recipe);
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
