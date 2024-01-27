package cn.nukkit.inventory.request;

import lombok.extern.slf4j.Slf4j;
import org.allaymc.api.entity.interfaces.EntityPlayer;
import org.allaymc.api.item.recipe.Recipe;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.CreateAction;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.ItemStackRequestAction;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.ItemStackRequestActionType;

import java.util.Map;

import static org.allaymc.api.container.FullContainerType.CREATED_OUTPUT;
import static org.allaymc.server.container.processor.CraftRecipeActionProcessor.RECIPE_DATA_KEY;

/**
 * Allay Project 2023/12/2
 *
 * @author daoge_cmd
 */
@Slf4j
public class CreateActionProcessor implements ContainerActionProcessor<CreateAction> {
    @Override
    public ItemStackRequestActionType getType() {
        return ItemStackRequestActionType.CREATE;
    }

    @Override
    public ActionResponse handle(CreateAction action, EntityPlayer player, int currentActionIndex, ItemStackRequestAction[] actions, Map<Object, Object> dataPool) {
        Recipe recipe = (Recipe) dataPool.get(RECIPE_DATA_KEY);
        if (recipe == null) {
            log.warn("Recipe not found in data pool!");
            return error();
        }
        var output = recipe.getOutputs()[action.getSlot()];
        var createdOutput = player.getContainer(CREATED_OUTPUT);
        createdOutput.setItemStack(0, output);
        return null;
    }
}
