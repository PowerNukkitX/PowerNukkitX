package cn.nukkit.block.customblock.data;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;


public record CraftingTable(@NotNull String tableName, @Nullable List<String> craftingTags) implements NBTData {
    public NbtMap toCompoundTag() {
        final List<String> craftingTags = new ObjectArrayList<>();
        if (this.craftingTags != null) {
            craftingTags.addAll(this.craftingTags);
        }
        return NbtMap.builder()
                .putList("crafting_tags", NbtType.STRING, craftingTags)
                .putString("table_name", tableName)
                .build();
    }
}
