package cn.nukkit.entity;

import cn.nukkit.AdventureSettings;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.*;
import cn.nukkit.block.*;
import cn.nukkit.blockentity.BlockEntityPistonArm;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.entity.custom.CustomEntity;
import cn.nukkit.entity.custom.CustomEntityDefinition;
import cn.nukkit.entity.data.*;
import cn.nukkit.entity.item.*;
import cn.nukkit.entity.mob.*;
import cn.nukkit.entity.passive.*;
import cn.nukkit.entity.projectile.*;
import cn.nukkit.entity.provider.ClassEntityProvider;
import cn.nukkit.entity.provider.CustomEntityProvider;
import cn.nukkit.entity.provider.EntityProvider;
import cn.nukkit.entity.provider.EntityProviderWithClass;
import cn.nukkit.entity.weather.EntityLightning;
import cn.nukkit.event.Event;
import cn.nukkit.event.block.FarmLandDecayEvent;
import cn.nukkit.event.entity.*;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.EntityPortalEnterEvent.PortalType;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerInteractEvent.Action;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTotem;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.*;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.particle.ExplodeParticle;
import cn.nukkit.level.vibration.VibrationEvent;
import cn.nukkit.level.vibration.VibrationType;
import cn.nukkit.math.*;
import cn.nukkit.metadata.MetadataValue;
import cn.nukkit.metadata.Metadatable;
import cn.nukkit.nbt.tag.*;
import cn.nukkit.network.protocol.*;
import cn.nukkit.network.protocol.types.EntityLink;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.potion.Effect;
import cn.nukkit.scheduler.Task;
import cn.nukkit.utils.*;
import com.google.common.collect.Iterables;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntCollection;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

import static cn.nukkit.network.protocol.SetEntityLinkPacket.*;
import static cn.nukkit.utils.Utils.dynamic;

/**
 * @author MagicDroidX
 */
@Log4j2
@PowerNukkitDifference(since = "1.4.0.0-PN",
        info = "All DATA constants were made dynamic because they have tendency to change on Minecraft updates, " +
                "these dynamic calls will avoid the need of plugin recompilations after Minecraft updates that " +
                "shifts the data values")
public abstract class Entity extends Location implements Metadatable {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final Entity[] EMPTY_ARRAY = new Entity[0];

    public static final int NETWORK_ID = -1;
    public static final int DATA_TYPE_BYTE = 0;
    public static final int DATA_TYPE_SHORT = 1;
    public static final int DATA_TYPE_INT = 2;
    public static final int DATA_TYPE_FLOAT = 3;
    public static final int DATA_TYPE_STRING = 4;
    public static final int DATA_TYPE_NBT = 5;
    public static final int DATA_TYPE_POS = 6;
    public static final int DATA_TYPE_LONG = 7;
    public static final int DATA_TYPE_VECTOR3F = 8;
    public static final int DATA_FLAGS = dynamic(0);
    public static final int DATA_HEALTH = dynamic(1); //int (minecart/boat)
    public static final int DATA_VARIANT = dynamic(2); //int
    public static final int DATA_COLOR = dynamic(3); //byte
    public static final int DATA_COLOUR = DATA_COLOR;
    public static final int DATA_NAMETAG = dynamic(4); //string
    public static final int DATA_OWNER_EID = dynamic(5); //long
    public static final int DATA_TARGET_EID = dynamic(6); //long
    public static final int DATA_AIR = dynamic(7); //short
    public static final int DATA_POTION_COLOR = dynamic(8); //int (ARGB!)
    public static final int DATA_POTION_AMBIENT = dynamic(9); //byte
    public static final int DATA_JUMP_DURATION = dynamic(10); //long
    public static final int DATA_HURT_TIME = dynamic(11); //int (minecart/boat)
    public static final int DATA_HURT_DIRECTION = dynamic(12); //int (minecart/boat)
    public static final int DATA_PADDLE_TIME_LEFT = dynamic(13); //float
    public static final int DATA_PADDLE_TIME_RIGHT = dynamic(14); //float
    public static final int DATA_EXPERIENCE_VALUE = dynamic(15); //int (xp orb)
    public static final int DATA_DISPLAY_ITEM = dynamic(16); //int (id | (data << 16))
    public static final int DATA_DISPLAY_OFFSET = dynamic(17); //int
    public static final int DATA_HAS_DISPLAY = dynamic(18); //byte (must be 1 for minecart to show block inside)
    @Since("1.2.0.0-PN")
    public static final int DATA_SWELL = dynamic(19);
    @Since("1.2.0.0-PN")
    public static final int DATA_OLD_SWELL = dynamic(20);
    @Since("1.2.0.0-PN")
    public static final int DATA_SWELL_DIR = dynamic(21);
    @Since("1.2.0.0-PN")
    public static final int DATA_CHARGE_AMOUNT = dynamic(22);
    public static final int DATA_ENDERMAN_HELD_RUNTIME_ID = dynamic(23); //short
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final int DATA_CLIENT_EVENT = dynamic(24); //byte
    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", by = "PowerNukkit",
            reason = "Apparently this the ID 24 was reused to represent CLIENT_EVENT but Cloudburst Nukkit is still mapping it as age")
    public static final int DATA_ENTITY_AGE = dynamic(DATA_CLIENT_EVENT); //short
    public static final int DATA_PLAYER_FLAG_SLEEP = 1;
    public static final int DATA_PLAYER_FLAG_DEAD = 2;
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final int DATA_USING_ITEM = dynamic(25); //byte

    public static final int DATA_PLAYER_FLAGS = dynamic(26); //byte
    @Since("1.2.0.0-PN")
    public static final int DATA_PLAYER_INDEX = dynamic(27);
    public static final int DATA_PLAYER_BED_POSITION = dynamic(28); //block coords
    public static final int DATA_PLAYER_BUTTON_TEXT = 40;

    public static final int DATA_FIREBALL_POWER_X = dynamic(29); //float
    public static final int DATA_FIREBALL_POWER_Y = dynamic(30); //float
    public static final int DATA_FIREBALL_POWER_Z = dynamic(31); //float
    @Since("1.2.0.0-PN")
    public static final int DATA_AUX_POWER = dynamic(32); //???
    @Since("1.2.0.0-PN")
    public static final int DATA_FISH_X = dynamic(33); //float
    @Since("1.2.0.0-PN")
    public static final int DATA_FISH_Z = dynamic(34); //float
    @Since("1.2.0.0-PN")
    public static final int DATA_FISH_ANGLE = dynamic(35); //float
    public static final int DATA_POTION_AUX_VALUE = dynamic(36); //short
    public static final int DATA_LEAD_HOLDER_EID = dynamic(37); //long
    public static final int DATA_SCALE = dynamic(38); //float
    @Since("1.2.0.0-PN")
    public static final int DATA_HAS_NPC_COMPONENT = dynamic(39); //byte
    public static final int DATA_NPC_SKIN_DATA = dynamic(40); //string
    public static final int DATA_NPC_ACTIONS = dynamic(41); //string
    public static final int DATA_MAX_AIR = dynamic(42); //short
    public static final int DATA_MARK_VARIANT = dynamic(43); //int
    public static final int DATA_CONTAINER_TYPE = dynamic(44); //byte
    public static final int DATA_CONTAINER_BASE_SIZE = dynamic(45); //int
    public static final int DATA_CONTAINER_EXTRA_SLOTS_PER_STRENGTH = dynamic(46); //int
    public static final int DATA_BLOCK_TARGET = dynamic(47); //block coords (ender crystal)
    public static final int DATA_WITHER_INVULNERABLE_TICKS = dynamic(48); //int
    public static final int DATA_WITHER_TARGET_1 = dynamic(49); //long
    public static final int DATA_WITHER_TARGET_2 = dynamic(50); //long
    public static final int DATA_WITHER_TARGET_3 = dynamic(51); //long
    @Since("1.2.0.0-PN")
    public static final int DATA_AERIAL_ATTACK = dynamic(52);
    public static final int DATA_BOUNDING_BOX_WIDTH = dynamic(53); //float
    public static final int DATA_BOUNDING_BOX_HEIGHT = dynamic(54); //float
    public static final int DATA_FUSE_LENGTH = dynamic(55); //int
    public static final int DATA_RIDER_SEAT_POSITION = dynamic(56); //vector3f
    public static final int DATA_RIDER_ROTATION_LOCKED = dynamic(57); //byte
    public static final int DATA_RIDER_MAX_ROTATION = dynamic(58); //float
    public static final int DATA_RIDER_MIN_ROTATION = dynamic(59); //float
    @Since("1.4.0.0-PN")
    public static final int DATA_RIDER_ROTATION_OFFSET = dynamic(60);
    public static final int DATA_AREA_EFFECT_CLOUD_RADIUS = dynamic(61); //float
    public static final int DATA_AREA_EFFECT_CLOUD_WAITING = dynamic(62); //int
    public static final int DATA_AREA_EFFECT_CLOUD_PARTICLE_ID = dynamic(63); //int
    @Since("1.2.0.0-PN")
    public static final int DATA_SHULKER_PEEK_ID = dynamic(64); //int
    public static final int DATA_SHULKER_ATTACH_FACE = dynamic(65); //byte
    @Since("1.2.0.0-PN")
    public static final int DATA_SHULKER_ATTACHED = dynamic(66); //short
    public static final int DATA_SHULKER_ATTACH_POS = dynamic(67); //block coords
    public static final int DATA_TRADING_PLAYER_EID = dynamic(68); //long
    @Since("1.2.0.0-PN")
    public static final int DATA_TRADING_CAREER = dynamic(69);
    @Since("1.2.0.0-PN")
    public static final int DATA_HAS_COMMAND_BLOCK = dynamic(70); //byte
    @Since("1.2.0.0-PN")
    public static final int DATA_COMMAND_BLOCK_COMMAND = dynamic(71); //string
    public static final int DATA_COMMAND_BLOCK_LAST_OUTPUT = dynamic(72); //string
    public static final int DATA_COMMAND_BLOCK_TRACK_OUTPUT = dynamic(73); //byte
    public static final int DATA_CONTROLLING_RIDER_SEAT_NUMBER = dynamic(74); //byte
    public static final int DATA_STRENGTH = dynamic(75); //int
    public static final int DATA_MAX_STRENGTH = dynamic(76); //int
    @Since("1.2.0.0-PN")
    public static final int DATA_SPELL_CASTING_COLOR = dynamic(77); //int
    public static final int DATA_LIMITED_LIFE = dynamic(78); //int
    public static final int DATA_ARMOR_STAND_POSE_INDEX = dynamic(79); //int
    public static final int DATA_ENDER_CRYSTAL_TIME_OFFSET = dynamic(80); //int
    public static final int DATA_ALWAYS_SHOW_NAMETAG = dynamic(81); //byte
    public static final int DATA_COLOR_2 = dynamic(82); //byte
    @Since("1.2.0.0-PN")
    public static final int DATA_NAME_AUTHOR = dynamic(83);
    public static final int DATA_SCORE_TAG = dynamic(84); //String
    public static final int DATA_BALLOON_ATTACHED_ENTITY = dynamic(85); //long
    public static final int DATA_PUFFERFISH_SIZE = dynamic(86); //byte
    @Since("1.2.0.0-PN")
    public static final int DATA_BUBBLE_TIME = dynamic(87); //int
    @Since("1.2.0.0-PN")
    public static final int DATA_AGENT = dynamic(88); //long
    @Since("1.2.0.0-PN")
    public static final int DATA_SITTING_AMOUNT = dynamic(89); //??
    @Since("1.2.0.0-PN")
    public static final int DATA_SITTING_AMOUNT_PREVIOUS = dynamic(90); //??
    @Since("1.2.0.0-PN")
    public static final int DATA_EATING_COUNTER = dynamic(91); //int
    public static final int DATA_FLAGS_EXTENDED = dynamic(92); //flags
    @Since("1.2.0.0-PN")
    public static final int DATA_LAYING_AMOUNT = dynamic(93); //??
    @Since("1.2.0.0-PN")
    public static final int DATA_LAYING_AMOUNT_PREVIOUS = dynamic(94); //??
    @Since("1.2.0.0-PN")
    public static final int DATA_DURATION = dynamic(95); //int
    @Since("1.2.0.0-PN")
    public static final int DATA_SPAWN_TIME = dynamic(96); //int
    @Since("1.2.0.0-PN")
    public static final int DATA_CHANGE_RATE = dynamic(97); //float
    @Since("1.2.0.0-PN")
    public static final int DATA_CHANGE_ON_PICKUP = dynamic(98); //float
    @Since("1.2.0.0-PN")
    public static final int DATA_PICKUP_COUNT = dynamic(99); //int
    @Since("1.4.0.0-PN")
    public static final int DATA_INTERACTIVE_TAG = dynamic(100); //string (button text)
    @PowerNukkitOnly("Removed from Cloudburst Nukkit")
    @Deprecated
    @DeprecationDetails(by = "Cloudburst Nukkit", reason = "Duplicated and removed", replaceWith = "DATA_INTERACTIVE_TAG", since = "FUTURE")
    @Since("1.2.0.0-PN")
    public static final int DATA_INTERACT_TEXT = dynamic(DATA_INTERACTIVE_TAG); //string
    public static final int DATA_TRADE_TIER = dynamic(101); //int 这个没啥用
    public static final int DATA_MAX_TRADE_TIER = dynamic(102); //int 这个控制村民最大等级
    @Since("1.2.0.0-PN")
    public static final int DATA_TRADE_EXPERIENCE = dynamic(103); //int这个控制当前经验
    @Since("1.1.1.0-PN")
    public static final int DATA_SKIN_ID = dynamic(104); //int
    @Since("1.2.0.0-PN")
    public static final int DATA_SPAWNING_FRAMES = dynamic(105); //??
    @Since("1.2.0.0-PN")
    public static final int DATA_COMMAND_BLOCK_TICK_DELAY = dynamic(106); //int
    @Since("1.2.0.0-PN")
    public static final int DATA_COMMAND_BLOCK_EXECUTE_ON_FIRST_TICK = dynamic(107); //byte
    @Since("1.2.0.0-PN")
    public static final int DATA_AMBIENT_SOUND_INTERVAL = dynamic(108); //float
    @Since("1.3.0.0-PN")
    public static final int DATA_AMBIENT_SOUND_INTERVAL_RANGE = dynamic(109); //float
    @Since("1.2.0.0-PN")
    public static final int DATA_AMBIENT_SOUND_EVENT_NAME = dynamic(110); //string
    @Since("1.2.0.0-PN")
    public static final int DATA_FALL_DAMAGE_MULTIPLIER = dynamic(111); //float
    @Since("1.2.0.0-PN")
    public static final int DATA_NAME_RAW_TEXT = dynamic(112); //string
    @Since("1.2.0.0-PN")
    public static final int DATA_CAN_RIDE_TARGET = dynamic(113); //byte
    @Since("1.3.0.0-PN")
    public static final int DATA_LOW_TIER_CURED_DISCOUNT = dynamic(114); //int
    @Since("1.3.0.0-PN")
    public static final int DATA_HIGH_TIER_CURED_DISCOUNT = dynamic(115); //int
    @Since("1.3.0.0-PN")
    public static final int DATA_NEARBY_CURED_DISCOUNT = dynamic(116); //int
    @Since("1.3.0.0-PN")
    public static final int DATA_NEARBY_CURED_DISCOUNT_TIMESTAMP = dynamic(117); //int
    @Since("1.3.0.0-PN")
    public static final int DATA_HITBOX = dynamic(118); //NBT
    @Since("1.3.0.0-PN")
    public static final int DATA_IS_BUOYANT = dynamic(119); //byte
    @Since("1.4.0.0-PN")
    public static final int DATA_FREEZING_EFFECT_STRENGTH = dynamic(120); //float
    @Since("1.3.0.0-PN")
    public static final int DATA_BUOYANCY_DATA = dynamic(121); //string
    @Since("1.4.0.0-PN")
    public static final int DATA_GOAT_HORN_COUNT = dynamic(122); // ???
    @Since("1.5.0.0-PN")
    public static final int DATA_BASE_RUNTIME_ID = dynamic(123); // ???
    public static final int DATA_MOVEMENT_SOUND_DISTANCE_OFFSET = dynamic(124); // ???
    //Deprecated
    //@Since("1.5.0.0-PN")
    //public static final int DATA_UPDATE_PROPERTIES = dynamic(124); // ???
    public static final int DATA_HEARTBEAT_INTERVAL_TICKS = dynamic(125); // ???
    public static final int DATA_HEARTBEAT_SOUND_EVENT = dynamic(126); // ???
    @Since("1.19.40-r3")
    public static final int DATA_PLAYER_LAST_DEATH_POS = 127;// ???
    @Since("1.19.40-r3")
    public static final int DATA_PLAYER_LAST_DEATH_DIMENSION = 128;// ???
    @Since("1.19.40-r3")
    public static final int DATA_PLAYER_HAS_DIED = 129;// ???

    @Since("1.20.10-r1")
    public static final int DATA_COLLISION_BOX = 130; //vector3f

    // Flags
    public static final int DATA_FLAG_ONFIRE = dynamic(0);
    public static final int DATA_FLAG_SNEAKING = dynamic(1);
    public static final int DATA_FLAG_RIDING = dynamic(2);
    public static final int DATA_FLAG_SPRINTING = dynamic(3);
    public static final int DATA_FLAG_ACTION = dynamic(4);
    public static final int DATA_FLAG_INVISIBLE = dynamic(5);
    public static final int DATA_FLAG_TEMPTED = dynamic(6);
    public static final int DATA_FLAG_INLOVE = dynamic(7);
    public static final int DATA_FLAG_SADDLED = dynamic(8);
    public static final int DATA_FLAG_POWERED = dynamic(9);
    public static final int DATA_FLAG_IGNITED = dynamic(10);
    public static final int DATA_FLAG_BABY = dynamic(11); //disable head scaling
    public static final int DATA_FLAG_CONVERTING = dynamic(12);
    public static final int DATA_FLAG_CRITICAL = dynamic(13);
    public static final int DATA_FLAG_CAN_SHOW_NAMETAG = dynamic(14);
    public static final int DATA_FLAG_ALWAYS_SHOW_NAMETAG = dynamic(15);
    public static final int DATA_FLAG_IMMOBILE = dynamic(16);
    public static final int DATA_FLAG_NO_AI = DATA_FLAG_IMMOBILE;
    public static final int DATA_FLAG_SILENT = dynamic(17);
    public static final int DATA_FLAG_WALLCLIMBING = dynamic(18);
    public static final int DATA_FLAG_CAN_CLIMB = dynamic(19);
    public static final int DATA_FLAG_SWIMMER = dynamic(20);
    public static final int DATA_FLAG_CAN_FLY = dynamic(21);
    public static final int DATA_FLAG_WALKER = dynamic(22);
    public static final int DATA_FLAG_RESTING = dynamic(23);
    public static final int DATA_FLAG_SITTING = dynamic(24);
    public static final int DATA_FLAG_ANGRY = dynamic(25);
    public static final int DATA_FLAG_INTERESTED = dynamic(26);
    public static final int DATA_FLAG_CHARGED = dynamic(27);
    public static final int DATA_FLAG_TAMED = dynamic(28);
    public static final int DATA_FLAG_ORPHANED = dynamic(29);
    public static final int DATA_FLAG_LEASHED = dynamic(30);
    public static final int DATA_FLAG_SHEARED = dynamic(31);
    public static final int DATA_FLAG_GLIDING = dynamic(32);
    public static final int DATA_FLAG_ELDER = dynamic(33);
    public static final int DATA_FLAG_MOVING = dynamic(34);
    public static final int DATA_FLAG_BREATHING = dynamic(35);
    public static final int DATA_FLAG_CHESTED = dynamic(36);
    public static final int DATA_FLAG_STACKABLE = dynamic(37);
    public static final int DATA_FLAG_SHOWBASE = dynamic(38);
    //STANDING
    public static final int DATA_FLAG_REARING = dynamic(39);
    public static final int DATA_FLAG_VIBRATING = dynamic(40);
    public static final int DATA_FLAG_IDLING = dynamic(41);
    public static final int DATA_FLAG_EVOKER_SPELL = dynamic(42);
    public static final int DATA_FLAG_CHARGE_ATTACK = dynamic(43);
    public static final int DATA_FLAG_WASD_CONTROLLED = dynamic(44);
    public static final int DATA_FLAG_CAN_POWER_JUMP = dynamic(45);
    public static final int DATA_FLAG_CAN_DASH = dynamic(46);

    public static final int DATA_FLAG_LINGER = dynamic(47);
    public static final int DATA_FLAG_HAS_COLLISION = dynamic(48);
    public static final int DATA_FLAG_GRAVITY = dynamic(49);
    public static final int DATA_FLAG_FIRE_IMMUNE = dynamic(50);
    public static final int DATA_FLAG_DANCING = dynamic(51);
    public static final int DATA_FLAG_ENCHANTED = dynamic(52);
    public static final int DATA_FLAG_SHOW_TRIDENT_ROPE = dynamic(53); // tridents show an animated rope when enchanted with loyalty after they are thrown and return to their owner. To be combined with DATA_OWNER_EID
    public static final int DATA_FLAG_CONTAINER_PRIVATE = dynamic(54); //inventory is private, doesn't drop contents when killed if true
    @Since("1.2.0.0-PN")
    public static final int DATA_FLAG_IS_TRANSFORMING = dynamic(55);
    public static final int DATA_FLAG_SPIN_ATTACK = dynamic(56);
    public static final int DATA_FLAG_SWIMMING = dynamic(57);
    public static final int DATA_FLAG_BRIBED = dynamic(58); //dolphins have this set when they go to find treasure for the player
    public static final int DATA_FLAG_PREGNANT = dynamic(59);
    public static final int DATA_FLAG_LAYING_EGG = dynamic(60);
    @Since("1.2.0.0-PN")
    public static final int DATA_FLAG_RIDER_CAN_PICK = dynamic(61);
    @PowerNukkitOnly
    @Since("1.2.0.0-PN")
    public static final int DATA_FLAG_TRANSITION_SITTING = dynamic(62); // PowerNukkit but without typo
    /**
     * @see #DATA_FLAG_TRANSITION_SITTING
     * @deprecated This is from NukkitX but it has a typo which we can't remove unless NukkitX removes from their side.
     */
    @Deprecated
    @DeprecationDetails(
            reason = "This is from NukkitX but it has a typo which we can't remove unless NukkitX removes from their side.",
            since = "1.2.0.0-PN",
            replaceWith = "DATA_FLAG_TRANSITION_SITTING")
    @Since("1.2.0.0-PN")
    public static final int DATA_FLAG_TRANSITION_SETTING = DATA_FLAG_TRANSITION_SITTING; // NukkitX with the same typo
    public static final int DATA_FLAG_EATING = dynamic(63);
    public static final int DATA_FLAG_LAYING_DOWN = dynamic(64);
    public static final int DATA_FLAG_SNEEZING = dynamic(65);
    public static final int DATA_FLAG_TRUSTING = dynamic(66);
    public static final int DATA_FLAG_ROLLING = dynamic(67);
    public static final int DATA_FLAG_SCARED = dynamic(68);
    public static final int DATA_FLAG_IN_SCAFFOLDING = dynamic(69);
    public static final int DATA_FLAG_OVER_SCAFFOLDING = dynamic(70);
    public static final int DATA_FLAG_FALL_THROUGH_SCAFFOLDING = dynamic(71);
    public static final int DATA_FLAG_BLOCKING = dynamic(72); //shield
    @Since("1.2.0.0-PN")
    public static final int DATA_FLAG_TRANSITION_BLOCKING = dynamic(73);
    @Since("1.2.0.0-PN")
    public static final int DATA_FLAG_BLOCKED_USING_SHIELD = dynamic(74);
    @Since("1.2.0.0-PN")
    public static final int DATA_FLAG_BLOCKED_USING_DAMAGED_SHIELD = dynamic(75);
    @Since("1.2.0.0-PN")
    public static final int DATA_FLAG_SLEEPING = dynamic(76);
    @Since("FUTURE")
    public static final int DATA_FLAG_ENTITY_GROW_UP = dynamic(77);
    @Since("1.2.0.0-PN")
    public static final int DATA_FLAG_TRADE_INTEREST = dynamic(78);
    @Since("1.2.0.0-PN")
    public static final int DATA_FLAG_DOOR_BREAKER = dynamic(79);
    @Since("1.2.0.0-PN")
    public static final int DATA_FLAG_BREAKING_OBSTRUCTION = dynamic(80);
    @Since("1.2.0.0-PN")
    public static final int DATA_FLAG_DOOR_OPENER = dynamic(81);
    @Since("1.2.0.0-PN")
    public static final int DATA_FLAG_IS_ILLAGER_CAPTAIN = dynamic(82);
    @Since("1.2.0.0-PN")
    public static final int DATA_FLAG_STUNNED = dynamic(83);
    @Since("1.2.0.0-PN")
    public static final int DATA_FLAG_ROARING = dynamic(84);
    @Since("1.2.0.0-PN")
    public static final int DATA_FLAG_DELAYED_ATTACK = dynamic(85);
    @Since("1.2.0.0-PN")
    public static final int DATA_FLAG_IS_AVOIDING_MOBS = dynamic(86);
    @Since("1.3.0.0-PN")
    public static final int DATA_FLAG_IS_AVOIDING_BLOCKS = dynamic(87);
    @Since("1.2.0.0-PN")
    public static final int DATA_FLAG_FACING_TARGET_TO_RANGE_ATTACK = dynamic(88);
    @Since("1.2.0.0-PN")
    public static final int DATA_FLAG_HIDDEN_WHEN_INVISIBLE = dynamic(89);
    @Since("1.2.0.0-PN")
    public static final int DATA_FLAG_IS_IN_UI = dynamic(90);
    @Since("1.2.0.0-PN")
    public static final int DATA_FLAG_STALKING = dynamic(91);
    @Since("1.2.0.0-PN")
    public static final int DATA_FLAG_EMOTING = dynamic(92);
    @Since("1.2.0.0-PN")
    public static final int DATA_FLAG_CELEBRATING = dynamic(93);
    @Since("1.3.0.0-PN")
    public static final int DATA_FLAG_ADMIRING = dynamic(94);
    @Since("1.3.0.0-PN")
    public static final int DATA_FLAG_CELEBRATING_SPECIAL = dynamic(95);
    @Since("1.4.0.0-PN")
    public static final int DATA_FLAG_RAM_ATTACK = dynamic(97);
    @Since("1.5.0.0-PN")
    public static final int DATA_FLAG_PLAYING_DEAD = dynamic(98);
    @Since("FUTURE")
    public static final int DATA_FLAG_IN_ASCENDABLE_BLOCK = dynamic(99);
    @Since("FUTURE")
    public static final int DATA_FLAG_OVER_DESCENDABLE_BLOCK = dynamic(100);
    @Since("1.6.0.0-PNX")
    public static final int DATA_FLAG_CROAKING = dynamic(101);
    @Since("1.6.0.0-PNX")
    public static final int DATA_FLAG_EAT_MOB = dynamic(102);
    @Since("1.6.0.0-PNX")
    public static final int DATA_FLAG_JUMP_GOAL_JUMP = dynamic(103);
    @Since("1.6.0.0-PNX")
    public static final int DATA_FLAG_EMERGING = dynamic(104);
    @Since("1.6.0.0-PNX")
    public static final int DATA_FLAG_SNIFFING = dynamic(105);
    @Since("1.6.0.0-PNX")
    public static final int DATA_FLAG_DIGGING = dynamic(106);
    @Since("1.19.21-r4")
    public static final int DATA_FLAG_SONIC_BOOM = dynamic(107);
    @Since("1.19.50-r1")
    public static final int DATA_FLAG_HAS_DASH_COOLDOWN = dynamic(108);
    @Since("1.19.50-r1")
    public static final int DATA_FLAG_PUSH_TOWARDS_CLOSEST_SPACE = dynamic(109);
    @Since("1.19.70-r1")
    public static final int DATA_FLAG_SCENTING = dynamic(110);
    @Since("1.19.70-r1")
    public static final int DATA_FLAG_RISING = dynamic(111);
    @Since("1.19.70-r1")
    public static final int DATA_FLAG_FEELING_HAPPY = dynamic(112);
    @Since("1.19.70-r1")
    public static final int DATA_FLAG_SEARCHING = dynamic(113);
    @Since("1.20.10-r1")
    public static final int DATA_FLAG_CRAWLING = dynamic(114);

    private static final Set<CustomEntityDefinition> entityDefinitions = new HashSet<>();
    private static final Map<String, EntityProvider<? extends Entity>> knownEntities = new HashMap<>();
    private static final Map<String, String> shortNames = new HashMap<>();
    public static long entityCount = 1;
    public final List<Entity> passengers = new ArrayList<>();
    public final AxisAlignedBB offsetBoundingBox = new SimpleAxisAlignedBB(0, 0, 0, 0, 0, 0);
    protected final Map<Integer, Player> hasSpawned = new ConcurrentHashMap<>();
    protected final Map<Integer, Effect> effects = new ConcurrentHashMap<>();
    protected final EntityMetadata dataProperties = new EntityMetadata()
            .putLong(DATA_FLAGS, 0)
            .putByte(DATA_COLOR, 0)
            .putShort(DATA_AIR, 400)
            .putShort(DATA_MAX_AIR, 400)
            .putString(DATA_NAMETAG, "")
            .putLong(DATA_LEAD_HOLDER_EID, -1)
            .putFloat(DATA_SCALE, 1f);
    /**
     * 这个实体骑在谁身上
     * <p>
     * Who is this entity riding on
     */
    public Entity riding = null;
    public FullChunk chunk;
    public List<Block> blocksAround = new ArrayList<>();
    public List<Block> collisionBlocks = new ArrayList<>();
    public double lastX;
    public double lastY;
    public double lastZ;
    public boolean firstMove = true;
    public double motionX;
    public double motionY;
    public double motionZ;
    /**
     * 临时向量，其值没有任何含义
     */
    public Vector3 temporalVector;
    public double lastMotionX;
    public double lastMotionY;
    public double lastMotionZ;
    public double lastPitch;
    @Since("FUTURE")
    public double lastYaw;
    @Since("FUTURE")
    public double lastHeadYaw;
    public double pitchDelta;
    @Since("FUTURE")
    public double yawDelta;
    @Since("FUTURE")
    public double headYawDelta;
    public double entityCollisionReduction = 0; // Higher than 0.9 will result a fast collisions
    public AxisAlignedBB boundingBox;
    public boolean onGround;
    public boolean inBlock = false;
    public boolean positionChanged;
    public boolean motionChanged;
    public int deadTicks = 0;
    /**
     * Player do not use
     */
    public boolean keepMovement = false;
    public float fallDistance = 0;
    public int ticksLived = 0;
    public int lastUpdate;
    public int maxFireTicks;
    public int fireTicks = 0;
    public int inPortalTicks = 0;
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public int freezingTicks = 0;//0 - 140
    public float scale = 1;
    public CompoundTag namedTag;
    public boolean isCollided = false;
    public boolean isCollidedHorizontally = false;
    public boolean isCollidedVertically = false;
    public int noDamageTicks;
    public boolean justCreated;
    public boolean fireProof;
    public boolean invulnerable;
    public double highestPosition;
    public boolean closed = false;
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean noClip = false;
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    //spawned by server
    //player's UUID is sent by client,so this value cannot be used in Player
    protected UUID entityUniqueId;
    //runtime id (changed after you restart the server)
    protected long id;
    protected EntityDamageEvent lastDamageCause = null;
    protected int age = 0;
    protected float health = 20;
    protected float absorption = 0;
    /**
     * Player do not use
     */
    protected float ySize = 0;
    @PowerNukkitOnly
    @Since("1.2.1.0-PN")
    protected boolean inEndPortal;
    protected boolean isStatic = false;
    protected Server server;
    protected boolean isPlayer = this instanceof Player;
    private int maxHealth = 20;
    private volatile boolean initialized;

    @PowerNukkitXOnly
    @Since("1.20.10-r1")
    protected volatile boolean saveWithChunk = true;

    public Entity(FullChunk chunk, CompoundTag nbt) {
        if (this instanceof Player) {
            return;
        }

        this.init(chunk, nbt);
    }


    /**
     * 从mc标准实体标识符创建实体，形如(minecraft:sheep)
     * <p>
     * Create an entity from the entity identifier, like (minecraft:sheep)
     *
     * @param identifier the identifier
     * @param pos        the pos
     * @param args       the args
     * @return the entity
     */
    @PowerNukkitXOnly
    @Since("1.19.60-r1")
    @Nullable
    public static Entity createEntity(Identifier identifier, @NotNull Position pos, @Nullable Object... args) {
        Integer id = EntityIds.IDENTIFIER_2_IDS.get(identifier.toString());
        String name;
        if (id == null) {
            name = identifier.toString();
        } else name = id.toString();
        return createEntity(name, pos, args);
    }

    /**
     * 创建一个实体从实体名,名称从{@link Entity#init registerEntities}源代码查询
     * <p>
     * Create an entity from entity name, name from {@link Entity#init registerEntities} source code query
     *
     * @param name the name
     * @param pos  the pos
     * @param args the args
     * @return the entity
     */
    @Nullable
    public static Entity createEntity(@NotNull String name, @NotNull Position pos, @Nullable Object... args) {
        return createEntity(name, Objects.requireNonNull(pos.getChunk()), getDefaultNBT(pos), args);
    }

    /**
     * 创建一个实体从网络id
     * <p>
     * Create an entity from the network id
     *
     * @param type 网络ID<br> network id
     * @param pos  the pos
     * @param args the args
     * @return the entity
     */
    @Nullable
    public static Entity createEntity(int type, @NotNull Position pos, @Nullable Object... args) {
        return createEntity(String.valueOf(type), Objects.requireNonNull(pos.getChunk()), getDefaultNBT(pos), args);
    }

    /**
     * 创建一个实体从网络id
     * <p>
     * Create an entity from the network id
     *
     * @param type  网络ID<br> network id
     * @param chunk the chunk
     * @param nbt   the nbt
     * @param args  the args
     * @return the entity
     */
    @Nullable
    public static Entity createEntity(int type, @NotNull FullChunk chunk, @NotNull CompoundTag nbt, @Nullable Object... args) {
        return createEntity(String.valueOf(type), chunk, nbt, args);
    }

    /**
     * @see #registerEntity(String, Class<? extends Entity>, boolean)
     */
    @Nullable
    public static Entity createEntity(@NotNull String name, @NotNull FullChunk chunk, @NotNull CompoundTag nbt, @Nullable Object... args) {
        var provider = knownEntities.get(name);
        if (provider != null) {
            return provider.provideEntity(chunk, nbt, args);
        }
        return null;
    }

    /**
     * @see #registerEntity(String, Class<? extends Entity>, boolean)
     */
    public static boolean registerEntity(String name, Class<? extends Entity> clazz) {
        return registerEntity(name, clazz, false);
    }

    /**
     * 注册一个实体，仅供内部使用，自定义实体请使用{@link #registerCustomEntity registerCustomEntity}
     * <p>
     * Register an entity for internal use only, for custom entities please use {@link #registerCustomEntity registerCustomEntity}
     *
     * @param name  the name
     * @param clazz the clazz
     * @param force the force
     * @return the boolean
     */
    @PowerNukkitXDifference(since = "1.19.21-r1", info = "Use internal provider instead.")
    public static boolean registerEntity(String name, Class<? extends Entity> clazz, boolean force) {
        if (clazz == null) {
            return false;
        }

        EntityProvider<?> provider;
        try {
            int networkId = clazz.getField("NETWORK_ID").getInt(null);
            provider = new ClassEntityProvider(name, clazz, networkId);
            knownEntities.put(String.valueOf(networkId), provider);
        } catch (Exception e) {
            provider = new ClassEntityProvider(name, clazz, Entity.NETWORK_ID);
            if (!force) {
                return false;
            }
        }

        knownEntities.put(name, provider);
        shortNames.put(clazz.getSimpleName(), name);
        return true;
    }

    /**
     * @see #registerEntity(EntityProvider<? extends Entity>, boolean)
     */
    @PowerNukkitXOnly
    @Since("1.19.21-r2")
    public static boolean registerEntity(EntityProvider<? extends Entity> provider) {
        return registerEntity(provider, false);
    }

    /**
     * 注册一个实体，仅供内部使用，自定义实体请使用{@link #registerCustomEntity registerCustomEntity}
     * <p>
     * Register an entity for internal use only, for custom entities please use {@link #registerCustomEntity registerCustomEntity}
     *
     * @param provider the provider
     * @param force    the force
     * @return the boolean
     */
    @PowerNukkitXOnly
    @Since("1.19.21-r2")
    public static boolean registerEntity(EntityProvider<? extends Entity> provider, boolean force) {
        if (provider == null) {
            return false;
        }

        if (provider.getNetworkId() != Entity.NETWORK_ID) {
            knownEntities.put(String.valueOf(provider.getNetworkId()), provider);
        } else {
            if (!force) {
                return false;
            }
        }

        knownEntities.put(provider.getName(), provider);
        shortNames.put(provider.getSimpleName(), provider.getName());
        return true;
    }

    /**
     * 获取全部自定义实体定义的拷贝
     * <p>
     * Get a copy of all custom entity definitions
     *
     * @return the entity definitions
     */
    @PowerNukkitXOnly
    public static Set<CustomEntityDefinition> getEntityDefinitions() {
        return new HashSet<>(entityDefinitions);
    }

    /**
     * 注册一个自定义实体
     * <p>
     * Register a custom entity
     *
     * @param customEntityProvider the custom entity provider
     * @return the ok
     */
    @PowerNukkitXOnly
    @Since("1.19.21-r2")
    public static OK<?> registerCustomEntity(CustomEntityProvider customEntityProvider) {
        if (!Server.getInstance().isEnableExperimentMode() || Server.getInstance().getConfig("settings.waterdogpe", false)) {
            return new OK<>(false, "The server does not have the experiment mode feature enabled.Unable to register custom entity!");
        }
        entityDefinitions.add(customEntityProvider.getCustomEntityDefinition());
        return new OK<Void>(registerEntity(customEntityProvider, true));
    }

    /**
     * 获得全部实体的网络id
     * <p>
     * Get the network id of all entities
     *
     * @return the known entity ids
     */
    @NotNull
    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    public static IntCollection getKnownEntityIds() {
        return knownEntities.keySet().stream()
                .filter(Utils::isInteger)
                .mapToInt(Integer::parseInt)
                .collect(IntArrayList::new, IntArrayList::add, IntArrayList::addAll);
    }

    /**
     * 获取全部已经注册的实体，包括自定义实体
     * <p>
     * Get all registered entities, including custom entities
     *
     * @return the known entities
     */
    @NotNull
    @PowerNukkitXOnly
    @Since("1.19.20-r4")
    @Deprecated
    public static Map<String, Class<? extends Entity>> getKnownEntities() {
        return knownEntities.entrySet().stream()
                .filter(e -> e.getValue() instanceof EntityProviderWithClass)
                .map(e -> new OldStringClass(e.getKey(), ((EntityProviderWithClass) e.getValue()).getEntityClass()))
                .collect(Collectors.toMap(OldStringClass::key, OldStringClass::value));
    }

    @NotNull
    @PowerNukkitXOnly
    @Since("1.19.20-r4")
    @Deprecated
    public static Map<String, EntityProvider<? extends Entity>> getKnownEntityProviders() {
        return Collections.unmodifiableMap(knownEntities);
    }

    /**
     * 获取全部已经注册的实体提供者
     * <p>
     * Get all registered entity providers
     *
     * @return the known entity providers
     */
    @NotNull
    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    public static List<String> getSaveIds() {
        return new ArrayList<>(shortNames.values());
    }

    /**
     * 从key id 查询该实体的网络id,{@link #knownEntities}
     * <p>
     * Query the network id of the entity from the key id,{@link #knownEntities}
     *
     * @param id the id
     * @return the save id
     */
    @NotNull
    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    public static OptionalInt getSaveId(String id) {
        var entityProvider = knownEntities.get(id);
        if (entityProvider == null) {
            return OptionalInt.empty();
        }
        return knownEntities.entrySet().stream()
                .filter(entry -> entry.getValue().equals(entityProvider))
                .map(Map.Entry::getKey)
                .filter(Utils::isInteger)
                .mapToInt(Integer::parseInt)
                .findFirst();
    }

    /**
     * 从网络id 查询该实体的Name,对应{@link #knownEntities} key
     * <p>
     * Query the name of the entity from its network id, corresponding to {@link #knownEntities} key
     *
     * @param id the id
     * @return the save id
     */
    @Nullable
    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    public static String getSaveId(int id) {
        var entityProvider = knownEntities.get(Integer.toString(id));
        if (entityProvider == null) {
            return null;
        }
        return shortNames.get(entityProvider.getSimpleName());
    }

    /**
     * 获取指定网络id实体的标识符
     * <p>
     * Get the identifier of the specified network id entity
     *
     * @return the identifier
     */
    @Nullable
    public static Identifier getIdentifier(int networkID) {
        var str = AddEntityPacket.LEGACY_IDS.get(networkID);
        if (str == null) return null;
        return new Identifier(str);
    }

    /**
     * @see #getDefaultNBT(Vector3, Vector3, float, float)
     */
    @NotNull
    public static CompoundTag getDefaultNBT(@NotNull Vector3 pos) {
        return getDefaultNBT(pos, null);
    }

    @NotNull
    public static CompoundTag getDefaultNBT(@NotNull Vector3 pos, @Nullable Vector3 motion) {
        Location loc = pos instanceof Location ? (Location) pos : null;

        if (loc != null) {
            return getDefaultNBT(pos, motion, (float) loc.getYaw(), (float) loc.getPitch());
        }

        return getDefaultNBT(pos, motion, 0, 0);
    }

    /**
     * 获得该实体的默认NBT，带有位置,motion，yaw pitch等信息
     * <p>
     * Get the default NBT of the entity, with information such as position, motion, yaw pitch, etc.
     *
     * @param pos    the pos
     * @param motion the motion
     * @param yaw    the yaw
     * @param pitch  the pitch
     * @return the default nbt
     */
    @NotNull
    public static CompoundTag getDefaultNBT(@NotNull Vector3 pos, @Nullable Vector3 motion, float yaw, float pitch) {
        return new CompoundTag()
                .putList(new ListTag<DoubleTag>("Pos")
                        .add(new DoubleTag("", pos.x))
                        .add(new DoubleTag("", pos.y))
                        .add(new DoubleTag("", pos.z)))
                .putList(new ListTag<DoubleTag>("Motion")
                        .add(new DoubleTag("", motion != null ? motion.x : 0))
                        .add(new DoubleTag("", motion != null ? motion.y : 0))
                        .add(new DoubleTag("", motion != null ? motion.z : 0)))
                .putList(new ListTag<FloatTag>("Rotation")
                        .add(new FloatTag("", yaw))
                        .add(new FloatTag("", pitch)));
    }

    /**
     * Batch play animation on entity groups<br/>
     * This method is recommended if you need to play the same animation on a large number of entities at the same time, as it only sends packets once for each player, which greatly reduces bandwidth pressure
     * <p>
     * 在实体群上批量播放动画<br/>
     * 若你需要同时在大量实体上播放同一动画，建议使用此方法，因为此方法只会针对每个玩家发送一次包，这能极大地缓解带宽压力
     *
     * @param animation 动画对象 Animation objects
     * @param entities  需要播放动画的实体群 Group of entities that need to play animations
     * @param players   可视玩家 Visible Player
     */
    @PowerNukkitXOnly
    @Since("1.19.50-r3")
    public static void playAnimationOnEntities(AnimateEntityPacket.Animation animation, Collection<Entity> entities, Collection<Player> players) {
        var pk = new AnimateEntityPacket();
        pk.parseFromAnimation(animation);
        entities.forEach(entity -> pk.getEntityRuntimeIds().add(entity.getId()));
        pk.encode();
        Server.broadcastPacket(players, pk);
    }

    /**
     * @see #playAnimationOnEntities(AnimateEntityPacket.Animation, Collection<Entity>, Collection<Player>)
     */
    @PowerNukkitXOnly
    @Since("1.19.50-r3")
    public static void playAnimationOnEntities(AnimateEntityPacket.Animation animation, Collection<Entity> entities) {
        var viewers = new HashSet<Player>();
        entities.forEach(entity -> {
            viewers.addAll(entity.getViewers().values());
            if (entity.isPlayer) viewers.add((Player) entity);
        });
        playAnimationOnEntities(animation, entities, viewers);
    }

    @PowerNukkitXInternal
    public static void init() {
        registerEntity("Lightning", EntityLightning.class);
        registerEntity("Arrow", EntityArrow.class);
        registerEntity("EnderPearl", EntityEnderPearl.class);
        registerEntity("FallingSand", EntityFallingBlock.class);
        registerEntity("Firework", EntityFirework.class);
        registerEntity("Item", EntityItem.class);
        registerEntity("Painting", EntityPainting.class);
        registerEntity("PrimedTnt", EntityPrimedTNT.class);
        registerEntity("Snowball", EntitySnowball.class);
        //Monsters
        registerEntity("Blaze", EntityBlaze.class);
        registerEntity("CaveSpider", EntityCaveSpider.class);
        registerEntity("Creeper", EntityCreeper.class);
        registerEntity("Drowned", EntityDrowned.class);
        registerEntity("ElderGuardian", EntityElderGuardian.class);
        registerEntity("EnderDragon", EntityEnderDragon.class);
        registerEntity("Enderman", EntityEnderman.class);
        registerEntity("Endermite", EntityEndermite.class);
        registerEntity("Evoker", EntityEvoker.class);
        registerEntity("Ghast", EntityGhast.class);
        registerEntity("GlowSquid", EntityGlowSquid.class);
        registerEntity("Guardian", EntityGuardian.class);
        registerEntity("Hoglin", EntityHoglin.class);
        registerEntity("Husk", EntityHusk.class);
        registerEntity("MagmaCube", EntityMagmaCube.class);
        registerEntity("Phantom", EntityPhantom.class);
        registerEntity("Piglin", EntityPiglin.class);
        registerEntity("PiglinBrute", EntityPiglinBrute.class);
        registerEntity("Pillager", EntityPillager.class);
        registerEntity("Ravager", EntityRavager.class);
        registerEntity("Shulker", EntityShulker.class);
        registerEntity("Silverfish", EntitySilverfish.class);
        registerEntity("Skeleton", EntitySkeleton.class);
        registerEntity("Slime", EntitySlime.class);
        registerEntity("IronGolem", EntityIronGolem.class);
        registerEntity("SnowGolem", EntitySnowGolem.class);
        registerEntity("Spider", EntitySpider.class);
        registerEntity("Stray", EntityStray.class);
        registerEntity("Vex", EntityVex.class);
        registerEntity("Vindicator", EntityVindicator.class);
        registerEntity("Warden", EntityWarden.class);
        registerEntity("Witch", EntityWitch.class);
        registerEntity("Wither", EntityWither.class);
        registerEntity("WitherSkeleton", EntityWitherSkeleton.class);
        registerEntity("Zombie", EntityZombie.class);
        registerEntity("Zoglin", EntityZoglin.class);
        registerEntity("ZombiePigman", EntityZombiePigman.class);
        registerEntity("ZombieVillager", EntityZombieVillager.class);
        registerEntity("ZombieVillagerV1", EntityZombieVillagerV1.class);
        //Passive
        registerEntity("Allay", EntityAllay.class);
        registerEntity("Axolotl", EntityAxolotl.class);
        registerEntity("Bat", EntityBat.class);
        registerEntity("Bee", EntityBee.class);
        registerEntity("Cat", EntityCat.class);
        registerEntity("Chicken", EntityChicken.class);
        registerEntity("Cod", EntityCod.class);
        registerEntity("Cow", EntityCow.class);
        registerEntity("Dolphin", EntityDolphin.class);
        registerEntity("Donkey", EntityDonkey.class);
        registerEntity("Fox", EntityFox.class);
        registerEntity("Frog", EntityFrog.class);
        registerEntity("Goat", EntityGoat.class);
        registerEntity("Horse", EntityHorse.class);
        registerEntity("Llama", EntityLlama.class);
        registerEntity("Mooshroom", EntityMooshroom.class);
        registerEntity("Mule", EntityMule.class);
        registerEntity("Ocelot", EntityOcelot.class);
        registerEntity("Panda", EntityPanda.class);
        registerEntity("Parrot", EntityParrot.class);
        registerEntity("Pig", EntityPig.class);
        registerEntity("PolarBear", EntityPolarBear.class);
        registerEntity("Pufferfish", EntityPufferfish.class);
        registerEntity("Rabbit", EntityRabbit.class);
        registerEntity("Salmon", EntitySalmon.class);
        registerEntity("Sheep", EntitySheep.class);
        registerEntity("SkeletonHorse", EntitySkeletonHorse.class);
        registerEntity("Squid", EntitySquid.class);
        registerEntity("Strider", EntityStrider.class);
        registerEntity("Tadpole", EntityTadpole.class);
        registerEntity("TropicalFish", EntityTropicalFish.class);
        registerEntity("Turtle", EntityTurtle.class);
        registerEntity("Villager", EntityVillager.class);
        registerEntity("VillagerV1", EntityVillagerV1.class);
        registerEntity("WanderingTrader", EntityWanderingTrader.class);
        registerEntity("Wolf", EntityWolf.class);
        registerEntity("ZombieHorse", EntityZombieHorse.class);
        registerEntity("NPC", EntityNPCEntity.class);
        registerEntity("Camel", EntityCamel.class);
        //Projectile
        registerEntity("Small FireBall", EntitySmallFireBall.class);
        registerEntity("AreaEffectCloud", EntityAreaEffectCloud.class);
        registerEntity("Egg", EntityEgg.class);
        registerEntity("LingeringPotion", EntityPotionLingering.class);
        registerEntity("ThrownExpBottle", EntityExpBottle.class);
        registerEntity("ThrownPotion", EntityPotion.class);
        registerEntity("ThrownTrident", EntityThrownTrident.class);
        registerEntity("XpOrb", EntityXPOrb.class);
        registerEntity("ArmorStand", EntityArmorStand.class);

        registerEntity("Human", EntityHuman.class, true);
        //Vehicle
        registerEntity("Boat", EntityBoat.class);
        registerEntity("ChestBoat", EntityChestBoat.class);
        registerEntity("MinecartChest", EntityMinecartChest.class);
        registerEntity("MinecartHopper", EntityMinecartHopper.class);
        registerEntity("MinecartRideable", EntityMinecartEmpty.class);
        registerEntity("MinecartTnt", EntityMinecartTNT.class);

        registerEntity("EndCrystal", EntityEndCrystal.class);
        registerEntity("FishingHook", EntityFishingHook.class);
    }

    /**
     * 获取该实体的标识符
     * <p>
     * Get the identifier of the entity
     *
     * @return the identifier
     */
    @Nullable
    public Identifier getIdentifier() {
        return Entity.getIdentifier(this.getNetworkId());
    }

    /**
     * 获得该实体的网络ID
     * <p>
     * Get the network ID of the entity
     *
     * @return the network id
     */
    public abstract int getNetworkId();

    /**
     * 实体高度
     * <p>
     * entity Height
     *
     * @return the height
     */
    public float getHeight() {
        return 0;
    }

    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    public float getCurrentHeight() {
        if (isSwimming()) {
            return getSwimmingHeight();
        } else {
            return getHeight();
        }
    }

    public float getEyeHeight() {
        return getCurrentHeight() / 2 + 0.1f;
    }

    public float getWidth() {
        return 0;
    }

    public float getLength() {
        return 0;
    }

    protected double getStepHeight() {
        return 0;
    }

    public boolean canCollide() {
        return true;
    }

    protected float getGravity() {
        return 0;
    }

    protected float getDrag() {
        return 0;
    }

    protected float getBaseOffset() {
        return 0;
    }

    @PowerNukkitXOnly
    @Since("1.19.50-r2")
    public int getFrostbiteInjury() {
        return 1;
    }

    /**
     * 实体初始化顺序，先初始化Entity类字段->Entity构造函数->进入init方法->调用initEntity方法->子类字段初始化->子类构造函数
     * <p>
     * 用于初始化实体的NBT和实体字段的方法
     * <p>
     * Entity initialization order, first initialize the Entity class field->Entity constructor->Enter the init method->Call the init Entity method-> subclass field initialization-> subclass constructor
     * <p>
     * The method used to initialize the NBT and entity fields of the entity
     */
    protected void initEntity() {
        if (!(this instanceof Player)) {
            if (this.namedTag.contains("uuid")) {
                this.entityUniqueId = UUID.fromString(this.namedTag.getString("uuid"));
            } else {
                this.entityUniqueId = UUID.randomUUID();
            }
        }

        if (this.namedTag.contains("ActiveEffects")) {
            ListTag<CompoundTag> effects = this.namedTag.getList("ActiveEffects", CompoundTag.class);
            for (CompoundTag e : effects.getAll()) {
                Effect effect = Effect.getEffect(e.getByte("Id"));
                if (effect == null) {
                    continue;
                }

                effect.setAmplifier(e.getByte("Amplifier")).setDuration(e.getInt("Duration")).setVisible(e.getBoolean("ShowParticles"));

                this.addEffect(effect);
            }
        }

        if (this.namedTag.contains("CustomName")) {
            this.setNameTag(this.namedTag.getString("CustomName"));
            if (this.namedTag.contains("CustomNameVisible")) {
                this.setNameTagVisible(this.namedTag.getBoolean("CustomNameVisible"));
            }
            if (this.namedTag.contains("CustomNameAlwaysVisible")) {
                this.setNameTagAlwaysVisible(this.namedTag.getBoolean("CustomNameAlwaysVisible"));
            }
        }

        this.setDataFlag(DATA_FLAGS, DATA_FLAG_HAS_COLLISION, true);
        this.dataProperties.putFloat(DATA_BOUNDING_BOX_HEIGHT, this.getHeight());
        this.dataProperties.putFloat(DATA_BOUNDING_BOX_WIDTH, this.getWidth());
        this.dataProperties.putInt(DATA_HEALTH, (int) this.getHealth());

        this.scheduleUpdate();
    }

    protected final void init(FullChunk chunk, CompoundTag nbt) {
        if ((chunk == null || chunk.getProvider() == null)) {
            throw new ChunkException("Invalid garbage Chunk given to Entity");
        }

        if (this.initialized) {
            // We've already initialized this entity
            return;
        }
        this.initialized = true;

        this.isPlayer = this instanceof Player;
        this.temporalVector = new Vector3();

        this.id = Entity.entityCount++;
        this.justCreated = true;
        this.namedTag = nbt;

        this.chunk = chunk;
        this.setLevel(chunk.getProvider().getLevel());
        this.server = chunk.getProvider().getLevel().getServer();

        this.boundingBox = new SimpleAxisAlignedBB(0, 0, 0, 0, 0, 0);

        ListTag<DoubleTag> posList = this.namedTag.getList("Pos", DoubleTag.class);
        ListTag<FloatTag> rotationList = this.namedTag.getList("Rotation", FloatTag.class);
        ListTag<DoubleTag> motionList = this.namedTag.getList("Motion", DoubleTag.class);
        this.setPositionAndRotation(
                this.temporalVector.setComponents(
                        posList.get(0).data,
                        posList.get(1).data,
                        posList.get(2).data
                ),
                rotationList.get(0).data,
                rotationList.get(1).data
        );

        this.setMotion(this.temporalVector.setComponents(
                motionList.get(0).data,
                motionList.get(1).data,
                motionList.get(2).data
        ));

        if (!this.namedTag.contains("FallDistance")) {
            this.namedTag.putFloat("FallDistance", 0);
        }
        this.fallDistance = this.namedTag.getFloat("FallDistance");
        this.highestPosition = this.y + this.namedTag.getFloat("FallDistance");

        if (!this.namedTag.contains("Fire") || this.namedTag.getShort("Fire") > 32767) {
            this.namedTag.putShort("Fire", 0);
        }
        this.fireTicks = this.namedTag.getShort("Fire");

        if (!this.namedTag.contains("Air")) {
            this.namedTag.putShort("Air", 300);
        }
        this.setDataProperty(new ShortEntityData(DATA_AIR, this.namedTag.getShort("Air")), false);

        if (!this.namedTag.contains("OnGround")) {
            this.namedTag.putBoolean("OnGround", false);
        }
        this.onGround = this.namedTag.getBoolean("OnGround");

        if (!this.namedTag.contains("Invulnerable")) {
            this.namedTag.putBoolean("Invulnerable", false);
        }
        this.invulnerable = this.namedTag.getBoolean("Invulnerable");

        if (!this.namedTag.contains("Scale")) {
            this.namedTag.putFloat("Scale", 1);
        }
        this.scale = this.namedTag.getFloat("Scale");
        this.setDataProperty(new FloatEntityData(DATA_SCALE, scale), false);
        this.setDataProperty(new ByteEntityData(DATA_COLOR, 0), false);

        try {
            this.chunk.addEntity(this);
            this.level.addEntity(this);

            this.initEntity();

            this.lastUpdate = this.server.getTick();

            EntitySpawnEvent event = new EntitySpawnEvent(this);

            this.server.getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                this.close(false);
            } else {
                this.scheduleUpdate();
            }
        } catch (Exception e) {
            this.close(false);
            throw e;
        }
    }

    public boolean hasCustomName() {
        return !this.getNameTag().isEmpty();
    }

    public String getNameTag() {
        return this.getDataPropertyString(DATA_NAMETAG);
    }

    public void setNameTag(String name) {
        this.setDataProperty(new StringEntityData(DATA_NAMETAG, name));
    }

    public boolean isNameTagVisible() {
        return this.getDataFlag(DATA_FLAGS, DATA_FLAG_CAN_SHOW_NAMETAG);
    }

    public void setNameTagVisible(boolean value) {
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_CAN_SHOW_NAMETAG, value);
    }

    public boolean isNameTagAlwaysVisible() {
        return this.getDataPropertyByte(DATA_ALWAYS_SHOW_NAMETAG) == 1;
    }

    public void setNameTagAlwaysVisible(boolean value) {
        this.setDataProperty(new ByteEntityData(DATA_ALWAYS_SHOW_NAMETAG, value ? 1 : 0));
    }

    public void setNameTagVisible() {
        this.setNameTagVisible(true);
    }

    public void setNameTagAlwaysVisible() {
        this.setNameTagAlwaysVisible(true);
    }

    public String getScoreTag() {
        return this.getDataPropertyString(DATA_SCORE_TAG);
    }

    public void setScoreTag(String score) {
        this.setDataProperty(new StringEntityData(DATA_SCORE_TAG, score));
    }

    public boolean isSneaking() {
        return this.getDataFlag(DATA_FLAGS, DATA_FLAG_SNEAKING);
    }

    public void setSneaking(boolean value) {
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_SNEAKING, value);
    }

    public void setSneaking() {
        this.setSneaking(true);
    }

    public boolean isSwimming() {
        return this.getDataFlag(DATA_FLAGS, DATA_FLAG_SWIMMING);
    }

    public void setSwimming(boolean value) {
        if (isSwimming() == value) {
            return;
        }
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_SWIMMING, value);
        if (Float.compare(getSwimmingHeight(), getHeight()) != 0) {
            recalculateBoundingBox(true);
        }
    }

    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    public float getSwimmingHeight() {
        return getHeight();
    }

    public void setSwimming() {
        this.setSwimming(true);
    }

    public boolean isSprinting() {
        return this.getDataFlag(DATA_FLAGS, DATA_FLAG_SPRINTING);
    }

    public void setSprinting(boolean value) {
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_SPRINTING, value);
    }

    public void setSprinting() {
        this.setSprinting(true);
    }

    public boolean isGliding() {
        return this.getDataFlag(DATA_FLAGS, DATA_FLAG_GLIDING);
    }

    public void setGliding(boolean value) {
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_GLIDING, value);
    }

    public void setGliding() {
        this.setGliding(true);
    }

    public boolean isImmobile() {
        return this.getDataFlag(DATA_FLAGS, DATA_FLAG_IMMOBILE);
    }

    public void setImmobile(boolean value) {
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_IMMOBILE, value);
    }

    public void setImmobile() {
        this.setImmobile(true);
    }

    public boolean canClimb() {
        return this.getDataFlag(DATA_FLAGS, DATA_FLAG_CAN_CLIMB);
    }

    public void setCanClimb() {
        this.setCanClimb(true);
    }

    public void setCanClimb(boolean value) {
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_CAN_CLIMB, value);
    }

    public boolean canClimbWalls() {
        return this.getDataFlag(DATA_FLAGS, DATA_FLAG_WALLCLIMBING);
    }

    public void setCanClimbWalls() {
        this.setCanClimbWalls(true);
    }

    public void setCanClimbWalls(boolean value) {
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_WALLCLIMBING, value);
    }

    public float getScale() {
        return this.scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
        this.setDataProperty(new FloatEntityData(DATA_SCALE, this.scale));
        this.recalculateBoundingBox();
    }

    public List<Entity> getPassengers() {
        return passengers;
    }

    public Entity getPassenger() {
        return Iterables.getFirst(this.passengers, null);
    }

    public boolean isPassenger(Entity entity) {
        return this.passengers.contains(entity);
    }

    public boolean isControlling(Entity entity) {
        return this.passengers.indexOf(entity) == 0;
    }

    public boolean hasControllingPassenger() {
        return !this.passengers.isEmpty() && isControlling(this.passengers.get(0));
    }

    public Entity getRiding() {
        return riding;
    }

    public Map<Integer, Effect> getEffects() {
        return effects;
    }

    public void removeAllEffects() {
        for (Effect effect : this.effects.values()) {
            this.removeEffect(effect.getId());
        }
    }

    public void removeEffect(int effectId) {
        if (this.effects.containsKey(effectId)) {
            Effect effect = this.effects.get(effectId);
            this.effects.remove(effectId);
            effect.remove(this);

            this.recalculateEffectColor();
        }
    }

    public Effect getEffect(int effectId) {
        return this.effects.getOrDefault(effectId, null);
    }

    public boolean hasEffect(int effectId) {
        return this.effects.containsKey(effectId);
    }

    public void addEffect(Effect effect) {
        if (effect == null) {
            return; //here add null means add nothing
        }
        Effect oldEffect = this.getEffect(effect.getId());
        if (oldEffect != null && (Math.abs(effect.getAmplifier()) < Math.abs(oldEffect.getAmplifier()) ||
                Math.abs(effect.getAmplifier()) == Math.abs(oldEffect.getAmplifier())
                        && effect.getDuration() < oldEffect.getDuration())) {
            return;
        }
        effect.add(this);
        this.effects.put(effect.getId(), effect);

        this.recalculateEffectColor();

        if (effect.getId() == Effect.HEALTH_BOOST) {
            this.setHealth(this.getHealth() + 4 * (effect.getAmplifier() + 1));
        }

    }

    public void recalculateBoundingBox() {
        this.recalculateBoundingBox(true);
    }

    public void recalculateBoundingBox(boolean send) {
        float entityHeight = getCurrentHeight();
        float height = entityHeight * this.scale;
        double radius = (this.getWidth() * this.scale) / 2d;
        this.boundingBox.setBounds(
                x - radius,
                y,
                z - radius,

                x + radius,
                y + height,
                z + radius
        );

        FloatEntityData bbH = new FloatEntityData(DATA_BOUNDING_BOX_HEIGHT, entityHeight);
        FloatEntityData bbW = new FloatEntityData(DATA_BOUNDING_BOX_WIDTH, this.getWidth());
        this.dataProperties.put(bbH);
        this.dataProperties.put(bbW);
        if (send) {
            sendData(this.hasSpawned.values().toArray(Player.EMPTY_ARRAY), new EntityMetadata().put(bbH).put(bbW));
        }
    }

    protected void recalculateEffectColor() {
        int[] color = new int[3];
        int count = 0;
        boolean ambient = true;
        for (Effect effect : this.effects.values()) {
            if (effect.isVisible()) {
                int[] c = effect.getColor();
                color[0] += c[0] * (effect.getAmplifier() + 1);
                color[1] += c[1] * (effect.getAmplifier() + 1);
                color[2] += c[2] * (effect.getAmplifier() + 1);
                count += effect.getAmplifier() + 1;
                if (!effect.isAmbient()) {
                    ambient = false;
                }
            }
        }

        if (count > 0) {
            int r = (color[0] / count) & 0xff;
            int g = (color[1] / count) & 0xff;
            int b = (color[2] / count) & 0xff;

            this.setDataProperty(new IntEntityData(Entity.DATA_POTION_COLOR, (r << 16) + (g << 8) + b));
            this.setDataProperty(new ByteEntityData(Entity.DATA_POTION_AMBIENT, ambient ? 1 : 0));
        } else {
            this.setDataProperty(new IntEntityData(Entity.DATA_POTION_COLOR, 0));
            this.setDataProperty(new ByteEntityData(Entity.DATA_POTION_AMBIENT, 0));
        }
    }

    public void saveNBT() {
        if (!(this instanceof Player)) {
            this.namedTag.putString("id", this.getSaveId());
            if (!this.getNameTag().equals("")) {
                this.namedTag.putString("CustomName", this.getNameTag());
                this.namedTag.putBoolean("CustomNameVisible", this.isNameTagVisible());
                this.namedTag.putBoolean("CustomNameAlwaysVisible", this.isNameTagAlwaysVisible());
            } else {
                this.namedTag.remove("CustomName");
                this.namedTag.remove("CustomNameVisible");
                this.namedTag.remove("CustomNameAlwaysVisible");
            }
            if (this.entityUniqueId == null) {
                this.entityUniqueId = UUID.randomUUID();
            }
            this.namedTag.putString("uuid", this.entityUniqueId.toString());
        }

        this.namedTag.putList(new ListTag<DoubleTag>("Pos")
                .add(new DoubleTag("0", this.x))
                .add(new DoubleTag("1", this.y))
                .add(new DoubleTag("2", this.z))
        );

        this.namedTag.putList(new ListTag<DoubleTag>("Motion")
                .add(new DoubleTag("0", this.motionX))
                .add(new DoubleTag("1", this.motionY))
                .add(new DoubleTag("2", this.motionZ))
        );

        this.namedTag.putList(new ListTag<FloatTag>("Rotation")
                .add(new FloatTag("0", (float) this.yaw))
                .add(new FloatTag("1", (float) this.pitch))
        );

        this.namedTag.putFloat("FallDistance", this.fallDistance);
        this.namedTag.putShort("Fire", this.fireTicks);
        this.namedTag.putShort("Air", this.getDataPropertyShort(DATA_AIR));
        this.namedTag.putBoolean("OnGround", this.onGround);
        this.namedTag.putBoolean("Invulnerable", this.invulnerable);
        this.namedTag.putFloat("Scale", this.scale);

        if (!this.effects.isEmpty()) {
            ListTag<CompoundTag> list = new ListTag<>("ActiveEffects");
            for (Effect effect : this.effects.values()) {
                list.add(new CompoundTag(String.valueOf(effect.getId()))
                        .putByte("Id", effect.getId())
                        .putByte("Amplifier", effect.getAmplifier())
                        .putInt("Duration", effect.getDuration())
                        .putBoolean("Ambient", false)
                        .putBoolean("ShowParticles", effect.isVisible())
                );
            }

            this.namedTag.putList(list);
        } else {
            this.namedTag.remove("ActiveEffects");
        }
    }

    /**
     * The name that English name of the type of this entity.
     */
    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    public String getOriginalName() {
        return this.getSaveId();
    }

    /**
     * Similar to {@link #getName()}, but if the name is blank or empty it returns the static name instead.
     */
    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    public final String getVisibleName() {
        String name = getName();
        if (!TextFormat.clean(name).trim().isEmpty()) {
            return name;
        } else {
            return getOriginalName();
        }
    }

    /**
     * The current name used by this entity in the name tag, or the static name if the entity don't have nametag.
     */
    @NotNull
    public String getName() {
        if (this.hasCustomName()) {
            return this.getNameTag();
        } else {
            return this.getOriginalName();
        }
    }

    public final String getSaveId() {
        return shortNames.getOrDefault(this.getClass().getSimpleName(), "");
    }

    /**
     * 将这个实体在客户端生成，让该玩家可以看到它
     * <p>
     * Spawn this entity on the client side so that the player can see it
     *
     * @param player the player
     */
    public void spawnTo(Player player) {

        if (!this.hasSpawned.containsKey(player.getLoaderId()) && this.chunk != null && player.usedChunks.containsKey(Level.chunkHash(this.chunk.getX(), this.chunk.getZ()))) {
            this.hasSpawned.put(player.getLoaderId(), player);
            player.dataPacket(createAddEntityPacket());
        }

        if (this.riding != null) {
            this.riding.spawnTo(player);

            SetEntityLinkPacket pkk = new SetEntityLinkPacket();
            pkk.vehicleUniqueId = this.riding.getId();
            pkk.riderUniqueId = this.getId();
            pkk.type = 1;
            pkk.immediate = 1;

            player.dataPacket(pkk);
        }
    }

    protected DataPacket createAddEntityPacket() {
        AddEntityPacket addEntity = new AddEntityPacket();
        addEntity.type = this.getNetworkId();
        addEntity.entityUniqueId = this.getId();
        if (this instanceof CustomEntity customEntity) {
            addEntity.id = customEntity.getDefinition().getStringId();
        }
        addEntity.entityRuntimeId = this.getId();
        addEntity.yaw = (float) this.yaw;
        addEntity.headYaw = (float) this.yaw;
        addEntity.pitch = (float) this.pitch;
        addEntity.x = (float) this.x;
        addEntity.y = (float) this.y + this.getBaseOffset();
        addEntity.z = (float) this.z;
        addEntity.speedX = (float) this.motionX;
        addEntity.speedY = (float) this.motionY;
        addEntity.speedZ = (float) this.motionZ;
        addEntity.metadata = this.dataProperties;

        addEntity.links = new EntityLink[this.passengers.size()];
        for (int i = 0; i < addEntity.links.length; i++) {
            addEntity.links[i] = new EntityLink(this.getId(), this.passengers.get(i).getId(), i == 0 ? EntityLink.TYPE_RIDER : TYPE_PASSENGER, false, false);
        }

        return addEntity;
    }

    public Map<Integer, Player> getViewers() {
        return hasSpawned;
    }

    public void sendPotionEffects(Player player) {
        for (Effect effect : this.effects.values()) {
            MobEffectPacket pk = new MobEffectPacket();
            pk.eid = this.getId();
            pk.effectId = effect.getId();
            pk.amplifier = effect.getAmplifier();
            pk.particles = effect.isVisible();
            pk.duration = effect.getDuration();
            pk.eventId = MobEffectPacket.EVENT_ADD;

            player.dataPacket(pk);
        }
    }

    public void sendData(Player player) {
        this.sendData(player, null);
    }

    public void sendData(Player player, EntityMetadata data) {
        SetEntityDataPacket pk = new SetEntityDataPacket();
        pk.eid = this.getId();
        pk.metadata = data == null ? this.dataProperties : data;

        player.dataPacket(pk);
    }

    public void sendData(Player[] players) {
        this.sendData(players, null);
    }

    public void sendData(Player[] players, EntityMetadata data) {
        SetEntityDataPacket pk = new SetEntityDataPacket();
        pk.eid = this.getId();
        pk.metadata = data == null ? this.dataProperties : data;

        for (Player player : players) {
            if (player == this) {
                continue;
            }
            player.dataPacket(pk.clone());
        }
        if (this instanceof Player) {
            ((Player) this).dataPacket(pk);
        }
    }

    public void despawnFrom(Player player) {
        if (this.hasSpawned.containsKey(player.getLoaderId())) {
            RemoveEntityPacket pk = new RemoveEntityPacket();
            pk.eid = this.getId();
            player.dataPacket(pk);
            this.hasSpawned.remove(player.getLoaderId());
        }
    }

    /**
     * 当一个实体被攻击时(即接受一个实体伤害事件 这个事件可以是由其他实体攻击导致，也可能是自然伤害)调用.
     * <p>
     * Called when an entity is attacked (i.e. receives an entity damage event. This event can be caused by an attack by another entity, or it can be a natural damage).
     *
     * @param source 记录伤害源的事件<br>Record the event of the source of the attack
     * @return 是否攻击成功<br>Whether the attack was successful
     */
    public boolean attack(EntityDamageEvent source) {
        //火焰保护附魔实现
        if (hasEffect(Effect.FIRE_RESISTANCE)
                && (source.getCause() == DamageCause.FIRE
                || source.getCause() == DamageCause.FIRE_TICK
                || source.getCause() == DamageCause.LAVA)) {
            return false;
        }

        //水生生物免疫溺水
        if (this instanceof EntitySwimmable swimmable && !swimmable.canDrown() && source.getCause() == DamageCause.DROWNING)
            return false;

        //飞行生物免疫摔伤
        if (this instanceof EntityFlyable flyable && !flyable.hasFallingDamage() && source.getCause() == DamageCause.FALL)
            return false;

        //事件回调函数
        getServer().getPluginManager().callEvent(source);
        if (source.isCancelled()) {
            return false;
        }

        // Make fire aspect to set the target in fire before dealing any damage so the target is in fire on death even if killed by the first hit
        if (source instanceof EntityDamageByEntityEvent) {
            Enchantment[] enchantments = ((EntityDamageByEntityEvent) source).getWeaponEnchantments();
            if (enchantments != null) {
                for (Enchantment enchantment : enchantments) {
                    enchantment.doAttack(((EntityDamageByEntityEvent) source).getDamager(), this);
                }
            }
        }

        //吸收伤害实现
        if (this.absorption > 0) {  // Damage Absorption
            this.setAbsorption(Math.max(0, this.getAbsorption() + source.getDamage(EntityDamageEvent.DamageModifier.ABSORPTION)));
        }

        //修改最后一次伤害
        setLastDamageCause(source);

        //计算血量
        float newHealth = getHealth() - source.getFinalDamage();

        //only player
        if (newHealth < 1 && this instanceof Player player) {
            if (source.getCause() != DamageCause.VOID && source.getCause() != DamageCause.SUICIDE) {
                boolean totem = false;
                boolean isOffhand = false;
                if (player.getOffhandInventory().getItem(0) instanceof ItemTotem) {
                    totem = true;
                    isOffhand = true;
                } else if (player.getInventory().getItemInHand() instanceof ItemTotem) {
                    totem = true;
                }
                //复活图腾实现
                if (totem) {
                    this.getLevel().addLevelEvent(this, LevelEventPacket.EVENT_SOUND_TOTEM);
                    this.getLevel().addParticleEffect(this, ParticleEffect.TOTEM);

                    this.extinguish();
                    this.removeAllEffects();
                    this.setHealth(1);

                    this.addEffect(Effect.getEffect(Effect.REGENERATION).setDuration(800).setAmplifier(1));
                    this.addEffect(Effect.getEffect(Effect.FIRE_RESISTANCE).setDuration(800));
                    this.addEffect(Effect.getEffect(Effect.ABSORPTION).setDuration(100).setAmplifier(1));

                    EntityEventPacket pk = new EntityEventPacket();
                    pk.eid = this.getId();
                    pk.event = EntityEventPacket.CONSUME_TOTEM;
                    player.dataPacket(pk);

                    if (isOffhand) {
                        player.getOffhandInventory().clear(0, true);
                    } else {
                        player.getInventory().clear(player.getInventory().getHeldItemIndex(), true);
                    }

                    source.setCancelled(true);
                    return false;
                }
            }
        }

        Entity attacker = source instanceof EntityDamageByEntityEvent ? ((EntityDamageByEntityEvent) source).getDamager() : null;

        setHealth(newHealth);

        if (!(this instanceof EntityArmorStand)) {
            this.level.getVibrationManager().callVibrationEvent(new VibrationEvent(attacker, this.clone(), VibrationType.ENTITY_DAMAGE));
        }

        return true;
    }

    public boolean attack(float damage) {
        return this.attack(new EntityDamageEvent(this, DamageCause.CUSTOM, damage));
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public int getAge() {
        return this.age;
    }

    public void heal(EntityRegainHealthEvent source) {
        this.server.getPluginManager().callEvent(source);
        if (source.isCancelled()) {
            return;
        }
        this.setHealth(this.getHealth() + source.getAmount());
    }

    public void heal(float amount) {
        this.heal(new EntityRegainHealthEvent(this, amount, EntityRegainHealthEvent.CAUSE_REGEN));
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        if (this.health == health) {
            return;
        }

        if (health < 1) {
            if (this.isAlive()) {
                this.kill();
            }
        } else if (health <= this.getMaxHealth() || health < this.health) {
            this.health = health;
        } else {
            this.health = this.getMaxHealth();
        }

        setDataProperty(new IntEntityData(DATA_HEALTH, (int) this.health));
    }

    public boolean isAlive() {
        return this.health > 0;
    }

    public boolean isClosed() {
        return closed;
    }

    public EntityDamageEvent getLastDamageCause() {
        return lastDamageCause;
    }

    public void setLastDamageCause(EntityDamageEvent type) {
        this.lastDamageCause = type;
    }

    public int getMaxHealth() {
        return maxHealth + (this.hasEffect(Effect.HEALTH_BOOST) ? 4 * (this.getEffect(Effect.HEALTH_BOOST).getAmplifier() + 1) : 0);
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public boolean canCollideWith(Entity entity) {
        return !this.justCreated && this != entity && !this.noClip;
    }

    @PowerNukkitXOnly
    @Since("1.20.10-r1")
    public boolean canBeSavedWithChunk() { return saveWithChunk; }

    @PowerNukkitXOnly
    @Since("1.20.10-r1")
    public void setCanBeSavedWithChunk(boolean saveWithChunk) { this.saveWithChunk = saveWithChunk; }

    protected boolean checkObstruction(double x, double y, double z) {
        if (this.level.fastCollisionCubes(this, this.getBoundingBox(), false).size() == 0 || this.noClip) {
            return false;
        }

        int i = NukkitMath.floorDouble(x);
        int j = NukkitMath.floorDouble(y);
        int k = NukkitMath.floorDouble(z);

        double diffX = x - i;
        double diffY = y - j;
        double diffZ = z - k;

        if (!Block.isTransparent(this.level.getBlockIdAt(i, j, k))) {
            boolean flag = Block.isTransparent(this.level.getBlockIdAt(i - 1, j, k));
            boolean flag1 = Block.isTransparent(this.level.getBlockIdAt(i + 1, j, k));
            boolean flag2 = Block.isTransparent(this.level.getBlockIdAt(i, j - 1, k));
            boolean flag3 = Block.isTransparent(this.level.getBlockIdAt(i, j + 1, k));
            boolean flag4 = Block.isTransparent(this.level.getBlockIdAt(i, j, k - 1));
            boolean flag5 = Block.isTransparent(this.level.getBlockIdAt(i, j, k + 1));

            int direction = -1;
            double limit = 9999;

            if (flag) {
                limit = diffX;
                direction = 0;
            }

            if (flag1 && 1 - diffX < limit) {
                limit = 1 - diffX;
                direction = 1;
            }

            if (flag2 && diffY < limit) {
                limit = diffY;
                direction = 2;
            }

            if (flag3 && 1 - diffY < limit) {
                limit = 1 - diffY;
                direction = 3;
            }

            if (flag4 && diffZ < limit) {
                limit = diffZ;
                direction = 4;
            }

            if (flag5 && 1 - diffZ < limit) {
                direction = 5;
            }

            double force = ThreadLocalRandom.current().nextDouble() * 0.2 + 0.1;

            if (direction == 0) {
                this.motionX = -force;

                return true;
            }

            if (direction == 1) {
                this.motionX = force;

                return true;
            }

            if (direction == 2) {
                this.motionY = -force;

                return true;
            }

            if (direction == 3) {
                this.motionY = force;

                return true;
            }

            if (direction == 4) {
                this.motionZ = -force;

                return true;
            }

            if (direction == 5) {
                this.motionZ = force;

                return true;
            }
        }

        return false;
    }

    public boolean entityBaseTick() {
        return this.entityBaseTick(1);
    }

    public boolean entityBaseTick(int tickDiff) {
        if (!this.isPlayer) {
            this.blocksAround = null;
            this.collisionBlocks = null;
        }
        this.justCreated = false;

        if (!this.isAlive()) {
            this.removeAllEffects();
            this.despawnFromAll();
            if (!this.isPlayer) {
                this.close();
            }
            return false;
        }
        if (riding != null && !riding.isAlive() && riding instanceof EntityRideable) {
            ((EntityRideable) riding).dismountEntity(this);
        }

        updatePassengers();

        if (!this.effects.isEmpty()) {
            for (Effect effect : this.effects.values()) {
                if (effect.canTick()) {
                    effect.applyEffect(this);
                }
                effect.setDuration(effect.getDuration() - tickDiff);

                if (effect.getDuration() <= 0) {
                    this.removeEffect(effect.getId());
                }
            }
        }

        boolean hasUpdate = false;

        this.checkBlockCollision();

        if (this.y < (level.getMinHeight() - 18) && this.isAlive()) {
            if (this instanceof Player player) {
                if (!player.isCreative()) this.attack(new EntityDamageEvent(this, DamageCause.VOID, 10));
            } else {
                this.attack(new EntityDamageEvent(this, DamageCause.VOID, 10));
                hasUpdate = true;
            }
        }

        if (this.fireTicks > 0) {
            if (this.fireProof) {
                this.fireTicks -= 4 * tickDiff;
                if (this.fireTicks < 0) {
                    this.fireTicks = 0;
                }
            } else {
                if (!this.hasEffect(Effect.FIRE_RESISTANCE) && ((this.fireTicks % 20) == 0 || tickDiff > 20)) {
                    this.attack(new EntityDamageEvent(this, DamageCause.FIRE_TICK, 1));
                }
                this.fireTicks -= tickDiff;
            }
            if (this.fireTicks <= 0) {
                this.extinguish();
            } else if (!this.fireProof && (!(this instanceof Player) || !((Player) this).isSpectator())) {
                this.setDataFlag(DATA_FLAGS, DATA_FLAG_ONFIRE, true);
                hasUpdate = true;
            }
        }

        if (this.noDamageTicks > 0) {
            this.noDamageTicks -= tickDiff;
            if (this.noDamageTicks < 0) {
                this.noDamageTicks = 0;
            }
        }

        if (this.inPortalTicks == 80) {
            EntityPortalEnterEvent ev = new EntityPortalEnterEvent(this, PortalType.NETHER);
            getServer().getPluginManager().callEvent(ev);

            if (!ev.isCancelled() && (level.getDimension() == Level.DIMENSION_OVERWORLD || level.getDimension() == Level.DIMENSION_NETHER)) {
                Position newPos = EnumLevel.convertPosBetweenNetherAndOverworld(this);
                if (newPos != null) {
                    /*for (int x = -1; x < 2; x++) {
                        for (int z = -1; z < 2; z++) {
                            int chunkX = (newPos.getFloorX() >> 4) + x, chunkZ = (newPos.getFloorZ() >> 4) + z;
                            FullChunk chunk = newPos.level.getChunk(chunkX, chunkZ, false);
                            if (chunk == null || !(chunk.isGenerated() || chunk.isPopulated())) {
                                newPos.level.generateChunk(chunkX, chunkZ, true);
                            }
                        }
                    }*/
                    Position nearestPortal = getNearestValidPortal(newPos);
                    if (nearestPortal != null) {
                        teleport(nearestPortal.add(0.5, 0, 0.5), PlayerTeleportEvent.TeleportCause.NETHER_PORTAL);
                    } else {
                        final Position finalPos = newPos.add(1.5, 1, 1.5);
                        if (teleport(finalPos, PlayerTeleportEvent.TeleportCause.NETHER_PORTAL)) {
                            server.getScheduler().scheduleDelayedTask(new Task() {
                                @Override
                                public void onRun(int currentTick) {
                                    // dirty hack to make sure chunks are loaded and generated before spawning
                                    // player
                                    inPortalTicks = 81;
                                    teleport(finalPos, PlayerTeleportEvent.TeleportCause.NETHER_PORTAL);
                                    BlockNetherPortal.spawnPortal(newPos);
                                }
                            }, 5);
                        }
                    }
                }
            }
        }
        this.age += tickDiff;
        this.ticksLived += tickDiff;

        return hasUpdate;
    }

    private Position getNearestValidPortal(Position currentPos) {
        AxisAlignedBB axisAlignedBB = new SimpleAxisAlignedBB(
                new Vector3(currentPos.getFloorX() - 128.0, currentPos.level.getDimension() == Level.DIMENSION_NETHER ? 0 : -64, currentPos.getFloorZ() - 128.0),
                new Vector3(currentPos.getFloorX() + 128.0, currentPos.level.getDimension() == Level.DIMENSION_NETHER ? 128 : 320, currentPos.getFloorZ() + 128.0));
        BiPredicate<BlockVector3, BlockState> condition = (pos, state) -> state.getBlockId() == BlockID.NETHER_PORTAL;
        List<Block> blocks = currentPos.level.scanBlocks(axisAlignedBB, condition);

        if (blocks.isEmpty()) {
            return null;
        }

        final Vector2 currentPosV2 = new Vector2(currentPos.getFloorX(), currentPos.getFloorZ());
        final double by = currentPos.getFloorY();
        Comparator<Block> euclideanDistance = Comparator.comparingDouble(block -> currentPosV2.distanceSquared(block.getFloorX(), block.getFloorZ()));
        Comparator<Block> heightDistance = Comparator.comparingDouble(block -> {
            double ey = by - block.y;
            return ey * ey;
        });

        Block nearestPortal = blocks.stream()
                .filter(block -> block.down().getId() != BlockID.NETHER_PORTAL)
                .min(euclideanDistance.thenComparing(heightDistance))
                .orElse(null);

        return nearestPortal;
    }

    @PowerNukkitDifference(since = "1.6.0.0-PNX", info = "add support for the new movement packet MoveEntityDeltaPacket")
    public void updateMovement() {
        //这样做是为了向后兼容旧插件
        if (!enableHeadYaw()) {
            this.headYaw = this.yaw;
        }
        double diffPosition = (this.x - this.lastX) * (this.x - this.lastX) + (this.y - this.lastY) * (this.y - this.lastY) + (this.z - this.lastZ) * (this.z - this.lastZ);
        double diffRotation = (enableHeadYaw() ? (this.headYaw - this.lastHeadYaw) * (this.headYaw - this.lastHeadYaw) : 0) + (this.yaw - this.lastYaw) * (this.yaw - this.lastYaw) + (this.pitch - this.lastPitch) * (this.pitch - this.lastPitch);

        double diffMotion = (this.motionX - this.lastMotionX) * (this.motionX - this.lastMotionX) + (this.motionY - this.lastMotionY) * (this.motionY - this.lastMotionY) + (this.motionZ - this.lastMotionZ) * (this.motionZ - this.lastMotionZ);

        if (diffPosition > 0.0001 || diffRotation > 1.0) { //0.2 ** 2, 1.5 ** 2
            if (diffPosition > 0.0001) {
                if (this.isOnGround()) {
                    this.level.getVibrationManager().callVibrationEvent(new VibrationEvent(this instanceof EntityProjectile projectile ? projectile.shootingEntity : this, this.clone(), VibrationType.STEP));
                } else if (this.isTouchingWater()) {
                    this.level.getVibrationManager().callVibrationEvent(new VibrationEvent(this instanceof EntityProjectile projectile ? projectile.shootingEntity : this, this.clone(), VibrationType.SWIM));
                }
            }

            this.addMovement(this.x, this.isPlayer ? this.y : this.y + this.getBaseOffset(), this.z, this.yaw, this.pitch, this.headYaw);

            this.lastX = this.x;
            this.lastY = this.y;
            this.lastZ = this.z;

            this.lastPitch = this.pitch;
            this.lastYaw = this.yaw;
            this.lastHeadYaw = this.headYaw;

            this.positionChanged = true;
        } else {
            this.positionChanged = false;
        }

        if (diffMotion > 0.0025 || (diffMotion > 0.0001 && this.getMotion().lengthSquared() <= 0.0001)) { //0.05 ** 2
            this.lastMotionX = this.motionX;
            this.lastMotionY = this.motionY;
            this.lastMotionZ = this.motionZ;

            this.addMotion(this.motionX, this.motionY, this.motionZ);
        }
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public boolean enableHeadYaw() {
        return false;
    }

    /**
     * 增加运动 (仅发送数据包，如果需要请使用{@link #setMotion})
     * <p>
     * Add motion (just sending packet will not make the entity actually move, use {@link #setMotion} if needed)
     *
     * @param x       x
     * @param y       y
     * @param z       z
     * @param yaw     左右旋转
     * @param pitch   上下旋转
     * @param headYaw headYaw
     */
    public void addMovement(double x, double y, double z, double yaw, double pitch, double headYaw) {
        this.level.addEntityMovement(this, x, y, z, yaw, pitch, headYaw);
    }

    /*
     * 请注意此方法仅向客户端发motion包，并不会真正的将motion数值加到实体的motion(x|y|z)上<p/>
     * 如果你想在实体的motion基础上增加，请直接将要添加的motion数值加到实体的motion(x|y|z)上，像这样：<p/>
     * entity.motionX += vector3.x;<p/>
     * entity.motionY += vector3.y;<p/>
     * entity.motionZ += vector3.z;<p/>
     * 对于玩家实体，你不应该使用此方法！
     */
    public void addMotion(double motionX, double motionY, double motionZ) {
        SetEntityMotionPacket pk = new SetEntityMotionPacket();
        pk.eid = this.id;
        pk.motionX = (float) motionX;
        pk.motionY = (float) motionY;
        pk.motionZ = (float) motionZ;

        Server.broadcastPacket(this.hasSpawned.values(), pk);
    }

    @PowerNukkitXOnly
    @Since("1.19.31-r1")
    protected void broadcastMovement() {
        var pk = new MoveEntityAbsolutePacket();
        pk.eid = this.getId();
        pk.x = this.x;
        //因为以前处理MOVE_PLAYER_PACKET的时候是y - this.getBaseOffset()
        //现在统一 MOVE_PLAYER_PACKET和PLAYER_AUTH_INPUT_PACKET 均为this.y - this.getEyeHeight()，所以这里不再需要对两种移动方式分别处理
        pk.y = this.y + this.getBaseOffset();
        pk.z = this.z;
        pk.headYaw = yaw;
        pk.pitch = pitch;
        pk.yaw = yaw;
        pk.teleport = false;
        pk.onGround = this.onGround;
        Server.broadcastPacket(hasSpawned.values(), pk);
    }

    @PowerNukkitXDifference(info = "There is no need to set the temporalVector, because the result is prone to change in an asynchronous environment.")
    @Override
    public Vector3 getDirectionVector() {
        return super.getDirectionVector();
    }

    public Vector2 getDirectionPlane() {
        return (new Vector2((float) (-Math.cos(Math.toRadians(this.yaw) - Math.PI / 2)), (float) (-Math.sin(Math.toRadians(this.yaw) - Math.PI / 2)))).normalize();
    }

    public BlockFace getHorizontalFacing() {
        return BlockFace.fromHorizontalIndex(NukkitMath.floorDouble((this.yaw * 4.0F / 360.0F) + 0.5D) & 3);
    }

    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        if (!this.isAlive()) {
            ++this.deadTicks;
            if (this.deadTicks >= 15) {
                //apply death smoke cloud only if it is a creature
                if (this instanceof EntityCreature) {
                    //通过碰撞箱大小动态添加 death smoke cloud
                    var aabb = this.getBoundingBox();
                    for (double x = aabb.getMinX(); x <= aabb.getMaxX(); x += 0.5) {
                        for (double z = aabb.getMinZ(); z <= aabb.getMaxZ(); z += 0.5) {
                            for (double y = aabb.getMinY(); y <= aabb.getMaxY(); y += 0.5) {
                                this.getLevel().addParticle(new ExplodeParticle(new Vector3(x, y, z)));
                            }
                        }
                    }
                    //使用DEATH_SMOKE_CLOUD会导致两次死亡音效
//                    EntityEventPacket pk = new EntityEventPacket();
//                    pk.eid = this.getId();
//                    pk.event = EntityEventPacket.DEATH_SMOKE_CLOUD;
//
//                    Server.broadcastPacket(this.hasSpawned.values(), pk);
                }
                this.despawnFromAll();
                if (!this.isPlayer) {
                    this.close();
                }
            }
            return this.deadTicks < 10;
        }

        int tickDiff = currentTick - this.lastUpdate;

        if (tickDiff <= 0) {
            return false;
        }

        this.lastUpdate = currentTick;

        boolean hasUpdate = this.entityBaseTick(tickDiff);

        if (!this.isImmobile()) {
            this.updateMovement();
        }

        return hasUpdate;
    }

    public boolean mountEntity(Entity entity) {
        return mountEntity(entity, TYPE_RIDE);
    }

    /**
     * Mount an Entity from a/into vehicle
     *
     * @param entity The target Entity
     * @return {@code true} if the mounting successful
     */
    public boolean mountEntity(Entity entity, byte mode) {
        Objects.requireNonNull(entity, "The target of the mounting entity can't be null");

        if (isPassenger(entity) || entity.riding != null && !entity.riding.dismountEntity(entity, false)) {
            return false;
        }

        // Entity entering a vehicle
        EntityVehicleEnterEvent ev = new EntityVehicleEnterEvent(entity, this);
        server.getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return false;
        }

        broadcastLinkPacket(entity, mode);

        // Add variables to entity
        entity.riding = this;
        entity.setDataFlag(DATA_FLAGS, DATA_FLAG_RIDING, true);
        passengers.add(entity);

        entity.setSeatPosition(getMountedOffset(entity));
        updatePassengerPosition(entity);
        return true;
    }

    public boolean dismountEntity(Entity entity) {
        return this.dismountEntity(entity, true);
    }

    public boolean dismountEntity(Entity entity, boolean sendLinks) {
        // Run the events
        EntityVehicleExitEvent ev = new EntityVehicleExitEvent(entity, this);
        server.getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            int seatIndex = this.passengers.indexOf(entity);
            if (seatIndex == 0) {
                this.broadcastLinkPacket(entity, TYPE_RIDE);
            } else if (seatIndex != -1) {
                this.broadcastLinkPacket(entity, TYPE_PASSENGER);
            }
            return false;
        }

        if (sendLinks) {
            broadcastLinkPacket(entity, TYPE_REMOVE);
        }

        // Refurbish the entity
        entity.riding = null;
        entity.setDataFlag(DATA_FLAGS, DATA_FLAG_RIDING, false);
        passengers.remove(entity);

        entity.setSeatPosition(new Vector3f());
        updatePassengerPosition(entity);

        return true;
    }

    protected void broadcastLinkPacket(Entity rider, byte type) {
        SetEntityLinkPacket pk = new SetEntityLinkPacket();
        pk.vehicleUniqueId = getId();         // To the?
        pk.riderUniqueId = rider.getId(); // From who?
        pk.type = type;
        pk.riderInitiated = type > 0;
        Server.broadcastPacket(this.hasSpawned.values(), pk);
    }

    public void updatePassengers() {
        if (this.passengers.isEmpty()) {
            return;
        }

        for (Entity passenger : new ArrayList<>(this.passengers)) {
            if (!passenger.isAlive()) {
                dismountEntity(passenger);
                continue;
            }

            updatePassengerPosition(passenger);
        }
    }

    protected void updatePassengerPosition(Entity passenger) {
        passenger.setPosition(this.add(passenger.getSeatPosition().asVector3()));
    }

    public Vector3f getSeatPosition() {
        return this.getDataPropertyVector3f(DATA_RIDER_SEAT_POSITION);
    }

    public void setSeatPosition(Vector3f pos) {
        this.setDataProperty(new Vector3fEntityData(DATA_RIDER_SEAT_POSITION, pos));
    }

    public Vector3f getMountedOffset(Entity entity) {
        return new Vector3f(0, getHeight() * 0.75f);
    }

    public final void scheduleUpdate() {
        this.level.updateEntities.put(this.id, this);
    }

    public boolean isOnFire() {
        return this.fireTicks > 0;
    }

    public void setOnFire(int seconds) {
        int ticks = seconds * 20;
        if (ticks > this.fireTicks) {
            this.fireTicks = ticks;
        }
    }

    public float getAbsorption() {
        return absorption;
    }

    public void setAbsorption(float absorption) {
        if (absorption != this.absorption) {
            this.absorption = absorption;
            if (this instanceof Player)
                ((Player) this).setAttribute(Attribute.getAttribute(Attribute.ABSORPTION).setValue(absorption));
        }
    }

    @PowerNukkitOnly
    public boolean canBePushed() {
        return true;
    }

    public BlockFace getDirection() {
        double rotation = this.yaw % 360;
        if (rotation < 0) {
            rotation += 360.0;
        }
        if ((0 <= rotation && rotation < 45) || (315 <= rotation && rotation < 360)) {
            return BlockFace.SOUTH;
        } else if (45 <= rotation && rotation < 135) {
            return BlockFace.WEST;
        } else if (135 <= rotation && rotation < 225) {
            return BlockFace.NORTH;
        } else if (225 <= rotation && rotation < 315) {
            return BlockFace.EAST;
        } else {
            return null;
        }
    }

    public void extinguish() {
        this.fireTicks = 0;
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_ONFIRE, false);
    }

    public boolean canTriggerWalking() {
        return true;
    }

    @PowerNukkitXDifference(since = "1.19.20-r5")
    public void resetFallDistance() {
        if (this.level != null) {
            this.highestPosition = this.level.getMinHeight();
        } else {
            this.highestPosition = 0;
        }
    }

    protected void updateFallState(boolean onGround) {
        if (onGround) {
            fallDistance = (float) (this.highestPosition - this.y);

            if (fallDistance > 0) {
                // check if we fell into at least 1 block of water
                var lb = this.getLevelBlock();
                var lb2 = this.getLevelBlockAtLayer(1);
                if (this instanceof EntityLiving && !(lb instanceof BlockWater || lb instanceof BlockFence ||
                        (lb2 instanceof BlockWater && lb2.getMaxY() == 1d))) {
                    this.fall(fallDistance);
                }
                this.resetFallDistance();
            }
        }
    }

    public AxisAlignedBB getBoundingBox() {
        return this.boundingBox;
    }

    public void fall(float fallDistance) {//todo: check why @param fallDistance always less than the real distance
        if (this.hasEffect(Effect.SLOW_FALLING)) {
            return;
        }

        Location floorLocation = this.floor();
        Block down = this.level.getBlock(floorLocation.down());

        EntityFallEvent event = new EntityFallEvent(this, down, fallDistance);
        this.server.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        fallDistance = event.getFallDistance();

        if ((!this.isPlayer || level.getGameRules().getBoolean(GameRule.FALL_DAMAGE)) && down.useDefaultFallDamage()) {
            int jumpBoost = this.hasEffect(Effect.JUMP_BOOST) ? (getEffect(Effect.JUMP_BOOST).getAmplifier() + 1) : 0;
            float damage = fallDistance - 3 - jumpBoost;

            if (damage > 0) {
                if (!this.isSneaking()) {
                    if (!(this instanceof EntityItem item) || item.getItem().getBlockId() != BlockID.WOOL) {
                        this.level.getVibrationManager().callVibrationEvent(new VibrationEvent(this, this.clone(), VibrationType.HIT_GROUND));
                    }
                }
                this.attack(new EntityDamageEvent(this, DamageCause.FALL, damage));
            }
        }

        down.onEntityFallOn(this, fallDistance);

        if (fallDistance > 0.75) {//todo: moving these into their own classes (method "onEntityFallOn()")
            if (down.getId() == Block.FARMLAND) {
                if (onPhysicalInteraction(down, false)) {
                    return;
                }
                var farmEvent = new FarmLandDecayEvent(this, down);
                this.server.getPluginManager().callEvent(farmEvent);
                if (farmEvent.isCancelled()) return;
                this.level.setBlock(down, new BlockDirt(), false, true);
                return;
            }

            Block floor = this.level.getBlock(floorLocation);

            if (floor instanceof BlockTurtleEgg) {
                if (onPhysicalInteraction(floor, ThreadLocalRandom.current().nextInt(10) >= 3)) {
                    return;
                }
                this.level.useBreakOn(this, null, null, true);
            }
        }
    }

    @PowerNukkitXDifference(info = "change to protected")
    protected boolean onPhysicalInteraction(Block block, boolean cancelled) {
        Event ev;

        if (this instanceof Player) {
            ev = new PlayerInteractEvent((Player) this, null, block, null, Action.PHYSICAL);
        } else {
            ev = new EntityInteractEvent(this, block);
        }

        ev.setCancelled(cancelled);

        this.server.getPluginManager().callEvent(ev);
        return ev.isCancelled();
    }

    public void handleLavaMovement() {
        //todo
    }

    public void moveFlying(float strafe, float forward, float friction) {
        // This is special for Nukkit! :)
        float speed = strafe * strafe + forward * forward;
        if (speed >= 1.0E-4F) {
            speed = MathHelper.sqrt(speed);
            if (speed < 1.0F) {
                speed = 1.0F;
            }
            speed = friction / speed;
            strafe *= speed;
            forward *= speed;
            float nest = MathHelper.sin((float) (this.yaw * 3.1415927F / 180.0F));
            float place = MathHelper.cos((float) (this.yaw * 3.1415927F / 180.0F));
            this.motionX += strafe * place - forward * nest;
            this.motionZ += forward * place + strafe * nest;
        }
    }

    public void onCollideWithPlayer(EntityHuman entityPlayer) {

    }

    public void applyEntityCollision(Entity entity) {
        if (entity.riding != this && !entity.passengers.contains(this)) {
            double dx = entity.x - this.x;
            double dy = entity.z - this.z;
            double dz = NukkitMath.getDirection(dx, dy);

            if (dz >= 0.009999999776482582D) {
                dz = MathHelper.sqrt((float) dz);
                dx /= dz;
                dy /= dz;
                double d3 = 1.0D / dz;

                if (d3 > 1.0D) {
                    d3 = 1.0D;
                }

                dx *= d3;
                dy *= d3;
                dx *= 0.05000000074505806;
                dy *= 0.05000000074505806;
                dx *= 1F + entityCollisionReduction;

                if (this.riding == null) {
                    motionX -= dx;
                    motionZ -= dy;
                }
            }
        }
    }

    public void onStruckByLightning(Entity entity) {
        if (this.attack(new EntityDamageByEntityEvent(entity, this, DamageCause.LIGHTNING, 5))) {
            if (this.fireTicks < 8 * 20) {
                this.setOnFire(8);
            }
        }
    }

    @PowerNukkitOnly
    public void onPushByPiston(BlockEntityPistonArm piston) {

    }

    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        return onInteract(player, item);
    }

    public boolean onInteract(Player player, Item item) {
        return false;
    }

    protected boolean switchLevel(Level targetLevel) {
        if (this.closed) {
            return false;
        }

        if (this.isValid()) {
            EntityLevelChangeEvent ev = new EntityLevelChangeEvent(this, this.level, targetLevel);
            this.server.getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                return false;
            }

            this.level.removeEntity(this);
            if (this.chunk != null) {
                this.chunk.removeEntity(this);
            }
            this.despawnFromAll();
        }

        this.setLevel(targetLevel);
        this.level.addEntity(this);
        this.chunk = null;

        return true;
    }

    @NotNull
    public Position getPosition() {
        return new Position(this.x, this.y, this.z, this.level);
    }

    @Override
    @NotNull
    public Location getLocation() {
        return new Location(this.x, this.y, this.z, this.yaw, this.pitch, this.headYaw, this.level);
    }

    @PowerNukkitOnly
    public boolean isTouchingWater() {
        return hasWaterAt(0) || hasWaterAt(this.getEyeHeight());
    }

    public boolean isInsideOfWater() {
        return hasWaterAt(this.getEyeHeight());
    }

    @PowerNukkitXOnly
    public boolean isUnderBlock() {
        int x = this.getFloorX();
        int y = this.getFloorY();
        int z = this.getFloorZ();
        for (int i = y + 1; i <= this.getLevel().getMaxHeight(); i++)
            if (this.getLevel().getBlock(x, i, z).getId() != BlockID.AIR) return true;
        return false;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @PowerNukkitXDifference(info = "Make as public method", since = "1.19.60-r1")
    public boolean hasWaterAt(float height) {
        return hasWaterAt(height, false);
    }

    @PowerNukkitXOnly
    @Since("1.19.60-r1")
    protected boolean hasWaterAt(float height, boolean tickCached) {
        double y = this.y + height;
        Block block = tickCached ?
                this.level.getTickCachedBlock(this.temporalVector.setComponents(NukkitMath.floorDouble(this.x), NukkitMath.floorDouble(y), NukkitMath.floorDouble(this.z))) :
                this.level.getBlock(this.temporalVector.setComponents(NukkitMath.floorDouble(this.x), NukkitMath.floorDouble(y), NukkitMath.floorDouble(this.z)));

        boolean layer1 = false;
        Block block1 = tickCached ? block.getTickCachedLevelBlockAtLayer(1) : block.getLevelBlockAtLayer(1);
        if (!(block instanceof BlockBubbleColumn) && (
                block instanceof BlockWater
                        || (layer1 = block1 instanceof BlockWater))) {
            BlockWater water = (BlockWater) (layer1 ? block1 : block);
            double f = (block.y + 1) - (water.getFluidHeightPercent() - 0.1111111);
            return y < f;
        }

        return false;
    }

    public boolean isInsideOfSolid() {
        double y = this.y + this.getEyeHeight();
        Block block = this.level.getBlock(
                this.temporalVector.setComponents(
                        NukkitMath.floorDouble(this.x),
                        NukkitMath.floorDouble(y),
                        NukkitMath.floorDouble(this.z))
        );

        AxisAlignedBB bb = block.getBoundingBox();

        return bb != null && block.isSolid() && !block.isTransparent() && bb.intersectsWith(this.getBoundingBox());

    }

    public boolean isInsideOfFire() {
        for (Block block : this.getCollisionBlocks()) {
            if (block instanceof BlockFire) {
                return true;
            }
        }

        return false;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public <T extends Block> boolean collideWithBlock(Class<T> classType) {
        for (Block block : this.getCollisionBlocks()) {
            if (classType.isInstance(block)) {
                return true;
            }
        }
        return false;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean isInsideOfLava() {
        for (Block block : this.getCollisionBlocks()) {
            if (block instanceof BlockLava) {
                return true;
            }
        }

        return false;
    }

    public boolean isOnLadder() {
        Block b = this.getLevelBlock();

        return b.getId() == Block.LADDER;
    }

    public boolean fastMove(double dx, double dy, double dz) {
        if (dx == 0 && dy == 0 && dz == 0) {
            return true;
        }

        AxisAlignedBB newBB = this.boundingBox.getOffsetBoundingBox(dx, dy, dz);

        if (server.getAllowFlight()
                || isPlayer && ((Player) this).getAdventureSettings().get(AdventureSettings.Type.NO_CLIP)
                || !this.level.hasCollision(this, newBB, false)) {
            this.boundingBox = newBB;
        }

        this.x = (this.boundingBox.getMinX() + this.boundingBox.getMaxX()) / 2;
        this.y = this.boundingBox.getMinY() - this.ySize;
        this.z = (this.boundingBox.getMinZ() + this.boundingBox.getMaxZ()) / 2;

        this.checkChunks();

        if ((!this.onGround || dy != 0) && !this.noClip) {
            AxisAlignedBB bb = this.boundingBox.clone();
            bb.setMinY(bb.getMinY() - 0.75);

            this.onGround = this.level.getCollisionBlocks(bb).length > 0;
        }
        this.isCollided = this.onGround;
        this.updateFallState(this.onGround);
        return true;
    }

    //Player do not use
    @PowerNukkitXDifference(since = "1.19.60-r1", info = "The onGround is updated when the entity motion is 0")
    public boolean move(double dx, double dy, double dz) {
        if (dx == 0 && dz == 0 && dy == 0) {
            this.onGround = !this.getPosition().setComponents(this.down()).getTickCachedLevelBlock().canPassThrough();
            return true;
        }

        if (this.keepMovement) {
            this.boundingBox.offset(dx, dy, dz);
            this.setPosition(this.temporalVector.setComponents((this.boundingBox.getMinX() + this.boundingBox.getMaxX()) / 2, this.boundingBox.getMinY(), (this.boundingBox.getMinZ() + this.boundingBox.getMaxZ()) / 2));
            this.onGround = this.isPlayer;
            return true;
        } else {

            this.ySize *= 0.4;

            double movX = dx;
            double movY = dy;
            double movZ = dz;

            AxisAlignedBB axisalignedbb = this.boundingBox.clone();

            var list = this.noClip ? AxisAlignedBB.EMPTY_LIST : this.level.fastCollisionCubes(this, this.boundingBox.addCoord(dx, dy, dz), false);

            for (AxisAlignedBB bb : list) {
                dy = bb.calculateYOffset(this.boundingBox, dy);
            }

            this.boundingBox.offset(0, dy, 0);

            boolean fallingFlag = (this.onGround || (dy != movY && movY < 0));

            for (AxisAlignedBB bb : list) {
                dx = bb.calculateXOffset(this.boundingBox, dx);
            }

            this.boundingBox.offset(dx, 0, 0);

            for (AxisAlignedBB bb : list) {
                dz = bb.calculateZOffset(this.boundingBox, dz);
            }

            this.boundingBox.offset(0, 0, dz);

            if (this.getStepHeight() > 0 && fallingFlag && this.ySize < 0.05 && (movX != dx || movZ != dz)) {
                double cx = dx;
                double cy = dy;
                double cz = dz;
                dx = movX;
                dy = this.getStepHeight();
                dz = movZ;

                AxisAlignedBB axisalignedbb1 = this.boundingBox.clone();

                this.boundingBox.setBB(axisalignedbb);

                list = this.level.fastCollisionCubes(this, this.boundingBox.addCoord(dx, dy, dz), false);

                for (AxisAlignedBB bb : list) {
                    dy = bb.calculateYOffset(this.boundingBox, dy);
                }

                this.boundingBox.offset(0, dy, 0);

                for (AxisAlignedBB bb : list) {
                    dx = bb.calculateXOffset(this.boundingBox, dx);
                }

                this.boundingBox.offset(dx, 0, 0);

                for (AxisAlignedBB bb : list) {
                    dz = bb.calculateZOffset(this.boundingBox, dz);
                }

                this.boundingBox.offset(0, 0, dz);

                this.boundingBox.offset(0, 0, dz);

                if ((cx * cx + cz * cz) >= (dx * dx + dz * dz)) {
                    dx = cx;
                    dy = cy;
                    dz = cz;
                    this.boundingBox.setBB(axisalignedbb1);
                } else {
                    this.ySize += 0.5;
                }

            }

            this.x = (this.boundingBox.getMinX() + this.boundingBox.getMaxX()) / 2;
            this.y = this.boundingBox.getMinY() - this.ySize;
            this.z = (this.boundingBox.getMinZ() + this.boundingBox.getMaxZ()) / 2;

            this.checkChunks();

            this.checkGroundState(movX, movY, movZ, dx, dy, dz);
            this.updateFallState(this.onGround);

            if (movX != dx) {
                this.motionX = 0;
            }

            if (movY != dy) {
                this.motionY = 0;
            }

            if (movZ != dz) {
                this.motionZ = 0;
            }

            //TODO: vehicle collision events (first we need to spawn them!)
            return true;
        }
    }

    protected void checkGroundState(double movX, double movY, double movZ, double dx, double dy, double dz) {
        if (this.noClip) {
            this.isCollidedVertically = false;
            this.isCollidedHorizontally = false;
            this.isCollided = false;
            this.onGround = false;
        } else {
            this.isCollidedVertically = movY != dy;
            this.isCollidedHorizontally = (movX != dx || movZ != dz);
            this.isCollided = (this.isCollidedHorizontally || this.isCollidedVertically);
            this.onGround = (movY != dy && movY < 0);
        }
    }

    public List<Block> getBlocksAround() {
        if (this.blocksAround == null) {
            int minX = NukkitMath.floorDouble(this.boundingBox.getMinX());
            int minY = NukkitMath.floorDouble(this.boundingBox.getMinY());
            int minZ = NukkitMath.floorDouble(this.boundingBox.getMinZ());
            int maxX = NukkitMath.ceilDouble(this.boundingBox.getMaxX());
            int maxY = NukkitMath.ceilDouble(this.boundingBox.getMaxY());
            int maxZ = NukkitMath.ceilDouble(this.boundingBox.getMaxZ());

            this.blocksAround = new ArrayList<>();

            for (int z = minZ; z <= maxZ; ++z) {
                for (int x = minX; x <= maxX; ++x) {
                    for (int y = minY; y <= maxY; ++y) {
                        Block block = this.level.getBlock(this.temporalVector.setComponents(x, y, z));
                        this.blocksAround.add(block);
                    }
                }
            }
        }

        return this.blocksAround;
    }

    @PowerNukkitXOnly
    @Since("1.19.50-r2")
    public List<Block> getTickCachedBlocksAround() {
        if (this.blocksAround == null) {
            int minX = NukkitMath.floorDouble(this.boundingBox.getMinX());
            int minY = NukkitMath.floorDouble(this.boundingBox.getMinY());
            int minZ = NukkitMath.floorDouble(this.boundingBox.getMinZ());
            int maxX = NukkitMath.ceilDouble(this.boundingBox.getMaxX());
            int maxY = NukkitMath.ceilDouble(this.boundingBox.getMaxY());
            int maxZ = NukkitMath.ceilDouble(this.boundingBox.getMaxZ());

            this.blocksAround = new ArrayList<>();

            for (int z = minZ; z <= maxZ; ++z) {
                for (int x = minX; x <= maxX; ++x) {
                    for (int y = minY; y <= maxY; ++y) {
                        this.blocksAround.add(this.level.getTickCachedBlock(this.temporalVector.setComponents(x, y, z)));
                    }
                }
            }
        }

        return this.blocksAround;
    }

    public List<Block> getCollisionBlocks() {
        if (this.collisionBlocks == null) {
            this.collisionBlocks = new ArrayList<>();

            for (Block b : getBlocksAround()) {
                if (b.collidesWithBB(this.getBoundingBox(), true)) {
                    this.collisionBlocks.add(b);
                }
            }
        }

        return this.collisionBlocks;
    }

    @PowerNukkitXOnly
    @Since("1.19.50-r2")
    public List<Block> getTickCachedCollisionBlocks() {
        if (this.collisionBlocks == null) {
            this.collisionBlocks = new ArrayList<>();

            for (Block b : getTickCachedBlocksAround()) {
                if (b.collidesWithBB(this.getBoundingBox(), true)) {
                    this.collisionBlocks.add(b);
                }
            }
        }

        return this.collisionBlocks;
    }

    /**
     * Returns whether this entity can be moved by currents in liquids.
     *
     * @return boolean
     */
    public boolean canBeMovedByCurrents() {
        return true;
    }

    protected void checkBlockCollision() {
        if (this.noClip) {
            return;
        }

        boolean needsRecalcCurrent = true;
        if (this instanceof EntityPhysical entityPhysical) {
            needsRecalcCurrent = entityPhysical.needsRecalcMovement;
        }

        Vector3 vector = new Vector3(0, 0, 0);
        boolean portal = false;
        boolean scaffolding = false;
        boolean endPortal = false;
        for (var block : this.getTickCachedCollisionBlocks()) {
            switch (block.getId()) {
                case Block.NETHER_PORTAL -> portal = true;
                case BlockID.SCAFFOLDING -> scaffolding = true;
                case BlockID.END_PORTAL -> endPortal = true;
            }

            block.onEntityCollide(this);
            block.getTickCachedLevelBlockAtLayer(1).onEntityCollide(this);
            if (needsRecalcCurrent)
                block.addVelocityToEntity(this, vector);
        }

        setDataFlag(DATA_FLAGS_EXTENDED, DATA_FLAG_IN_SCAFFOLDING, scaffolding);

        if (Math.abs(this.y % 1) > 0.125) {
            int minX = NukkitMath.floorDouble(boundingBox.getMinX());
            int minZ = NukkitMath.floorDouble(boundingBox.getMinZ());
            int maxX = NukkitMath.ceilDouble(boundingBox.getMaxX());
            int maxZ = NukkitMath.ceilDouble(boundingBox.getMaxZ());
            int Y = (int) y;

            outerScaffolding:
            for (int i = minX; i <= maxX; i++) {
                for (int j = minZ; j <= maxZ; j++) {
                    if (level.getBlockIdAt(i, Y, j) == BlockID.SCAFFOLDING) {
                        setDataFlag(DATA_FLAGS_EXTENDED, DATA_FLAG_OVER_SCAFFOLDING, true);
                        break outerScaffolding;
                    }
                }
            }
        }

        if (endPortal) {
            if (!inEndPortal) {
                inEndPortal = true;
                if (this.getRiding() == null && this.getPassengers().isEmpty() && !(this instanceof EntityEnderDragon)) {
                    EntityPortalEnterEvent ev = new EntityPortalEnterEvent(this, PortalType.END);
                    getServer().getPluginManager().callEvent(ev);

                    if (!ev.isCancelled() && (level == EnumLevel.OVERWORLD.getLevel() || level == EnumLevel.THE_END.getLevel())) {
                        final Position newPos = EnumLevel.moveToTheEnd(this);
                        if (newPos != null) {
                            if (newPos.getLevel().getDimension() == Level.DIMENSION_THE_END) {
                                if (teleport(newPos.add(0.5, 1, 0.5), PlayerTeleportEvent.TeleportCause.END_PORTAL)) {
                                    server.getScheduler().scheduleDelayedTask(new Task() {
                                        @Override
                                        public void onRun(int currentTick) {
                                            // dirty hack to make sure chunks are loaded and generated before spawning player
                                            teleport(newPos.add(0.5, 1, 0.5), PlayerTeleportEvent.TeleportCause.END_PORTAL);
                                            BlockEndPortal.spawnObsidianPlatform(newPos);
                                        }
                                    }, 5);
                                }
                            } else {
                                if (teleport(newPos, PlayerTeleportEvent.TeleportCause.END_PORTAL)) {
                                    server.getScheduler().scheduleDelayedTask(new Task() {
                                        @Override
                                        public void onRun(int currentTick) {
                                            // dirty hack to make sure chunks are loaded and generated before spawning player
                                            teleport(newPos, PlayerTeleportEvent.TeleportCause.END_PORTAL);
                                        }
                                    }, 5);
                                }
                            }
                        }
                    }
                }
            }
        } else {
            inEndPortal = false;
        }

        if (portal) {
            if (this.inPortalTicks <= 80) {
                // 81 means the server won't try to teleport
                this.inPortalTicks++;
            }
        } else {
            this.inPortalTicks = 0;
        }

        if (needsRecalcCurrent)
            if (vector.lengthSquared() > 0) {
                vector = vector.normalize();
                double d = 0.018d;
                var dx = vector.x * d;
                var dy = vector.y * d;
                var dz = vector.z * d;
                this.motionX += dx;
                this.motionY += dy;
                this.motionZ += dz;
                if (this instanceof EntityPhysical entityPhysical) {
                    entityPhysical.previousCurrentMotion.x = dx;
                    entityPhysical.previousCurrentMotion.y = dy;
                    entityPhysical.previousCurrentMotion.z = dz;
                }
            } else {
                if (this instanceof EntityPhysical entityPhysical) {
                    entityPhysical.previousCurrentMotion.x = 0;
                    entityPhysical.previousCurrentMotion.y = 0;
                    entityPhysical.previousCurrentMotion.z = 0;
                }
            }
        else ((EntityPhysical) this).addPreviousLiquidMovement();
    }

    public boolean setPositionAndRotation(Vector3 pos, double yaw, double pitch) {
        this.setRotation(yaw, pitch);
        return this.setPosition(pos);
    }

    /**
     * Sets position and rotation.
     *
     * @param pos     the pos
     * @param yaw     the yaw
     * @param pitch   the pitch
     * @param headYaw the head yaw
     * @return 切换地图失败会返回false
     */
    public boolean setPositionAndRotation(Vector3 pos, double yaw, double pitch, double headYaw) {
        this.setRotation(yaw, pitch, headYaw);
        return this.setPosition(pos);
    }

    public void setRotation(double yaw, double pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.scheduleUpdate();
    }

    @Since("FUTURE")
    public void setRotation(double yaw, double pitch, double headYaw) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.headYaw = headYaw;
        this.scheduleUpdate();
    }

    /**
     * Whether the entity can active pressure plates.
     * Used for {@link cn.nukkit.entity.passive.EntityBat}s only.
     *
     * @return triggers pressure plate
     */
    public boolean doesTriggerPressurePlate() {
        return true;
    }

    public boolean canPassThrough() {
        return true;
    }

    protected void checkChunks() {
        if (this.chunk == null || (this.chunk.getX() != ((int) this.x >> 4)) || this.chunk.getZ() != ((int) this.z >> 4)) {
            if (this.chunk != null) {
                this.chunk.removeEntity(this);
            }
            this.chunk = this.level.getChunk((int) this.x >> 4, (int) this.z >> 4, true);

            if (!this.justCreated) {
                Map<Integer, Player> newChunk = this.level.getChunkPlayers((int) this.x >> 4, (int) this.z >> 4);
                for (Player player : new ArrayList<>(this.hasSpawned.values())) {
                    if (!newChunk.containsKey(player.getLoaderId())) {
                        this.despawnFrom(player);
                    } else {
                        newChunk.remove(player.getLoaderId());
                    }
                }

                for (Player player : newChunk.values()) {
                    this.spawnTo(player);
                }
            }

            if (this.chunk == null) {
                return;
            }

            this.chunk.addEntity(this);
        }
    }

    public boolean setPosition(Vector3 pos) {
        if (this.closed) {
            return false;
        }

        if (pos instanceof Position && ((Position) pos).level != null && ((Position) pos).level != this.level) {
            if (!this.switchLevel(((Position) pos).getLevel())) {
                return false;
            }
        }

        this.x = pos.x;
        this.y = pos.y;
        this.z = pos.z;

        this.recalculateBoundingBox(false); // Don't need to send BB height/width to client on position change

        this.checkChunks();

        return true;
    }

    public Vector3 getMotion() {
        return new Vector3(this.motionX, this.motionY, this.motionZ);
    }

    /**
     * 设置一个运动向量(会使得实体移动这个向量的距离，非精准移动)<p/>
     * <p>
     * Set a motion vector (will make the entity move the distance of this vector, not move precisely)
     *
     * @param motion 运动向量<br>a motion vector
     * @return boolean
     */
    public boolean setMotion(Vector3 motion) {
        if (!this.justCreated) {
            EntityMotionEvent ev = new EntityMotionEvent(this, motion);
            this.server.getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                return false;
            }
        }

        this.motionX = motion.x;
        this.motionY = motion.y;
        this.motionZ = motion.z;

        if (!this.justCreated && !this.isImmobile()) {
            this.updateMovement();
        }

        return true;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public void kill() {
        this.health = 0;
        this.scheduleUpdate();

        for (Entity passenger : new ArrayList<>(this.passengers)) {
            dismountEntity(passenger);
        }
    }

    public boolean teleport(Vector3 pos) {
        return this.teleport(pos, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    public boolean teleport(Vector3 pos, PlayerTeleportEvent.TeleportCause cause) {
        return this.teleport(Location.fromObject(pos, this.level, this.yaw, this.pitch, this.headYaw), cause);
    }

    public boolean teleport(Position pos) {
        return this.teleport(pos, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    public boolean teleport(Position pos, PlayerTeleportEvent.TeleportCause cause) {
        return this.teleport(Location.fromObject(pos, pos.level, this.yaw, this.pitch, this.headYaw), cause);
    }

    public boolean teleport(Location location) {
        return this.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    public boolean teleport(Location location, PlayerTeleportEvent.TeleportCause cause) {
        double yaw = location.yaw;
        double pitch = location.pitch;

        Location from = this.getLocation();
        Location to = location;
        if (cause != null) {
            EntityTeleportEvent ev = new EntityTeleportEvent(this, from, to, cause);
            this.server.getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                return false;
            }
            to = ev.getTo();
        }

        final Entity currentRide = getRiding();
        if (currentRide != null && !currentRide.dismountEntity(this)) {
            return false;
        }

        this.positionChanged = true;
        this.ySize = 0;

        this.setMotion(this.temporalVector.setComponents(0, 0, 0));

        if (this.setPositionAndRotation(to, yaw, pitch)) {
            this.resetFallDistance();
            this.onGround = !this.noClip;
            this.updateMovement();
            return true;
        }

        return false;
    }

    //return runtime id (changed after restart the server)
    public long getId() {
        return this.id;
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public UUID getUniqueId() {
        return this.entityUniqueId;
    }

    public void respawnToAll() {
        Player[] players = this.hasSpawned.values().toArray(Player.EMPTY_ARRAY);
        this.hasSpawned.clear();

        for (Player player : players) {
            this.spawnTo(player);
        }
    }

    public void spawnToAll() {
        if (this.chunk == null || this.closed) {
            return;
        }

        for (Player player : this.level.getChunkPlayers(this.chunk.getX(), this.chunk.getZ()).values()) {
            if (player.isOnline()) {
                this.spawnTo(player);
            }
        }
    }

    public void despawnFromAll() {
        for (Player player : new ArrayList<>(this.hasSpawned.values())) {
            this.despawnFrom(player);
        }
    }

    public void close() {
        close(true);
    }

    private void close(boolean despawn) {
        if (!this.closed) {
            this.closed = true;

            if (despawn) {
                try {
                    EntityDespawnEvent event = new EntityDespawnEvent(this);

                    this.server.getPluginManager().callEvent(event);

                    if (event.isCancelled()) {
                        this.closed = false;
                        return;
                    }
                } catch (Throwable e) {
                    this.closed = false;
                    throw e;
                }
            }

            try {
                this.despawnFromAll();
            } finally {
                try {
                    if (this.chunk != null) {
                        this.chunk.removeEntity(this);
                    }
                } finally {
                    if (this.level != null) {
                        this.level.removeEntity(this);
                    }
                }
            }
        }
    }

    public boolean setDataProperty(EntityData data) {
        return setDataProperty(data, true);
    }

    public boolean setDataProperty(EntityData data, boolean send) {
        if (Objects.equals(data, this.getDataProperties().get(data.getId()))) {
            return false;
        }

        this.getDataProperties().put(data);
        if (send) {
            EntityMetadata metadata = new EntityMetadata();
            metadata.put(this.dataProperties.get(data.getId()));
            if (data.getId() == DATA_FLAGS_EXTENDED) {
                metadata.put(this.dataProperties.get(DATA_FLAGS));
            }
            this.sendData(this.hasSpawned.values().toArray(Player.EMPTY_ARRAY), metadata);
        }
        return true;
    }

    public EntityMetadata getDataProperties() {
        return this.dataProperties;
    }

    public EntityData getDataProperty(int id) {
        return this.getDataProperties().get(id);
    }

    public int getDataPropertyInt(int id) {
        return this.getDataProperties().getInt(id);
    }

    public int getDataPropertyShort(int id) {
        return this.getDataProperties().getShort(id);
    }

    public int getDataPropertyByte(int id) {
        return this.getDataProperties().getByte(id);
    }

    public boolean getDataPropertyBoolean(int id) {
        return this.getDataProperties().getBoolean(id);
    }

    public long getDataPropertyLong(int id) {
        return this.getDataProperties().getLong(id);
    }

    public String getDataPropertyString(int id) {
        return this.getDataProperties().getString(id);
    }

    public float getDataPropertyFloat(int id) {
        return this.getDataProperties().getFloat(id);
    }

    public CompoundTag getDataPropertyNBT(int id) {
        return this.getDataProperties().getNBT(id);
    }

    public Vector3 getDataPropertyPos(int id) {
        return this.getDataProperties().getPosition(id);
    }

    public Vector3f getDataPropertyVector3f(int id) {
        return this.getDataProperties().getFloatPosition(id);
    }

    public int getDataPropertyType(int id) {
        return this.getDataProperties().exists(id) ? this.getDataProperty(id).getType() : -1;
    }

    public void setDataFlag(int propertyId, int id) {
        this.setDataFlag(propertyId, id, true);
    }

    public void setDataFlag(int propertyId, int id, boolean value) {
        if (this.getDataFlag(propertyId, id) != value) {
            if (propertyId == EntityHuman.DATA_PLAYER_FLAGS) {
                byte flags = (byte) this.getDataPropertyByte(propertyId);
                flags ^= 1 << id;
                this.setDataProperty(new ByteEntityData(propertyId, flags));
            } else {
                long flags = this.getDataPropertyLong(propertyId);
                flags ^= 1L << id;
                this.setDataProperty(new LongEntityData(propertyId, flags));
            }

        }
    }

    public boolean getDataFlag(int propertyId, int id) {
        return (((propertyId == EntityHuman.DATA_PLAYER_FLAGS ? this.getDataPropertyByte(propertyId) & 0xff : this.getDataPropertyLong(propertyId))) & (1L << id)) > 0;
    }

    @Override
    public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
        this.server.getEntityMetadata().setMetadata(this, metadataKey, newMetadataValue);
    }

    @Override
    public List<MetadataValue> getMetadata(String metadataKey) {
        return this.server.getEntityMetadata().getMetadata(this, metadataKey);
    }

    @Override
    public boolean hasMetadata(String metadataKey) {
        return this.server.getEntityMetadata().hasMetadata(this, metadataKey);
    }

    @Override
    public void removeMetadata(String metadataKey, Plugin owningPlugin) {
        this.server.getEntityMetadata().removeMetadata(this, metadataKey, owningPlugin);
    }

    public Server getServer() {
        return server;
    }

    @PowerNukkitOnly
    public boolean isUndead() {
        return false;
    }

    @PowerNukkitOnly
    @Since("1.2.1.0-PN")
    public boolean isInEndPortal() {
        return inEndPortal;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean isPreventingSleep(Player player) {
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Entity other = (Entity) obj;
        return this.getId() == other.getId();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = (int) (29 * hash + this.getId());
        return hash;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean isSpinAttacking() {
        return this.getDataFlag(DATA_FLAGS, DATA_FLAG_SPIN_ATTACK);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setSpinAttacking(boolean value) {
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_SPIN_ATTACK, value);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setSpinAttacking() {
        this.setSpinAttacking(true);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean isNoClip() {
        return noClip;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setNoClip(boolean noClip) {
        this.noClip = noClip;
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_HAS_COLLISION, noClip);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean isBoss() {
        return false;
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public void addTag(String tag) {
        this.namedTag.putList(this.namedTag.getList("Tags", StringTag.class).add(new StringTag("", tag)));
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public void removeTag(String tag) {
        ListTag<StringTag> tags = this.namedTag.getList("Tags", StringTag.class);
        tags.remove(new StringTag("", tag));
        this.namedTag.putList(tags);
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public boolean containTag(String tag) {
        return this.namedTag.getList("Tags", StringTag.class).getAll().stream().anyMatch(t -> t.data.equals(tag));
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public List<StringTag> getAllTags() {
        return this.namedTag.getList("Tags", StringTag.class).getAll();
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public float getFreezingEffectStrength() {
        return ((FloatEntityData) this.getDataProperty(DATA_FREEZING_EFFECT_STRENGTH)).getData();
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public void setFreezingEffectStrength(float strength) {
        if (strength < 0 || strength > 1)
            throw new IllegalArgumentException("Freezing Effect Strength must be between 0 and 1");
        this.setDataProperty(new FloatEntityData(DATA_FREEZING_EFFECT_STRENGTH, strength));
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public int getFreezingTicks() {
        return this.freezingTicks;
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public void setFreezingTicks(int ticks) {
        if (ticks < 0) this.freezingTicks = 0;
        else if (ticks > 140) this.freezingTicks = 140;
        else this.freezingTicks = ticks;
        setFreezingEffectStrength(ticks / 140f);
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public void addFreezingTicks(int increments) {
        if (freezingTicks + increments < 0) this.freezingTicks = 0;
        else if (freezingTicks + increments > 140) this.freezingTicks = 140;
        else this.freezingTicks += increments;
        setFreezingEffectStrength(this.freezingTicks / 140f);
    }

    @PowerNukkitXOnly
    @Since("1.19.21-r4")
    public void setAmbientSoundInterval(float interval) {
        this.setDataProperty(new FloatEntityData(Entity.DATA_AMBIENT_SOUND_INTERVAL, interval));
    }

    @PowerNukkitXOnly
    @Since("1.19.21-r4")
    public void setAmbientSoundIntervalRange(float range) {
        this.setDataProperty(new FloatEntityData(Entity.DATA_AMBIENT_SOUND_INTERVAL_RANGE, range));
    }

    @PowerNukkitXOnly
    @Since("1.19.21-r4")
    public void setAmbientSoundEvent(Sound sound) {
        this.setAmbientSoundEventName(sound.getSound());
    }

    @PowerNukkitXOnly
    @Since("1.19.21-r4")
    public void setAmbientSoundEventName(String eventName) {
        this.setDataProperty(new StringEntityData(Entity.DATA_AMBIENT_SOUND_EVENT_NAME, eventName));
    }

    @PowerNukkitXOnly
    @Since("1.19.50-r3")
    public void playAnimation(AnimateEntityPacket.Animation animation) {
        var viewers = new HashSet<>(this.getViewers().values());
        if (this.isPlayer) viewers.add((Player) this);
        playAnimation(animation, viewers);
    }

    /**
     * Play the animation of this entity to a specified group of players
     * <p>
     * 向指定玩家群体播放此实体的动画
     *
     * @param animation 动画对象 Animation objects
     * @param players   可视玩家 Visible Player
     */
    @PowerNukkitXOnly
    @Since("1.19.50-r3")
    public void playAnimation(AnimateEntityPacket.Animation animation, Collection<Player> players) {
        var pk = new AnimateEntityPacket();
        pk.parseFromAnimation(animation);
        pk.getEntityRuntimeIds().add(this.getId());
        pk.encode();
        Server.broadcastPacket(players, pk);
    }

    @PowerNukkitXOnly
    @Since("1.19.60-r1")
    public void playActionAnimation(AnimatePacket.Action action, float rowingTime) {
        var viewers = new HashSet<>(this.getViewers().values());
        if (this.isPlayer) viewers.add((Player) this);
        playActionAnimation(action, rowingTime, viewers);
    }

    /**
     * Play the action animation of this entity to a specified group of players
     * <p>
     * 向指定玩家群体播放此实体的action动画
     *
     * @param action     the action
     * @param rowingTime the rowing time
     * @param players    可视玩家 Visible Player
     */
    @PowerNukkitXOnly
    @Since("1.19.60-r1")
    public void playActionAnimation(AnimatePacket.Action action, float rowingTime, Collection<Player> players) {
        var pk = new AnimatePacket();
        pk.action = action;
        pk.rowingTime = rowingTime;
        pk.eid = this.getId();
        pk.encode();
        Server.broadcastPacket(players, pk);
    }

    private record OldStringClass(String key, Class<? extends Entity> value) {
    }
}
