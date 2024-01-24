package cn.nukkit.network.protocol.types.itemstack.request.action;

import lombok.Value;

/**
 * Called when renaming an item in an anvil or cartography table. Uses the filter strings present in the request.
 */
@Value
public class CraftRecipeOptionalAction implements ItemStackRequestAction {
    /**
     * For the cartography table, if a certain MULTI recipe is being called, this points to the network ID that was assigned.
     */
    int recipeNetworkId;
    /**
     * Most likely the index in the request's filter strings that this action is using
     */
    int filteredStringIndex;

    @Override
    public ItemStackRequestActionType getType() {
        return ItemStackRequestActionType.CRAFT_RECIPE_OPTIONAL;
    }
}
