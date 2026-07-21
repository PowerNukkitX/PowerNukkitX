package org.powernukkitx.recipe.descriptor;

import lombok.Value;
import org.cloudburstmc.protocol.bedrock.data.inventory.descriptor.RecipeIngredient;
import org.powernukkitx.item.Item;

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

    @Override
    public RecipeIngredient toNetwork() {
        final org.cloudburstmc.protocol.bedrock.data.inventory.descriptor.ComplexAliasDescriptor descriptor =
            new org.cloudburstmc.protocol.bedrock.data.inventory.descriptor.ComplexAliasDescriptor(this.name);
        return new RecipeIngredient(descriptor, this.getCount());
    }
}
