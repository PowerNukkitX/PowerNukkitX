package cn.nukkit.inventory.recipe;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;

import java.util.Objects;

@PowerNukkitXOnly
@Since("Future")
public class DeferredDescriptor implements ItemDescriptor {
    private final String fullName;
    private final int auxValue;
    private final int count;

    public DeferredDescriptor(String fullName, int auxValue, int count) {
        this.fullName = fullName;
        this.auxValue = auxValue;
        this.count = count;
    }

    @Override
    public ItemDescriptorType getType() {
        return ItemDescriptorType.DEFERRED;
    }

    @Override
    public Item toItem() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ItemDescriptor clone() throws CloneNotSupportedException {
        return (ItemDescriptor) super.clone();
    }

    public String getFullName() {
        return this.fullName;
    }

    public int getAuxValue() {
        return this.auxValue;
    }

    public int getCount() {
        return this.count;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof final DeferredDescriptor other)) return false;
        final Object this$fullName = this.getFullName();
        final Object other$fullName = other.getFullName();
        if (!Objects.equals(this$fullName, other$fullName)) return false;
        if (this.getAuxValue() != other.getAuxValue()) return false;
        return this.getCount() == other.getCount();
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $fullName = this.getFullName();
        result = result * PRIME + ($fullName == null ? 43 : $fullName.hashCode());
        result = result * PRIME + this.getAuxValue();
        result = result * PRIME + this.getCount();
        return result;
    }

    public String toString() {
        return "DeferredDescriptor(fullName=" + this.getFullName() + ", auxValue=" + this.getAuxValue() + ", count=" + this.getCount() + ")";
    }
}
