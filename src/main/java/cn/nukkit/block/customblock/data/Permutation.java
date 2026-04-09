package cn.nukkit.block.customblock.data;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;
import org.cloudburstmc.nbt.NbtType;

import java.util.Arrays;
import java.util.List;

/**
 * The type Permutation builder.
 */


public record Permutation(Component component, String condition, String[] blockTags) implements NBTData {
    public Permutation(Component component, String condition) {
        this(component, condition, new String[]{});
    }

    @Override
    public NbtMap toCompoundTag() {
        NbtMapBuilder result = NbtMap.builder()
                .putCompound("components", component.toCompoundTag())
                .putString("condition", condition);
        List<String> stringTagListTag = new ObjectArrayList<>();
        stringTagListTag.addAll(Arrays.asList(blockTags));
        if (!stringTagListTag.isEmpty()) {
            result.putList("blockTags", NbtType.STRING, stringTagListTag);
        }
        return result.build();
    }
}
