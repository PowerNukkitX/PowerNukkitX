package org.powernukkitx.level.village;

import org.powernukkitx.math.BlockVector3;

import javax.annotation.Nullable;
import java.util.UUID;

public final class Village {
    private final UUID uuid;
    private final VillageDwellers dwellers;
    private VillageInfo info;
    private final VillagePlayers players;
    private final VillagePois pois;
    private final @Nullable VillageRaid raid;

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
    public long population() {
        return dwellers.dwellers().stream().flatMap(dweller -> dweller.actors().stream()).count();
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
