package cn.nukkit.nbt.tag;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

import java.util.LinkedHashMap;
import java.util.Map;

@PowerNukkitXOnly
@Since("1.19.20-r4")
public class LinkedCompoundTag extends CompoundTag {
    public LinkedCompoundTag() {
        this("");
    }

    public LinkedCompoundTag(String name) {
        this(name, new LinkedHashMap<>());
    }

    public LinkedCompoundTag(Map<String, Tag> tags) {
        super("", tags);
    }

    public LinkedCompoundTag(String name, Map<String, Tag> tags) {
        super(name, tags);
    }

    @Override
    public LinkedCompoundTag copy() {
        var nbt = new LinkedCompoundTag();
        this.getTags().forEach((key, value) -> nbt.put(key, value.copy()));
        return nbt;
    }
}
