package cn.nukkit.level.particle;

import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.DataPacket;

import java.lang.reflect.Field;
import java.util.Locale;

import static cn.nukkit.utils.Utils.dynamic;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class Particle extends Vector3 {
    public static final int TYPE_UNDEFINED = dynamic(0);
    public static final int TYPE_BUBBLE = dynamic(1);
    public static final int TYPE_BUBBLE_MANUAL = dynamic(2);
    public static final int TYPE_CRIT = dynamic(3);
    public static final int TYPE_BLOCK_FORCE_FIELD = dynamic(4);
    public static final int TYPE_SMOKE = dynamic(5);
    public static final int TYPE_EXPLODE = dynamic(6);
    public static final int TYPE_EVAPORATION = dynamic(7);
    public static final int TYPE_FLAME = dynamic(8);
    public static final int TYPE_CANDLE_FLAME = dynamic(9);
    public static final int TYPE_LAVA = dynamic(10);
    public static final int TYPE_LARGE_SMOKE = dynamic(11);
    public static final int TYPE_RED_DUST = dynamic(12);
    public static final int TYPE_RISING_BORDER_DUST = dynamic(13);
    public static final int TYPE_ICON_CRACK = dynamic(14);
    public static final int TYPE_SNOWBALL_POOF = dynamic(15);
    public static final int TYPE_LARGE_EXPLODE = dynamic(16);
    public static final int TYPE_HUGE_EXPLOSION = dynamic(17);
    public static final int TYPE_BREEZE_WIND_EXPLOSION = dynamic(18);
    public static final int TYPE_MOB_FLAME = dynamic(19);
    public static final int TYPE_HEART = dynamic(20);
    public static final int TYPE_TERRAIN = dynamic(21);
    public static final int TYPE_TOWN_AURA = dynamic(22);
    public static final int TYPE_PORTAL = dynamic(23);
    public static final int TYPE_MOB_PORTAL = dynamic(24);
    public static final int TYPE_WATER_SPLASH = dynamic(25);
    public static final int TYPE_WATER_SPLASH_MANUAL = dynamic(26);
    public static final int TYPE_WATER_WAKE = dynamic(27);
    public static final int TYPE_DRIP_WATER = dynamic(28);
    public static final int TYPE_DRIP_LAVA = dynamic(29);
    public static final int TYPE_DRIP_HONEY = dynamic(30);
    public static final int TYPE_STALACTITE_DRIP_WATER = dynamic(31);
    public static final int TYPE_STALACTITE_DRIP_LAVA = dynamic(32);
    public static final int TYPE_FALLING_DUST = dynamic(33);
    public static final int TYPE_MOB_SPELL = dynamic(34);
    public static final int TYPE_MOB_SPELL_AMBIENT = dynamic(35);
    public static final int TYPE_MOB_SPELL_INSTANTANEOUS = dynamic(36);
    public static final int TYPE_INK = dynamic(37);
    public static final int TYPE_SLIME = dynamic(38);
    public static final int TYPE_RAIN_SPLASH = dynamic(39);
    public static final int TYPE_VILLAGER_ANGRY = dynamic(40);
    public static final int TYPE_VILLAGER_HAPPY = dynamic(41);
    public static final int TYPE_ENCHANTING_TABLE = dynamic(42);
    public static final int TYPE_TRACKER_EMITTER = dynamic(43);
    public static final int TYPE_NOTE = dynamic(44);
    public static final int TYPE_WITCH_SPELL = dynamic(45);
    public static final int TYPE_CARROT_BOOST = dynamic(46);
    public static final int TYPE_MOB_APPEARANCE = dynamic(47);
    public static final int TYPE_END_ROD = dynamic(48);
    public static final int TYPE_DRAGON_BREATH = dynamic(49);
    public static final int TYPE_SPIT = dynamic(50);
    public static final int TYPE_TOTEM = dynamic(51);
    public static final int TYPE_FOOD = dynamic(52);
    public static final int TYPE_FIREWORKS_STARTER = dynamic(53);
    public static final int TYPE_FIREWORKS = dynamic(54);
    public static final int TYPE_FIREWORKS_OVERLAY = dynamic(55);
    public static final int TYPE_BALLOON_GAS = dynamic(56);
    public static final int TYPE_COLORED_FLAME = dynamic(57);
    public static final int TYPE_SPARKLER = dynamic(58);
    public static final int TYPE_CONDUIT = dynamic(59);
    public static final int TYPE_BUBBLE_COLUMN_UP = dynamic(60);
    public static final int TYPE_BUBBLE_COLUMN_DOWN = dynamic(61);
    public static final int TYPE_SNEEZE = dynamic(62);
    public static final int TYPE_SHULKER_BULLET = dynamic(63);
    public static final int TYPE_BLEACH = dynamic(64);
    public static final int TYPE_DRAGON_DESTROY_BLOCK = dynamic(65);
    public static final int TYPE_MYCELIUM_DUST = dynamic(66);
    public static final int TYPE_FALLING_BORDER_DUST = dynamic(67);
    public static final int TYPE_CAMPFIRE_SMOKE = dynamic(68);
    public static final int TYPE_CAMPFIRE_SMOKE_TALL = dynamic(69);
    public static final int TYPE_DRAGON_BREATH_FIRE = dynamic(70);
    public static final int TYPE_DRAGON_BREATH_TRAIL = dynamic(71);
    public static final int TYPE_BLUE_FLAME = dynamic(72);
    public static final int TYPE_SOUL = dynamic(73);
    public static final int TYPE_OBSIDIAN_TEAR = dynamic(74);
    public static final int TYPE_PORTAL_REVERSE = dynamic(75);
    public static final int TYPE_SNOWFLAKE = dynamic(76);
    public static final int TYPE_VIBRATION_SIGNAL = dynamic(77);
    public static final int TYPE_SCULK_SENSOR_REDSTONE = dynamic(78);
    public static final int TYPE_SPORE_BLOSSOM_SHOWER = dynamic(79);
    public static final int TYPE_SPORE_BLOSSOM_AMBIENT = dynamic(80);
    public static final int TYPE_WAX = dynamic(81);
    public static final int TYPE_ELECTRIC_SPARK = dynamic(82);
    public static final int TYPE_SHRIEK = dynamic(83);
    public static final int TYPE_SCULK_SOUL = dynamic(84);
    public static final int TYPE_SONIC_EXPLOSION = dynamic(85);
    public static final int TYPE_BRUSH_DUST = dynamic(86);
    public static final int TYPE_CHERRY_LEAVES = dynamic(87);
    public static final int TYPE_DUST_PLUME = dynamic(88);
    public static final int TYPE_WHITE_SMOKE = dynamic(89);
    public static final int TYPE_VAULT_CONNECTION = dynamic(90);
    public static final int TYPE_WIND_EXPLOSION = dynamic(91);


    public Particle() {
        super(0, 0, 0);
    }

    public Particle(double x) {
        super(x, 0, 0);
    }

    public Particle(double x, double y) {
        super(x, y, 0);
    }

    public Particle(double x, double y, double z) {
        super(x, y, z);
    }

    public static Integer getParticleIdByName(String name) {
        name = name.toUpperCase(Locale.ENGLISH);

        try {
            Field field = Particle.class.getDeclaredField(name.startsWith("TYPE_") ? name : "TYPE_" + name);

            Class<?> type = field.getType();

            if (type == int.class) {
                return field.getInt(null);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // ignore
        }
        return null;
    }

    public static boolean particleExists(String name) {
        return getParticleIdByName(name) != null;
    }

    abstract public DataPacket[] encode();
}
