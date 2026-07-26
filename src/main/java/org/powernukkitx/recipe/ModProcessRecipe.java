package org.powernukkitx.recipe;

import org.powernukkitx.energy.EnergyType;
import org.powernukkitx.registry.RecipeRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public interface ModProcessRecipe extends Recipe {
    String getCategory();

    default @NotNull String getRecipeId() {
        return RecipeRegistry.computeRecipeId(getResults(), getIngredients(), getType());
    }

    @Nullable
    default EnergyType getEnergyType() {
        return null;
    }

    default double getEnergyCost() {
        return 0;
    }

    default boolean costEnergy() {
        return getEnergyType() != null && getEnergyCost() > 0;
    }

    @Override
    default RecipeType getType() {
        return RecipeType.MOD_PROCESS;
    }
}