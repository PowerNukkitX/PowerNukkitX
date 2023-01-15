package cn.nukkit.entity.ai.memory;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.math.Vector3;

import java.util.HashMap;
import java.util.Map;

/**
 * 核心使用到的记忆类型枚举
 */
@PowerNukkitXOnly
@Since("1.19.50-r1")
public interface CoreMemoryTypes {
    //运动控制器相关
    MemoryType<Vector3> LOOK_TARGET = new MemoryType<>("minecraft:look_target");
    MemoryType<Vector3> MOVE_TARGET = new MemoryType<>("minecraft:move_target");
    MemoryType<Vector3> MOVE_DIRECTION_START = new MemoryType<>("minecraft:move_direction_start");
    MemoryType<Vector3> MOVE_DIRECTION_END = new MemoryType<>("minecraft:move_direction_end");
    MemoryType<Boolean> SHOULD_UPDATE_MOVE_DIRECTION = new MemoryType<>("minecraft:should_update_move_direction", false);
    MemoryType<Boolean> ENABLE_PITCH = new MemoryType<>("minecraft:enable_pitch", true);

    MemoryType<Boolean> ENABLE_LIFT_FORCE = new MemoryType<>("minecraft:enable_lift_force", true);
    MemoryType<Boolean> ENABLE_DIVE_FORCE = new MemoryType<>("minecraft:enable_dive_force", true);
    //以下这两个暂时未使用到
//    MemoryType<Boolean> ENABLE_YAW = new MemoryType<>("minecraft:enable_yaw", true);
//    MemoryType<Boolean> ENABLE_HEAD_YAW = new MemoryType<>("minecraft:enable_head_yaw", true);

    MemoryType<EntityDamageEvent> BE_ATTACKED_EVENT = new MemoryType<>("minecraft:be_attacked_event");
    MemoryType<Integer> LAST_BE_ATTACKED_TIME = new MemoryType<>("minecraft:last_be_attacked_time", -65536);
    MemoryType<Entity> ATTACK_TARGET = new MemoryType<>("minecraft:attack_target");
    MemoryType<Boolean> IS_ATTACK_TARGET_CHANGED = new MemoryType<>("minecraft:is_attack_target_changed", false);
    MemoryType<Integer> ENTITY_SPAWN_TIME = new MemoryType<>("minecraft:entity_spawn_time", () -> Server.getInstance().getTick());
    MemoryType<Boolean> SHOULD_EXPLODE = new MemoryType<>("minecraft:should_explode", false);
    MemoryType<Boolean> EXPLODE_CANCELLABLE = new MemoryType<>("minecraft:explode_cancellable", true);
    MemoryType<Boolean> IS_IN_LOVE = new MemoryType<>("minecraft:is_in_love", false);
    MemoryType<Integer> LAST_IN_LOVE_TIME = new MemoryType<>("minecraft:last_in_love_time", -65536);
    MemoryType<Integer> LAST_EGG_SPAWN_TIME = new MemoryType<>("minecraft:last_egg_spawn_time", () -> Server.getInstance().getTick());
    //最近可攻击生物（注意，非仇恨目标）
    MemoryType<Entity> NEAREST_SUITABLE_ATTACK_TARGET = new MemoryType<>("minecraft:nearest_suitable_attack_target");
    MemoryType<Player> NEAREST_FEEDING_PLAYER = new MemoryType<>("minecraft:nearest_feeding_player");
    MemoryType<Player> NEAREST_PLAYER = new MemoryType<>("minecraft:nearest_player");
    MemoryType<Entity> ENTITY_ATTACKED_BY_OWNER = new MemoryType<>("minecraft:entity_attacked_by_owner");
    MemoryType<Entity> ENTITY_ATTACKING_OWNER = new MemoryType<>("minecraft:entity_attacking_owner");
    MemoryType<Integer> LAST_BE_FED_TIME = new MemoryType<>("minecraft:last_be_fed_time", -65536);
    MemoryType<Player> LAST_FEED_PLAYER = new MemoryType<>("minecraft:last_feeding_player");
    MemoryType<Integer> ROUTE_UNREACHABLE_TIME = new MemoryType<>("minecraft:route_unreachable_time", 0);
    MemoryType<Entity> ENTITY_SPOUSE = new MemoryType<>("minecraft:entity_spouse");
    MemoryType<Map<Entity, Integer>> WARDEN_ANGER_VALUE = new MemoryType<>("minecraft:warden_anger_value", new HashMap<>());
    MemoryType<Entity> NEAREST_SKELETON = new MemoryType<>("minecraft:nearest_skeleton");
}
