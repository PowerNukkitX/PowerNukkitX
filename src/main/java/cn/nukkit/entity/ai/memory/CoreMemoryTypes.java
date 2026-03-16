package cn.nukkit.entity.ai.memory;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockBed;
import cn.nukkit.block.BlockWoodenDoor;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.ai.memory.codec.BooleanMemoryCodec;
import cn.nukkit.entity.ai.memory.codec.NumberMemoryCodec;
import cn.nukkit.entity.ai.memory.codec.StringMemoryCodec;
import cn.nukkit.entity.data.EntityDataTypes;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.entity.mob.EntityEvocationIllager;
import cn.nukkit.entity.passive.EntityVillagerV2;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration of memory types used by the core
 */
public interface CoreMemoryTypes {
        ///////////////////////
        /// Volatile Memory ///
        ///////////////////////

        /** Physical visual target memory */
        MemoryType<Vector3> LOOK_TARGET = new MemoryType<>("minecraft:look_target");
        /** Entity moving target memory */
        MemoryType<Vector3> MOVE_TARGET = new MemoryType<>("minecraft:move_target");

        MemoryType<Boolean> FORCE_PERCHING = new MemoryType<>("minecraft:force_perching", false);

        MemoryType<Vector3> STAY_NEARBY = new MemoryType<>("minecraft:stay_nearby");

        /** Entity movement starting point memory */
        MemoryType<Vector3> MOVE_DIRECTION_START = new MemoryType<>("minecraft:move_direction_start");
        /** Entity movement endpoint memory */
        MemoryType<Vector3> MOVE_DIRECTION_END = new MemoryType<>("minecraft:move_direction_end");
        /** Whether the entity needs to update the memory of the path */
        MemoryType<Boolean> SHOULD_UPDATE_MOVE_DIRECTION = new MemoryType<>("minecraft:should_update_move_direction", false);
        /** Whether pitch is enabled for the entity */
        MemoryType<Boolean> ENABLE_PITCH = new MemoryType<>("minecraft:enable_pitch", true);
        /** Whether the control entity activates the lift controller's memory */
        MemoryType<Boolean> ENABLE_LIFT_FORCE = new MemoryType<>("minecraft:enable_lift_force", true);
        /** Control entity to activate the memory of the dive controller */
        MemoryType<Boolean> ENABLE_DIVE_FORCE = new MemoryType<>("minecraft:enable_dive_force", true);
        // The following two are not currently in use.
        //MemoryType<Boolean> ENABLE_YAW = new MemoryType<>("minecraft:enable_yaw", true);
        //MemoryType<Boolean> ENABLE_HEAD_YAW = new MemoryType<>("minecraft:enable_head_yaw", true);
        /** Attack events resulting from the attack on an entity */
        MemoryType<EntityDamageEvent> BE_ATTACKED_EVENT = new MemoryType<>("minecraft:be_attacked_event");
        /** The last tick of the entity being attacked */
        MemoryType<Integer> LAST_BE_ATTACKED_TIME = new MemoryType<>("minecraft:last_be_attacked_time", -65536);

        MemoryType<Integer> LAST_ATTACK_TIME = new MemoryType<>("minecraft:last_attack_time", 0);

        MemoryType<Entity> LAST_ATTACK_ENTITY = new MemoryType<>("minecraft:last_attack_entity");

        MemoryType<Integer> LAST_HOGLIN_ATTACK_TIME = new MemoryType<>("minecraft:last_hoglin_attack_time", 0);

        MemoryType<Integer> NEXT_SHED = new MemoryType<>("minecraft:next_shed", 0);

        MemoryType<EntityEvocationIllager.SPELL> LAST_MAGIC = new MemoryType<>("minecraft:last_spell", EntityEvocationIllager.SPELL.NONE);

        MemoryType<Integer> LAST_GOSSIP = new MemoryType<>("minecraft:last_gossip", -65536);

        MemoryType<Object2ObjectArrayMap<String, IntArrayList>> GOSSIP = new MemoryType<>("minecraft:gossip");

        MemoryType<Integer> LAST_REFILL_SHIFT = new MemoryType<>("minecraft:last_refill_shift", -1);

        /** Entity's Attack Target */
        MemoryType<Entity> ATTACK_TARGET = new MemoryType<>("minecraft:attack_target");
        /** Whether the entity's attack target has been changed is currently only available in Warden. */
        MemoryType<Boolean> IS_ATTACK_TARGET_CHANGED = new MemoryType<>("minecraft:is_attack_target_changed", false);
        /** The entity is generated from the server tick. */
        MemoryType<Integer> ENTITY_SPAWN_TIME = new MemoryType<>("minecraft:entity_spawn_time", () -> Server.getInstance().getTick());
        /** Currently only used by creepers to control whether a creeper should explode. */
        MemoryType<Boolean> SHOULD_EXPLODE = new MemoryType<>("minecraft:should_explode", false);
        /** Currently only used in Creeper. */
        MemoryType<Boolean> EXPLODE_CANCELLABLE = new MemoryType<>("minecraft:explode_cancellable", true);
        /** Control whether the entity is reproducing */
        MemoryType<Boolean> IS_IN_LOVE = new MemoryType<>("minecraft:is_in_love", false);
        /** Last breeding time tick */
        MemoryType<Integer> LAST_IN_LOVE_TIME = new MemoryType<>("minecraft:last_in_love_time", -65536);
        /** Control whether the entity is pregnant */
        MemoryType<Boolean> IS_PREGNANT = new MemoryType<>("minecraft:is_pregnant", false);

        MemoryType<Boolean> WILLING = new MemoryType<>("minecraft:willing", false);

        MemoryType<Entity> PARENT = new MemoryType<>("minecraft:parent");

        /** The last time an egg was laid. Currently only used in Chicken. */
        MemoryType<Integer> LAST_EGG_SPAWN_TIME = new MemoryType<>("minecraft:last_egg_spawn_time", () -> Server.getInstance().getTick());
        /** Recent attack targets that meet the criteria. Typically written in NearestTargetEntitySensor */
        MemoryType<Entity> NEAREST_SUITABLE_ATTACK_TARGET = new MemoryType<>("minecraft:nearest_suitable_attack_target");
        /** Track feeding nearest player holding specific food */
        MemoryType<Player> NEAREST_FEEDING_PLAYER = new MemoryType<>("minecraft:nearest_feeding_player");
        /** Track nearest player */
        MemoryType<Player> NEAREST_PLAYER = new MemoryType<>("minecraft:nearest_player");

        MemoryType<Player> STARING_PLAYER = new MemoryType<>("minecraft:staring_player");
        /** The entity the player last attacked */
        MemoryType<Entity> ENTITY_ATTACKED_BY_OWNER = new MemoryType<>("minecraft:entity_attacked_by_owner");
        /** The entity that attacked the owner player last time */
        MemoryType<Entity> ENTITY_ATTACKING_OWNER = new MemoryType<>("minecraft:entity_attacking_owner");
        /** Last feeding time */
        MemoryType<Integer> LAST_BE_FEED_TIME = new MemoryType<>("minecraft:last_be_feed_time", -65536);
        /** The player who fed last time */
        MemoryType<Player> LAST_FEED_PLAYER = new MemoryType<>("minecraft:last_feeding_player");

        MemoryType<BlockVector3> LAST_ENDER_CRYSTAL_DESTROY = new MemoryType<>("minecraft:last_ender_crystal_destroy");

        /** Currently only used in Warden */
        MemoryType<Integer> ROUTE_UNREACHABLE_TIME = new MemoryType<>("minecraft:route_unreachable_time", 0);
        /** Entity's Breeding Memory */
        MemoryType<Entity> ENTITY_SPOUSE = new MemoryType<>("minecraft:entity_spouse");
        MemoryType<Integer> BREEDING_TICK = new MemoryType<>("minecraft:breeding_tick", 0);
        MemoryType<Boolean> IS_BREEDING = new MemoryType<>("minecraft:is_breeding", false);
        /** Currently only used in Warden */
        MemoryType<Map<Entity, Integer>> WARDEN_ANGER_VALUE = new MemoryType<>("minecraft:warden_anger_value", new HashMap<>());
        /** Track nearest skeleton entity */
        MemoryType<Entity> NEAREST_SKELETON = new MemoryType<>("minecraft:nearest_skeleton");
        /** Track nearest zombie entity */
        MemoryType<Entity> NEAREST_ZOMBIE = new MemoryType<>("minecraft:nearest_zombie");
        /** Track nearest endermite entity */
        MemoryType<Entity> NEAREST_ENDERMITE = new MemoryType<>("minecraft:nearest_endermite");
        /** Track nearest golem entity */
        MemoryType<Entity> NEAREST_GOLEM = new MemoryType<>("minecraft:nearest_golem");

        MemoryType<Entity> NEAREST_SHARED_ENTITY = new MemoryType<>("minecraft:nearest_shared_entity");

        MemoryType<Class<? extends Block>> LOOKING_BLOCK = new MemoryType<>("minecraft:looking_block");

        MemoryType<Class<? extends Item>> LOOKING_ITEM = new MemoryType<>("minecraft:looking_item");

        MemoryType<BlockBed> OCCUPIED_BED = new MemoryType<>("minecraft:occupied_bed");

        MemoryType<Block> NEAREST_BLOCK = new MemoryType<>("minecraft:nearest_block");

        MemoryType<Block> NEAREST_BLOCK_2 = new MemoryType<>("minecraft:nearest_block_2");

        MemoryType<Block> SITE_BLOCK = new MemoryType<>("minecraft:site_block");

        MemoryType<BlockWoodenDoor> NEAREST_DOOR = new MemoryType<>("minecraft:nearest_door");

        MemoryType<EntityItem> NEAREST_ITEM = new MemoryType<>("minecraft:nearest_item");

        MemoryType<EntityVillagerV2> GOSSIP_TARGET = new MemoryType<>("minecraft:gossip_target");

        MemoryType<Integer> LAST_ATTACK_CAST = new MemoryType<>("minecraft:last_attack_cast", 0);

        MemoryType<Integer> LAST_ATTACK_SUMMON = new MemoryType<>("minecraft:last_attack_summon", 0);

        MemoryType<Integer> LAST_ATTACK_DASH = new MemoryType<>("minecraft:last_attack_dash", 0);


        MemoryType<Integer> LAST_CONVERSION = new MemoryType<>("minecraft:last_conversion", 0);

        MemoryType<Integer> INVULNERABLE_TICKS = new MemoryType<>("minecraft:invulnerable_ticks", 0);

        MemoryType<Integer> LAST_SITTING_CHECK = new MemoryType<>("minecraft:last_sitting_check", 0);

        MemoryType<Integer> PIG_BOOST = new MemoryType<>("minecraft:pig_boost", 0);

        MemoryType<ObjectList<InventoryHolder>> CHESTS = new MemoryType<>("minecraft:chests", new ObjectArrayList<>());

        MemoryType<ObjectList<InventoryHolder>> COPPER_CHESTS = new MemoryType<>("minecraft:copper_chests", new ObjectArrayList<>());

        MemoryType<Integer> FORCE_WANDERING = new MemoryType<>("minecraft:force_wandering", 0);

        /** The owner of the entity */
        MemoryType<Player> OWNER = new MemoryType<>("minecraft:owner");
        /** An entity that represents riding on a certain entity. */
        MemoryType<String> RIDER_NAME = new MemoryType<String>("minecraft:rider_name");


        //////////////////////////
        //// Persistent Memory ///
        //////////////////////////

        /**
         * Represents the state of anger and is bound to {@link EntityFlag#ANGRY} <p>
         * Currently only used in Wolf.
         */
        MemoryType<Boolean> IS_ANGRY = new MemoryType<>("minecraft:is_angry", false)
                .withCodec(new BooleanMemoryCodec("Angry")
                        .onInit((data, entity) -> entity.setDataFlag(EntityFlag.ANGRY, data))
                );
        /**
         * The state representing whether an entity is sitting is bound to {@link EntityFlag#SITTING}. <p>
         * Currently only used in Wolf.
         */
        MemoryType<Boolean> IS_SITTING = new MemoryType<>("minecraft:is_sitting", false)
                .withCodec(new BooleanMemoryCodec("Sitting")
                        .onInit((data, entity) -> {
                                entity.setDataFlag(EntityFlag.SITTING, data);
                        })
                );
        /**
         * Represents the entity owner and is bound to {@link EntityFlag#TAMED} and {@link EntityDataTypes#OWNER_EID}. <p>
         * Currently only used in Wolf.
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
         * Represents a variant of the entity and is bound to {@link EntityDataTypes#VARIANT}.
         */
        MemoryType<Integer> VARIANT = new MemoryType<Integer>("minecraft:variant")
                .withCodec(new NumberMemoryCodec<Integer>("Variant")
                        .onInit((data, entity) -> {
                                if (data != null) entity.setDataProperty(EntityDataTypes.VARIANT, data);
                        })
                );
        /**
         * Represents a mark variant of the entity and is bound to {@link EntityDataTypes#MARK_VARIANT}.
         */
        MemoryType<Integer> MARK_VARIANT = new MemoryType<Integer>("minecraft:mark_variant")
                .withCodec(new NumberMemoryCodec<Integer>("MarkVariant")
                        .onInit((data, entity) -> {
                                if (data != null) entity.setDataProperty(EntityDataTypes.MARK_VARIANT, data);
                        })
                );
        /**
         * The color representing the entity is bound to {@link EntityDataTypes#COLOR}. <p>
         * Wolf collar, Cat collar, Sheep wool, Tropical Fish base color
         */
        MemoryType<Byte> COLOR = new MemoryType<Byte>("minecraft:color")
                .withCodec(new NumberMemoryCodec<Byte>("Color")
                        .onInit((data, entity) -> {
                                if (data != null) entity.setDataProperty(EntityDataTypes.COLOR, data);
                        })
                );
        /**
         * The second color representing the entity is bound to {@link EntityDataTypes#COLOR_2}. <p>
         * Tropical Fish secondary color
         */
        MemoryType<Byte> COLOR2 = new MemoryType<Byte>("minecraft:color2")
                .withCodec(new NumberMemoryCodec<Byte>("Color2")
                        .onInit((data, entity) -> {
                                if (data != null) entity.setDataProperty(EntityDataTypes.COLOR_2, data);
                        })
                );
        /**
         * Bind to {@link EntityFlag#SHEARED} <p>
         * Sheep, Snow Golem
         */
        MemoryType<Boolean> IS_SHEARED = new MemoryType<>("minecraft:is_sheared", false)
                .withCodec(new BooleanMemoryCodec("Sheared")
                        .onInit((data, entity) -> entity.setDataFlag(EntityFlag.SHEARED, data))
                );
}
