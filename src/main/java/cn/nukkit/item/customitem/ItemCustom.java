package cn.nukkit.item.customitem;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import lombok.Getter;
import lombok.Setter;

/**
 * @author lt_name
 */
@PowerNukkitOnly
@Since("1.6.0.0-PNX")
public abstract class ItemCustom extends Item {

    @Setter
    @Getter
    private String textureName;

    public ItemCustom(int id) {
        this(id, 0, 1, UNKNOWN_STR);
    }

    public ItemCustom(int id, Integer meta) {
        this(id, meta, 1, UNKNOWN_STR);
    }

    public ItemCustom(int id, Integer meta, int count) {
        this(id, meta, count, UNKNOWN_STR);
    }

    public ItemCustom(int id, Integer meta, int count, String name) {
        this(id, meta, count, name, name);
    }

    public ItemCustom(int id, Integer meta, int count, String name, String textureName) {
        super(id, meta, count, name);
        this.textureName = textureName;
    }

    public boolean allowOffHand() {
        return false;
    }

    public int getCreativeCategory() {
        return 4;
    }

    public CompoundTag getComponentsData() {
        CompoundTag data = new CompoundTag();
        data.putCompound("components", new CompoundTag()
                .putCompound("item_properties", new CompoundTag()
                        .putBoolean("allow_off_hand", this.allowOffHand())
                        .putBoolean("hand_equipped", this.isTool())
                        .putInt("creative_category", this.getCreativeCategory())
                        .putInt("max_stack_size", this.getMaxStackSize())
                        .putCompound("minecraft:icon", new CompoundTag()
                                .putString("texture", this.getTextureName() != null ? this.getTextureName() : this.name))));
        return data;
    }

}
