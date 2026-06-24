package cn.nukkit.utils;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public record SemVersion(int major, int minor, int patch, int revision, int build) {
    private static final SemVersion EMPTY = new SemVersion(0, 0, 0, 0, 0);
    @NotNull
    public static SemVersion from(List<Integer> versions) {
        if (versions.isEmpty()) {
            return EMPTY;
        }
        Preconditions.checkArgument(versions.size() == 5);

        return new SemVersion(versions.get(0), versions.get(1), versions.get(2), versions.get(3), versions.get(4));
    }

    public static SemVersion fromString(String s) {
        if(!s.contains(".")){
            return EMPTY;
        }
        final String[] array = s.split("\\.");
        return new SemVersion(
                Integer.parseInt(array[0]),
                array.length >= 2 ? Integer.parseInt(array[1]) : 0,
                array.length >= 3 ? Integer.parseInt(array[2]) : 0,
                array.length >= 4 ? Integer.parseInt(array[3]) : 0,
                array.length >= 5 ? Integer.parseInt(array[4]) : 0
        );
    }

    public List<Integer> toTag() {
        return Arrays.asList(major, minor, patch, revision, build);
    }
}
