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
import cn.nukkit.recipe.descriptor.ItemDescriptor;
import org.jetbrains.annotations.NotNull;

/**
 * The type Smithing recipe for upgrade equipment.
 *
 * @author joserobjr
 * @since 2020 -09-28
 */
public class SmithingTransformRecipe extends BaseRecipe {

    public SmithingTransformRecipe(@NotNull String recipeId, Item result, ItemDescriptor base, ItemDescriptor addition, ItemDescriptor template) {
        super(recipeId);
        this.ingredients.add(base);
        this.ingredients.add(addition);
        this.ingredients.add(template);
        this.results.add(result);
    }

    @Override
    public boolean match(Input input) {
        return false;
    }

    public Item getResult() {
        return results.getFirst();
    }

    @Override
    public RecipeType getType() {
        return RecipeType.SMITHING_TRANSFORM;
    }

    public ItemDescriptor getBase() {
        return ingredients.getFirst();
    }

    public ItemDescriptor getAddition() {
        return ingredients.get(1);
    }

    public ItemDescriptor getTemplate() {
        return ingredients.get(2);
    }
}
