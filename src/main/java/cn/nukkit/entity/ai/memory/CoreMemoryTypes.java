package cn.nukkit.entity.ai.memory;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.ai.memory.codec.BooleanMemoryCodec;
import cn.nukkit.entity.ai.memory.codec.NumberMemoryCodec;
import cn.nukkit.entity.ai.memory.codec.StringMemoryCodec;
import cn.nukkit.entity.data.EntityDataTypes;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.math.Vector3;

import java.util.HashMap;
import java.util.Map;

/**
 * 核心使用到的记忆类型枚举
 * <p>
 * Enumeration of memory types used by the core
 */


public interface CoreMemoryTypes {
    // region 运动控制器相关
    /**
     * 实体视线目标记忆
     * <p>
     * Entity gaze target memory
     */
    MemoryType<Vector3> LOOK_TARGET = new MemoryType<>("minecraft:look_target");
    /**
     * 实体移动目标记忆
     * <p>
     * Entity moving target memory
     */
    MemoryType<Vector3> MOVE_TARGET = new MemoryType<>("minecraft:move_target");
    /**
     * 实体移动起点记忆
     * <p>
     * Entity movement starting point memory
     */
    MemoryType<Vector3> MOVE_DIRECTION_START = new MemoryType<>("minecraft:move_direction_start");
    /**
     * 实体移动终点记忆
     * <p>
     * Entity movement endpoint memory
     */
    MemoryType<Vector3> MOVE_DIRECTION_END = new MemoryType<>("minecraft:move_direction_end");
    /**
     * 实体是否需要更新路径的记忆
     * <p>
     * Whether the entity needs to update the memory of the path
     */
    MemoryType<Boolean> SHOULD_UPDATE_MOVE_DIRECTION = new MemoryType<>("minecraft:should_update_move_direction", false);
    /**
     * 实体是否开启pitch
     * <p>
     * Whether pitch is enabled for the entity
     */
    MemoryType<Boolean> ENABLE_PITCH = new MemoryType<>("minecraft:enable_pitch", true);

    /**
     * 控制实体是否开启升力控制器的记忆
     */
    MemoryType<Boolean> ENABLE_LIFT_FORCE = new MemoryType<>("minecraft:enable_lift_force", true);
    /**
     * 控制实体是否开启下潜控制器的记忆
     */
    MemoryType<Boolean> ENABLE_DIVE_FORCE = new MemoryType<>("minecraft:enable_dive_force", true);
    //以下这两个暂时未使用到
    //MemoryType<Boolean> ENABLE_YAW = new MemoryType<>("minecraft:enable_yaw", true);
    //MemoryType<Boolean> ENABLE_HEAD_YAW = new MemoryType<>("minecraft:enable_head_yaw", true);
    // endregion
    /**
     * 实体被攻击产生的攻击事件
     */
    MemoryType<EntityDamageEvent> BE_ATTACKED_EVENT = new MemoryType<>("minecraft:be_attacked_event");
    /**
     * 实体上一次被攻击的tick
     */
    MemoryType<Integer> LAST_BE_ATTACKED_TIME = new MemoryType<>("minecraft:last_be_attacked_time", -65536);
    /**
     * 实体的仇恨目标
     */
    MemoryType<Entity> ATTACK_TARGET = new MemoryType<>("minecraft:attack_target");
    /**
     * 实体的攻击目标是否被改变,目前仅在warden中使用
     */
    MemoryType<Boolean> IS_ATTACK_TARGET_CHANGED = new MemoryType<>("minecraft:is_attack_target_changed", false);
    /**
     * 实体从生成的服务器tick
     */
    MemoryType<Integer> ENTITY_SPAWN_TIME = new MemoryType<>("minecraft:entity_spawn_time", () -> Server.getInstance().getTick());
    /**
     * 目前仅在creeper中使用，控制苦力怕是否应该爆炸
     */
    MemoryType<Boolean> SHOULD_EXPLODE = new MemoryType<>("minecraft:should_explode", false);
    /**
     * 目前仅在creeper中使用
     */
    MemoryType<Boolean> EXPLODE_CANCELLABLE = new MemoryType<>("minecraft:explode_cancellable", true);
    /**
     * 控制实体是否在繁殖
     */
    MemoryType<Boolean> IS_IN_LOVE = new MemoryType<>("minecraft:is_in_love", false);
    /**
     * 上一次繁殖的时间tick
     */
    MemoryType<Integer> LAST_IN_LOVE_TIME = new MemoryType<>("minecraft:last_in_love_time", -65536);
    /**
     * 上一次下蛋的时间
     * <p>
     * 目前仅在Chicken中使用
     */
    MemoryType<Integer> LAST_EGG_SPAWN_TIME = new MemoryType<>("minecraft:last_egg_spawn_time", () -> Server.getInstance().getTick());
    /**
     * 最近符合条件的攻击目标
     * <p>
     * 通常写入在在NearestTargetEntitySensor
     */
    MemoryType<Entity> NEAREST_SUITABLE_ATTACK_TARGET = new MemoryType<>("minecraft:nearest_suitable_attack_target");
    /**
     * 最近持有动物要食用的食物的玩家
     */
    MemoryType<Player> NEAREST_FEEDING_PLAYER = new MemoryType<>("minecraft:nearest_feeding_player");
    /**
     * 最近的玩家
     */
    MemoryType<Player> NEAREST_PLAYER = new MemoryType<>("minecraft:nearest_player");
    /**
     * 玩家上一次攻击的实体
     */
    MemoryType<Entity> ENTITY_ATTACKED_BY_OWNER = new MemoryType<>("minecraft:entity_attacked_by_owner");
    /**
     * 上一次攻击玩家的实体
     */
    MemoryType<Entity> ENTITY_ATTACKING_OWNER = new MemoryType<>("minecraft:entity_attacking_owner");
    /**
     * 上一次喂养的时间
     */
    MemoryType<Integer> LAST_BE_FEED_TIME = new MemoryType<>("minecraft:last_be_feed_time", -65536);
    /**
     * 上一次喂养的玩家
     */
    MemoryType<Player> LAST_FEED_PLAYER = new MemoryType<>("minecraft:last_feeding_player");
    /**
     * 目前仅在warden中使用
     */
    MemoryType<Integer> ROUTE_UNREACHABLE_TIME = new MemoryType<>("minecraft:route_unreachable_time", 0);
    /**
     * 实体的配偶
     */
    MemoryType<Entity> ENTITY_SPOUSE = new MemoryType<>("minecraft:entity_spouse");
    /**
     * 目前仅在warden中使用
     */
    MemoryType<Map<Entity, Integer>> WARDEN_ANGER_VALUE = new MemoryType<>("minecraft:warden_anger_value", new HashMap<>());
    /**
     * 最近的骷髅目标
     */
    MemoryType<Entity> NEAREST_SKELETON = new MemoryType<>("minecraft:nearest_skeleton");
    /**
     * 实体的主人
     */
    MemoryType<Player> OWNER = new MemoryType<>("minecraft:owner");

    // region 可持久化的记忆(会写入NBT)
    /**
     * 代表愤怒状态 和{@link EntityFlag#ANGRY}绑定
     * <p>
     * 目前仅在wolf中使用
     */
    MemoryType<Boolean> IS_ANGRY = new MemoryType<>("minecraft:is_angry", false)
            .withCodec(new BooleanMemoryCodec("Angry")
                    .onInit((data, entity) -> entity.setDataFlag(EntityFlag.ANGRY, data))
            );
    /**
     * 代表实体是否坐着的状态 和{@link EntityFlag#SITTING}绑定
     * <p>
     * 目前仅在wolf中使用
     */
    MemoryType<Boolean> IS_SITTING = new MemoryType<>("minecraft:is_sitting", false)
            .withCodec(new BooleanMemoryCodec("Sitting")
                    .onInit((data, entity) -> {
                        entity.setDataFlag(EntityFlag.SITTING, data);
                    })
            );
    /**
     * 代表实体主人 和{@link EntityFlag#TAMED} {@link EntityDataTypes#OWNER_EID}绑定
     * <p>
     * 目前仅在wolf中使用
     */
    MemoryType<String> OWNER_NAME = new MemoryType<String>("minecraft:owner_name")
            .withCodec(new StringMemoryCodec("OwnerName")
                    .onInit((data, entity) -> {
                        if (data == null) {
                            entity.setDataProperty(EntityDataTypes.OWNER_EID, 0L);
                            entity.setDataFlag(EntityFlag.TAMED, false);
                        } else {
                            entity.setDataFlag(EntityFlag.TAMED, true);
                            var owner = entity.getServer().getPlayerExact(data);
                            if (owner != null && owner.isOnline()) {
                                entity.setDataProperty(EntityDataTypes.OWNER_EID, owner.getId());
                            }
                        }
                    })
            );
    /**
     * 代表骑着某个实体的实体
     */
    MemoryType<String> RIDER_NAME = new MemoryType<String>("minecraft:rider_name")
            .withCodec(new StringMemoryCodec("RiderName")
                    .onInit((data, entity) -> {
                        if (data == null) {
                            entity.setDataFlag(EntityFlag.WASD_CONTROLLED, false);
                        } else {
                            entity.setDataFlag(EntityFlag.WASD_CONTROLLED);
                        }
                    })
            );
    /**
     * 代表实体的变种,和{@link EntityDataTypes#VARIANT}绑定
     */
    MemoryType<Integer> VARIANT = new MemoryType<Integer>("minecraft:variant")
            .withCodec(new NumberMemoryCodec<Integer>("Variant")
                    .onInit((data, entity) -> {
                        if (data != null) {
                            entity.setDataProperty(EntityDataTypes.VARIANT, data);
                        }
                    })
            );
    /**
     * 代表实体的次要变种,和{@link EntityDataTypes#MARK_VARIANT}绑定
     */
    MemoryType<Integer> MARK_VARIANT = new MemoryType<Integer>("minecraft:mark_variant")
            .withCodec(new NumberMemoryCodec<Integer>("MarkVariant")
                    .onInit((data, entity) -> {
                        if (data != null) {
                            entity.setDataProperty(EntityDataTypes.MARK_VARIANT, data);
                        }
                    })
            );
    /**
     * 代表实体的颜色，和{@link EntityDataTypes#COLOR}绑定
     * <br>
     * 例如狼的项圈
     * <br>
     * Wolf collar, Cat collar, Sheep wool, Tropical Fish base color
     */
    MemoryType<Byte> COLOR = new MemoryType<Byte>("minecraft:color")
            .withCodec(new NumberMemoryCodec<Byte>("Color")
                    .onInit((data, entity) -> {
                        if (data != null) {
                            entity.setDataProperty(EntityDataTypes.COLOR, data);
                        }
                    })
            );
    /**
     * 代表实体的颜第二色，和{@link EntityDataTypes#COLOR_2}绑定
     * <br>
     * Tropical Fish secondary color
     */
    MemoryType<Byte> COLOR2 = new MemoryType<Byte>("minecraft:color2")
            .withCodec(new NumberMemoryCodec<Byte>("Color2")
                    .onInit((data, entity) -> {
                        if (data != null) {
                            entity.setDataProperty(EntityDataTypes.COLOR_2, data);
                        }
                    })
            );
    /**
     * 和{@link EntityFlag#SHEARED}绑定
     * <p>
     * Sheep, Snow Golem
     */
    MemoryType<Boolean> IS_SHEARED = new MemoryType<>("minecraft:is_sheared", false)
            .withCodec(new BooleanMemoryCodec("Sheared")
                    .onInit((data, entity) -> entity.setDataFlag(EntityFlag.SHEARED, data))
            );
    // endregion
}
