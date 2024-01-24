/*
 * https://PowerNukkit.org - The Nukkit you know but Powerful!
 * Copyright (C) 2020  José Roberto de Araújo Júnior
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package cn.nukkit.recipe;

import cn.nukkit.item.Item;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static cn.nukkit.recipe.Recipe.matchItemList;

/**
 * @author joserobjr
 * @since 2020-09-28
 */
@ToString
public class SmithingRecipe implements Recipe {
    private final String recipeId;
    private final ItemDescriptor base;//被锻造的物品
    private final ItemDescriptor addition; //锻造所用的材料
    private final ItemDescriptor template;//锻造模板
    private final Item result; //输出结果

    private final List<Item> ingredientsAggregate;
    private final List<String> needTags;

    public SmithingRecipe(String recipeId, Item result, ItemDescriptor base, ItemDescriptor addition, ItemDescriptor template) {
        this.recipeId = recipeId;
        this.needTags = new ArrayList<>();
        this.base = base;
        this.addition = addition;
        this.template = template;
        this.result = result;
        this.ingredientsAggregate = new ArrayList<>();
        for (ItemDescriptor itemDescriptor : List.of(base, addition, template)) {
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
                case ITEM_TAG -> this.needTags.add(((ItemTagDescriptor) itemDescriptor).getItemTag());
                default -> {
                }
            }
        }
    }

    @Override
    public String getRecipeId() {
        return recipeId;
    }

    @Override
    public Item getResult() {
        return result;
    }

    public Item getFinalResult(Item equip) {
        Item finalResult = getResult().clone();

        if (equip.hasCompoundTag()) {
            finalResult.setCompoundTag(equip.getCompoundTag());
        }

        int maxDurability = finalResult.getMaxDurability();
        if (maxDurability <= 0 || equip.getMaxDurability() <= 0) {
            return finalResult;
        }

        int damage = equip.getDamage();
        if (damage <= 0) {
            return finalResult;
        }

        finalResult.setDamage(Math.min(maxDurability, damage));
        return finalResult;
    }

    @Override
    public void registerToCraftingManager(CraftingManager manager) {
        manager.registerSmithingRecipe(this);
    }

    @Override
    public RecipeType getType() {
        return RecipeType.SMITHING_TRANSFORM;
    }

    public ItemDescriptor getTemplate() {
        return template;
    }

    public ItemDescriptor getEquipment() {
        return base;
    }

    public ItemDescriptor getIngredient() {
        return addition;
    }

    public List<Item> getIngredientsAggregate() {
        return ingredientsAggregate;
    }

    public List<String> getNeedTags() {
        return needTags;
    }

    public boolean matchItems(List<Item> inputList) {
        return matchItems(inputList, 1);
    }

    public boolean matchItems(List<Item> inputList, int multiplier) {
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

        return matchItemList(haveInputs, needInputs);
    }
}
