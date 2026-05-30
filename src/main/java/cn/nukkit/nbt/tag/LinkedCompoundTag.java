package cn.nukkit.nbt.tag;

import java.util.LinkedHashMap;
import java.util.Map;


public class LinkedCompoundTag extends CompoundTag {
    public LinkedCompoundTag() {
        this(new LinkedHashMap<>());
    }


    public LinkedCompoundTag(Map<String, Tag> tags) {
        super( tags);
    }

    @Override
    public Map<String, Tag> getTags() {
        return new LinkedHashMap<>(this.tags);
    }

    @Override
    public Map<String, Object> parseValue() {
        Map<String, Object> value = new LinkedHashMap<>(this.tags.size());

        for (Map.Entry<String, Tag> entry : this.tags.entrySet()) {
            value.put(entry.getKey(), entry.getValue().parseValue());
        }

        return value;
    }

    @Override
    public LinkedCompoundTag copy() {
        var nbt = new LinkedCompoundTag();
        this.getTags().forEach((key, value) -> nbt.put(key, value.copy()));
        return nbt;
    }
}
