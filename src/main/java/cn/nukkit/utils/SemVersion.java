package cn.nukkit.utils;

import cn.nukkit.nbt.tag.IntTag;
import cn.nukkit.nbt.tag.ListTag;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

public record SemVersion(int major, int minor, int patch, int revision, int build) {
    @NotNull
    public static SemVersion from(ListTag<IntTag> versions) {
        if (versions.size() == 0) {
            return new SemVersion(0, 0, 0, 0, 0);
        }
        Preconditions.checkArgument(versions.size() == 5);

        return new SemVersion(versions.get(0).getData(), versions.get(1).getData(), versions.get(2).getData(), versions.get(3).getData(), versions.get(4).getData());
    }

    public ListTag<IntTag> toTag() {
        ListTag<IntTag> tag = new ListTag<>();
        tag.add(new IntTag(major));
        tag.add(new IntTag(minor));
        tag.add(new IntTag(patch));
        tag.add(new IntTag(revision));
        tag.add(new IntTag(build));
        return tag;
    }
}
