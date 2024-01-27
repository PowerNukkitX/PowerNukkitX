package cn.nukkit.recipe.descriptor;

import cn.nukkit.tags.ItemTags;
import cn.nukkit.item.Item;

import java.util.Objects;


public class ItemTagDescriptor implements ItemDescriptor {
    private final String itemTag;
    private final int count;

    public ItemTagDescriptor(String itemTag, int count) {
        this.itemTag = itemTag;
        this.count = count;
    }

    @Override
    public ItemDescriptorType getType() {
        return ItemDescriptorType.ITEM_TAG;
    }

    @Override
    public Item toItem() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean match(Item item) {
        return item.getCount() >= count && ItemTags.getTagSet(item.getId()).contains(itemTag);
    }

    @Override
    public ItemDescriptor clone() throws CloneNotSupportedException {
        return (ItemDescriptor) super.clone();
    }

    public String getItemTag() {
        return this.itemTag;
    }

    public int getCount() {
        return this.count;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof final ItemTagDescriptor other)) return false;
        final Object this$itemTag = this.getItemTag();
        final Object other$itemTag = other.getItemTag();
        if (!Objects.equals(this$itemTag, other$itemTag)) return false;
        return this.getCount() == other.getCount();
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $itemTag = this.getItemTag();
        result = result * PRIME + ($itemTag == null ? 43 : $itemTag.hashCode());
        result = result * PRIME + this.getCount();
        return result;
    }

    public String toString() {
        return "ItemTagDescriptor(itemTag=" + this.getItemTag() + ", count=" + this.getCount() + ")";
    }
}
