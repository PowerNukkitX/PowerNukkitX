package cn.nukkit.inventory;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.energy.EnergyType;
import cn.nukkit.item.Item;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@PowerNukkitXOnly
@Since("1.19.50-r3")
public interface ModProcessRecipe extends Recipe {
    String getCategory();

    @NotNull
    List<Item> getIngredients();

    @NotNull
    List<Item> getExtraResults();

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

    @NotNull
    default List<Item> getAllResults() {
        var mainResult = getResult();
        var extraResults = getExtraResults().toArray(Item.EMPTY_ARRAY);
        var results = new Item[extraResults.length + (mainResult == null || mainResult.isNull() ? 0 : 1)];
        if (mainResult != null && !mainResult.isNull()) {
            results[0] = mainResult;
        }
        System.arraycopy(extraResults, 0, results, results.length - extraResults.length, extraResults.length);
        return List.of(results);
    }

    @Override
    default void registerToCraftingManager(CraftingManager manager) {
        manager.registerModProcessRecipe(this);
    }
}