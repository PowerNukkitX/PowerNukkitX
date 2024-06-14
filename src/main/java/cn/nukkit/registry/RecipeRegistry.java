package cn.nukkit.registry;

import cn.nukkit.Server;
import cn.nukkit.block.BlockState;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.CraftingDataPacket;
import cn.nukkit.network.protocol.types.RecipeUnlockingRequirement;
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
import com.google.common.collect.Collections2;
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
import java.nio.ByteOrder;
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
        Int2ObjectArrayMap<Set<Recipe>> recipe_data = recipeMaps.get(RecipeType.FURNACE_DATA);
        if (recipe_data != null) {
            for (var s : recipe_data.values()) {
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
        Int2ObjectArrayMap<Set<Recipe>> map2 = recipeMaps.get(RecipeType.FURNACE_DATA);
        if (map2 == null) return null;
        Set<Recipe> recipes2 = map2.get(items.length);
        if (recipes2 != null) {
            for (var r : recipes2) {
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
        Int2ObjectArrayMap<Set<Recipe>> recipe_data = recipeMaps.get(RecipeType.BLAST_FURNACE_DATA);
        if (recipe_data != null) {
            for (var s : recipe_data.values()) {
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
        Int2ObjectArrayMap<Set<Recipe>> map2 = recipeMaps.get(RecipeType.BLAST_FURNACE_DATA);
        Set<Recipe> recipes2 = map2.get(items.length);
        if (recipes2 == null)
            return null;

        for (var r : recipes2) {
            if (r.fastCheck(items)) return (BlastFurnaceRecipe) r;
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
        Int2ObjectArrayMap<Set<Recipe>> smoker_data = recipeMaps.get(RecipeType.SMOKER_DATA);
        if (smoker_data != null) {
            for (var s : smoker_data.values()) {
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
        Int2ObjectArrayMap<Set<Recipe>> map2 = recipeMaps.get(RecipeType.SMOKER_DATA);
        if (map2 == null) return null;
        Set<Recipe> recipes2 = map2.get(items.length);
        if (recipes2 != null) {
            for (var r : recipes2) {
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
        Int2ObjectArrayMap<Set<Recipe>> r2 = recipeMaps.get(RecipeType.CAMPFIRE_DATA);
        if (r2 != null) {
            for (var s : r2.values()) {
                result.addAll(Collections2.transform(s, d -> (CampfireRecipe) d));
            }
        }
        Int2ObjectArrayMap<Set<Recipe>> r3 = recipeMaps.get(RecipeType.SOUL_CAMPFIRE);
        if (r3 != null) {
            for (var s : r3.values()) {
                result.addAll(Collections2.transform(s, d -> (CampfireRecipe) d));
            }
        }
        Int2ObjectArrayMap<Set<Recipe>> r4 = recipeMaps.get(RecipeType.SOUL_CAMPFIRE_DATA);
        if (r4 != null) {
            for (var s : r4.values()) {
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
        Set<Recipe> recipes2 = recipeMaps.get(RecipeType.CAMPFIRE_DATA).get(items.length);
        if (recipes2 != null) {
            for (var r : recipes2) {
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
            case STONECUTTER, SHAPELESS, CARTOGRAPHY, SHULKER_BOX, SMITHING_TRANSFORM, SMITHING_TRIM,
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
                        String id = recipe.get("id").toString();
                        Map<String, Object> base = (Map<String, Object>) recipe.get("base");
                        ItemDescriptor baseItem = parseRecipeItem(base);
                        Map<String, Object> addition = (Map<String, Object>) recipe.get("addition");
                        ItemDescriptor additionItem = parseRecipeItem(addition);
                        Map<String, Object> template = (Map<String, Object>) recipe.get("template");
                        ItemDescriptor templateItem = parseRecipeItem(template);
                        if (additionItem == null || baseItem == null || templateItem == null) {
                            yield null;
                        }
                        yield new SmithingTrimRecipe(id, baseItem, additionItem, templateItem, "smithing_table");
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
                            case "campfire" -> new CampfireRecipe(result, input);
                            case "soul_campfire" -> new SoulCampfireRecipe(result, input);
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
                    log.warn("Load recipe {} with null!", recipe.toString().substring(0, 60));
                    continue;
                }
                this.register(re);
            }
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

        RecipeUnlockingRequirement recipeUnlockingRequirement = parseRequirement(recipeObject);

        return switch (craftingBlock) {
            case "crafting_table", "deprecated" ->
                    new ShapelessRecipe(id, uuid, priority, resultItem, itemDescriptors, recipeUnlockingRequirement);
            case "shulker_box" ->
                    new ShulkerBoxRecipe(id, uuid, priority, resultItem, itemDescriptors, recipeUnlockingRequirement);
            case "stonecutter" ->
                    new StonecutterRecipe(id, uuid, priority, resultItem, itemDescriptors.get(0).toItem(), recipeUnlockingRequirement);
            case "cartography_table" ->
                    new CartographyRecipe(id, uuid, priority, resultItem, itemDescriptors, recipeUnlockingRequirement);
            default -> null;
        };
    }

    private RecipeUnlockingRequirement parseRequirement(Map<String, Object> recipeObject) {
        if (recipeObject.containsKey("requirement")) {
            Map<String, Object> requirement = (Map<String, Object>) recipeObject.get("requirement");
            String string = requirement.get("context").toString();
            RecipeUnlockingRequirement.UnlockingContext unlockingContext = RecipeUnlockingRequirement.UnlockingContext.valueOf(string.toUpperCase(Locale.ENGLISH));

            List<Map<String, Object>> items = (List<Map<String, Object>>) requirement.get("items");
            RecipeUnlockingRequirement recipeUnlockingRequirement = new RecipeUnlockingRequirement(unlockingContext);
            recipeUnlockingRequirement.getIngredients().addAll(items.stream().map(this::parseRecipeItem).toList());
            return recipeUnlockingRequirement;
        }
        return null;
    }

    private Recipe parseShapeRecipe(Map<String, Object> recipeObject) {
        String id = recipeObject.get("id").toString();
        UUID uuid = UUID.fromString(recipeObject.get("uuid").toString());
        List<Map<String, Object>> outputs = (List<Map<String, Object>>) recipeObject.get("output");

        Map<String, Object> first = outputs.removeFirst();
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

        RecipeUnlockingRequirement recipeUnlockingRequirement = parseRequirement(recipeObject);
        return new ShapedRecipe(id, uuid, priority, primaryResult.toItem(), shape, ingredients, extraResults, mirror, recipeUnlockingRequirement);
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
                } else if (data.containsKey("name")) {
                    name = data.get("name").toString();
                }
                if (name == null) yield null;

                int count = data.containsKey("count") ? Utils.toInt(data.get("count")) : 1;

                String nbt = (String) data.get("nbt");
                byte[] nbtBytes = nbt != null ? Base64.getDecoder().decode(nbt) : EmptyArrays.EMPTY_BYTES;

                Integer meta = null;
                if (data.containsKey("damage")) {
                    meta = Utils.toInt(data.get("damage"));
                } else if (data.containsKey("auxValue")) {
                    meta = Utils.toInt(data.get("auxValue"));
                }

                //block item
                if (data.containsKey("blockHash") || data.containsKey("block_states")) {
                    BlockState blockState;
                    if (data.containsKey("blockHash")) {
                        int blockHash = Utils.toInt(data.get("blockHash"));
                        blockState = Registries.BLOCKSTATE.get(blockHash);
                    } else {
                        try {
                            blockState = NBTIO.getBlockStateHelper(new CompoundTag()
                                    .putString("name", name)
                                    .putCompound("states", NBTIO.read(Base64.getDecoder().decode(data.get("block_states").toString()), ByteOrder.LITTLE_ENDIAN))
                            );
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    if (blockState == null) {
                        log.error("parseRecipeItem {} error because blockstate is null", name);
                        yield null;
                    }
                    item = blockState.toItem();
                    item.setCount(count);
                    if (nbtBytes != EmptyArrays.EMPTY_BYTES) {
                        item.setCompoundTag(nbtBytes);
                    }
                    if (meta != null && (meta == Short.MAX_VALUE || meta == -1)) {
                        item.disableMeta();
                    }
                    yield new DefaultDescriptor(item);
                }

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
            case COMPLEX_ALIAS -> {
                String name = data.get("name").toString();
                yield new ComplexAliasDescriptor(name);
            }
            case ITEM_TAG -> {
                var itemTag = data.get("itemTag").toString();
                int count = data.containsKey("count") ? Utils.toInt(data.get("count")) : 1;
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
