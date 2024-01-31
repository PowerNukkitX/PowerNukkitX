package cn.nukkit.inventory.request;

import cn.nukkit.network.protocol.types.itemstack.response.ItemStackResponseContainer;

import java.util.List;

public record ActionResponse(boolean ok, List<ItemStackResponseContainer> containers) {
}
