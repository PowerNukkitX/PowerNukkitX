package cn.nukkit.level.vibration;


import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Position;

/**
 * 振动监听器
 */
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
     * 请注意，若此方法被调用，则声波必定可到达
     * @param event 振动事件
     * @return boolean
     */
    boolean onVibrationOccur(VibrationEvent event);

    /**
     * 声波到达事件
     * @param event 振动事件
     */
    void onVibrationArrive(VibrationEvent event);

    /**
     * 是否是实体
     * 若为实体，则在发送声波粒子时会使用实体专属的nbt tag
     * 若不是，则将此监听器作为方块处理（eg: 潜声传感器）
     * @return boolean
     */
    default boolean isEntity() {
        return this instanceof Entity;
    }

    /**
     * 在 isEntity() 为true的前提下,返回此振动监听器对应实体对象
     * @return Entity
     */
    default Entity asEntity() {
        return (Entity) this;
    }
}
