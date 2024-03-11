package cn.nukkit.recipe.descriptor;

import cn.nukkit.item.Item;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class ItemDescriptorWithCount {

    public static final ItemDescriptorWithCount EMPTY = new ItemDescriptorWithCount(InvalidDescriptor.INSTANCE, 0);

    private final ItemDescriptor descriptor;
    private final int count;

    public Item toItem() {
        if (descriptor == InvalidDescriptor.INSTANCE) {
            return Item.AIR;
        }
        return descriptor.toItem();
    }

    public static ItemDescriptorWithCount fromItem(Item item) {
        if (item == Item.AIR) {
            return EMPTY;
        }
        return new ItemDescriptorWithCount(new DefaultDescriptor(item),item.getCount());
    }
}