package org.powernukkitx.inventory.request;

import lombok.Getter;
import lombok.Setter;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerEnumName;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.ItemStackRequest;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.response.ItemStackResponseContainerInfo;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ItemStackRequestContext {
    @Getter
    @Setter
    private int currentActionIndex;
    @Getter
    private final ItemStackRequest itemStackRequest;
    private final Map<String, Object> extraData;
    private final Set<ContainerEnumName> serverConsumedSlots;

    public ItemStackRequestContext(ItemStackRequest request) {
        this.itemStackRequest = request;
        this.extraData = new HashMap<>();
        this.serverConsumedSlots = EnumSet.noneOf(ContainerEnumName.class);
    }

    /**
     * Declares that this request already emptied the given slot types server side. The consume actions
     * the client sends afterwards only have to be acknowledged for them, never applied a second time.
     */
    public void markServerConsumed(ContainerEnumName... slotTypes) {
        Collections.addAll(serverConsumedSlots, slotTypes);
    }

    public boolean isServerConsumed(ContainerEnumName slotType) {
        return serverConsumedSlots.contains(slotType);
    }

    public boolean hasServerConsumed() {
        return !serverConsumedSlots.isEmpty();
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
