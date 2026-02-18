package cn.nukkit.inventory.request;

import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.response.ItemStackResponseContainer;

import java.util.List;

public record ActionResponse(boolean ok, List<ItemStackResponseContainer> containers) {
}
