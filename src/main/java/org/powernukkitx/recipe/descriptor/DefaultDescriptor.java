package org.powernukkitx.recipe.descriptor;

import org.cloudburstmc.protocol.bedrock.data.definitions.SimpleItemDefinition;
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemVersion;
import org.cloudburstmc.protocol.bedrock.data.inventory.descriptor.NameDescriptor;
import org.cloudburstmc.protocol.bedrock.data.inventory.descriptor.RecipeIngredient;
import org.powernukkitx.item.Item;
import org.powernukkitx.registry.Registries;


public class DefaultDescriptor implements ItemDescriptor {
    private final Item item;
    private final String networkIdentifier;
    private final int networkAuxValue;

    public DefaultDescriptor(Item item) {
        this(item, item.getId(), item.hasMeta() ? item.getDamage() : -1);
    }

    public DefaultDescriptor(Item item, String networkIdentifier, int networkAuxValue) {
        this.item = item;
        this.networkIdentifier = networkIdentifier;
        this.networkAuxValue = networkAuxValue;
    }

    @Override
    public ItemDescriptorType getType() {
        return ItemDescriptorType.DEFAULT;
    }

    @Override
    public Item toItem() {
        return item.clone();
    }

    @Override
    public ItemDescriptor clone() throws CloneNotSupportedException {
        return (ItemDescriptor) super.clone();
    }

    @Override
    public int getCount() {
        return this.item.getCount();
    }

    @Override
    public boolean match(Item item) {
        return this.item.equals(item, true, false);
    }

    @Override
    public RecipeIngredient toNetwork() {
        var itemDefinition = this.item.getItemDefinition();

        if (!this.networkIdentifier.equals(this.item.getId())) {
            int runtimeId = Registries.ITEM_RUNTIMEID.getInt(this.networkIdentifier);

            if (runtimeId != Integer.MAX_VALUE) {
                itemDefinition = new SimpleItemDefinition(
                        this.networkIdentifier,
                        runtimeId,
                        ItemVersion.NONE,
                        false,
                        null
                );
            }
        }

        final NameDescriptor descriptor = new NameDescriptor(
                itemDefinition,
                this.networkAuxValue
        );

        return new RecipeIngredient(descriptor, this.getCount());
    }

    @Override
    public int hashCode() {
        return item.hashCode();
    }

    public Item getItem() {
        return this.item;
    }

    public String toString() {
        return "DefaultDescriptor(item=" + this.getItem() + ")";
    }
}
