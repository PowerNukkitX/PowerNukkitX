package cn.nukkit.level.updater.util.tagupdater;

import org.cloudburstmc.nbt.NbtMap;

import java.util.ArrayList;
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
            return NbtMap.fromMap(updated);
        }
    }

    public NbtMap updateStates(NbtMap tag, int version) {
        Map<String, Object> updated = this.updateStates0(tag, version);
        return updated == null ? tag : NbtMap.fromMap(updated);
    }

    private Map<String, Object> updateStates0(NbtMap tag, int version) {
        Map<String, Object> mutableTag = null;
        boolean updated = false;
        for (CompoundTagUpdater updater : updaters) {
            if (updater.getVersion() < version) {
                continue;
            }

            if (mutableTag == null) {
                mutableTag = tag;
            }
            updated |= updater.update(mutableTag);
        }

        if (mutableTag == null || !updated) {
            return null;
        }
        return mutableTag;
    }

    private CompoundTagUpdater getLatestUpdater() {
        return this.updaters.isEmpty() ? null : this.updaters.get(this.updaters.size() - 1);
    }

    public int getLatestVersion() {
        CompoundTagUpdater updater = this.getLatestUpdater();
        return updater == null ? 0 : updater.getVersion();
    }
}
