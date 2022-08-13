package cn.nukkit.level.format;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.level.DimensionData;
import org.jetbrains.annotations.Nullable;

@PowerNukkitXOnly
@Since("1.19.20-r3")
public interface DimensionDataProvider {
    @Nullable
    DimensionData getDimensionData();

    void setDimensionData(@Nullable DimensionData dimensionData);
}
