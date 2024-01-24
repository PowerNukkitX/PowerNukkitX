package cn.nukkit.network.protocol.types.itemstack.request.action;

import lombok.Value;

@Value
public class CraftGrindstoneAction implements ItemStackRequestAction {
    int recipeNetworkId;
    int repairCost;

    @Override
    public ItemStackRequestActionType getType() {
        return ItemStackRequestActionType.CRAFT_REPAIR_AND_DISENCHANT;
    }
}