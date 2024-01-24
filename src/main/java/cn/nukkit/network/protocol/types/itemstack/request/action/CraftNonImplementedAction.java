package cn.nukkit.network.protocol.types.itemstack.request.action;

import lombok.Value;

/**
 * CraftNonImplementedStackRequestActionData is an action sent for inventory actions that aren't yet implemented
 * in the new system. These include, for example, anvils
 */
@Value
public class CraftNonImplementedAction implements ItemStackRequestAction {

    @Override
    public ItemStackRequestActionType getType() {
        return ItemStackRequestActionType.CRAFT_NON_IMPLEMENTED_DEPRECATED;
    }
}
