package cn.nukkit.utils;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

public record SemVersion(int major, int minor, int patch, int revision, int build) {
    public @NotNull static SemVersion from(int[] versions) {
        if (versions.length == 0) {
            return new SemVersion(0, 0, 0, 0, 0);
        }
        Preconditions.checkArgument(versions.length == 5);
        return new SemVersion(versions[0], versions[1], versions[2], versions[3], versions[4]);
    }

    public int[] toArray() {
        return new int[]{major, minor, patch, revision, build};
    }
}
