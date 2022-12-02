package cn.nukkit.inventory;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.inventory.recipe.DefaultDescriptor;
import cn.nukkit.inventory.recipe.ItemDescriptor;
import cn.nukkit.inventory.recipe.ItemDescriptorType;
import cn.nukkit.inventory.recipe.ItemTagDescriptor;
import cn.nukkit.item.Item;

import java.util.*;

import static cn.nukkit.inventory.Recipe.matchItemList;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ShapelessRecipe implements CraftingRecipe {

    private String recipeId;

    private final Item output;

    private long least, most;

    @Deprecated
    @DeprecationDetails(since = "1.19.50-r2", reason = "new ingredients format", replaceWith = "newIngredients")
    private final List<Item> ingredients = null;
    private final List<Item> ingredientsAggregate;

    @PowerNukkitXOnly
    @Since("1.19.50-r2")
    private final List<String> needTags;

    @PowerNukkitXOnly
    @Since("1.19.50-r2")
    private final List<ItemDescriptor> newIngredients;

    private final int priority;

    public ShapelessRecipe(Item result, Collection<Item> ingredients) {
        this(null, 10, result, ingredients);
    }

    public ShapelessRecipe(String recipeId, int priority, Item result, Collection<Item> ingredients) {
        this(recipeId, priority, result, ingredients.stream().map(item -> (ItemDescriptor) new DefaultDescriptor(item)).toList());
    }

    @PowerNukkitXOnly
    @Since("1.19.50-r2")
    public ShapelessRecipe(String recipeId, int priority, Item result, List<ItemDescriptor> ingredients) {
        this.recipeId = recipeId;
        this.priority = priority;
        this.output = result.clone();
        if (ingredients.size() > 9) {
            throw new IllegalArgumentException("Shapeless recipes cannot have more than 9 ingredients");
        }

        this.ingredientsAggregate = new ArrayList<>();
        this.newIngredients = new ArrayList<>();
        this.needTags = new ArrayList<>();
        for (ItemDescriptor itemDescriptor : ingredients) {
            newIngredients.add(itemDescriptor);
            switch (itemDescriptor.getType()) {
                case DEFAULT -> {
                    var item = itemDescriptor.toItem();
                    if (item.getCount() < 1) {
                        throw new IllegalArgumentException("Recipe '" + recipeId + "' Ingredient amount was not 1 (value: " + item.getCount() + ")");
                    }
                    boolean found = false;
                    for (Item existingIngredient : this.ingredientsAggregate) {
                        if (existingIngredient.equals(item, item.hasMeta(), item.hasCompoundTag())) {
                            existingIngredient.setCount(existingIngredient.getCount() + item.getCount());
                            found = true;
                            break;
                        }
                    }
                    if (!found)
                        this.ingredientsAggregate.add(item.clone());
                    this.ingredientsAggregate.sort(CraftingManager.recipeComparator);
                }
                case ITEM_TAG -> {
                    this.needTags.add(((ItemTagDescriptor) itemDescriptor).getItemTag());
                }
                default -> {
                }
            }
        }
    }

    @Override
    public Item getResult() {
        return this.output.clone();
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
            this.recipeId = this.getId().toString();
        }
    }

    public List<Item> getIngredientList() {
        return this.newIngredients
                .stream()
                .filter(itemDescriptor -> itemDescriptor.getType().equals(ItemDescriptorType.DEFAULT))
                .map(ItemDescriptor::toItem)
                .toList();
    }

    public int getIngredientCount() {
        return this.newIngredients.size();
    }

    @Override
    public void registerToCraftingManager(CraftingManager manager) {
        manager.registerShapelessRecipe(this);
    }

    @Override
    public RecipeType getType() {
        return RecipeType.SHAPELESS;
    }

    @Override
    public boolean requiresCraftingTable() {
        return this.newIngredients.size() > 4;
    }

    @Override
    public List<Item> getExtraResults() {
        return new ArrayList<>();
    }

    @Override
    public List<Item> getAllResults() {
        return null;
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

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
            Set<String> tags = new HashSet<>();
            for (var hInput : haveInputs) {
                var t = ItemTag.getTags(hInput.getNamespaceId());
                if (t != null) tags.addAll(t);
            }
            return tags.containsAll(needTags);
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
    public List<Item> getIngredientsAggregate() {
        return ingredientsAggregate;
    }

    @PowerNukkitXOnly
    @Since("1.19.50-r2")
    public List<ItemDescriptor> getNewIngredients() {
        return newIngredients;
    }
}
