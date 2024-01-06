package cn.nukkit.network.protocol.types.itemstack.request.action;

/**
 * RecipeStackRequestActionData is the structure shared by StackRequestActions that contain the
 * network id of the recipe the client is about to craft
 */
public interface RecipeItemStackRequestAction extends ItemStackRequestAction {

    int getRecipeNetworkId();
}
