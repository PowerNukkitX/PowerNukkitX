package cn.nukkit.inventory.request;

import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.response.ItemStackResponseContainerInfo;

import java.util.List;

public record ActionResponse(boolean ok, List<ItemStackResponseContainerInfo> containers) {
}
