package org.powernukkitx.inventory.request;

import org.powernukkitx.Player;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.ConsumeAction;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.DestroyAction;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.ItemStackRequestAction;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public final class ConsumeActionHelper {

    private ConsumeActionHelper() {
    }

    public static List<ConsumeAction> findAllConsumeActions(ItemStackRequestAction[] actions, int startIndex) {
        var found = new ArrayList<ConsumeAction>();
        for (int i = startIndex; i < actions.length; i++) {
            var action = actions[i];
            if (action instanceof ConsumeAction consumeAction) {
                found.add(consumeAction);
            }
        }
        return found;
    }

    public static List<DestroyAction> findAllDestroyActions(ItemStackRequestAction[] actions, int startIndex) {
        var found = new ArrayList<DestroyAction>();
        for (int i = startIndex; i < actions.length; i++) {
            var action = actions[i];
            if (action instanceof DestroyAction destroyAction) {
                found.add(destroyAction);
            }
        }
        return found;
    }

    public static ActionResponse validateConsumes(ItemStackRequestContext context, Player player, String station, int expectedConsumes) {
        int actualConsumes = findAllConsumeActions(context.getItemStackRequest().getActions(), context.getCurrentActionIndex() + 1).size();
        if (actualConsumes < expectedConsumes) {
            log.warn("{}: {} result taken without consuming inputs (expected {} consume actions, got {})", player.getName(), station, expectedConsumes, actualConsumes);
            return context.error();
        }
        return null;
    }
}
