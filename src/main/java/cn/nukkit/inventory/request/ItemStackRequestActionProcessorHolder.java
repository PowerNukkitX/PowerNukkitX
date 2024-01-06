package cn.nukkit.inventory.request;

import cn.nukkit.network.protocol.types.itemstack.request.action.ItemStackRequestActionType;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Map;

public interface ItemStackRequestActionProcessorHolder {
    @Nullable <R extends ItemStackRequestActionProcessor<?>> R getProcessor(ItemStackRequestActionType type);

    void registerProcessor(ItemStackRequestActionProcessor<?> processor);

    @UnmodifiableView
    Map<ItemStackRequestActionType, ItemStackRequestActionProcessor<?>> getProcessors();

    static void registerProcessors(ItemStackRequestActionProcessorHolder holder) {
    }
}
