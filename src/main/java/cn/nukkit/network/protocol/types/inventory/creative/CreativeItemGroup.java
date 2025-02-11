package cn.nukkit.network.protocol.types.inventory.creative;

import cn.nukkit.item.Item;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
public class CreativeItemGroup {
    private final CreativeItemCategory category;
    private final String name;
    private final Item icon;
}
