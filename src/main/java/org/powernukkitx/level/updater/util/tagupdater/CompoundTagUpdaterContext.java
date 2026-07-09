package org.powernukkitx.level.updater.util.tagupdater;

import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CompoundTagUpdaterContext {

    private final List<CompoundTagUpdater> updaters = new ArrayList<>();

    private static int mergeVersions(int baseVersion, int updaterVersion) {
        return updaterVersion | baseVersion;
    }

    private static int baseVersion(int version) {
        return version & 0xFFFFFF00;
    }

    public static int updaterVersion(int version) {
        return version & 0x000000FF;
    }

    public static int makeVersion(int major, int minor, int patch) {
        return (major << 24) | //major
                (minor << 16) | //minor
                (patch << 8); //patch
    }

    public CompoundTagUpdater.Builder addUpdater(int major, int minor, int patch) {
        return this.addUpdater(major, minor, patch, false);
    }

    public CompoundTagUpdater.Builder addUpdater(int major, int minor, int patch, boolean resetVersion) {
        return this.addUpdater(major, minor, patch, resetVersion, true);
    }

    public CompoundTagUpdater.Builder addUpdater(int major, int minor, int patch, boolean resetVersion, boolean bumpVersion) {
        int version = makeVersion(major, minor, patch);
        CompoundTagUpdater prevUpdater = this.getLatestUpdater();

        int updaterVersion;
        if (resetVersion || prevUpdater == null || baseVersion(prevUpdater.getVersion()) != version) {
            updaterVersion = 0;
        } else {
            updaterVersion = updaterVersion(prevUpdater.getVersion());
            if (bumpVersion) {
                updaterVersion++;
            }
        }
        version = mergeVersions(version, updaterVersion);

        CompoundTagUpdater updater = new CompoundTagUpdater(version);
        this.updaters.add(updater);
        this.updaters.sort(null);
        return updater.builder();
    }

    public NbtMap update(NbtMap tag, int version) {
        Map<String, Object> updated = this.updateStates0(tag, version);

        if (updated == null && version != this.getLatestVersion()) {
            tag = tag.toBuilder().putInt("version", this.getLatestVersion()).build();
            return tag;
        } else if (updated == null) {
            return tag;
        } else {
            updated.put("version", this.getLatestVersion());
            return toNbt(updated);
        }
    }

    public NbtMap updateStates(NbtMap tag, int version) {
        Map<String, Object> updated = this.updateStates0(tag, version);
        return updated == null ? tag : toNbt(updated);
    }

    private Map<String, Object> updateStates0(NbtMap tag, int version) {
        Map<String, Object> mutableTag = null;
        boolean updated = false;
        for (CompoundTagUpdater updater : updaters) {
            if (updater.getVersion() < version) {
                continue;
            }

            if (mutableTag == null) {
                mutableTag = toMutable(tag);
            }
            updated |= updater.update(mutableTag);
        }

        if (mutableTag == null || !updated) {
            return null;
        }
        return mutableTag;
    }

    private static Map<String, Object> toMutable(NbtMap tag) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : tag.entrySet()) {
            result.put(entry.getKey(), toMutableValue(entry.getValue()));
        }
        return result;
    }

    private static Object toMutableValue(Object value) {
        if (value instanceof NbtMap nbtMap) {
            return toMutable(nbtMap);
        } else if (value instanceof List<?> list) {
            List<Object> result = new ArrayList<>(list.size());
            for (Object element : list) {
                result.add(toMutableValue(element));
            }
            return result;
        }
        return value;
    }

    private static NbtMap toNbt(Map<String, Object> map) {
        NbtMapBuilder builder = NbtMap.builder();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            builder.put(entry.getKey(), toNbtValue(entry.getValue()));
        }
        return builder.build();
    }

    @SuppressWarnings("unchecked")
    private static Object toNbtValue(Object value) {
        if (value instanceof Map) {
            return toNbt((Map<String, Object>) value);
        } else if (value instanceof List<?> list) {
            List<Object> result = new ArrayList<>(list.size());
            for (Object element : list) {
                result.add(toNbtValue(element));
            }
            return result;
        }
        return value;
    }

    private CompoundTagUpdater getLatestUpdater() {
        return this.updaters.isEmpty() ? null : this.updaters.get(this.updaters.size() - 1);
    }

    public int getLatestVersion() {
        CompoundTagUpdater updater = this.getLatestUpdater();
        return updater == null ? 0 : updater.getVersion();
    }
}
