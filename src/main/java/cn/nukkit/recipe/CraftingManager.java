package cn.nukkit.recipe;

import cn.nukkit.Server;
import cn.nukkit.inventory.BlastFurnaceRecipe;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemPotion;
import cn.nukkit.network.protocol.CraftingDataPacket;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.potion.Potion;
import cn.nukkit.utils.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.netty.util.collection.CharObjectHashMap;
import io.netty.util.internal.EmptyArrays;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.*;
import java.util.*;
import java.util.zip.Deflater;

/**
 * 用于管理合成配方
 * <p>
 * Used to manage crafting recipes
 *
 * @author MagicDroidX (Nukkit Project)
 */
@SuppressWarnings("unchecked")
@Slf4j
public class CraftingManager {
    //<editor-fold desc="deprecated fields" defaultstate="collapsed">
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
    public static final int SPECIAL_RECIPE_TYPE = 4;
    /**
     * 缓存着配方数据包
     */
    private static DataPacket packet = null;
    private static int RECIPE_COUNT = 0;
    private final VanillaRecipeParser vanillaRecipeParser;
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
    private final Map<String, Map<UUID, ModProcessRecipe>> modProcessRecipeMap = new HashMap<>();
    private final Object2DoubleOpenHashMap<Recipe> recipeXpMap = new Object2DoubleOpenHashMap<>();
    private final Map<String, Recipe> recipeList = new Object2ObjectOpenHashMap<>();
    //</editor-fold>

    //<editor-fold desc="constructors and setup" defaultstate="collapsed">
    public CraftingManager() {
        log.info("Loading recipes...");
        this.vanillaRecipeParser = new VanillaRecipeParser();
        this.loadRecipes();
        this.rebuildPacket();
        log.info("Loaded {} recipes.", this.recipeList.values().size());
    }
    //</editor-fold>

    //<editor-fold desc="getters" defaultstate="collapsed">
    public VanillaRecipeParser getVanillaRecipeParser() {
        return vanillaRecipeParser;
    }

    public double getRecipeXp(Recipe recipe) {
        return recipeXpMap.getOrDefault(recipe, 0.0);
    }

    public static DataPacket getCraftingPacket() {
        return packet;
    }

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

    public static UUID getItemWithTagsHash(Collection<Item> items, Collection<String> tags) {
        BinaryStream stream = new BinaryStream();
        for (Item item : items) {
            stream.putVarInt(getFullItemHash(item));
        }
        for (var des : tags) {
            stream.putVarInt(des.hashCode());
        }
        return UUID.nameUUIDFromBytes(stream.getBuffer());
    }

    public static UUID getShapelessItemDescriptorHash(Collection<ItemDescriptor> itemDescriptors) {
        var stream = new BinaryStream();
        itemDescriptors.stream().mapToInt(Objects::hashCode).sorted().forEachOrdered(stream::putVarInt);
        return UUID.nameUUIDFromBytes(stream.getBuffer());
    }

    public static UUID getMultiItemHash(Collection<Item> items) {
        BinaryStream stream = new BinaryStream();
        for (Item item : items) {
            stream.putVarInt(getFullItemHash(item));
        }
        return UUID.nameUUIDFromBytes(stream.getBuffer());
    }

    public static UUID getShapelessMultiItemHash(Collection<Item> items) {
        var stream = new BinaryStream();
        items.stream().mapToInt(CraftingManager::getFullItemHash).sorted().forEachOrdered(stream::putVarInt);
        return UUID.nameUUIDFromBytes(stream.getBuffer());
    }

    public static int getFullItemHash(Item item) {
        return getItemHash(item) + item.getCount();
    }

    public static int getItemHash(Item item) {
        String id = item.getId();
        return Objects.hash(id, item.getDamage());
    }

    public static int getPotionHash(Item ingredient, Item potion) {
        String ingredientId = ingredient.getId();
        String potionId = potion.getId();
        return Objects.hash(ingredientId, ingredient.getDamage(), potionId, potion.getDamage());
    }

    public static int getContainerHash(@NotNull Item ingredient, @NotNull Item container) {
        String ingredientId = ingredient.getId();
        String containerId = container.getId();
        return Objects.hash(ingredientId, ingredient.getDamage(), container, container.getDamage());
    }

    public Int2ObjectMap<Map<UUID, ShapedRecipe>> getShapedRecipeMap() {
        return shapedRecipeMap;
    }

    public Int2ObjectMap<FurnaceRecipe> getFurnaceRecipesMap() {
        return furnaceRecipeMap;
    }

    public Int2ObjectMap<BlastFurnaceRecipe> getBlastFurnaceRecipeMap() {
        return blastFurnaceRecipeMap;
    }

    public Int2ObjectMap<SmokerRecipe> getSmokerRecipeMap() {
        return smokerRecipeMap;
    }

    public Int2ObjectMap<CampfireRecipe> getCampfireRecipeMap() {
        return campfireRecipeMap;
    }

    public Map<UUID, MultiRecipe> getMultiRecipeMap() {
        return multiRecipeMap;
    }

    public Int2ObjectMap<BrewingRecipe> getBrewingRecipeMap() {
        return brewingRecipeMap;
    }

    public Int2ObjectMap<ContainerRecipe> getContainerRecipeMap() {
        return containerRecipeMap;
    }

    public Int2ObjectMap<StonecutterRecipe> getStonecutterRecipeMap() {
        return stonecutterRecipeMap;
    }

    public Int2ObjectMap<Map<UUID, ShapelessRecipe>> getShapelessRecipeMap() {
        return shapelessRecipeMap;
    }

    public Int2ObjectMap<Map<UUID, CartographyRecipe>> getCartographyRecipeMap() {
        return cartographyRecipeMap;
    }

    public Int2ObjectMap<Map<UUID, SmithingRecipe>> getSmithingRecipeMap() {
        return smithingRecipeMap;
    }

    public Object2DoubleOpenHashMap<Recipe> getRecipeXpMap() {
        return recipeXpMap;
    }

    // Get Mod-Processing Recipes


    public Map<String, Map<UUID, ModProcessRecipe>> getModProcessRecipeMap() {
        return modProcessRecipeMap;
    }

    public Collection<Recipe> getRecipes() {
        return recipeList.values();
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
        packet = pk;
    }

    public FurnaceRecipe matchFurnaceRecipe(Item input) {
        if (input.isNull()) {
            return null;
        }
        FurnaceRecipe recipe = getFurnaceRecipesMap().get(getItemHash(input));
        if (recipe == null) recipe = getFurnaceRecipesMap().get(getItemHash(input));
        return recipe;
    }

    public CampfireRecipe matchCampfireRecipe(Item input) {
        if (input.isNull()) {
            return null;
        }
        CampfireRecipe recipe = getCampfireRecipeMap().get(getItemHash(input));
        if (recipe == null) recipe = getCampfireRecipeMap().get(getItemHash(input));
        return recipe;
    }

    public BlastFurnaceRecipe matchBlastFurnaceRecipe(Item input) {
        if (input.isNull()) {
            return null;
        }
        BlastFurnaceRecipe recipe = getBlastFurnaceRecipeMap().get(getItemHash(input));
        if (recipe == null) recipe = getBlastFurnaceRecipeMap().get(getItemHash(input));
        return recipe;
    }

    public SmokerRecipe matchSmokerRecipe(Item input) {
        if (input.isNull()) {
            return null;
        }
        SmokerRecipe recipe = getSmokerRecipeMap().get(getItemHash(input));
        if (recipe == null) recipe = getSmokerRecipeMap().get(getItemHash(input));
        return recipe;
    }

    public void registerRecipe(Recipe recipe) {
        recipe.registerToCraftingManager(this);
    }

    public void registerCartographyRecipe(CartographyRecipe recipe) {
        this.addRecipe(recipe);
        List<Item> list1 = recipe.getIngredientsAggregate();
        List<ItemDescriptor> list2 = recipe.getNewIngredients();

        UUID hash = getItemWithItemDescriptorsHash(list1, list2);

        int resultHash = getItemHash(recipe.getResult());
        Map<UUID, ShapelessRecipe> map1 = getShapelessRecipeMap().computeIfAbsent(resultHash, k -> new HashMap<>());
        Map<UUID, CartographyRecipe> map2 = getCartographyRecipeMap().computeIfAbsent(resultHash, k -> new HashMap<>());
        map1.put(hash, recipe);
        map2.put(hash, recipe);
    }

    public void registerShapedRecipe(ShapedRecipe recipe) {
        this.addRecipe(recipe);
        int resultHash = getItemHash(recipe.getResult());
        Map<UUID, ShapedRecipe> map = getShapedRecipeMap().computeIfAbsent(resultHash, k -> new HashMap<>());
        List<Item> list1 = recipe.getIngredientsAggregate();
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

    public void registerStonecutterRecipe(StonecutterRecipe recipe) {
        this.addRecipe(recipe);
        getStonecutterRecipeMap().put(getItemHash(recipe.getResult()), recipe);
    }

    public void registerFurnaceRecipe(FurnaceRecipe recipe) {
        this.addRecipe(recipe);
        Item input = recipe.getInput();
        getFurnaceRecipesMap().put(getItemHash(input), recipe);
    }

    public void registerBlastFurnaceRecipe(BlastFurnaceRecipe recipe) {
        this.addRecipe(recipe);
        Item input = recipe.getInput();
        getBlastFurnaceRecipeMap().put(getItemHash(input), recipe);
    }

    public void registerSmokerRecipe(SmokerRecipe recipe) {
        this.addRecipe(recipe);
        Item input = recipe.getInput();
        getSmokerRecipeMap().put(getItemHash(input), recipe);
    }

    public void registerCampfireRecipe(CampfireRecipe recipe) {
        this.addRecipe(recipe);
        Item input = recipe.getInput();
        getCampfireRecipeMap().put(getItemHash(input), recipe);
    }

    public void registerModProcessRecipe(@NotNull ModProcessRecipe recipe) {
        this.addRecipe(recipe);
        var map = getModProcessRecipeMap().computeIfAbsent(recipe.getCategory(), k -> new HashMap<>());
        var inputHash = getShapelessItemDescriptorHash(recipe.getIngredients());
        map.put(inputHash, recipe);
    }

    public void registerSmithingRecipe(@NotNull SmithingRecipe recipe) {
        this.addRecipe(recipe);
        List<Item> list1 = recipe.getIngredientsAggregate();
        UUID hash = getItemWithTagsHash(list1, recipe.getNeedTags());
        int resultHash = getItemHash(recipe.getResult());
        Map<UUID, SmithingRecipe> map2 = getSmithingRecipeMap().computeIfAbsent(resultHash, k -> new HashMap<>());
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

    public void registerMultiRecipe(MultiRecipe recipe) {
        this.addRecipe(recipe);
        getMultiRecipeMap().put(recipe.getId(), recipe);
    }

    public @Nullable SmithingRecipe matchSmithingRecipe(Item equipment, Item ingredient) {
        List<Item> inputList = new ArrayList<>(2);
        inputList.add(equipment.decrement(equipment.count - 1));
        inputList.add(ingredient.decrement(ingredient.count - 1));
        return matchSmithingRecipe(inputList);
    }

    public @Nullable SmithingRecipe matchSmithingRecipe(@NotNull List<Item> inputList) {
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

    public @Nullable SmithingRecipe matchSmithingRecipe(@NotNull Item equipment, @NotNull Item ingredient, @NotNull Item primaryOutput) {
        List<Item> inputList = Arrays.asList(equipment, ingredient);
        return matchSmithingRecipe(inputList, primaryOutput);
    }

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

    public StonecutterRecipe matchStonecutterRecipe(Item output) {
        return getStonecutterRecipeMap().get(getItemHash(output));
    }

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

    public @Nullable ModProcessRecipe matchModProcessRecipe(@NotNull String category, @NotNull List<Item> inputList) {
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

    public void setRecipeXp(Recipe recipe, double xp) {
        recipeXpMap.put(recipe, xp);
    }

    public static void setCraftingPacket(DataPacket craftingPacket) {
        CraftingManager.packet = craftingPacket;
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
        this.recipeList.putIfAbsent(recipe.getRecipeId(), recipe);
    }

    private CraftingManager instance() {
        return this;
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
                registerBrewingRecipe(new BrewingRecipe(
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
                registerContainerRecipe(new ContainerRecipe(Item.get(fromItemId), Item.get(ingredient), Item.get(toItemId)));
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
                        Map<String, Object> resultMap = (Map) recipe.get("output");
                        ItemDescriptor resultItem = parseRecipeItem(resultMap);
                        if (resultItem == null) {
                            yield null;
                        }
                        Map<String, Object> inputMap = (Map) recipe.get("input");
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
                this.registerRecipe(re);
            }
        } catch (IOException e) {
            log.warn("Failed to load recipes config");
        }

        // Allow to rename without crafting
        registerCartographyRecipe(new CartographyRecipe(Item.get(ItemID.EMPTY_MAP), Collections.singletonList(Item.get(ItemID.EMPTY_MAP))));
        registerCartographyRecipe(new CartographyRecipe(Item.get(ItemID.EMPTY_MAP, 2), Collections.singletonList(Item.get(ItemID.EMPTY_MAP, 2))));
        registerCartographyRecipe(new CartographyRecipe(Item.get(ItemID.FILLED_MAP), Collections.singletonList(Item.get(ItemID.FILLED_MAP))));
        registerCartographyRecipe(new CartographyRecipe(Item.get(ItemID.FILLED_MAP, 3), Collections.singletonList(Item.get(ItemID.FILLED_MAP, 3))));
        registerCartographyRecipe(new CartographyRecipe(Item.get(ItemID.FILLED_MAP, 4), Collections.singletonList(Item.get(ItemID.FILLED_MAP, 4))));
        registerCartographyRecipe(new CartographyRecipe(Item.get(ItemID.FILLED_MAP, 5), Collections.singletonList(Item.get(ItemID.FILLED_MAP, 5))));
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
            return new SmithingRecipe(id, outputItem.toItem(), baseItem, additionItem, templateItem);
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

    public final class VanillaRecipeParser {
        private static final Gson GSON = new GsonBuilder().setObjectToNumberStrategy(jsonReader -> {
            String value = jsonReader.nextString();
            return Double.valueOf(value).intValue();
        }).create();
        private static final String SHAPED_KEY = "minecraft:recipe_shaped";
        private static final String SHAPELESS_KEY = "minecraft:recipe_shapeless";
        private static final String FURNACE_KEY = "minecraft:recipe_furnace";
        private static final String BREW_KEY = "minecraft:recipe_brewing_mix";
        private static final String CONTAINER_KEY = "minecraft:recipe_brewing_container";

        private static final String STONE_CUTTER_TAG = "stonecutter";
        private static final String CRAFTING_TABLE_TAG = "crafting_table";
        private static final String SHULKER_BOX_TAG = "shulker_box";
        private static final String CARTOGRAPHY_TABLE_TAG = "cartography_table";
        private static final String FURNACE_TAG = "furnace";
        private static final String SMOKER_TAG = "smoker";
        private static final String BLAST_FURNACE_TAG = "blast_furnace";
        private static final String CAMPFIRE_TAG = "campfire";
        private static final String SOUL_CAMPFIRE_TAG = "soul_campfire";
        private static final String BREW_STAND_TAG = "brewing_stand";

        private VanillaRecipeParser() {
        }

        public void parseAndRegisterRecipe(@NotNull File file) {
            try (var reader = new FileReader(file)) {
                matchAndParse(reader);
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }

        public void parseAndRegisterRecipe(@NotNull InputStream file) {
            try (var reader = new InputStreamReader(file)) {
                matchAndParse(reader);
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }

        private void matchAndParse(InputStreamReader reader) {
            Map<String, Object> map = GSON.fromJson(reader, Map.class);
            if (map.containsKey(SHAPED_KEY)) {
                parseAndRegisterShapedRecipe((Map<String, Object>) map.get(SHAPED_KEY));
            } else if (map.containsKey(SHAPELESS_KEY)) {
                parseAndRegisterShapeLessRecipe((Map<String, Object>) map.get(SHAPELESS_KEY));
            } else if (map.containsKey(FURNACE_KEY)) {
                parseAndRegisterFurnaceRecipe((Map<String, Object>) map.get(FURNACE_KEY));
            } else if (map.containsKey(BREW_KEY)) {
                parseAndRegisterBrewRecipe((Map<String, Object>) map.get(BREW_KEY));
            } else if (map.containsKey(CONTAINER_KEY)) {
                parseAndRegisterContainerRecipe((Map<String, Object>) map.get(CONTAINER_KEY));
            }
        }

        private void parseAndRegisterShapedRecipe(Map<String, Object> recipeData) {
            List<String> tags = tags(recipeData);
            if (tags.size() == 1 && tags.get(0).equals("crafting_table")) {
                int prior = (int) recipeData.getOrDefault("priority", 0);
                List<String> pattern = (List<String>) recipeData.get("pattern");
                String[] shapes;
                if (pattern.size() > 1) {
                    int maxWidth = pattern.stream().map(s -> s.toCharArray().length).max(Integer::compare).get().intValue();
                    shapes = pattern.stream().map(shape -> {
                        StringBuilder builder = new StringBuilder();
                        char[] charArray = shape.toCharArray();
                        for (char c : charArray) {
                            builder.append(c);
                        }
                        return builder.append(" ".repeat(Math.max(0, maxWidth - charArray.length))).toString();
                    }).toArray(String[]::new);
                } else {
                    shapes = pattern.toArray(String[]::new);
                }
                Map<String, Map<String, Object>> key = (Map<String, Map<String, Object>>) recipeData.get("key");
                final Map<Character, ItemDescriptor> ingredients = new LinkedHashMap<>();
                try {
                    key.forEach((k, v) -> {
                        if (v.containsKey("tag")) {
                            var tag = v.get("tag").toString();
                            int count = (int) v.getOrDefault("count", 1);
                            ingredients.put(k.charAt(0), new ItemTagDescriptor(tag, count));
                        } else {
                            ingredients.put(k.charAt(0), new DefaultDescriptor(parseItem(v)));
                        }
                    });
                    Object o = recipeData.get("result");
                    Map<String, Object> result = Map.of();
                    if (o instanceof Map<?, ?> map) {
                        result = (Map<String, Object>) map;
                    } else if (o instanceof List<?> list) {
                        result = (Map<String, Object>) list.get(0);
                    }
                    new ShapedRecipe(description(recipeData), prior, parseItem(result), shapes, ingredients, List.of()).registerToCraftingManager(instance());
                } catch (AssertionError ignore) {
                }
            }
        }

        private void parseAndRegisterShapeLessRecipe(Map<String, Object> recipeData) {
            int prior = (int) recipeData.getOrDefault("priority", 0);
            List<Map<String, Object>> ingredients = (List<Map<String, Object>>) recipeData.get("ingredients");
            final List<ItemDescriptor> itemDescriptors = new ArrayList<>();
            try {
                ingredients.forEach(v -> {
                    if (v.containsKey("tag")) {
                        var tag = v.get("tag").toString();
                        int count = (int) v.getOrDefault("count", 1);
                        itemDescriptors.add(new ItemTagDescriptor(tag, count));
                    } else {
                        itemDescriptors.add(new DefaultDescriptor(parseItem(v)));
                    }
                });
                Map<String, Object> result = (Map<String, Object>) recipeData.get("result");
                Item re = parseItem(result);
                List<String> tags = tags(recipeData);
                for (var tag : tags) {
                    Recipe recipe = switch (tag) {
                        case CRAFTING_TABLE_TAG ->
                                new ShapelessRecipe(description(recipeData), prior, re, itemDescriptors);
                        case SHULKER_BOX_TAG ->
                                new ShulkerBoxRecipe(description(recipeData), prior, re, itemDescriptors);
                        case STONE_CUTTER_TAG ->
                                new StonecutterRecipe(description(recipeData), prior, re, itemDescriptors.get(0).toItem());
                        case CARTOGRAPHY_TABLE_TAG ->
                                new CartographyRecipe(description(recipeData), prior, re, itemDescriptors);
                        default -> throw new IllegalArgumentException(tag);
                    };
                    recipe.registerToCraftingManager(instance());
                }
            } catch (AssertionError ignore) {
            }
        }

        private void parseAndRegisterFurnaceRecipe(Map<String, Object> recipeData) {
            Item input = Item.get(recipeData.get("input").toString());
            Item output = Item.get(recipeData.get("output").toString());
            if (input.isNull() || output.isNull()) {
                return;
            }
            List<String> tags = tags(recipeData);
            for (var tag : tags) {
                Recipe recipe = switch (tag) {
                    case FURNACE_TAG -> new FurnaceRecipe(description(recipeData), output, input);
                    case SMOKER_TAG -> new SmokerRecipe(description(recipeData), output, input);
                    case BLAST_FURNACE_TAG -> new BlastFurnaceRecipe(description(recipeData), output, input);
                    case CAMPFIRE_TAG, SOUL_CAMPFIRE_TAG -> new CampfireRecipe(description(recipeData), output, input);
                    default -> throw new IllegalArgumentException(tag);
                };
                recipe.registerToCraftingManager(instance());
            }
        }

        private void parseAndRegisterBrewRecipe(Map<String, Object> recipeData) {
            String inputID = "minecraft:" + recipeData.get("input").toString().split(":")[2].toLowerCase(Locale.ENGLISH);
            Item input = ItemPotion.fromPotion(Potion.getPotionByName(inputID));
            String outputID = "minecraft:" + recipeData.get("output").toString().split(":")[2].toLowerCase(Locale.ENGLISH);
            Item output = ItemPotion.fromPotion(Potion.getPotionByName(outputID));
            Item reagent = Item.get(recipeData.get("reagent").toString());
            if (input.isNull() || output.isNull() || reagent.isNull()) {
                return;
            }
            List<String> tags = tags(recipeData);
            if (tags.get(0).equals(BREW_STAND_TAG)) {
                new BrewingRecipe(description(recipeData), input, reagent, output).registerToCraftingManager(instance());
            }
        }

        private void parseAndRegisterContainerRecipe(Map<String, Object> recipeData) {
            Item input = Item.get(recipeData.get("input").toString());
            Item output = Item.get(recipeData.get("output").toString());
            Item reagent = Item.get(recipeData.get("reagent").toString());
            if (input.isNull() || output.isNull() || reagent.isNull()) {
                return;
            }
            List<String> tags = tags(recipeData);
            if (tags.get(0).equals(BREW_STAND_TAG)) {
                new ContainerRecipe(description(recipeData), input, reagent, output).registerToCraftingManager(instance());
            }
        }

        private Item parseItem(Map<String, Object> v) throws AssertionError {
            String item = (String) v.get("item");
            int count = (int) v.getOrDefault("count", 1);
            int data = (int) v.getOrDefault("data", 32767);
            Item i = Item.get(item);
            if (i.isNull()) {
                throw new AssertionError();
            }
            if (data != 0) {
                if (data == 32767) {
                    i = i.createFuzzyCraftingRecipe();
                } else {
                    i.setDamage(data);
                }
            }
            i.setCount(count);
            return i;
        }

        private String description(Map<String, Object> recipeData) {
            return ((Map<String, String>) recipeData.get("description")).get("identifier");
        }

        private List<String> tags(Map<String, Object> recipeData) {
            return (List<String>) recipeData.get("tags");
        }
    }
}
