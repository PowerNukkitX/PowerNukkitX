package cn.nukkit.block.customblock.data;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import java.util.List;
import javax.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

@PowerNukkitXOnly
@Since("1.19.60-r1")
public record CraftingTable(@NotNull String tableName, @Nullable List<String> craftingTags) implements NBTData {
    public CompoundTag toCompoundTag() {
        var listTag = new ListTag<StringTag>("crafting_tags");
        if (craftingTags != null) {
            craftingTags.forEach(t -> listTag.add(new StringTag(t)));
        }
        return new CompoundTag("minecraft:crafting_table").putList(listTag).putString("table_name", tableName);
    }
}
