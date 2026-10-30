package org.powernukkitx.addons.parser;

import org.powernukkitx.addons.AddonFeatureTracker;
import org.powernukkitx.item.Item;
import org.powernukkitx.entity.effect.PotionType;
import org.powernukkitx.item.ItemPotion;
import org.powernukkitx.recipe.BlastFurnaceRecipe;
import org.powernukkitx.recipe.BrewingRecipe;
import org.powernukkitx.recipe.CampfireRecipe;
import org.powernukkitx.recipe.CartographyRecipe;
import org.powernukkitx.recipe.ContainerRecipe;
import org.powernukkitx.recipe.FurnaceRecipe;
import org.powernukkitx.recipe.Recipe;
import org.powernukkitx.recipe.ShapedRecipe;
import org.powernukkitx.recipe.ShapelessRecipe;
import org.powernukkitx.recipe.SmithingTransformRecipe;
import org.powernukkitx.recipe.SmithingTrimRecipe;
import org.powernukkitx.recipe.SmokerRecipe;
import org.powernukkitx.recipe.SoulCampfireRecipe;
import org.powernukkitx.recipe.StonecutterRecipe;
import org.powernukkitx.recipe.UserDataShapelessRecipe;
import org.powernukkitx.recipe.descriptor.DefaultDescriptor;
import org.powernukkitx.recipe.descriptor.ItemDescriptor;
import org.powernukkitx.recipe.descriptor.ItemTagDescriptor;
import org.powernukkitx.registry.RecipeRegistry;
import org.powernukkitx.registry.RegisterException;
import org.powernukkitx.registry.Registries;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.cloudburstmc.protocol.bedrock.data.payload.crafting.RecipeUnlockingContext;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Turns a behaviour pack's {@code recipes/*.json} into registered recipes.
 * <p>
 * Supported: {@code minecraft:recipe_shaped} and {@code minecraft:recipe_shapeless} for the
 * crafting table and for custom crafting tables named by a block's {@code minecraft:crafting_table}
 * component, shapeless recipes tagged {@code stonecutter}, {@code cartography_table} or
 * {@code shulker_box}, {@code minecraft:recipe_furnace} for the furnace, smoker, blast furnace
 * and both campfires, {@code minecraft:recipe_smithing_transform}, {@code minecraft:recipe_smithing_trim},
 * {@code minecraft:recipe_brewing_mix} and {@code minecraft:recipe_brewing_container}. A document with
 * several tags registers one recipe per tag. A crafting recipe's {@code unlock} conditions decide when it
 * appears in the recipe book; without them it is always unlocked.
 * <p>
 * A recipe whose {@code minecraft:} identifier names a vanilla recipe replaces it.
 * <p>
 * Recipes are registered after the vanilla set, so the caller must rebuild the crafting data
 * packet with {@link RecipeRegistry#rebuildPacket()} once every pack has been read. Anything
 * else - other recipe types, an unknown station, an ingredient that is not registered - is
 * recorded on the {@link AddonFeatureTracker} and the recipe is skipped.
 */
@Slf4j
public final class BehaviorRecipeParser {

    private static final String SHAPED_KEY = "minecraft:recipe_shaped";
    private static final String SHAPELESS_KEY = "minecraft:recipe_shapeless";
    private static final String FURNACE_KEY = "minecraft:recipe_furnace";
    private static final String SMITHING_TRANSFORM_KEY = "minecraft:recipe_smithing_transform";
    private static final String SMITHING_TRIM_KEY = "minecraft:recipe_smithing_trim";
    private static final String BREWING_MIX_KEY = "minecraft:recipe_brewing_mix";
    private static final String BREWING_CONTAINER_KEY = "minecraft:recipe_brewing_container";
    private static final String CRAFTING_TABLE_TAG = "crafting_table";
    private static final int ANY_DATA = 32767;

    private final AddonFeatureTracker tracker;

    public BehaviorRecipeParser(AddonFeatureTracker tracker) {
        this.tracker = tracker;
    }

    /**
     * Parses one recipe document and registers the recipes it describes.
     * <p>
     * Must be called on the thread that loads the registries, after every item and block the
     * pack defines is registered, because ingredients and results are resolved against the item
     * registry here and network IDs are handed out from the recipes registered so far.
     *
     * @param packName the pack the document came from, for the tracker
     * @param fileName the entry name inside the pack, for logging
     * @param root     the parsed document
     * @return how many recipes were registered, one per tag; 0 when the document was skipped
     */
    public int parseAndRegister(@NotNull String packName, @NotNull String fileName, @NotNull JsonObject root) {
        int count = 0;
        for (Map.Entry<String, JsonElement> element : root.entrySet()) {
            String type = element.getKey();
            if (type.equals("format_version")) {
                continue;
            }
            if (!element.getValue().isJsonObject()) {
                tracker.reportUnsupported(packName, "recipe-invalid", fileName);
                continue;
            }
            JsonObject recipe = element.getValue().getAsJsonObject();
            try {
                count += switch (type) {
                    case SHAPED_KEY -> registerShaped(packName, fileName, recipe);
                    case SHAPELESS_KEY -> registerShapeless(packName, fileName, recipe);
                    case FURNACE_KEY -> registerFurnace(packName, fileName, recipe);
                    case SMITHING_TRANSFORM_KEY -> registerSmithingTransform(packName, fileName, recipe);
                    case SMITHING_TRIM_KEY -> registerSmithingTrim(packName, fileName, recipe);
                    case BREWING_MIX_KEY -> registerBrewing(packName, fileName, recipe, false);
                    case BREWING_CONTAINER_KEY -> registerBrewing(packName, fileName, recipe, true);
                    default -> {
                        tracker.reportUnsupported(packName, "recipe-type", type);
                        yield 0;
                    }
                };
            } catch (UnknownItemException e) {
                tracker.reportUnsupported(packName, "recipe-unknown-item", e.getMessage());
            } catch (RuntimeException e) {
                log.debug("Behavior pack '{}': recipe {} rejected", packName, fileName, e);
                tracker.reportUnsupported(packName, "recipe-invalid", fileName);
            }
        }
        return count;
    }

    private int registerShaped(String packName, String fileName, JsonObject recipe) {
        String id = identifier(packName, fileName, recipe);
        List<String> tags = tags(packName, fileName, recipe);
        if (id == null || tags.isEmpty()) {
            return 0;
        }
        String[] shape = shape(recipe.get("pattern"));
        Map<Character, ItemDescriptor> ingredients = new LinkedHashMap<>();
        JsonObject key = recipe.getAsJsonObject("key");
        if (key != null) {
            String symbols = String.join("", shape);
            for (Map.Entry<String, JsonElement> entry : key.entrySet()) {
                char symbol = entry.getKey().charAt(0);
                if (entry.getKey().length() == 1 && symbols.indexOf(symbol) >= 0) {
                    ingredients.put(symbol, descriptor(entry.getValue()));
                }
            }
        }
        List<Item> results = results(recipe.get("result"));
        int priority = asInt(recipe.get("priority"), 0);
        boolean mirror = !recipe.has("assume_symmetry") || recipe.get("assume_symmetry").getAsBoolean();

        int count = 0;
        for (String tag : tags) {
            String recipeKey = registryKey(id, tags, tag);
            ShapedRecipe.Builder builder = ShapedRecipe.builder()
                    .id(recipeKey)
                    .netId(Registries.RECIPE.nextNetworkId())
                    .priority(priority)
                    .result(results.getFirst())
                    .shape(shape)
                    .mirror(mirror)
                    .craftingTag(tag);
            applyUnlock(recipe.get("unlock"), builder::unlockBy, builder::unlockingContext);
            ingredients.forEach(builder::ingredient);
            for (int i = 1; i < results.size(); i++) {
                builder.extraResult(results.get(i));
            }
            count += register(packName, recipeKey, builder.build());
        }
        return count;
    }

    private int registerShapeless(String packName, String fileName, JsonObject recipe) {
        String id = identifier(packName, fileName, recipe);
        List<String> tags = tags(packName, fileName, recipe);
        if (id == null || tags.isEmpty()) {
            return 0;
        }
        List<ItemDescriptor> ingredients = new ArrayList<>();
        JsonElement ingredientsElement = recipe.get("ingredients");
        if (ingredientsElement != null && ingredientsElement.isJsonArray()) {
            for (JsonElement ingredient : ingredientsElement.getAsJsonArray()) {
                ingredients.add(descriptor(ingredient));
            }
        }
        if (ingredients.isEmpty()) {
            tracker.reportUnsupported(packName, "recipe-invalid", fileName);
            return 0;
        }
        Item result = results(recipe.get("result")).getFirst();
        int priority = asInt(recipe.get("priority"), 0);

        int count = 0;
        for (String tag : tags) {
            String key = registryKey(id, tags, tag);
            Recipe built = switch (tag) {
                case "stonecutter" -> ingredients.size() == 1 && ingredients.getFirst() instanceof DefaultDescriptor single
                        ? new StonecutterRecipe(key, Registries.RECIPE.nextNetworkId(), priority, result, single.getItem())
                        : null;
                case "cartography_table" ->
                        new CartographyRecipe(key, Registries.RECIPE.nextNetworkId(), priority, result, ingredients);
                case "shulker_box" ->
                        new UserDataShapelessRecipe(key, Registries.RECIPE.nextNetworkId(), priority, result, ingredients);
                default -> {
                    ShapelessRecipe.Builder builder = ShapelessRecipe.builder()
                            .id(key)
                            .netId(Registries.RECIPE.nextNetworkId())
                            .priority(priority)
                            .result(result)
                            .craftingTag(tag);
                    applyUnlock(recipe.get("unlock"), builder::unlockBy, builder::unlockingContext);
                    ingredients.forEach(builder::ingredient);
                    yield builder.build();
                }
            };
            if (built == null) {
                tracker.reportUnsupported(packName, "recipe-invalid", fileName);
                continue;
            }
            count += register(packName, key, built);
        }
        return count;
    }

    private int registerFurnace(String packName, String fileName, JsonObject recipe) {
        String id = identifier(packName, fileName, recipe);
        List<String> tags = tags(packName, fileName, recipe);
        if (id == null || tags.isEmpty()) {
            return 0;
        }
        Item input = item(recipe.get("input"), ANY_DATA);
        Item output = item(recipe.get("output"), 0);

        int count = 0;
        for (String tag : tags) {
            String key = registryKey(id, tags, tag);
            Recipe built = switch (tag) {
                case "furnace" -> new FurnaceRecipe(key, output, input);
                case "smoker" -> new SmokerRecipe(key, output, input);
                case "blast_furnace" -> new BlastFurnaceRecipe(key, output, input);
                case "campfire" -> new CampfireRecipe(key, output, input);
                case "soul_campfire" -> new SoulCampfireRecipe(key, output, input);
                default -> null;
            };
            if (built == null) {
                tracker.reportUnsupported(packName, "recipe-station", tag);
                continue;
            }
            count += register(packName, key, built);
        }
        return count;
    }

    private int registerSmithingTransform(String packName, String fileName, JsonObject recipe) {
        String id = identifier(packName, fileName, recipe);
        List<String> tags = tags(packName, fileName, recipe);
        if (id == null || tags.isEmpty()) {
            return 0;
        }
        ItemDescriptor template = descriptor(required(recipe, "template"));
        ItemDescriptor base = descriptor(required(recipe, "base"));
        ItemDescriptor addition = descriptor(required(recipe, "addition"));
        Item result = item(recipe.get("result"), 0);
        int count = 0;
        for (String tag : tags) {
            String key = registryKey(id, tags, tag);
            count += register(packName, key,
                    new SmithingTransformRecipe(key, Registries.RECIPE.nextNetworkId(), result, base, addition, template));
        }
        return count;
    }

    private int registerSmithingTrim(String packName, String fileName, JsonObject recipe) {
        String id = identifier(packName, fileName, recipe);
        List<String> tags = tags(packName, fileName, recipe);
        if (id == null || tags.isEmpty()) {
            return 0;
        }
        ItemDescriptor template = descriptor(required(recipe, "template"));
        ItemDescriptor base = descriptor(required(recipe, "base"));
        ItemDescriptor addition = descriptor(required(recipe, "addition"));
        int count = 0;
        for (String tag : tags) {
            String key = registryKey(id, tags, tag);
            count += register(packName, key,
                    new SmithingTrimRecipe(key, Registries.RECIPE.nextNetworkId(), base, addition, template, tag));
        }
        return count;
    }

    /**
     * Brewing mixes name potions by type, {@code minecraft:potion_type:water}; container recipes, which
     * turn one kind of potion into another, name plain items.
     */
    private int registerBrewing(String packName, String fileName, JsonObject recipe, boolean container) {
        String id = identifier(packName, fileName, recipe);
        List<String> tags = tags(packName, fileName, recipe);
        if (id == null || tags.isEmpty()) {
            return 0;
        }
        if (!tags.contains("brewing_stand")) {
            tags.forEach(tag -> tracker.reportUnsupported(packName, "recipe-station", tag));
            return 0;
        }
        Item reagent = item(recipe.get("reagent"), 0);
        Item input = container ? item(recipe.get("input"), 0) : potion(recipe.get("input"));
        Item output = container ? item(recipe.get("output"), 0) : potion(recipe.get("output"));
        return register(packName, id, container
                ? new ContainerRecipe(id, input, reagent, output)
                : new BrewingRecipe(id, input, reagent, output));
    }

    private static Item potion(@Nullable JsonElement element) {
        String reference = element != null && element.isJsonPrimitive() ? element.getAsString() : "";
        String prefix = "minecraft:potion_type:";
        if (!reference.startsWith(prefix)) {
            return item(element, 0);
        }
        String typeId = "minecraft:" + reference.substring(prefix.length()).toLowerCase(Locale.ENGLISH);
        PotionType type = PotionType.get(typeId);
        if (type == null) {
            throw new UnknownItemException(reference);
        }
        return ItemPotion.fromPotion(type);
    }

    private static JsonElement required(JsonObject recipe, String key) {
        JsonElement element = recipe.get(key);
        if (element == null) {
            throw new IllegalArgumentException(key + " missing");
        }
        return element;
    }

    /**
     * Reads a crafting recipe's {@code unlock} list: items or item tags that unlock it when the player
     * obtains one, or a {@code context} such as {@code PlayerInWater} or {@code AlwaysUnlocked}.
     */
    private static void applyUnlock(@Nullable JsonElement unlock, Consumer<ItemDescriptor> byItem,
                                    Consumer<RecipeUnlockingContext> byContext) {
        if (unlock == null) {
            return;
        }
        List<JsonElement> entries = new ArrayList<>();
        if (unlock.isJsonArray()) {
            unlock.getAsJsonArray().forEach(entries::add);
        } else {
            entries.add(unlock);
        }
        for (JsonElement entry : entries) {
            if (entry.isJsonObject() && entry.getAsJsonObject().has("context")) {
                RecipeUnlockingContext context = unlockingContext(entry.getAsJsonObject().get("context").getAsString());
                if (context != null && context != RecipeUnlockingContext.NONE) {
                    byContext.accept(context);
                    return;
                }
            }
        }
        for (JsonElement entry : entries) {
            if (!(entry.isJsonObject() && entry.getAsJsonObject().has("context"))) {
                byItem.accept(descriptor(entry));
            }
        }
    }

    private static @Nullable RecipeUnlockingContext unlockingContext(String name) {
        try {
            return RecipeUnlockingContext.valueOf(name.replaceAll("([a-z])([A-Z])", "$1_$2").toUpperCase(Locale.ENGLISH));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private int register(String packName, String key, Recipe recipe) {
        if (key.startsWith("minecraft:") && key.indexOf('#') < 0) {
            Registries.RECIPE.unregister(key);
        }
        try {
            Registries.RECIPE.register(key, recipe);
            return 1;
        } catch (RegisterException e) {
            tracker.reportUnsupported(packName, "duplicate-recipe", key);
            return 0;
        }
    }

    /**
     * A recipe with several tags is registered once per tag; the registry is keyed by recipe
     * identifier, so every tag after the first is kept apart by a suffix.
     */
    private static String registryKey(String id, List<String> tags, String tag) {
        return tag.equals(tags.getFirst()) ? id : id + "#" + tag;
    }

    private @Nullable String identifier(String packName, String fileName, JsonObject recipe) {
        JsonObject description = recipe.getAsJsonObject("description");
        if (description == null || !description.has("identifier")) {
            tracker.reportUnsupported(packName, "recipe-invalid", fileName);
            return null;
        }
        return description.get("identifier").getAsString();
    }

    private List<String> tags(String packName, String fileName, JsonObject recipe) {
        List<String> tags = new ArrayList<>();
        JsonElement element = recipe.get("tags");
        if (element != null && element.isJsonArray()) {
            for (JsonElement tag : element.getAsJsonArray()) {
                if (tag.isJsonPrimitive() && !tag.getAsString().isBlank() && !tags.contains(tag.getAsString())) {
                    tags.add(tag.getAsString());
                }
            }
        }
        if (tags.isEmpty()) {
            tracker.reportUnsupported(packName, "recipe-invalid", fileName);
        }
        return tags;
    }

    /**
     * Rows shorter than the widest one are padded with empty slots, as Bedrock reads them.
     */
    private static String[] shape(@Nullable JsonElement element) {
        if (element == null || !element.isJsonArray() || element.getAsJsonArray().isEmpty()) {
            throw new IllegalArgumentException("pattern missing");
        }
        JsonArray rows = element.getAsJsonArray();
        int width = 0;
        for (JsonElement row : rows) {
            width = Math.max(width, row.getAsString().length());
        }
        String[] shape = new String[rows.size()];
        for (int i = 0; i < shape.length; i++) {
            String row = rows.get(i).getAsString();
            shape[i] = row + " ".repeat(width - row.length());
        }
        return shape;
    }

    private static ItemDescriptor descriptor(JsonElement element) {
        if (element.isJsonObject() && element.getAsJsonObject().has("tag")) {
            JsonObject json = element.getAsJsonObject();
            return new ItemTagDescriptor(json.get("tag").getAsString(), asInt(json.get("count"), 1));
        }
        return new DefaultDescriptor(item(element, ANY_DATA));
    }

    private static List<Item> results(@Nullable JsonElement element) {
        List<Item> results = new ArrayList<>();
        if (element != null && element.isJsonArray()) {
            for (JsonElement result : element.getAsJsonArray()) {
                results.add(item(result, 0));
            }
        } else if (element != null) {
            results.add(item(element, 0));
        }
        if (results.isEmpty()) {
            throw new IllegalArgumentException("result missing");
        }
        return results;
    }

    /**
     * Reads an item reference: either an identifier string or an {@code {item, data, count}}
     * object.
     *
     * @param defaultData the data value when none is given; {@value #ANY_DATA} matches any
     * @throws UnknownItemException when the identifier is not registered
     */
    private static Item item(@Nullable JsonElement element, int defaultData) {
        String id;
        int data = defaultData;
        int count = 1;
        if (element != null && element.isJsonPrimitive()) {
            id = element.getAsString();
        } else if (element != null && element.isJsonObject() && element.getAsJsonObject().has("item")) {
            JsonObject json = element.getAsJsonObject();
            id = json.get("item").getAsString();
            data = asInt(json.get("data"), defaultData);
            count = asInt(json.get("count"), 1);
        } else {
            throw new IllegalArgumentException("item reference missing");
        }
        Item item = Item.get(id);
        if (item.isNull()) {
            throw new UnknownItemException(id);
        }
        if (data == ANY_DATA) {
            item.disableMeta();
        } else if (data != 0) {
            item.setDamage(data);
        }
        item.setCount(count);
        return item;
    }

    private static int asInt(@Nullable JsonElement element, int fallback) {
        try {
            return element != null && element.isJsonPrimitive() ? element.getAsInt() : fallback;
        } catch (RuntimeException e) {
            return fallback;
        }
    }

    private static final class UnknownItemException extends RuntimeException {

        private UnknownItemException(String id) {
            super(id, null, false, false);
        }
    }
}
