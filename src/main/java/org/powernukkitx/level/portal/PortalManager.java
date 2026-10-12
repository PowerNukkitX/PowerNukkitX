package org.powernukkitx.level.portal;

import org.powernukkitx.block.BlockID;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.leveldb.LevelDBStorage;
import org.powernukkitx.math.BlockVector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.nbt.tag.Tag;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Stores generated portal regions by dimension. It registers portals, removes stale regions, finds nearby portal
 * anchors, and persists the portal index.
 *
 * @author Curse
 */
public final class PortalManager {
    private final LevelDBStorage storage;
    private CompoundTag root = new CompoundTag();
    private CompoundTag data = new CompoundTag();
    private final List<PortalRecord> records = new ArrayList<>();
    private boolean loaded;
    private boolean dirty;

    /**
     * Creates a new PortalManager instance.
     *
     * @param storage value for this API
     */
    public PortalManager(LevelDBStorage storage) {
        this.storage = storage;
    }

    private void ensureLoaded() {
        if (loaded) return;
        load();
        loaded = true;
    }

    private void load() {
        CompoundTag stored = storage.readPortalsData();
        if (stored == null) return;

        root = stored.copy();
        if (!root.containsCompound("data")) {
            throw new IllegalStateException("Invalid portals data: missing data compound");
        }

        data = root.getCompound("data").copy();
        if (!data.containsList("PortalRecords")) {
            throw new IllegalStateException("Invalid portals data: missing PortalRecords");
        }

        ListTag<CompoundTag> list = data.getList("PortalRecords", CompoundTag.class);
        if (list.type != Tag.TAG_Compound) {
            throw new IllegalStateException("Invalid portals data: PortalRecords is not List<Compound>");
        }

        for (int i = 0; i < list.size(); i++) {
            CompoundTag tag = list.get(i);
            if (!tag.containsInt("DimId")
                    || !tag.containsInt("TpX")
                    || !tag.containsInt("TpY")
                    || !tag.containsInt("TpZ")
                    || !tag.containsByte("Span")
                    || !tag.containsByte("Xa")
                    || !tag.containsByte("Za")) {
                throw new IllegalStateException("Invalid PortalRecord");
            }

            int span = tag.getByte("Span");
            int xa = tag.getByte("Xa");
            int za = tag.getByte("Za");
            validate(span, xa, za);
            records.add(new PortalRecord(tag.getInt("DimId"), tag.getInt("TpX"), tag.getInt("TpY"), tag.getInt("TpZ"), span, xa, za));
        }
    }

    /**
     * Registers a portal region.
     *
     * @param level value for this API
     * @param anchor value for this API
     * @param span value for this API
     * @param height value for this API
     * @param xa value for this API
     * @param za value for this API
     */
    public synchronized void registerPortal(Level level, BlockVector3 anchor, int span, int height, int xa, int za) {
        ensureLoaded();
        validate(span, xa, za);
        if (height < 3 || height > 21) {
            throw new IllegalArgumentException("Invalid Nether portal height " + height);
        }

        int dimensionId = level.getDimension();
        int maxX = anchor.x + (span - 1) * xa;
        int maxY = anchor.y + height - 1;
        int maxZ = anchor.z + (span - 1) * za;
        boolean exists = false;

        Iterator<PortalRecord> iterator = records.iterator();
        while (iterator.hasNext()) {
            PortalRecord record = iterator.next();
            if (record.dimensionId != dimensionId
                    || record.tpX < anchor.x || record.tpX > maxX
                    || record.tpY < anchor.y || record.tpY > maxY
                    || record.tpZ < anchor.z || record.tpZ > maxZ) {
                continue;
            }
            if (record.tpX == anchor.x
                    && record.tpY == anchor.y
                    && record.tpZ == anchor.z
                    && level.getBlock(record.tpX, record.tpY, record.tpZ).getId().equals(BlockID.PORTAL)) {
                exists = true;
                continue;
            }
            iterator.remove();
            dirty = true;
        }

        if (!exists) {
            records.add(new PortalRecord(dimensionId, anchor.x, anchor.y, anchor.z, span, xa, za));
            dirty = true;
        }
    }

    /**
     * Removes a registered value.
     *
     * @param dimensionId value for this API
     * @param position value for this API
     */
    public synchronized void remove(int dimensionId, BlockVector3 position) {
        ensureLoaded();
        Iterator<PortalRecord> iterator = records.iterator();
        while (iterator.hasNext()) {
            PortalRecord record = iterator.next();
            if (record.dimensionId == dimensionId && record.tpX == position.x && record.tpY == position.y && record.tpZ == position.z) {
                iterator.remove();
                dirty = true;
                return;
            }
        }
    }

    /**
     * Finds the nearest matching portal.
     *
     * @param dimensionId value for this API
     * @param position value for this API
     * @param radius value for this API
     * @return the requested value
     */
    public synchronized BlockVector3 findNearest(int dimensionId, BlockVector3 position, int radius) {
        ensureLoaded();
        BlockVector3 nearest = null;
        long nearestDistance = Long.MAX_VALUE;

        for (PortalRecord record : records) {
            if (record.dimensionId != dimensionId) continue;

            for (int i = 0; i < record.span; i++) {
                int x = record.tpX + i * record.xa;
                int z = record.tpZ + i * record.za;
                long dx = x - (long) position.x;
                long dz = z - (long) position.z;
                if (Math.abs(dx) > radius || Math.abs(dz) > radius) continue;

                long dy = record.tpY - (long) position.y;
                long distance = dx * dx + dy * dy + dz * dz;
                if (distance < nearestDistance) {
                    nearestDistance = distance;
                    nearest = new BlockVector3(x, record.tpY, z);
                }
            }
        }
        return nearest;
    }

    /**
     * Persists the current state.
     */
    public synchronized void save() {
        if (!dirty) {
            return;
        }

        ListTag<CompoundTag> list = new ListTag<>(Tag.TAG_Compound);
        for (PortalRecord record : records) {
            list.add(new CompoundTag()
                    .putInt("DimId", record.dimensionId)
                    .putByte("Span", record.span)
                    .putInt("TpX", record.tpX)
                    .putInt("TpY", record.tpY)
                    .putInt("TpZ", record.tpZ)
                    .putByte("Xa", record.xa)
                    .putByte("Za", record.za));
        }

        data.putList("PortalRecords", list);
        root.putCompound("data", data);
        storage.writePortalsData(root);
        dirty = false;
    }

    private static void validate(int span, int xa, int za) {
        if (span < 2 || span > 21 || xa < 0 || xa > 1 || za < 0 || za > 1 || xa + za != 1) {
            throw new IllegalArgumentException("Invalid PortalRecord");
        }
    }

    private record PortalRecord(int dimensionId, int tpX, int tpY, int tpZ, int span, int xa, int za) {
    }
}
