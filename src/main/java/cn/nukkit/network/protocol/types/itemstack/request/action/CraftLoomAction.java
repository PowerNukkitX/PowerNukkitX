package cn.nukkit.network.protocol.types.itemstack.request.action;

import lombok.Value;

@Value
public class CraftLoomAction implements ItemStackRequestAction {
    String patternId;

    @Override
    public ItemStackRequestActionType getType() {
        return ItemStackRequestActionType.CRAFT_LOOM;
    }
}