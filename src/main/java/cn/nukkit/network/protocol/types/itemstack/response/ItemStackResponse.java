package cn.nukkit.network.protocol.types.itemstack.response;

import cn.nukkit.network.protocol.ItemStackResponsePacket;
import cn.nukkit.network.protocol.types.itemstack.request.ItemStackRequest;
import lombok.Data;

import java.util.List;

/**
 * Represents an individual response to a {@link ItemStackRequest}
 * sent as part of {@link ItemStackResponsePacket}.
 */
@Data
public class ItemStackResponse {

    /**
     * Replaces the success boolean as of v419
     */
    ItemStackResponseStatus result;

    /**
     * requestId is the unique ID of the request that this response is in reaction to. If rejected, the client
     * will undo the actions from the request with this ID.
     */
    int requestId;

    /**
     * containers holds information on the containers that had their contents changed as a result of the
     * request.
     */
    List<ItemStackResponseContainer> containers;

    public ItemStackResponse(ItemStackResponseStatus result, int requestId, List<ItemStackResponseContainer> containers) {
        this.result = result;
        this.requestId = requestId;
        this.containers = containers;
    }
}
