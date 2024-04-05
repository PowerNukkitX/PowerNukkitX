package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector3f;
import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static cn.nukkit.utils.Utils.dynamic;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LevelEventPacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.LEVEL_EVENT_PACKET;

    public static final int EVENT_UNDEFINED = dynamic(0);
    public static final int EVENT_SOUND_CLICK = dynamic(1000);
    public static final int EVENT_SOUND_CLICK_FAIL = dynamic(1001);
    public static final int EVENT_SOUND_LAUNCH = dynamic(1002);
    public static final int EVENT_SOUND_DOOR_OPEN = dynamic(1003);
    public static final int EVENT_SOUND_FIZZ = dynamic(1004);
    public static final int EVENT_SOUND_FUSE = dynamic(1005);
    public static final int EVENT_SOUND_PLAY_RECORDING = dynamic(1006);
    public static final int EVENT_SOUND_GHAST_WARNING = dynamic(1007);
    public static final int EVENT_SOUND_GHAST_FIREBALL = dynamic(1008);
    public static final int EVENT_SOUND_BLAZE_FIREBALL = dynamic(1009);
    public static final int EVENT_SOUND_ZOMBIE_DOOR_BUMP = dynamic(1010);
    public static final int EVENT_SOUND_ZOMBIE_DOOR_CRASH = dynamic(1012);
    public static final int EVENT_SOUND_ZOMBIE_INFECTED = dynamic(1016);
    public static final int EVENT_SOUND_ZOMBIE_CONVERTED = dynamic(1017);
    public static final int EVENT_SOUND_ENDERMAN_TELEPORT = dynamic(1018);
    public static final int EVENT_SOUND_ANVIL_BROKEN = dynamic(1020);
    public static final int EVENT_SOUND_ANVIL_USED = dynamic(1021);
    public static final int EVENT_SOUND_ANVIL_LAND = dynamic(1022);
    public static final int EVENT_SOUND_INFINITY_ARROW_PICKUP = dynamic(1030);
    public static final int EVENT_SOUND_TELEPORT_ENDERPEARL = dynamic(1032);
    public static final int EVENT_SOUND_ITEMFRAME_ITEM_ADD = dynamic(1040);
    public static final int EVENT_SOUND_ITEMFRAME_BREAK = dynamic(1041);
    public static final int EVENT_SOUND_ITEMFRAME_PLACE = dynamic(1042);
    public static final int EVENT_SOUND_ITEMFRAME_ITEM_REMOVE = dynamic(1043);
    public static final int EVENT_SOUND_ITEMFRAME_ITEM_ROTATE = dynamic(1044);
    public static final int EVENT_SOUND_CAMERA = dynamic(1050);
    public static final int EVENT_SOUND_EXPERIENCE_ORB_PICKUP = dynamic(1051);
    public static final int EVENT_SOUND_TOTEM_USED = dynamic(1052);
    public static final int EVENT_SOUND_ARMOR_STAND_BREAK = dynamic(1060);
    public static final int EVENT_SOUND_ARMOR_STAND_HIT = dynamic(1061);
    public static final int EVENT_SOUND_ARMOR_STAND_LAND = dynamic(1062);
    public static final int EVENT_SOUND_ARMOR_STAND_PLACE = dynamic(1063);
    public static final int EVENT_SOUND_POINTED_DRIPSTONE_LAND = dynamic(1064);
    public static final int EVENT_SOUND_DYE_USED = dynamic(1065);
    public static final int EVENT_SOUND_INK_SACE_USED = dynamic(1066);
    public static final int EVENT_SOUND_AMETHYST_RESONATE = dynamic(1067);
    public static final int EVENT_PARTICLE_SHOOT = dynamic(2000);
    public static final int EVENT_PARTICLE_DESTROY_BLOCK = dynamic(2001);
    public static final int EVENT_PARTICLE_POTION_SPLASH = dynamic(2002);
    public static final int EVENT_PARTICLE_EYE_OF_ENDER_DEATH = dynamic(2003);
    public static final int EVENT_PARTICLE_MOB_BLOCK_SPAWN = dynamic(2004);
    public static final int EVENT_PARTICLE_CROP_GROWTH = dynamic(2005);
    public static final int EVENT_PARTICLE_SOUND_GUARDIAN_GHOST = dynamic(2006);
    public static final int EVENT_PARTICLE_DEATH_SMOKE = dynamic(2007);
    public static final int EVENT_PARTICLE_DENY_BLOCK = dynamic(2008);
    public static final int EVENT_PARTICLE_GENERIC_SPAWN = dynamic(2009);
    public static final int EVENT_PARTICLE_DRAGON_EGG = dynamic(2010);
    public static final int EVENT_PARTICLE_CROP_EATEN = dynamic(2011);
    public static final int EVENT_PARTICLE_CRIT = dynamic(2012);
    public static final int EVENT_PARTICLE_TELEPORT = dynamic(2013);
    public static final int EVENT_PARTICLE_CRACK_BLOCK = dynamic(2014);
    public static final int EVENT_PARTICLE_BUBBLES = dynamic(2015);
    public static final int EVENT_PARTICLE_EVAPORATE = dynamic(2016);
    public static final int EVENT_PARTICLE_DESTROY_ARMOR_STAND = dynamic(2017);
    public static final int EVENT_PARTICLE_BREAKING_EGG = dynamic(2018);
    public static final int EVENT_PARTICLE_DESTROY_EGG = dynamic(2019);
    public static final int EVENT_PARTICLE_EVAPORATE_WATER = dynamic(2020);
    public static final int EVENT_PARTICLE_DESTROY_BLOCK_NO_SOUND = dynamic(2021);
    public static final int EVENT_PARTICLE_KNOCKBACK_ROAR = dynamic(2022);
    public static final int EVENT_PARTICLE_TELEPORT_TRAIL = dynamic(2023);
    public static final int EVENT_PARTICLE_POINT_CLOUD = dynamic(2024);
    public static final int EVENT_PARTICLE_EXPLOSION = dynamic(2025);
    public static final int EVENT_PARTICLE_BLOCK_EXPLOSION = dynamic(2026);
    public static final int EVENT_PARTICLE_VIBRATION_SIGNAL = dynamic(2027);
    public static final int EVENT_PARTICLE_DRIPSTONE_DRIP = dynamic(2028);
    public static final int EVENT_PARTICLE_FIZZ_EFFECT = dynamic(2029);
    public static final int EVENT_PARTICLE_WAX_ON = dynamic(2030);
    public static final int EVENT_PARTICLE_WAX_OFF = dynamic(2031);
    public static final int EVENT_PARTICLE_SCRAPE = dynamic(2032);
    public static final int EVENT_PARTICLE_ELECTRIC_SPARK = dynamic(2033);
    public static final int EVENT_PARTICLE_TURTLE_EGG = dynamic(2034);
    public static final int EVENT_PARTICLE_SCULK_SHRIEK = dynamic(2035);
    public static final int EVENT_SCULK_CATALYST_BLOOM = dynamic(2036);
    public static final int EVENT_SCULK_CHARGE = dynamic(2037);
    public static final int EVENT_SCULK_CHARGE_POP = dynamic(2038);
    public static final int EVENT_SONIC_EXPLOSION = dynamic(2039);
    public static final int EVENT_DUST_PLUME = dynamic(2040);
    public static final int EVENT_START_RAINING = dynamic(3001);
    public static final int EVENT_START_THUNDERSTORM = dynamic(3002);
    public static final int EVENT_STOP_RAINING = dynamic(3003);
    public static final int EVENT_STOP_THUNDERSTORM = dynamic(3004);
    public static final int EVENT_GLOBAL_PAUSE = dynamic(3005);
    public static final int EVENT_SIM_TIME_STEP = dynamic(3006);
    public static final int EVENT_SIM_TIME_SCALE = dynamic(3007);
    public static final int EVENT_ACTIVATE_BLOCK = dynamic(3500);
    public static final int EVENT_CAULDRON_EXPLODE = dynamic(3501);
    public static final int EVENT_CAULDRON_DYE_ARMOR = dynamic(3502);
    public static final int EVENT_CAULDRON_CLEAN_ARMOR = dynamic(3503);
    public static final int EVENT_CAULDRON_FILL_POTION = dynamic(3504);
    public static final int EVENT_CAULDRON_TAKE_POTION = dynamic(3505);
    public static final int EVENT_CAULDRON_FILL_WATER = dynamic(3506);
    public static final int EVENT_CAULDRON_TAKE_WATER = dynamic(3507);
    public static final int EVENT_CAULDRON_ADD_DYE = dynamic(3508);
    public static final int EVENT_CAULDRON_CLEAN_BANNER = dynamic(3509);
    public static final int EVENT_CAULDRON_FLUSH = dynamic(3510);
    public static final int EVENT_AGENT_SPAWN_EFFECT = dynamic(3511);
    public static final int EVENT_CAULDRON_FILL_LAVA = dynamic(3512);
    public static final int EVENT_CAULDRON_TAKE_LAVA = dynamic(3513);
    public static final int EVENT_CAULDRON_FILL_POWDER_SNOW = dynamic(3514);
    public static final int EVENT_CAULDRON_TAKE_POWDER_SNOW = dynamic(3515);
    public static final int EVENT_BLOCK_START_BREAK = dynamic(3600);
    public static final int EVENT_BLOCK_STOP_BREAK = dynamic(3601);
    public static final int EVENT_BLOCK_UPDATE_BREAK = dynamic(3602);
    public static final int EVENT_PARTICLE_BREAK_BLOCK_DOWN = dynamic(3603);
    public static final int EVENT_PARTICLE_BREAK_BLOCK_UP = dynamic(3604);
    public static final int EVENT_PARTICLE_BREAK_BLOCK_NORTH = dynamic(3605);
    public static final int EVENT_PARTICLE_BREAK_BLOCK_SOUTH = dynamic(3606);
    public static final int EVENT_PARTICLE_BREAK_BLOCK_WEST = dynamic(3607);
    public static final int EVENT_PARTICLE_BREAK_BLOCK_EAST = dynamic(3608);
    public static final int EVENT_PARTICLE_SHOOT_WHITE_SMOKE = dynamic(3609);
    public static final int EVENT_PARTICLE_BREEZE_WIND_EXPLOSION = dynamic(3610);
    public static final int EVENT_PARTICLE_TRAIL_SPAWNER_DETECTION = dynamic(3611);
    public static final int EVENT_PARTICLE_TRAIL_SPAWNER_SPAWNING = dynamic(3612);
    public static final int EVENT_PARTICLE_TRAIL_SPAWNER_EJECTING = dynamic(3613);
    public static final int EVENT_PARTICLE_WIND_EXPLOSION = dynamic(3614);
    public static final int EVENT_ALL_PLAYERS_SLEEPING = dynamic(3615);
    public static final int EVENT_SET_DATA = dynamic(4000);
    public static final int EVENT_SLEEPING_PLAYERS = dynamic(9801);
    public static final int EVENT_JUMP_PREVENTED = dynamic(9810);
    public static final int EVENT_ANIMATION_VAULT_ACTIVATE = dynamic(9811);
    public static final int EVENT_ANIMATION_VAULT_DEACTIVATE = dynamic(9812);
    public static final int EVENT_ANIMATION_VAULT_EJECT_ITEM = dynamic(9813);

    public static final int EVENT_ADD_PARTICLE_MASK = dynamic(0x4000);

    public int evid;
    public float x = 0;
    public float y = 0;
    public float z = 0;
    public int data = 0;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.evid = byteBuf.readVarInt();
        Vector3f v = byteBuf.readVector3f();
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.data = byteBuf.readVarInt();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {

        byteBuf.writeVarInt(this.evid);
        byteBuf.writeVector3f(this.x, this.y, this.z);
        byteBuf.writeVarInt(this.data);
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
