package cn.nukkit.entity;

/**
 * 实现了此接口的实体可飞行
 */


public interface EntityFlyable {
    /**
     * @return 是否具有摔落伤害
     */
    default boolean hasFallingDamage() {
        return false;
    }
}
