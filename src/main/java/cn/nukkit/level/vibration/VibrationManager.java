package cn.nukkit.level.vibration;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@PowerNukkitXOnly
@Since("1.19.21-r3")
public interface VibrationManager {
    void callVibrationEvent(VibrationEvent event);
    void addListener(VibrationListener listener);
    void removeListener(VibrationListener listener);
}
