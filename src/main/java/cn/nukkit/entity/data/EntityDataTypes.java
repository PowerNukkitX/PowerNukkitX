package cn.nukkit.entity.data;

import cn.nukkit.block.Block;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.EnumSet;
import java.util.Set;

public interface EntityDataTypes {
    EntityDataType<EnumSet<EntityFlag>> FLAGS = new EntityDataType<EnumSet<EntityFlag>>(EnumSet.noneOf(EntityFlag.class), "FLAGS", 0, Transformers.FLAGS) {
        @Override
        public boolean isInstance(Object value) {
            return value instanceof EnumSet &&
                    (((EnumSet<?>) value).isEmpty() || ((Set<?>) value).iterator().next() instanceof EntityFlag);
        }
    };
    EntityDataType<Integer> STRUCTURAL_INTEGRITY = new EntityDataType<>(0, "STRUCTURAL_INTEGRITY", 1);
    EntityDataType<Integer> VARIANT = new EntityDataType<>(0, "VARIANT", 2);
    EntityDataType<Byte> COLOR = new EntityDataType<>((byte) 0, "COLOR", 3);
    EntityDataType<String> NAME = new EntityDataType<>("", "NAME", 4);
    /**
     * Unique ID of the entity that owns or created this entity.
     */
    EntityDataType<Long> OWNER_EID = new EntityDataType<>(0L, "OWNER_EID", 5);
    EntityDataType<Long> TARGET_EID = new EntityDataType<>(0L, "TARGET_EID", 6);
    EntityDataType<Short> AIR_SUPPLY = new EntityDataType<>((short) 0, "AIR_SUPPLY", 7);
    EntityDataType<Integer> EFFECT_COLOR = new EntityDataType<>(0, "EFFECT_COLOR", 8);
    EntityDataType<Byte> EFFECT_AMBIENCE = new EntityDataType<>((byte) 0, "EFFECT_AMBIENCE", 9);
    EntityDataType<Byte> JUMP_DURATION = new EntityDataType<>((byte) 0, "JUMP_DURATION", 10);
    EntityDataType<Integer> HURT_TICKS = new EntityDataType<>(0, "HURT_TICKS", 11);
    EntityDataType<Integer> HURT_DIRECTION = new EntityDataType<>(0, "HURT_DIRECTION", 12);
    EntityDataType<Float> ROW_TIME_LEFT = new EntityDataType<>(0f, "ROW_TIME_LEFT", 13);
    EntityDataType<Float> ROW_TIME_RIGHT = new EntityDataType<>(0f, "ROW_TIME_RIGHT", 14);
    EntityDataType<Integer> VALUE = new EntityDataType<>(0, "VALUE", 15);
    // Same ID shares three different types -facepalm-
    EntityDataType<Integer> HORSE_FLAGS = new EntityDataType<>(0, "HORSE_FLAGS", 16);//int (id | (data << 16))
    EntityDataType<Integer> DISPLAY_OFFSET = new EntityDataType<>(0, "DISPLAY_OFFSET", 17);
    EntityDataType<Byte> CUSTOM_DISPLAY = new EntityDataType<>((byte) 0, "CUSTOM_DISPLAY", 18);
    EntityDataType<Byte> HORSE_TYPE = new EntityDataType<>((byte) 0, "HORSE_TYPE", 19);
    EntityDataType<Integer> OLD_SWELL = new EntityDataType<>(0, "OLD_SWELL", 20);
    EntityDataType<Integer> SWELL_DIRECTION = new EntityDataType<>(0, "SWELL_DIRECTION", 21);
    EntityDataType<Byte> CHARGE_AMOUNT = new EntityDataType<>((byte) 0, "CHARGE_AMOUNT", 22);
    EntityDataType<Block> CARRY_BLOCK_STATE = new EntityDataType<>(Block.get(Block.AIR), "CARRY_BLOCK_STATE", 23, Transformers.BLOCK);
    EntityDataType<Byte> CLIENT_EVENT = new EntityDataType<>((byte) 0, "CLIENT_EVENT", 24);
    EntityDataType<Boolean> USING_ITEM = new EntityDataType<>(false, "USING_ITEM", 25, Transformers.BOOLEAN);
    EntityDataType<Byte> PLAYER_FLAGS = new EntityDataType<>((byte) 0, "PLAYER_FLAGS", 26);
    EntityDataType<Integer> PLAYER_INDEX = new EntityDataType<>(0, "PLAYER_INDEX", 27);
    EntityDataType<BlockVector3> BED_POSITION = new EntityDataType<>(new BlockVector3(), "BED_POSITION", 28);
    /**
     * Power of fireball (x-axis)
     */
    EntityDataType<Float> FIREBALL_POWER_X = new EntityDataType<>(0f, "FIREBALL_POWER_X", 29);
    /**
     * Power of fireball (y-axis)
     */
    EntityDataType<Float> FIREBALL_POWER_Y = new EntityDataType<>(0f, "FIREBALL_POWER_Y", 30);
    /**
     * Power of fireball (z-axis)
     */
    EntityDataType<Float> FIREBALL_POWER_Z = new EntityDataType<>(0f, "FIREBALL_POWER_Z", 31);
    /**
     * Potion aux value used for an Arrow's trail. (Equal to the potion ID - 1)
     */
    EntityDataType<Byte> AUX_POWER = new EntityDataType<>((byte) 0, "AUX_POWER", 32);
    EntityDataType<Float> FISH_X = new EntityDataType<>(0f, "FISH_X", 33);
    EntityDataType<Float> FISH_Z = new EntityDataType<>(0f, "FISH_Z", 34);
    EntityDataType<Float> FISH_ANGLE = new EntityDataType<>(0f, "FISH_ANGLE", 35);
    EntityDataType<Short> AUX_VALUE_DATA = new EntityDataType<>((short) 0, "AUX_VALUE_DATA", 36);
    /**
     * Unique ID for the entity who holds a leash to the current entity.
     */
    EntityDataType<Long> LEASH_HOLDER = new EntityDataType<>(0L, "LEASH_HOLDER", 37);
    /**
     * Set the scale of this entity.
     * 1 is the default size defined by {@code EntityDataType#WIDTH} and {@code EntityDataType#HEIGHT}.
     */
    EntityDataType<Float> SCALE = new EntityDataType<>(0f, "SCALE", 38);
    EntityDataType<Boolean> HAS_NPC = new EntityDataType<>(false, "HAS_NPC", 39, Transformers.BOOLEAN);
    EntityDataType<String> NPC_DATA = new EntityDataType<>("", "NPC_DATA", 40);
    EntityDataType<String> ACTIONS = new EntityDataType<>("", "ACTIONS", 41);
    EntityDataType<Short> AIR_SUPPLY_MAX = new EntityDataType<>((short) 0, "AIR_SUPPLY_MAX", 42);
    EntityDataType<Integer> MARK_VARIANT = new EntityDataType<>(0, "MARK_VARIANT", 43);
    EntityDataType<Byte> CONTAINER_TYPE = new EntityDataType<>((byte) 0, "CONTAINER_TYPE", 44);
    EntityDataType<Integer> CONTAINER_SIZE = new EntityDataType<>(0, "CONTAINER_SIZE", 45);
    EntityDataType<Integer> CONTAINER_STRENGTH_MODIFIER = new EntityDataType<>(0, "CONTAINER_STRENGTH_MODIFIER", 46);
    /**
     * Target position of Ender Crystal beam.
     */
    EntityDataType<BlockVector3> BLOCK_TARGET_POS = new EntityDataType<>(new BlockVector3(), "BLOCK_TARGET_POS", 47);
    EntityDataType<Integer> WITHER_INVULNERABLE_TICKS = new EntityDataType<>(0, "WITHER_INVULNERABLE_TICKS", 48);
    /**
     * Unique entity ID to target for the left head of a Wither.
     */
    EntityDataType<Long> WITHER_TARGET_A = new EntityDataType<>(0L, "WITHER_TARGET_A", 49);
    /**
     * Unique entity ID to target for the middle head of a Wither.
     */
    EntityDataType<Long> WITHER_TARGET_B = new EntityDataType<>(0L, "WITHER_TARGET_B", 50);
    /**
     * Unique entity ID to target for the right head of a Wither.
     */
    EntityDataType<Long> WITHER_TARGET_C = new EntityDataType<>(0L, "WITHER_TARGET_C", 51);
    EntityDataType<Short> WITHER_AERIAL_ATTACK = new EntityDataType<>((short) 0, "WITHER_AERIAL_ATTACK", 52);
    EntityDataType<Float> WIDTH = new EntityDataType<>(0f, "WIDTH", 53);
    EntityDataType<Float> HEIGHT = new EntityDataType<>(0f, "HEIGHT", 54);
    EntityDataType<Integer> FUSE_TIME = new EntityDataType<>(0, "FUSE_TIME", 55);
    EntityDataType<Vector3f> SEAT_OFFSET = new EntityDataType<>(new Vector3f(), "SEAT_OFFSET", 56);
    EntityDataType<Boolean> SEAT_LOCK_RIDER_ROTATION = new EntityDataType<>(false, "SEAT_LOCK_RIDER_ROTATION", 57, Transformers.BOOLEAN);
    EntityDataType<Float> SEAT_LOCK_RIDER_ROTATION_DEGREES = new EntityDataType<>(0f, "SEAT_LOCK_RIDER_ROTATION_DEGREES", 58);
    EntityDataType<Boolean> SEAT_HAS_ROTATION = new EntityDataType<>(false, "SEAT_HAS_ROTATION", 59, Transformers.BOOLEAN);
    EntityDataType<Float> SEAT_ROTATION_OFFSET_DEGREES = new EntityDataType<>(0f, "SEAT_ROTATION_OFFSET_DEGREES", 60);
    /**
     * Radius of Area Effect Cloud
     */
    EntityDataType<Float> AREA_EFFECT_CLOUD_RADIUS = new EntityDataType<>(0f, "AREA_EFFECT_CLOUD_RADIUS", 61);
    EntityDataType<Integer> AREA_EFFECT_CLOUD_WAITING = new EntityDataType<>(0, "AREA_EFFECT_CLOUD_WAITING", 62);
    EntityDataType<Integer> AREA_EFFECT_CLOUD_PARTICLE = new EntityDataType<>(0, "AREA_EFFECT_CLOUD_PARTICLE", 63);
    EntityDataType<Integer> SHULKER_PEEK_AMOUNT = new EntityDataType<>(0, "SHULKER_PEEK_AMOUNT", 64);
    EntityDataType<Integer> SHULKER_ATTACH_FACE = new EntityDataType<>(0, "SHULKER_ATTACH_FACE", 65);
    EntityDataType<Boolean> SHULKER_ATTACHED = new EntityDataType<>(false, "SHULKER_ATTACHED", 66, Transformers.BOOLEAN);
    /**
     * Position a Shulker entity is attached from.
     */
    EntityDataType<BlockVector3> SHULKER_ATTACH_POS = new EntityDataType<>(new BlockVector3(), "SHULKER_ATTACH_POS", 67);
    /**
     * Sets the unique ID of the player that is trading with this entity.
     */
    EntityDataType<Long> TRADE_TARGET_EID = new EntityDataType<>(0L, "TRADE_TARGET_EID", 68);
    /**
     * Previously used for the villager V1 entity.
     */
    EntityDataType<Integer> CAREER = new EntityDataType<>(0, "CAREER", 69);
    EntityDataType<Boolean> COMMAND_BLOCK_ENABLED = new EntityDataType<>(false, "COMMAND_BLOCK_ENABLED", 70, Transformers.BOOLEAN);
    EntityDataType<String> COMMAND_BLOCK_NAME = new EntityDataType<>("", "COMMAND_BLOCK_NAME", 71);
    EntityDataType<String> COMMAND_BLOCK_LAST_OUTPUT = new EntityDataType<>("", "COMMAND_BLOCK_LAST_OUTPUT", 72);
    EntityDataType<Boolean> COMMAND_BLOCK_TRACK_OUTPUT = new EntityDataType<>(false, "COMMAND_BLOCK_TRACK_OUTPUT", 73, Transformers.BOOLEAN);
    EntityDataType<Byte> CONTROLLING_RIDER_SEAT_INDEX = new EntityDataType<>((byte) 0, "CONTROLLING_RIDER_SEAT_INDEX", 74);
    EntityDataType<Integer> STRENGTH = new EntityDataType<>(0, "STRENGTH", 75);
    EntityDataType<Integer> STRENGTH_MAX = new EntityDataType<>(0, "STRENGTH_MAX", 76);
    EntityDataType<Integer> EVOKER_SPELL_CASTING_COLOR = new EntityDataType<>(0, "EVOKER_SPELL_CASTING_COLOR", 77);
    EntityDataType<Integer> DATA_LIFETIME_TICKS = new EntityDataType<>(0, "DATA_LIFETIME_TICKS", 78);
    EntityDataType<Integer> ARMOR_STAND_POSE_INDEX = new EntityDataType<>(0, "ARMOR_STAND_POSE_INDEX", 79);
    EntityDataType<Integer> END_CRYSTAL_TICK_OFFSET = new EntityDataType<>(0, "END_CRYSTAL_TICK_OFFSET", 80);
    EntityDataType<Byte> NAMETAG_ALWAYS_SHOW = new EntityDataType<>((byte) 0, "NAMETAG_ALWAYS_SHOW", 81);
    EntityDataType<Byte> COLOR_2 = new EntityDataType<>((byte) 0, "COLOR_2", 82);
    EntityDataType<String> NAME_AUTHOR = new EntityDataType<>("", "NAME_AUTHOR", 83);
    EntityDataType<String> SCORE = new EntityDataType<>("", "SCORE", 84);
    /**
     * Unique entity ID that the balloon string is attached to.
     * Disable by setting value to -1.
     */
    EntityDataType<Long> BALLOON_ANCHOR_EID = new EntityDataType<>(0L, "BALLOON_ANCHOR_EID", 85);
    EntityDataType<Byte> PUFFED_STATE = new EntityDataType<>((byte) 0, "PUFFED_STATE", 86);
    EntityDataType<Integer> BOAT_BUBBLE_TIME = new EntityDataType<>(0, "BOAT_BUBBLE_TIME", 87);
    /**
     * The unique entity ID of the player's Agent. (Education Edition only)
     */
    EntityDataType<Long> AGENT_EID = new EntityDataType<>(0L, "AGENT_EID", 88);
    EntityDataType<Float> SITTING_AMOUNT = new EntityDataType<>(0f, "SITTING_AMOUNT", 89);
    EntityDataType<Float> SITTING_AMOUNT_PREVIOUS = new EntityDataType<>(0f, "SITTING_AMOUNT_PREVIOUS", 90);
    EntityDataType<Integer> EATING_COUNTER = new EntityDataType<>(0, "EATING_COUNTER", 91);
    EntityDataType<EnumSet<EntityFlag>> FLAGS_2 = new EntityDataType<>(EnumSet.noneOf(EntityFlag.class), "FLAGS_2", 92, Transformers.FLAGS_EXTEND);
    EntityDataType<Float> LAYING_AMOUNT = new EntityDataType<>(0f, "LAYING_AMOUNT", 93);
    EntityDataType<Float> LAYING_AMOUNT_PREVIOUS = new EntityDataType<>(0f, "LAYING_AMOUNT_PREVIOUS", 94);
    EntityDataType<Integer> AREA_EFFECT_CLOUD_DURATION = new EntityDataType<>(0, "AREA_EFFECT_CLOUD_DURATION", 95);
    EntityDataType<Integer> AREA_EFFECT_CLOUD_SPAWN_TIME = new EntityDataType<>(0, "AREA_EFFECT_CLOUD_SPAWN_TIME", 96);
    EntityDataType<Float> AREA_EFFECT_CLOUD_CHANGE_RATE = new EntityDataType<>(0f, "AREA_EFFECT_CLOUD_CHANGE_RATE", 97);
    EntityDataType<Float> AREA_EFFECT_CLOUD_CHANGE_ON_PICKUP = new EntityDataType<>(0f, "AREA_EFFECT_CLOUD_CHANGE_ON_PICKUP", 98);
    EntityDataType<Integer> AREA_EFFECT_CLOUD_PICKUP_COUNT = new EntityDataType<>(0, "AREA_EFFECT_CLOUD_PICKUP_COUNT", 99);
    EntityDataType<String> INTERACT_TEXT = new EntityDataType<>("", "INTERACT_TEXT", 100);
    EntityDataType<Integer> TRADE_TIER = new EntityDataType<>(0, "TRADE_TIER", 101);
    EntityDataType<Integer> MAX_TRADE_TIER = new EntityDataType<>(0, "MAX_TRADE_TIER", 102);
    EntityDataType<Integer> TRADE_EXPERIENCE = new EntityDataType<>(0, "TRADE_EXPERIENCE", 103);
    EntityDataType<Integer> SKIN_ID = new EntityDataType<>(0, "SKIN_ID", 104);
    EntityDataType<Integer> SPAWNING_FRAMES = new EntityDataType<>(0, "SPAWNING_FRAMES", 105);
    EntityDataType<Integer> COMMAND_BLOCK_TICK_DELAY = new EntityDataType<>(0, "COMMAND_BLOCK_TICK_DELAY", 106);
    EntityDataType<Boolean> COMMAND_BLOCK_EXECUTE_ON_FIRST_TICK = new EntityDataType<>(false, "COMMAND_BLOCK_EXECUTE_ON_FIRST_TICK", 107, Transformers.BOOLEAN);
    EntityDataType<Float> AMBIENT_SOUND_INTERVAL = new EntityDataType<>(0f, "AMBIENT_SOUND_INTERVAL", 108);
    EntityDataType<Float> AMBIENT_SOUND_INTERVAL_RANGE = new EntityDataType<>(0f, "AMBIENT_SOUND_INTERVAL_RANGE", 109);
    EntityDataType<String> AMBIENT_SOUND_EVENT_NAME = new EntityDataType<>("", "AMBIENT_SOUND_EVENT_NAME", 110);
    EntityDataType<Float> FALL_DAMAGE_MULTIPLIER = new EntityDataType<>(0f, "FALL_DAMAGE_MULTIPLIER", 111);
    EntityDataType<String> NAME_RAW_TEXT = new EntityDataType<>("", "NAME_RAW_TEXT", 112);
    EntityDataType<Boolean> CAN_RIDE_TARGET = new EntityDataType<>(false, "CAN_RIDE_TARGET", 113, Transformers.BOOLEAN);
    EntityDataType<Integer> LOW_TIER_CURED_TRADE_DISCOUNT = new EntityDataType<>(0, "LOW_TIER_CURED_TRADE_DISCOUNT", 114);
    EntityDataType<Integer> HIGH_TIER_CURED_TRADE_DISCOUNT = new EntityDataType<>(0, "HIGH_TIER_CURED_TRADE_DISCOUNT", 115);
    EntityDataType<Integer> NEARBY_CURED_TRADE_DISCOUNT = new EntityDataType<>(0, "NEARBY_CURED_TRADE_DISCOUNT", 116);
    EntityDataType<Integer> NEARBY_CURED_DISCOUNT_TIME_STAMP = new EntityDataType<>(0, "NEARBY_CURED_DISCOUNT_TIME_STAMP", 117);

    /**
     * Set custom hitboxes for an entity. This will override the hitbox defined with {@link EntityDataTypes#SCALE},
     * {@link EntityDataTypes#WIDTH} and {@link EntityDataTypes#HEIGHT}, but will not affect the collisions.
     * Setting the hitbox to an empty list will revert to default behaviour.
     * <p>
     * NBT format
     * <pre>
     * {
     *     "Hitboxes": [
     *          {
     *              "MinX": 0f,
     *              "MinY": 0f,
     *              "MinZ": 0f,
     *              "MaxX": 1f,
     *              "MaxY": 1f,
     *              "MaxZ": 1f,
     *              "PivotX": 0f,
     *              "PivotY": 0f,
     *              "PivotZ": 0f,
     *          }
     *     ]
     * }
     * </pre>
     */
    EntityDataType<CompoundTag> HITBOX = new EntityDataType<>(new CompoundTag(), "HITBOX", 118);
    EntityDataType<Boolean> IS_BUOYANT = new EntityDataType<>(false, "IS_BUOYANT", 119, Transformers.BOOLEAN);
    EntityDataType<Float> FREEZING_EFFECT_STRENGTH = new EntityDataType<>(0f, "FREEZING_EFFECT_STRENGTH", 120);
    EntityDataType<String> BUOYANCY_DATA = new EntityDataType<>("", "BUOYANCY_DATA", 121);
    EntityDataType<Integer> GOAT_HORN_COUNT = new EntityDataType<>(0, "GOAT_HORN_COUNT", 122);
    EntityDataType<String> BASE_RUNTIME_ID = new EntityDataType<>("", "BASE_RUNTIME_ID", 123);
    /**
     * @since v503
     */
    EntityDataType<Float> MOVEMENT_SOUND_DISTANCE_OFFSET = new EntityDataType<>(0f, "MOVEMENT_SOUND_DISTANCE_OFFSET", 124);
    /**
     * @since v503
     */
    EntityDataType<Integer> HEARTBEAT_INTERVAL_TICKS = new EntityDataType<>(0, "HEARTBEAT_INTERVAL_TICKS", 125);
    /**
     * @since v503
     */
    EntityDataType<Integer> HEARTBEAT_SOUND_EVENT = new EntityDataType<>(0, "HEARTBEAT_SOUND_EVENT", 126);
    /**
     * @since v527
     */
    EntityDataType<BlockVector3> PLAYER_LAST_DEATH_POS = new EntityDataType<>(new BlockVector3(), "PLAYER_LAST_DEATH_POS", 127);
    /**
     * @since v527
     */
    EntityDataType<Integer> PLAYER_LAST_DEATH_DIMENSION = new EntityDataType<>(0, "PLAYER_LAST_DEATH_DIMENSION", 128);
    /**
     * @since v527
     */
    EntityDataType<Boolean> PLAYER_HAS_DIED = new EntityDataType<>(false, "PLAYER_HAS_DIED", 129, Transformers.BOOLEAN);
    /**
     * @since v594
     */
    EntityDataType<Vector3f> COLLISION_BOX = new EntityDataType<>(new Vector3f(), "COLLISION_BOX", 130);
}
