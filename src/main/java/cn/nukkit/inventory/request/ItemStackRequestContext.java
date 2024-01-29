package cn.nukkit.inventory.request;

import cn.nukkit.network.protocol.types.itemstack.request.ItemStackRequest;
import cn.nukkit.network.protocol.types.itemstack.response.ItemStackResponse;
import cn.nukkit.network.protocol.types.itemstack.response.ItemStackResponseContainer;
import cn.nukkit.network.protocol.types.itemstack.response.ItemStackResponseStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemStackRequestContext {
    @Getter
    @Setter
    private int currentActionIndex;
    @Getter
    private final ItemStackRequest itemStackRequest;
    private final Map<String, Object> extraData;

    public ItemStackRequestContext(ItemStackRequest request) {
        this.itemStackRequest = request;
        this.extraData = new HashMap<>();
    }

    public void put(String key, Object value) {
        extraData.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) extraData.get(key);
    }

    public ItemStackResponse error() {
        return new ItemStackResponse(ItemStackResponseStatus.ERROR, itemStackRequest.getRequestId(), List.of());
    }

    public ItemStackResponse success(List<ItemStackResponseContainer> containers) {
        return new ItemStackResponse(ItemStackResponseStatus.OK, itemStackRequest.getRequestId(), containers);
    }
}
