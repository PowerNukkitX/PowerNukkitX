package cn.nukkit.network.protocol.types.inventory.creative;

import cn.nukkit.item.Item;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class CreativeItemData {
    private final Item item;
    private final int groupId;
}
