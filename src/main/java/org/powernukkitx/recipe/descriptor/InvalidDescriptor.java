package org.powernukkitx.recipe.descriptor;

import org.cloudburstmc.protocol.bedrock.data.inventory.descriptor.EmptyDescriptor;
import org.cloudburstmc.protocol.bedrock.data.inventory.descriptor.RecipeIngredient;
import org.powernukkitx.item.Item;


public class InvalidDescriptor implements ItemDescriptor {
    int count = 0;

    public static final InvalidDescriptor INSTANCE = new InvalidDescriptor();

    private InvalidDescriptor() {
    }

    @Override
    public ItemDescriptorType getType() {
        return ItemDescriptorType.INVALID;
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
        return count;
    }

    @Override
    public RecipeIngredient toNetwork() {
        return new RecipeIngredient(
                EmptyDescriptor.INSTANCE,
                this.count
        );
    }

    @Override
    public boolean match(Item item) {
        return item.equals(Item.AIR);
    }

    public String toString() {
        return "InvalidDescriptor(count=" + this.getCount() + ")";
    }
}
