package cn.nukkit.registry;

import cn.nukkit.Server;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.CraftingDataPacket;
import cn.nukkit.network.protocol.types.RecipeUnlockingRequirement;
import cn.nukkit.recipe.*;
import cn.nukkit.recipe.descriptor.DefaultDescriptor;
import cn.nukkit.recipe.descriptor.ItemDescriptor;
import cn.nukkit.recipe.descriptor.ItemDescriptorType;
import cn.nukkit.recipe.descriptor.ItemTagDescriptor;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.Identifier;
import cn.nukkit.utils.MinecraftNamespaceComparator;
import cn.nukkit.utils.Utils;
import com.google.common.collect.Collections2;
import com.google.gson.internal.LinkedTreeMap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.collection.CharObjectHashMap;
import io.netty.util.internal.EmptyArrays;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@SuppressWarnings("unchecked")
public class RecipeRegistry implements IRegistry<String, Recipe, Recipe> {
    private static final AtomicBoolean isLoad = new AtomicBoolean(false);
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
    private static ByteBuf buffer = null;
    private final VanillaRecipeParser vanillaRecipeParser = new VanillaRecipeParser(this);
    private final EnumMap<RecipeType, Int2ObjectArrayMap<Set<Recipe>>> recipeMaps = new EnumMap<>(RecipeType.class);
    private final Object2ObjectOpenHashMap<String, Recipe> allRecipeMaps = new Object2ObjectOpenHashMap<>();
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

    public Set<ShapelessRecipe> getShapelessRecipeMap() {
        HashSet<ShapelessRecipe> result = new HashSet<>();
        for (var s : recipeMaps.get(RecipeType.SHAPELESS).values()) {
            result.addAll(Collections2.transform(s, d -> (ShapelessRecipe) d));
        }
        return result;
    }

    public ShapelessRecipe findShapelessRecipe(Item... items) {
        Set<Recipe> recipes = recipeMaps.get(RecipeType.SHAPELESS).get(items.length);
        if (recipes != null) {
            for (var r : recipes) {
                if (r.fastCheck(items)) return (ShapelessRecipe) r;
            }
        }
        return null;
    }

    public Set<ShapedRecipe> getShapedRecipeMap() {
        HashSet<ShapedRecipe> result = new HashSet<>();
        for (var s : recipeMaps.get(RecipeType.SHAPED).values()) {
            result.addAll(Collections2.transform(s, d -> (ShapedRecipe) d));
        }
        return result;
    }

    public ShapedRecipe findShapedRecipe(Item... items) {
        Set<Recipe> recipes = recipeMaps.get(RecipeType.SHAPED).get(items.length);
        if (recipes != null) {
            for (var r : recipes) {
                if (r.fastCheck(items)) return (ShapedRecipe) r;
            }
        }
        return null;
    }

    public Set<FurnaceRecipe> getFurnaceRecipeMap() {
        HashSet<FurnaceRecipe> result = new HashSet<>();
        Int2ObjectArrayMap<Set<Recipe>> recipe = recipeMaps.get(RecipeType.FURNACE);
        if (recipe != null) {
            for (var s : recipe.values()) {
                result.addAll(Collections2.transform(s, d -> (FurnaceRecipe) d));
            }
        }
        return result;
    }

    public FurnaceRecipe findFurnaceRecipe(Item... items) {
        Int2ObjectArrayMap<Set<Recipe>> map1 = recipeMaps.get(RecipeType.FURNACE);
        Set<Recipe> recipes = map1.get(items.length);
        if (recipes != null) {
            for (var r : recipes) {
                if (r.fastCheck(items)) return (FurnaceRecipe) r;
            }
        }
        return null;
    }

    public Set<BlastFurnaceRecipe> getBlastFurnaceRecipeMap() {
        HashSet<BlastFurnaceRecipe> result = new HashSet<>();
        Int2ObjectArrayMap<Set<Recipe>> recipe = recipeMaps.get(RecipeType.BLAST_FURNACE);
        if (recipe != null) {
            for (var s : recipe.values()) {
                result.addAll(Collections2.transform(s, d -> (BlastFurnaceRecipe) d));
            }
        }
        return result;
    }

    public BlastFurnaceRecipe findBlastFurnaceRecipe(Item... items) {
        Int2ObjectArrayMap<Set<Recipe>> map1 = recipeMaps.get(RecipeType.BLAST_FURNACE);
        Set<Recipe> recipes = map1.get(items.length);
        if (recipes != null) {
            for (var r : recipes) {
                if (r.fastCheck(items)) return (BlastFurnaceRecipe) r;
            }
        }
        return null;
    }

    public Set<SmokerRecipe> getSmokerRecipeMap() {
        HashSet<SmokerRecipe> result = new HashSet<>();
        Int2ObjectArrayMap<Set<Recipe>> smoker = recipeMaps.get(RecipeType.SMOKER);
        if (smoker != null) {
            for (var s : smoker.values()) {
                result.addAll(Collections2.transform(s, d -> (SmokerRecipe) d));
            }
        }
        return result;
    }

    public SmokerRecipe findSmokerRecipe(Item... items) {
        Int2ObjectArrayMap<Set<Recipe>> map1 = recipeMaps.get(RecipeType.SMOKER);
        if (map1 != null) {
            Set<Recipe> recipes = map1.get(items.length);
            for (var r : recipes) {
                if (r.fastCheck(items)) return (SmokerRecipe) r;
            }
        }
        return null;
    }

    public Set<CampfireRecipe> getCampfireRecipeMap() {
        HashSet<CampfireRecipe> result = new HashSet<>();
        Int2ObjectArrayMap<Set<Recipe>> r1 = recipeMaps.get(RecipeType.CAMPFIRE);
        if (r1 != null) {
            for (var s : r1.values()) {
                result.addAll(Collections2.transform(s, d -> (CampfireRecipe) d));
            }
        }
        Int2ObjectArrayMap<Set<Recipe>> r3 = recipeMaps.get(RecipeType.SOUL_CAMPFIRE);
        if (r3 != null) {
            for (var s : r3.values()) {
                result.addAll(Collections2.transform(s, d -> (CampfireRecipe) d));
            }
        }
        return result;
    }

    public CampfireRecipe findCampfireRecipe(Item... items) {
        Set<Recipe> recipes = recipeMaps.get(RecipeType.CAMPFIRE).get(items.length);
        if (recipes != null) {
            for (var r : recipes) {
                if (r.fastCheck(items)) return (CampfireRecipe) r;
            }
        }
        return null;
    }

    public Set<MultiRecipe> getMultiRecipeMap() {
        HashSet<MultiRecipe> result = new HashSet<>();
        for (var s : recipeMaps.get(RecipeType.MULTI).values()) {
            result.addAll(Collections2.transform(s, d -> (MultiRecipe) d));
        }
        return result;
    }

    public MultiRecipe findMultiRecipe(Item... items) {
        Set<Recipe> recipes = recipeMaps.get(RecipeType.MULTI).get(items.length);
        if (recipes != null) {
            for (var r : recipes) {
                if (r.fastCheck(items)) return (MultiRecipe) r;
            }
        }
        return null;
    }

    public Set<StonecutterRecipe> getStonecutterRecipeMap() {
        HashSet<StonecutterRecipe> result = new HashSet<>();
        for (var s : recipeMaps.get(RecipeType.STONECUTTER).values()) {
            result.addAll(Collections2.transform(s, d -> (StonecutterRecipe) d));
        }
        return result;
    }

    public StonecutterRecipe findStonecutterRecipe(Item... items) {
        Set<Recipe> recipes = recipeMaps.get(RecipeType.STONECUTTER).get(items.length);
        if (recipes != null) {
            for (var r : recipes) {
                if (r.fastCheck(items)) return (StonecutterRecipe) r;
            }
        }
        return null;
    }

    public Set<CartographyRecipe> getCartographyRecipeMap() {
        HashSet<CartographyRecipe> result = new HashSet<>();
        for (var s : recipeMaps.get(RecipeType.CARTOGRAPHY).values()) {
            result.addAll(Collections2.transform(s, d -> (CartographyRecipe) d));
        }
        return result;
    }

    public CartographyRecipe findCartographyRecipe(Item... items) {
        Set<Recipe> recipes = recipeMaps.get(RecipeType.CARTOGRAPHY).get(items.length);
        for (var r : recipes) {
            if (r.fastCheck(items)) return (CartographyRecipe) r;
        }
        return null;
    }

    public Set<SmithingTransformRecipe> getSmithingTransformRecipeMap() {
        HashSet<SmithingTransformRecipe> result = new HashSet<>();
        for (var s : recipeMaps.get(RecipeType.SMITHING_TRANSFORM).values()) {
            result.addAll(Collections2.transform(s, d -> (SmithingTransformRecipe) d));
        }
        return result;
    }

    public SmithingTransformRecipe findSmithingTransform(Item... items) {
        Set<Recipe> recipes = recipeMaps.get(RecipeType.SMITHING_TRANSFORM).get(items.length);
        if (recipes != null) {
            for (var r : recipes) {
                if (r.fastCheck(items)) return (SmithingTransformRecipe) r;
            }
        }
        return null;
    }

    public Set<BrewingRecipe> getBrewingRecipeMap() {
        HashSet<BrewingRecipe> result = new HashSet<>();
        for (var s : recipeMaps.get(RecipeType.BREWING).values()) {
            result.addAll(Collections2.transform(s, d -> (BrewingRecipe) d));
        }
        return result;
    }

    public BrewingRecipe findBrewingRecipe(Item... items) {
        Set<Recipe> recipes = recipeMaps.get(RecipeType.BREWING).get(items.length);
        if (recipes != null) {
            for (var r : recipes) {
                if (r.fastCheck(items)) return (BrewingRecipe) r;
            }
        }
        return null;
    }

    public Set<ContainerRecipe> getContainerRecipeMap() {
        HashSet<ContainerRecipe> result = new HashSet<>();
        for (var s : recipeMaps.get(RecipeType.CONTAINER).values()) {
            result.addAll(Collections2.transform(s, d -> (ContainerRecipe) d));
        }
        return result;
    }

    public ContainerRecipe findContainerRecipe(Item... items) {
        Set<Recipe> recipes = recipeMaps.get(RecipeType.CONTAINER).get(items.length);
        if (recipes != null) {
            for (var r : recipes) {
                if (r.fastCheck(items)) return (ContainerRecipe) r;
            }
        }
        return null;
    }

    public Set<ModProcessRecipe> getModProcessRecipeMap() {
        HashSet<ModProcessRecipe> result = new HashSet<>();
        for (var s : recipeMaps.get(RecipeType.MOD_PROCESS).values()) {
            result.addAll(Collections2.transform(s, d -> (ModProcessRecipe) d));
        }
        return result;
    }

    public ModProcessRecipe findModProcessRecipe(Item... items) {
        Set<Recipe> recipes = recipeMaps.get(RecipeType.MOD_PROCESS).get(items.length);
        if (recipes != null) {
            for (var r : recipes) {
                if (r.fastCheck(items)) return (ModProcessRecipe) r;
            }
        }
        return null;
    }

    public ByteBuf getCraftingPacket() {
        return buffer.copy();
    }

    public int getRecipeCount() {
        return RECIPE_COUNT;
    }

    public Recipe getRecipeByNetworkId(int networkId) {
        return networkIdRecipeList.get(networkId - 1);
    }

    public static String computeRecipeIdWithItem(Collection<Item> results, Collection<Item> inputs, RecipeType type) {
        List<Item> inputs1 = new ArrayList<>(inputs);
        return computeRecipeId(results, inputs1.stream().map(DefaultDescriptor::new).toList(), type);
    }

    public static String computeRecipeId(Collection<Item> results, Collection<? extends ItemDescriptor> inputs, RecipeType type) {
        StringBuilder builder = new StringBuilder();
        Optional<Item> first = results.stream().findFirst();
        first.ifPresent(item -> builder.append(new Identifier(item.getId()).getPath())
                .append('_')
                .append(item.getCount())
                .append('_')
                .append(item.isBlock() ? item.getBlockUnsafe().getBlockState().specialValue() : item.getDamage())
                .append("_from_"));
        int limit = 5;
        for (var des : inputs) {
            if ((limit--) == 0) {
                break;
            }
            if (des instanceof ItemTagDescriptor tag) {
                builder.append("tag_").append(tag.getItemTag()).append("_and_");
            } else if (des instanceof DefaultDescriptor def) {
                Item item = def.getItem();
                builder.append(new Identifier(item.getId()).getPath())
                        .append('_')
                        .append(item.getCount())
                        .append('_')
                        .append(item.getDamage() != 0 ? item.getDamage() : item.isBlock() ? item.getBlockUnsafe().getRuntimeId() : 0)
                        .append("_and_");
            }
        }
        String r = builder.toString();
        return r.substring(0, r.lastIndexOf("_and_")) + "_" + type.name().toLowerCase(Locale.ENGLISH);
    }

    public static void setCraftingPacket(ByteBuf craftingPacket) {
        ReferenceCountUtil.safeRelease(buffer);
        buffer = craftingPacket.retain();
    }

    @Override
    public void init() {
        if (isLoad.getAndSet(true)) return;
        log.info("Loading recipes...");
        this.loadRecipes();
        this.rebuildPacket();
        log.info("Loaded {} recipes.", getRecipeCount());
    }

    @Override
    public Recipe get(String key) {
        return allRecipeMaps.get(key);
    }

    @Override
    public void trim() {
        recipeXpMap.trim();
        allRecipeMaps.trim();
    }

    public void reload() {
        isLoad.set(false);
        RECIPE_COUNT = 0;
        if (buffer != null) {
            buffer.release();
            buffer = null;
        }
        recipeMaps.clear();
        recipeXpMap.clear();
        allRecipeMaps.clear();
        networkIdRecipeList.clear();
        init();
    }

    @Override
    public void register(String key, Recipe recipe) throws RegisterException {
        if (recipe instanceof CraftingRecipe craftingRecipe) {
            Item item = recipe.getResults().getFirst();
            UUID id = Utils.dataToUUID(String.valueOf(RECIPE_COUNT), String.valueOf(item.getId()), String.valueOf(item.getDamage()), String.valueOf(item.getCount()), Arrays.toString(item.getCompoundTag()));
            if (craftingRecipe.getUUID() == null) craftingRecipe.setUUID(id);
        }
        if (allRecipeMaps.putIfAbsent(recipe.getRecipeId(), recipe) != null) {
            throw new RegisterException("Duplicate recipe %s type %s".formatted(recipe.getRecipeId(), recipe.getType()));
        }
        Int2ObjectArrayMap<Set<Recipe>> recipeMap = recipeMaps.computeIfAbsent(recipe.getType(), t -> new Int2ObjectArrayMap<>());
        Set<Recipe> r = recipeMap.computeIfAbsent(recipe.getIngredients().size(), i -> new HashSet<>());
        r.add(recipe);
        ++RECIPE_COUNT;
        switch (recipe.getType()) {
            case STONECUTTER, SHAPELESS, CARTOGRAPHY, USER_DATA_SHAPELESS_RECIPE, SMITHING_TRANSFORM, SMITHING_TRIM,
                 SHAPED, MULTI -> this.networkIdRecipeList.add(recipe);
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
        allRecipeMaps.clear();
        RECIPE_COUNT = 0;
        ReferenceCountUtil.safeRelease(buffer);
        buffer = null;
    }

    public void rebuildPacket() {
        ByteBuf buf = ByteBufAllocator.DEFAULT.ioBuffer(64);
        CraftingDataPacket pk = new CraftingDataPacket();
        pk.cleanRecipes = true;

        pk.addNetworkIdRecipe(networkIdRecipeList);

        for (FurnaceRecipe recipe : getFurnaceRecipeMap()) {
            pk.addFurnaceRecipe(recipe);
        }
        for (SmokerRecipe recipe : getSmokerRecipeMap()) {
            pk.addSmokerRecipe(recipe);
        }
        for (BlastFurnaceRecipe recipe : getBlastFurnaceRecipeMap()) {
            pk.addBlastFurnaceRecipe(recipe);
        }
        for (CampfireRecipe recipe : getCampfireRecipeMap()) {
            pk.addCampfireRecipeRecipe(recipe);
        }
        for (BrewingRecipe recipe : getBrewingRecipeMap()) {
            pk.addBrewingRecipe(recipe);
        }
        for (ContainerRecipe recipe : getContainerRecipeMap()) {
            pk.addContainerRecipe(recipe);
        }
        pk.encode(HandleByteBuf.of(buf));
        buffer = buf;
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

        // INFORMATION:
        // This code is used to read the recipes.json provided by endstone, but since
        // we do not have that data at the moment (for 1.21.20), we use recipes.json provided by Kaooot:
        // https://github.com/Kaooot/bedrock-network-data
        // We can use the original reader again, once endstone generates data again

        var recipeConfig = new Config(Config.JSON);
        try (var r = Server.class.getClassLoader().getResourceAsStream("recipes.json")) {
            recipeConfig.load(r);

            //load potionMixes
            List<Map<String, Object>> potionMixes = (List<Map<String, Object>>) recipeConfig.getList("potionMixes");
            for (Map<String, Object> recipe : potionMixes) {
                String inputId = (String) recipe.get("inputId");
                int inputMeta = (int) ((double) recipe.get("inputMeta"));

                String reagentId = (String) recipe.get("reagentId");
                int reagentMeta = (int) ((double) recipe.get("reagentMeta"));

                String outputId = (String) recipe.get("outputId");
                int outputMeta = (int) ((double) recipe.get("outputMeta"));

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

                // Endstone Reader
//                Map<String, Object> input = (Map<String, Object>) recipe.get("input");
//                String inputId = input.get("item").toString();
//                int inputMeta = Utils.toInt(input.get("data"));
//
//                Map<String, Object> output = (Map<String, Object>) recipe.get("output");
//                String outputId = output.get("item").toString();
//                int outputMeta = Utils.toInt(output.get("data"));
//
//                Map<String, Object> reagent = (Map<String, Object>) recipe.get("reagent");
//                String reagentId = reagent.get("item").toString();
//                int reagentMeta = Utils.toInt(reagent.get("data"));
//
//                Item inputItem = Item.get(inputId, inputMeta);
//                Item reagentItem = Item.get(reagentId, reagentMeta);
//                Item outputItem = Item.get(outputId, outputMeta);
//                if (inputItem.isNull() || reagentItem.isNull() || outputItem.isNull()) {
//                    continue;
//                }
//                register(new BrewingRecipe(
//                        inputItem,
//                        reagentItem,
//                        outputItem
//                ));
            }

            //load containerMixes
            List<Map<String, Object>> containerMixes = (List<Map<String, Object>>) recipeConfig.getList("containerMixes");
            for (Map<String, Object> containerMix : containerMixes) {

                String inputId = (String) containerMix.get("inputId");
                String reagentId = (String) containerMix.get("reagentId");
                String outputId = (String) containerMix.get("outputId");

                this.register(new ContainerRecipe(
                        Item.get(inputId),
                        Item.get(reagentId),
                        Item.get(outputId)
                ));

                // Endstone Reader
//                String fromItemId = containerMix.get("input").toString();
//                String ingredient = containerMix.get("reagent").toString();
//                String toItemId = containerMix.get("output").toString();
//                register(new ContainerRecipe(Item.get(fromItemId), Item.get(ingredient), Item.get(toItemId)));
            }

            //load all other recipes (Not Endstone Reader)
            List<Map<String, Object>> recipes = (List<Map<String, Object>>) recipeConfig.getList("recipes");
            for (Map<String, Object> recipe : recipes) {

                String block = (String) recipe.get("block");

                if (block == null)
                    continue;

                switch (block) {     // the block defines the type of recipe
                    case "smithing_table" -> {
                        String recipeId = (String) recipe.get("id");

                        ItemDescriptor baseDescriptor = parseDescription(
                                (Map<String, Object>) recipe.get("base"),
                                ParseType.SMITHING_TABLE
                        );
                        ItemDescriptor additionDescriptor = parseDescription(
                                (Map<String, Object>) recipe.get("addition"),
                                ParseType.SMITHING_TABLE
                        );
                        ItemDescriptor templateDescriptor = parseDescription(
                                (Map<String, Object>) recipe.get("template"),
                                ParseType.SMITHING_TABLE
                        );

                        if (recipe.containsKey("result")) { // is smithing transform recipe
                            Map<String, Object> result = (Map<String, Object>) recipe.get("result");
                            String itemId = (String) result.get("id");
                            int count = (int) ((double) result.get("count"));
                            this.register(new SmithingTransformRecipe(
                                    recipeId,
                                    Item.get(itemId, 0, count),
                                    baseDescriptor,
                                    additionDescriptor,
                                    templateDescriptor
                            ));
                        } else {    // is smithing trim recipe
                            this.register(new SmithingTrimRecipe(
                                    recipeId,
                                    baseDescriptor,
                                    additionDescriptor,
                                    templateDescriptor,
                                    "smithing_table"
                            ));
                        }
                    }
                    case "stonecutter" -> {
                        ParseType inputParseType = ParseType.STONECUTTER_INPUT;
                        ParseType outputParseType = ParseType.STONECUTTER_OUTPUT;

                        String recipeId = (String) recipe.get("id");
                        UUID uuid = UUID.fromString((String) recipe.get("uuid"));
                        int priority = (int) ((double) recipe.get("priority"));

                        List<Map<String, Object>> outputs = (List<Map<String, Object>>) recipe.get("output");
                        Map<String, Object> primaryResultData = outputs.removeFirst();
                        ItemDescriptor primaryResult = parseDescription(primaryResultData, outputParseType);

                        List<ItemDescriptor> ingredients = new ArrayList<>();
                        List<Map<String, Object>> inputs = (List<Map<String, Object>>) recipe.get("input");

                        for (Map<String, Object> input : inputs) {
                            ingredients.add(parseDescription(input, inputParseType));
                        }

                        this.register(new StonecutterRecipe(
                                recipeId,
                                uuid,
                                priority,
                                primaryResult.toItem(),
                                ingredients.getFirst().toItem(),
                                new RecipeUnlockingRequirement(
                                        RecipeUnlockingRequirement.UnlockingContext.ALWAYS_UNLOCKED
                                )
                        ));
                    }
                    case "cartography_table" -> {
                        ParseType inputParseType = ParseType.CARTOGRAPHY_TABLE_INPUT;
                        ParseType outputParseType = ParseType.CARTOGRAPHY_TABLE_OUTPUT;

                        String recipeId = (String) recipe.get("id");
                        UUID uuid = UUID.fromString((String) recipe.get("uuid"));
                        int priority = (int) ((double) recipe.get("priority"));

                        List<Map<String, Object>> outputs = (List<Map<String, Object>>) recipe.get("output");
                        Map<String, Object> primaryResultData = outputs.removeFirst();
                        ItemDescriptor primaryResult = parseDescription(primaryResultData, outputParseType);

                        List<ItemDescriptor> ingredients = new ArrayList<>();
                        List<Map<String, Object>> inputs = (List<Map<String, Object>>) recipe.get("input");

                        for (Map<String, Object> input : inputs) {
                            ingredients.add(parseDescription(input, inputParseType));
                        }

                        this.register(new CartographyRecipe(
                                recipeId,
                                uuid,
                                priority,
                                primaryResult.toItem(),
                                ingredients,
                                new RecipeUnlockingRequirement(
                                        RecipeUnlockingRequirement.UnlockingContext.ALWAYS_UNLOCKED
                                )
                        ));
                    }

                    case "crafting_table" -> {
                        ParseType inputParseType = ParseType.CRAFTING_TABLE_INPUT;
                        ParseType outputParseType = ParseType.CRAFTING_TABLE_OUTPUT;

                        switch (block) {
                            case "cartography_table" -> {
                                inputParseType = ParseType.CARTOGRAPHY_TABLE_INPUT;
                                outputParseType = ParseType.CARTOGRAPHY_TABLE_OUTPUT;
                            }
                        }

                        String recipeId = (String) recipe.get("id");
                        UUID uuid = UUID.fromString((String) recipe.get("uuid"));
                        int priority = (int) ((double) recipe.get("priority"));

                        List<Map<String, Object>> outputs = (List<Map<String, Object>>) recipe.get("output");
                        Map<String, Object> primaryResultData = outputs.removeFirst();
                        ItemDescriptor primaryResult = parseDescription(primaryResultData, outputParseType);

                        if (recipe.containsKey("shape")) {
                            List<Item> extraResults = new ArrayList<>();
                            for (Map<String, Object> output : outputs) {
                                extraResults.add(parseDescription(output, outputParseType).toItem());
                            }

                            String[] shape = ((List<String>) recipe.get("shape")).toArray(String[]::new);

                            Map<Character, ItemDescriptor> ingredients = new CharObjectHashMap<>();

                            LinkedTreeMap<String, Object> inputs = (LinkedTreeMap<String, Object>) recipe.get("input");

                            for (Map.Entry<String, Object> inputData : inputs.entrySet()) {
                                char patternKey = inputData.getKey().charAt(0);
                                Map<String, Object> ingredientData = (Map<String, Object>) inputData.getValue();

                                ingredients.put(
                                        patternKey,
                                        parseDescription(
                                                ingredientData,
                                                inputParseType
                                        )
                                );
                            }

                            this.register(new ShapedRecipe(
                                    recipeId,
                                    uuid,
                                    priority,
                                    primaryResult.toItem(),
                                    shape,
                                    ingredients,
                                    extraResults,
                                    false,
                                    new RecipeUnlockingRequirement(
                                            RecipeUnlockingRequirement.UnlockingContext.ALWAYS_UNLOCKED
                                    )
                            ));
                        } else {    // is shapeless recipe

                            List<ItemDescriptor> ingredients = new ArrayList<>();
                            List<Map<String, Object>> inputs = (List<Map<String, Object>>) recipe.get("input");

                            for (Map<String, Object> input : inputs) {
                                ingredients.add(parseDescription(input, inputParseType));
                            }

                            this.register(new ShapelessRecipe(
                                    recipeId,
                                    uuid,
                                    priority,
                                    primaryResult.toItem(),
                                    ingredients,
                                    new RecipeUnlockingRequirement(
                                            RecipeUnlockingRequirement.UnlockingContext.ALWAYS_UNLOCKED
                                    )
                            ));
                        }
                    }
                    case "furnace", "blast_furnace", "smoker", "campfire", "soul_campfire" -> {
                        Map<String, Object> inputData = (Map<String, Object>) recipe.get("input");
                        Map<String, Object> outputData = (Map<String, Object>) recipe.get("output");
                        Item inputItem = parseDescription(inputData, ParseType.FURNACE_INPUT).toItem();
                        Item outputItem = parseDescription(outputData, ParseType.FURNACE_OUTPUT).toItem();

                        SmeltingRecipe smeltingRecipe = switch (block) {
                            case "blast_furnace" -> new BlastFurnaceRecipe(outputItem, inputItem);
                            case "smoker" -> new SmokerRecipe(outputItem, inputItem);
                            case "campfire" -> new CampfireRecipe(outputItem, inputItem);
                            case "soul_campfire" -> new SoulCampfireRecipe(outputItem, inputItem);
                            default -> new FurnaceRecipe(outputItem, inputItem);
                        };

                        double xp = furnaceXpConfig.getDouble(inputItem.getId() + ":" + inputItem.getDamage());
                        if (xp != 0) {
                            this.setRecipeXp(smeltingRecipe, xp);
                        }

                        try {
                            this.register(smeltingRecipe);
                        } catch (Exception e) {     //this can be removed once duplicate recipes no longer exist
                        }
                    }
                }
            }

            //load furnace recipes
            // Endstone reader
//            List<Map<String, Object>> furnaceAuxes = (List<Map<String, Object>>) recipeConfig.getList("furnaceAux");
//            for (Map<String, Object> furnaceAux : furnaceAuxes) {
//                Map<String, Object> input = (Map<String, Object>) furnaceAux.get("input");
//                Item inputItem = Item.get(input.get("item").toString());
//                Map<String, Object> output = (Map<String, Object>) furnaceAux.get("output");
//                double outputCount = (double) output.get("count");
//                double outputData;
//                if (output.get("data") != null) {
//                    outputData = (double) output.get("data");
//                } else {
//                    outputData = 0;
//                }
//
//                Item outputItem = Item.get(output.get("item").toString());
//                outputItem.setCount((int) outputCount);
//                outputItem.setMeta((int) outputData);
//
//                String tag = furnaceAux.get("tag").toString();
//
//                Recipe furnaceRecipe = switch (tag) {
//                    case "furnace" -> new FurnaceRecipe(outputItem, inputItem);
//                    case "blast_furnace" -> new BlastFurnaceRecipe(outputItem, inputItem);
//                    case "smoker" -> new SmokerRecipe(outputItem, inputItem);
//                    case "campfire" -> new CampfireRecipe(outputItem, inputItem);
//                    case "soul_campfire" -> new SoulCampfireRecipe(outputItem, inputItem);
//                    default -> throw new IllegalStateException("Unexpected value: " + tag);
//                };
//
//                try {
//                    register(furnaceRecipe);
//                } catch (RuntimeException e) {
//
//                } //TODO: remove this when Endstone will fix duplication of furnaceAux recipes
//
//                var xp = furnaceXpConfig.getDouble(input.get("item") + ":" + (int) outputData);
//                if (xp != 0) {
//                    this.setRecipeXp(furnaceRecipe, xp);
//                }
//            }

            //Shaped recipes
            // Endstone reader
//            List<Map<String, Object>> shaped = (List<Map<String, Object>>) recipeConfig.getList("shaped");
//            for (Map<String, Object> shapedRecipe : shaped) {
//                register(parseShapeRecipe(shapedRecipe));
//            }

            //Shapeless
            //Endstone Reader
//            List<Map<String, Object>> shapeless = (List<Map<String, Object>>) recipeConfig.getList("shapeless");
//            for (Map<String, Object> shapelessRecipe : shapeless) {
//                String block = (String) shapelessRecipe.get("tag");
//                register(parseShapelessRecipe(shapelessRecipe, block));
//            }

            //Multi recipes
            // Endstone Reader
//            List<Map<String, Object>> multi = (List<Map<String, Object>>) recipeConfig.getList("multi");
//            for (Map<String, Object> multiRecipe : multi) {
//                UUID uuid = UUID.fromString((String) multiRecipe.get("uuid"));
//                register(new MultiRecipe(uuid));
//            }

            //Smithing Recipes
            // Endstone Reader
//            List<Map<String, Object>> smithingTrim = (List<Map<String, Object>>) recipeConfig.getList("smithingTrim");
//            for (Map<String, Object> smithingTrimRecipe : smithingTrim) {
//                String id = (String) smithingTrimRecipe.get("id");
//                Map<String, Object> base = (Map<String, Object>) smithingTrimRecipe.get("base");
//                ItemDescriptor baseItem = parseRecipeItem(base);
//                Map<String, Object> addition = (Map<String, Object>) smithingTrimRecipe.get("addition");
//                ItemDescriptor additionItem = parseRecipeItem(addition);
//                Map<String, Object> template = (Map<String, Object>) smithingTrimRecipe.get("template");
//                ItemDescriptor templateItem = parseRecipeItem(template);
//
//                register(new SmithingTrimRecipe(id, baseItem, additionItem, templateItem, "smithing_table"));
//            }

            // Endstone Reader
//            List<Map<String, Object>> smithingTransform = (List<Map<String, Object>>) recipeConfig.getList("smithingTransform");
//            for (Map<String, Object> smithingTransformRecipe : smithingTransform) {
//                register(parseShapelessRecipe(smithingTransformRecipe, "smithing_table"));
//            }

            // Endstone Reader
//            List<Map<String, Object>> shulkerBox = (List<Map<String, Object>>) recipeConfig.getList("shulkerBox");
//            for (Map<String, Object> shulkerBoxRecipe : shulkerBox) {
//                register(parseShapelessRecipe(shulkerBoxRecipe, "crafting_table"));
//            }

        } catch (IOException e) {
            log.warn("Failed to load recipes config");
        }

        // Allow to rename without crafting
        register(new CartographyRecipe(Item.get(ItemID.EMPTY_MAP, 0, 1, EmptyArrays.EMPTY_BYTES, false),
                Collections.singletonList(Item.get(ItemID.EMPTY_MAP, 0, 1, EmptyArrays.EMPTY_BYTES, false))));
        register(new CartographyRecipe(Item.get(ItemID.EMPTY_MAP, 2, 1, EmptyArrays.EMPTY_BYTES, false),
                Collections.singletonList(Item.get(ItemID.EMPTY_MAP, 2, 1, EmptyArrays.EMPTY_BYTES, false))));
        register(new CartographyRecipe(Item.get(ItemID.FILLED_MAP, 0, 1, EmptyArrays.EMPTY_BYTES, false),
                Collections.singletonList(Item.get(ItemID.FILLED_MAP, 0, 1, EmptyArrays.EMPTY_BYTES, false))));
        register(new CartographyRecipe(Item.get(ItemID.FILLED_MAP, 3, 1, EmptyArrays.EMPTY_BYTES, false),
                Collections.singletonList(Item.get(ItemID.FILLED_MAP, 3, 1, EmptyArrays.EMPTY_BYTES, false))));
        register(new CartographyRecipe(Item.get(ItemID.FILLED_MAP, 4, 1, EmptyArrays.EMPTY_BYTES, false),
                Collections.singletonList(Item.get(ItemID.FILLED_MAP, 4, 1, EmptyArrays.EMPTY_BYTES, false))));
        register(new CartographyRecipe(Item.get(ItemID.FILLED_MAP, 5, 1, EmptyArrays.EMPTY_BYTES, false),
                Collections.singletonList(Item.get(ItemID.FILLED_MAP, 5, 1, EmptyArrays.EMPTY_BYTES, false))));
    }

    /**
     * Temporary used for parsing information out of the new recipes.json format
     *
     * @return parsed ItemDescriptor
     */
    private ItemDescriptor parseDescription(Map<String, Object> data, ParseType parseType) {
        ItemDescriptor descriptor = null;

        switch (parseType) {
            case SMITHING_TABLE, CRAFTING_TABLE_INPUT, CARTOGRAPHY_TABLE_INPUT, STONECUTTER_INPUT -> {
                if (data.get("type").equals("item_tag")) {
                    String itemTag = (String) data.get("itemTag");
                    int count = data.containsKey("count") ? Utils.toInt(data.get("count")) : 1;
                    descriptor = new ItemTagDescriptor(itemTag, count);

                } else if (data.get("type").equals("complex_alias")) {
                    String itemId = (String) data.get("name");
                    int count = (int) ((double) data.get("count"));
                    Item item = Item.get(itemId, 0, count);
                    item.disableMeta();
                    descriptor = new DefaultDescriptor(item);

                } else {    // only other possibility is the "default" type
                    String itemId = (String) data.get("itemId");
                    int count = data.containsKey("count") ? Utils.toInt(data.get("count")) : 1;
                    short meta = (short) ((double) data.get("auxValue"));
                    Item item;
                    if (meta == Short.MAX_VALUE || meta == -1) {
                        item = Item.get(itemId, 0, count);
                        item.disableMeta();
                    } else {
                        item = Item.get(itemId, meta, count);
                    }
                    descriptor = new DefaultDescriptor(item);
                }
            }
            case CRAFTING_TABLE_OUTPUT, CARTOGRAPHY_TABLE_OUTPUT, STONECUTTER_OUTPUT, FURNACE_INPUT, FURNACE_OUTPUT -> {
                String id = (String) data.get("id");
                int count = 1;
                short meta = 0;

                if (data.containsKey("count")) {
                    count = (int) ((double) data.get("count"));
                }

                if (data.containsKey("damage")) {
                    meta = (short) ((double) data.get("damage"));
                }

                Item item;

                if (meta == Short.MAX_VALUE || meta == -1) {
                    item = Item.get(id, 0, count);
                    item.disableMeta();
                } else {
                    item = Item.get(id, meta, count);
                }
                descriptor = new DefaultDescriptor(item);
            }
        }

        return descriptor;
    }

    enum ParseType {
        SMITHING_TABLE,
        CRAFTING_TABLE_INPUT,
        CRAFTING_TABLE_OUTPUT,
        STONECUTTER_INPUT,
        STONECUTTER_OUTPUT,
        CARTOGRAPHY_TABLE_INPUT,
        CARTOGRAPHY_TABLE_OUTPUT,

        // we use this two parse types for all blocks similar to furnace:
        // blast_furnace, smoker, campfire and soul_campfire
        FURNACE_INPUT,
        FURNACE_OUTPUT
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
            ArrayList<Map<String, Object>> output = (ArrayList<Map<String, Object>>) recipeObject.get("output");
            ItemDescriptor outputItem = parseRecipeItem(output.get(0));
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
        Map<String, Object> first = outputs.getFirst();

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

        RecipeUnlockingRequirement recipeUnlockingRequirement = new RecipeUnlockingRequirement(RecipeUnlockingRequirement.UnlockingContext.ALWAYS_UNLOCKED);

        return switch (craftingBlock) {
            case "crafting_table", "deprecated" ->
                    new ShapelessRecipe(id, uuid, priority, resultItem, itemDescriptors, recipeUnlockingRequirement);
            case "shulker_box" ->
                    new UserDataShapelessRecipe(id, uuid, priority, resultItem, itemDescriptors, recipeUnlockingRequirement);
            case "stonecutter" ->
                    new StonecutterRecipe(id, uuid, priority, resultItem, itemDescriptors.get(0).toItem(), recipeUnlockingRequirement);
            case "cartography_table" ->
                    new CartographyRecipe(id, uuid, priority, resultItem, itemDescriptors, recipeUnlockingRequirement);
            default -> null;
        };
    }

    private Recipe parseShapeRecipe(Map<String, Object> recipeObject) {
        String id = recipeObject.get("id").toString();
        UUID uuid = UUID.fromString(recipeObject.get("uuid").toString());
        List<Map<String, Object>> outputs = (List<Map<String, Object>>) recipeObject.get("output");

        Map<String, Object> first = outputs.removeFirst();
        String[] shape = ((List<String>) recipeObject.get("pattern")).toArray(EmptyArrays.EMPTY_STRINGS);
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

        Map<?, ?> input = (Map<?, ?>) recipeObject.get("input");
        boolean mirror = false;
        if (input.containsKey("assumeSymetry")) {
            mirror = Boolean.parseBoolean(input.remove("assumeSymetry").toString());
        }
        Map<String, Map<String, Object>> input2 = (Map<String, Map<String, Object>>) input;
        for (Map.Entry<String, Map<String, Object>> ingredientEntry : input2.entrySet()) {
            char ingredientChar = ingredientEntry.getKey().charAt(0);
            var ingredient = ingredientEntry.getValue();
            ItemDescriptor itemDescriptor = parseRecipeItem(ingredient);
            if (itemDescriptor == null) return null;
            ingredients.put(ingredientChar, itemDescriptor);
        }

        RecipeUnlockingRequirement recipeUnlockingRequirement = null;
        return new ShapedRecipe(id, uuid, priority, primaryResult.toItem(), shape, ingredients, extraResults, mirror, recipeUnlockingRequirement);
    }

    private ItemDescriptor parseRecipeItem(Map<String, Object> data) {
        ItemDescriptorType itemDescriptorType = data.containsKey("tag") ? ItemDescriptorType.ITEM_TAG : ItemDescriptorType.DEFAULT;
        return switch (itemDescriptorType) {
            case DEFAULT -> {
                Item item;
                String name = data.get("item").toString();

                int count = data.containsKey("count") ? Utils.toInt(data.get("count")) : 1;

                String nbt = (String) data.get("nbt");
                byte[] nbtBytes = null;
                nbtBytes = nbt != null ? Base64.getDecoder().decode(nbt) : EmptyArrays.EMPTY_BYTES; //TODO: idk how to fix nbt, cuz we don't use Cloudburst NBT

                Integer meta = null;
                if (data.containsKey("data")) meta = Utils.toInt(data.get("data"));

                //normal item
                if (meta != null) {
                    if (meta == Short.MAX_VALUE || meta == -1) {
                        item = Item.get(name, 0, count, nbtBytes, false);
                        item.disableMeta();
                    } else {
                        item = Item.get(name, meta, count, nbtBytes, false);
                    }
                } else {
                    item = Item.get(name, 0, count, nbtBytes, false);
                }
                yield new DefaultDescriptor(item);
            }
            case ITEM_TAG -> {
                var itemTag = data.get("tag").toString();
                int count = data.containsKey("count") ? Utils.toInt(data.get("count")) : 1;
                yield new ItemTagDescriptor(itemTag, count);
            }
            default -> throw new IllegalStateException("Unexpected value: " + itemDescriptorType);
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
