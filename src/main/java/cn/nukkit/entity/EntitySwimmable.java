package cn.nukkit.entity;

/**
 * 实现了此接口的生物可游泳
 */


public interface EntitySwimmable {
    /**
     * @return 此实体是否会受到溺水伤害
     */
    default boolean canDrown() {
        return false;
    }
}
