package cn.nukkit.recipe.descriptor;

import cn.nukkit.item.Item;
import lombok.Value;

@Value
public class ComplexAliasDescriptor implements ItemDescriptor {
    String name;

    @Override
    public ItemDescriptorType getType() {
        return ItemDescriptorType.COMPLEX_ALIAS;
    }

    @Override
    public Item toItem() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ItemDescriptor clone() throws CloneNotSupportedException {
        return (ItemDescriptor) super.clone();
    }

    @Override
    public int getCount() {
        return 0;
    }
}
