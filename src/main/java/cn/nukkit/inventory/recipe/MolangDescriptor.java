package cn.nukkit.inventory.recipe;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;

import java.util.Objects;

@PowerNukkitXOnly
@Since("Future")
public class MolangDescriptor implements ItemDescriptor {
    private final String tagExpression;
    private final int molangVersion;
    private final int count;

    public MolangDescriptor(String tagExpression, int molangVersion, int count) {
        this.tagExpression = tagExpression;
        this.molangVersion = molangVersion;
        this.count = count;
    }

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

    public String getTagExpression() {
        return this.tagExpression;
    }

    public int getMolangVersion() {
        return this.molangVersion;
    }

    public int getCount() {
        return this.count;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof final MolangDescriptor other)) return false;
        final Object this$tagExpression = this.getTagExpression();
        final Object other$tagExpression = other.getTagExpression();
        if (!Objects.equals(this$tagExpression, other$tagExpression))
            return false;
        if (this.getMolangVersion() != other.getMolangVersion()) return false;
        return this.getCount() == other.getCount();
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $tagExpression = this.getTagExpression();
        result = result * PRIME + ($tagExpression == null ? 43 : $tagExpression.hashCode());
        result = result * PRIME + this.getMolangVersion();
        result = result * PRIME + this.getCount();
        return result;
    }

    public String toString() {
        return "MolangDescriptor(tagExpression=" + this.getTagExpression() + ", molangVersion=" + this.getMolangVersion() + ", count=" + this.getCount() + ")";
    }
}
