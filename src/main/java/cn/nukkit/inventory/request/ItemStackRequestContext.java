package cn.nukkit.inventory.request;

import lombok.Getter;
import lombok.Setter;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.ItemStackRequest;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.response.ItemStackResponseContainerInfo;

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

    public boolean has(String key) {
        return extraData.containsKey(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) extraData.get(key);
    }

    public ActionResponse error() {
        return new ActionResponse(false, List.of());
    }

    public ActionResponse success(List<ItemStackResponseContainerInfo> containers) {
        return new ActionResponse(true, containers);
    }
}
