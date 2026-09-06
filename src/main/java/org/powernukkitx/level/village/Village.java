package org.powernukkitx.level.village;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import org.powernukkitx.math.BlockVector3;

import javax.annotation.Nullable;
import java.util.UUID;

public final class Village {
    private final UUID uuid;
    private final VillageDwellers dwellers;
    private VillageInfo info;
    private final VillagePlayers players;
    private final VillagePois pois;
    private @Nullable VillageRaid raid;
    private long bellRingTick;
    private final Long2ObjectMap<BlockVector3> raiderPositions = new Long2ObjectOpenHashMap<>();

    public Village(UUID uuid, VillageDwellers dwellers, VillageInfo info, VillagePlayers players,
                   VillagePois pois, @Nullable VillageRaid raid) {
        this.uuid = uuid;
        this.dwellers = dwellers;
        this.info = info;
        this.players = players;
        this.pois = pois;
        this.raid = raid;
    }

    public UUID uuid() { return uuid; }
    public VillageDwellers dwellers() { return dwellers; }
    public VillageInfo info() { return info; }
    public VillagePlayers players() { return players; }
    public VillagePois pois() { return pois; }
    public @Nullable VillageRaid raid() { return raid; }

    public void setInfo(VillageInfo info) { this.info = info; }
    public void setRaid(@Nullable VillageRaid raid) { this.raid = raid; }

    /**
     * @return the tick the bells of this village are due to ring again while a raid is being prepared
     */
    public long bellRingTick() { return bellRingTick; }

    public void setBellRingTick(long bellRingTick) { this.bellRingTick = bellRingTick; }

    /**
     * @return the last position each raider of the running raid was seen at, used to tell a raider
     * that died from one that only left the loaded chunks
     */
    public Long2ObjectMap<BlockVector3> raiderPositions() { return raiderPositions; }

    public long population() {
        return dwellers.dwellers().stream().mapToLong(dweller -> dweller.actors().size()).sum();
    }

    public long houseCount() {
        return poiCount(PoiType.HOME);
    }

    public long populationCap() {
        return houseCount();
    }

    public long jobSiteCount() {
        return claimedPoiCount(PoiType.ACQUIRABLE_JOB_SITE);
    }

    public long gatheringSiteCount() {
        return claimedPoiCount(PoiType.MEETING);
    }

    public boolean isValid() {
        return population() > 0 && houseCount() > 0;
    }

    public BlockVector3 center() {
        return new BlockVector3((info.boundsMin().x + info.boundsMax().x) >> 1,
                (info.boundsMin().y + info.boundsMax().y) >> 1,
                (info.boundsMin().z + info.boundsMax().z) >> 1);
    }

    private long claimedPoiCount(PoiType type) {
        return pois.poi().stream().flatMap(group -> group.instances().stream())
                .filter(poi -> poi.type() == type && poi.ownerCount() > 0).count();
    }

    private long poiCount(PoiType type) {
        return pois.poi().stream().flatMap(group -> group.instances().stream())
                .filter(poi -> poi.type() == type).count();
    }
}
