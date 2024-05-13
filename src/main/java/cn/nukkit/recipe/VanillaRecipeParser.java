package cn.nukkit.recipe;

import cn.nukkit.entity.effect.PotionType;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemPotion;
import cn.nukkit.recipe.descriptor.DefaultDescriptor;
import cn.nukkit.recipe.descriptor.ItemDescriptor;
import cn.nukkit.recipe.descriptor.ItemTagDescriptor;
import cn.nukkit.registry.RecipeRegistry;
import cn.nukkit.registry.Registries;
import cn.nukkit.utils.JSONUtils;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Slf4j
public class VanillaRecipeParser {
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

    private final RecipeRegistry recipeRegistry;

    public VanillaRecipeParser(RecipeRegistry recipeRegistry) {
        this.recipeRegistry = recipeRegistry;
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
        Map<String, Object> map = JSONUtils.from(reader, new TypeToken<Map<String, Object>>() {
        });
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
                Registries.RECIPE.register(new ShapedRecipe(description(recipeData), prior, parseItem(result), shapes, ingredients, List.of()));
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
                    case CRAFTING_TABLE_TAG -> new ShapelessRecipe(description(recipeData), prior, re, itemDescriptors);
                    case SHULKER_BOX_TAG -> new ShulkerBoxRecipe(description(recipeData), prior, re, itemDescriptors);
                    case STONE_CUTTER_TAG ->
                            new StonecutterRecipe(description(recipeData), prior, re, itemDescriptors.get(0).toItem());
                    case CARTOGRAPHY_TABLE_TAG ->
                            new CartographyRecipe(description(recipeData), prior, re, itemDescriptors);
                    default -> throw new IllegalArgumentException(tag);
                };
                Registries.RECIPE.register(recipe);
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
            Registries.RECIPE.register(recipe);
        }
    }

    private void parseAndRegisterBrewRecipe(Map<String, Object> recipeData) {
        String inputID = "minecraft:" + recipeData.get("input").toString().split(":")[2].toLowerCase(Locale.ENGLISH);
        Item input = ItemPotion.fromPotion(PotionType.get(inputID));
        String outputID = "minecraft:" + recipeData.get("output").toString().split(":")[2].toLowerCase(Locale.ENGLISH);
        Item output = ItemPotion.fromPotion(PotionType.get(outputID));
        Item reagent = Item.get(recipeData.get("reagent").toString());
        if (input.isNull() || output.isNull() || reagent.isNull()) {
            return;
        }
        List<String> tags = tags(recipeData);
        if (tags.get(0).equals(BREW_STAND_TAG)) {
            Registries.RECIPE.register(new BrewingRecipe(description(recipeData), input, reagent, output));
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
            Registries.RECIPE.register(new ContainerRecipe(description(recipeData), input, reagent, output));
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
                i = i.clone();
                i.disableMeta();
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
