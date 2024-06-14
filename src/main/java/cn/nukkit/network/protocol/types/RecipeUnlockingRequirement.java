package cn.nukkit.network.protocol.types;


import cn.nukkit.recipe.descriptor.ItemDescriptor;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Value;

import java.util.List;

@Value
public class RecipeUnlockingRequirement {
    public static final RecipeUnlockingRequirement INVALID = new RecipeUnlockingRequirement(UnlockingContext.NONE);

    UnlockingContext context;
    List<ItemDescriptor> ingredients = new ObjectArrayList<>();

    public enum UnlockingContext {
        NONE,
        ALWAYS_UNLOCKED,
        PLAYER_IN_WATER,
        PLAYER_HAS_MANY_ITEMS;

        private static final UnlockingContext[] VALUES = values();

        public static UnlockingContext from(int id) {
            return VALUES[id];
        }
    }

    public boolean isInvalid() {
        return this.ingredients.isEmpty() && this.context.equals(UnlockingContext.NONE);
    }
}