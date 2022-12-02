package cn.nukkit.inventory.recipe;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import lombok.Value;

@PowerNukkitXOnly
@Since("Future")
@Value
public class MolangDescriptor implements ItemDescriptor {
    String tagExpression;
    int molangVersion;

    @Override
    public ItemDescriptorType getType() {
        return ItemDescriptorType.MOLANG;
    }

    @Override
    public Item toItem() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ItemDescriptor clone() throws CloneNotSupportedException {
        return (ItemDescriptor) super.clone();
    }
}
