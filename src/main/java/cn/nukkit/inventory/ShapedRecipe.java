package cn.nukkit.inventory;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.inventory.recipe.DefaultDescriptor;
import cn.nukkit.inventory.recipe.ItemDescriptor;
import cn.nukkit.inventory.recipe.ItemDescriptorType;
import cn.nukkit.inventory.recipe.ItemTagDescriptor;
import cn.nukkit.item.Item;
import com.google.common.collect.Maps;
import io.netty.util.collection.CharObjectHashMap;

import java.util.*;

import static cn.nukkit.inventory.Recipe.matchItemList;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ShapedRecipe implements CraftingRecipe {

    private String recipeId;
    private final Item primaryResult;
    private final List<Item> extraResults = new ArrayList<>();
    private final List<Item> ingredientsAggregate;
    private long least, most;
    private final String[] shape;
    private final int priority;
    @Deprecated
    @DeprecationDetails(since = "1.19.50-r2", reason = "new ingredients format", replaceWith = "newIngredients")
    private final CharObjectHashMap<Item> ingredients = new CharObjectHashMap<>();
    @PowerNukkitXOnly
    @Since("1.19.50-r2")
    private final CharObjectHashMap<ItemDescriptor> newIngredients = new CharObjectHashMap<>();

    public ShapedRecipe(Item primaryResult, String[] shape, Map<Character, Item> ingredients, List<Item> extraResults) {
        this(null, 1, primaryResult, shape, ingredients, extraResults);
    }

    /**
     * Constructs a ShapedRecipe instance.
     *
     * @param primaryResult    Primary result of the recipe
     * @param shape<br>        Array of 1, 2, or 3 strings representing the rows of the recipe.
     *                         This accepts an array of 1, 2 or 3 strings. Each string should be of the same length and must be at most 3
     *                         characters long. Each character represents a unique type of ingredient. Spaces are interpreted as air.
     * @param ingredients<br>  Char =&gt; Item map of items to be set into the shape.
     *                         This accepts an array of Items, indexed by character. Every unique character (except space) in the shape
     *                         array MUST have a corresponding item in this list. Space character is automatically treated as air.
     * @param extraResults<br> List of additional result items to leave in the crafting grid afterwards. Used for things like cake recipe
     *                         empty buckets.
     *                         <p>
     *                         Note: Recipes **do not** need to be square. Do NOT add padding for empty rows/columns.
     */
    public ShapedRecipe(String recipeId, int priority, Item primaryResult, String[] shape, Map<Character, Item> ingredients, List<Item> extraResults) {
        this(recipeId, priority, primaryResult, shape,
                Maps.transformEntries(ingredients, (Maps.EntryTransformer<Character, Item, ItemDescriptor>) (k, v) -> new DefaultDescriptor(v)),
                extraResults);
    }

    public ShapedRecipe(String recipeId, int priority, Item primaryResult, String[] shape, Map<Character, ItemDescriptor> ingredients, Collection<Item> extraResults) {
        this.recipeId = recipeId;
        this.priority = priority;
        int rowCount = shape.length;
        if (rowCount > 3 || rowCount <= 0) {
            throw new RuntimeException("Shaped recipes may only have 1, 2 or 3 rows, not " + rowCount);
        }

        int columnCount = shape[0].length();
        if (columnCount > 3 || columnCount <= 0) {
            throw new RuntimeException("Shaped recipes may only have 1, 2 or 3 columns, not " + columnCount);
        }


        for (int i = 0, shapeLength = shape.length; i < shapeLength; i++) {
            String row = shape[i];
            if (row.length() != columnCount) {
                throw new RuntimeException("Shaped recipe rows must all have the same length (expected " + columnCount + ", got " + row.length() + ")");
            }

            for (int x = 0; x < columnCount; ++x) {
                char c = row.charAt(x);

                if (c != ' ' && !ingredients.containsKey(c)) {
                    throw new RuntimeException("No item specified for symbol '" + c + "'");
                }
            }
            shape[i] = row.intern();
        }

        this.primaryResult = primaryResult.clone();
        this.extraResults.addAll(extraResults);

        this.shape = shape;

        for (var entry : ingredients.entrySet()) {
            this.setIngredient(entry.getKey(), entry.getValue());
        }

        this.ingredientsAggregate = new ArrayList<>();
        for (char c : String.join("", this.shape).toCharArray()) {
            if (c == ' ')
                continue;
            switch (this.newIngredients.get(c).getType()) {
                case DEFAULT -> {
                    Item ingredient = this.newIngredients.get(c).toItem().clone();
                    for (Item existingIngredient : this.ingredientsAggregate) {
                        if (existingIngredient.equals(ingredient, ingredient.hasMeta(), ingredient.hasCompoundTag())) {
                            existingIngredient.setCount(existingIngredient.getCount() + ingredient.getCount());
                            ingredient = null;
                            break;
                        }
                    }
                    if (ingredient != null)
                        this.ingredientsAggregate.add(ingredient);
                }
                default -> {
                }
            }
        }
        this.ingredientsAggregate.sort(CraftingManager.recipeComparator);
    }

    public int getWidth() {
        return this.shape[0].length();
    }

    public int getHeight() {
        return this.shape.length;
    }

    @Override
    public Item getResult() {
        return this.primaryResult;
    }

    @Override
    public String getRecipeId() {
        return this.recipeId;
    }

    @Override
    public UUID getId() {
        return new UUID(least, most);
    }

    @Override
    public void setId(UUID uuid) {
        this.least = uuid.getLeastSignificantBits();
        this.most = uuid.getMostSignificantBits();
        if (this.recipeId == null) {
            this.recipeId = getId().toString();
        }
    }

    public ShapedRecipe setIngredient(String key, Item item) {
        return this.setIngredient(key.charAt(0), item);
    }

    public ShapedRecipe setIngredient(char key, Item item) {
        return this.setIngredient(key, new DefaultDescriptor(item));
    }

    @PowerNukkitXOnly
    @Since("1.19.50-r2")
    public ShapedRecipe setIngredient(char key, ItemDescriptor item) {
        if (String.join("", this.shape).indexOf(key) < 0) {
            throw new RuntimeException("Symbol does not appear in the shape: " + key);
        }

        this.newIngredients.put(key, item);
        return this;
    }

    @Deprecated
    @DeprecationDetails(since = "1.19.50-r2", reason = "new ingredients format", replaceWith = "use getNewIngredientList()")
    public List<Item> getIngredientList() {
        List<Item> items = new ArrayList<>();
        for (int y = 0, y2 = getHeight(); y < y2; ++y) {
            for (int x = 0, x2 = getWidth(); x < x2; ++x) {
                items.add(getIngredient(x, y));
            }
        }
        return items;
    }

    public List<ItemDescriptor> getNewIngredientList() {
        List<ItemDescriptor> items = new ArrayList<>();
        for (int y = 0, y2 = getHeight(); y < y2; ++y) {
            for (int x = 0, x2 = getWidth(); x < x2; ++x) {
                items.add(getNewIngredient(x, y));
            }
        }
        return items;
    }

    public Map<Integer, Map<Integer, Item>> getIngredientMap() {
        Map<Integer, Map<Integer, Item>> ingredients = new LinkedHashMap<>();

        for (int y = 0, y2 = getHeight(); y < y2; ++y) {
            Map<Integer, Item> m = new LinkedHashMap<>();

            for (int x = 0, x2 = getWidth(); x < x2; ++x) {
                m.put(x, getIngredient(x, y));
            }

            ingredients.put(y, m);
        }

        return ingredients;
    }

    @Deprecated
    @DeprecationDetails(since = "1.19.50-r2", reason = "new ingredients format", replaceWith = "use getNewIngredient()")
    public Item getIngredient(int x, int y) {
        var descriptor = this.newIngredients.get(this.shape[y].charAt(x));

        if (descriptor.getType() == ItemDescriptorType.DEFAULT) {
            return descriptor.toItem() != null ? descriptor.toItem().clone() : Item.get(Item.AIR);
        }
        throw new UnsupportedOperationException("use getNewIngredient()");
    }

    public ItemDescriptor getNewIngredient(int x, int y) {
        try {
            var res = this.newIngredients.get(this.shape[y].charAt(x));
            return res != null ? res.clone() : new DefaultDescriptor(Item.get(Item.AIR));
        } catch (CloneNotSupportedException ignore) {
            return null;
        }
    }

    public String[] getShape() {
        return shape;
    }

    @Override
    public void registerToCraftingManager(CraftingManager manager) {
        manager.registerShapedRecipe(this);
    }

    @Override
    public RecipeType getType() {
        return RecipeType.SHAPED;
    }

    @Override
    public List<Item> getExtraResults() {
        return extraResults;
    }

    @Override
    public List<Item> getAllResults() {
        List<Item> list = new ArrayList<>();
        list.add(primaryResult);
        list.addAll(extraResults);

        return list;
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

    //todo 对旧类型配方仍然能匹配 但是带有item_tag的不行，等待后续实现
    @Override
    public boolean matchItems(List<Item> inputList, List<Item> extraOutputList, int multiplier) {
        List<Item> haveInputs = new ArrayList<>();
        for (Item item : inputList) {
            if (item.isNull())
                continue;
            haveInputs.add(item.clone());
        }
        List<Item> needInputs = new ArrayList<>();
        if (multiplier != 1) {
            for (Item item : ingredientsAggregate) {
                if (item.isNull())
                    continue;
                Item itemClone = item.clone();
                itemClone.setCount(itemClone.getCount() * multiplier);
                needInputs.add(itemClone);
            }
        } else {
            for (Item item : ingredientsAggregate) {
                if (item.isNull())
                    continue;
                needInputs.add(item.clone());
            }
        }

        if (!matchItemList(haveInputs, needInputs)) {
            return false;
        }

        List<Item> haveOutputs = new ArrayList<>();
        for (Item item : extraOutputList) {
            if (item.isNull())
                continue;
            haveOutputs.add(item.clone());
        }
        haveOutputs.sort(CraftingManager.recipeComparator);
        List<Item> needOutputs = new ArrayList<>();
        if (multiplier != 1) {
            for (Item item : getExtraResults()) {
                if (item.isNull())
                    continue;
                Item itemClone = item.clone();
                itemClone.setCount(itemClone.getCount() * multiplier);
                needOutputs.add(itemClone);
            }
        } else {
            for (Item item : getExtraResults()) {
                if (item.isNull())
                    continue;
                needOutputs.add(item.clone());
            }
        }
        needOutputs.sort(CraftingManager.recipeComparator);

        return matchItemList(haveOutputs, needOutputs);
    }

    /**
     * Returns whether the specified list of crafting grid inputs and outputs matches this recipe. Outputs DO NOT
     * include the primary result item.
     *
     * @param inputList       list of items taken from the crafting grid
     * @param extraOutputList list of items put back into the crafting grid (secondary results)
     * @return bool
     */
    @Override
    public boolean matchItems(List<Item> inputList, List<Item> extraOutputList) {
        return matchItems(inputList, extraOutputList, 1);
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(", ");
        newIngredients.forEach((character, itemDescriptor) -> {
            switch (itemDescriptor.getType()) {
                case DEFAULT -> {
                    var item = itemDescriptor.toItem();
                    joiner.add(item.getName() + ":" + item.getDamage());
                }
                case ITEM_TAG -> joiner.add(((ItemTagDescriptor) itemDescriptor).getItemTag());
                default -> {
                }
            }
        });
        return joiner.toString();
    }

    @Override
    public boolean requiresCraftingTable() {
        return this.getHeight() > 2 || this.getWidth() > 2;
    }

    @Override
    public List<Item> getIngredientsAggregate() {
        return ingredientsAggregate;
    }

    public static class Entry {
        public final int x;
        public final int y;

        public Entry(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
