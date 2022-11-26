package cn.nukkit.utils.collection;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@PowerNukkitXOnly
@Since("1.19.40-r4")
public interface AutoFreezable {
    FreezeStatus getFreezeStatus();

    int getTemperature();

    void setTemperature(int temperature);

    void freeze();

    void deepFreeze();

    @ShouldThaw
    void thaw();

    enum FreezeStatus {
        NONE, FREEZING, FREEZE, DEEP_FREEZING, DEEP_FREEZE, THAWING
    }
}
