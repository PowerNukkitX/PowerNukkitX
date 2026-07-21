package org.powernukkitx.level.village;

import javax.annotation.Nullable;
import java.util.UUID;

public record Village(UUID uuid, VillageDwellers dwellers, VillageInfo info, VillagePlayers players,
                      VillagePois pois, @Nullable VillageRaid raid) {
}
