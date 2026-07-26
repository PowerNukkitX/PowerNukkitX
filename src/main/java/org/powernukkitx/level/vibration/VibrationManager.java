package org.powernukkitx.level.vibration;


public interface VibrationManager {
    void callVibrationEvent(VibrationEvent event);

    void addListener(VibrationListener listener);

    void removeListener(VibrationListener listener);
}
