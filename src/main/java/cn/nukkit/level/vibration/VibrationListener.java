package cn.nukkit.level.vibration;


import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.level.Position;

@PowerNukkitXOnly
@Since("1.19.21-r3")
public interface VibrationListener {

    /**
     * 返回振动监听器的世界位置
     * @return Position
     */
    Position getListenerPosition();
    /**
     * 是否响应此振动
     * 若响应，将会从声波源发射声波到监听器位置，并在到达时调用 onVibrationArrive() 方法
     * @param event 振动事件
     * @return boolean
     */
    boolean onVibrationOccur(VibrationEvent event);

    /**
     * 声波到达事件
     * @param event 振动事件
     */
    void onVibrationArrive(VibrationEvent event);
}
