package cn.nukkit.level.particle;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.DataPacket;

import java.lang.reflect.Field;

import static cn.nukkit.utils.Utils.dynamic;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class Particle extends Vector3 {

    public static final int TYPE_BUBBLE = dynamic(1);
    @Since("1.4.0.0-PN") public static final int TYPE_BUBBLE_MANUAL = dynamic(2);
    public static final int TYPE_CRITICAL = dynamic(3);
    public static final int TYPE_BLOCK_FORCE_FIELD = dynamic(4);
    public static final int TYPE_SMOKE = dynamic(5);
    public static final int TYPE_EXPLODE = dynamic(6);
    public static final int TYPE_EVAPORATION = dynamic(7);
    public static final int TYPE_FLAME = dynamic(8);
    @PowerNukkitOnly @Since("1.5.2.0-PN") public static final int TYPE_CANDLE_FLAME = dynamic(9);
    public static final int TYPE_LAVA = dynamic(10);
    public static final int TYPE_LARGE_SMOKE = dynamic(11);
    public static final int TYPE_REDSTONE = dynamic(12);
    public static final int TYPE_RISING_RED_DUST = dynamic(13);
    public static final int TYPE_ITEM_BREAK = dynamic(14);
    public static final int TYPE_SNOWBALL_POOF = dynamic(15);
    public static final int TYPE_HUGE_EXPLODE = dynamic(16);
    public static final int TYPE_HUGE_EXPLODE_SEED = dynamic(17);
    public static final int TYPE_MOB_FLAME = dynamic(18);
    public static final int TYPE_HEART = dynamic(19);
    public static final int TYPE_TERRAIN = dynamic(20);
    public static final int TYPE_TOWN_AURA = dynamic(21);

    @Deprecated @DeprecationDetails(since = "1.5.2.0-PN", by = "PowerNukkit", reason = "Same as TYPE_TOWN_AURA",
            replaceWith = "TYPE_TOWN_AURA")
    public static final int TYPE_SUSPENDED_TOWN = TYPE_TOWN_AURA;
    
    public static final int TYPE_PORTAL = dynamic(22);
    @PowerNukkitOnly @Since("1.5.2.0-PN") public static final int TYPE_MOB_PORTAL = dynamic(23);
    public static final int TYPE_SPLASH = dynamic(24);

    @Deprecated @DeprecationDetails(since = "1.5.2.0-PN", by = "PowerNukkit", reason = "Same as TYPE_SPLASH",
            replaceWith = "TYPE_SPLASH")
    public static final int TYPE_WATER_SPLASH = TYPE_SPLASH;
    
    @Since("1.4.0.0-PN") public static final int TYPE_WATER_SPLASH_MANUAL = dynamic(25);
    public static final int TYPE_WATER_WAKE = dynamic(26);
    public static final int TYPE_DRIP_WATER = dynamic(27);
    public static final int TYPE_DRIP_LAVA = dynamic(28);
    public static final int TYPE_DRIP_HONEY = dynamic(29);
    @Since("1.4.0.0-PN") public static final int TYPE_STALACTITE_DRIP_WATER = dynamic(30);
    @Since("1.4.0.0-PN") public static final int TYPE_STALACTITE_DRIP_LAVA = dynamic(31);
    public static final int TYPE_FALLING_DUST = dynamic(32);
    
    @Deprecated @DeprecationDetails(since = "1.5.2.0-PN", by = "PowerNukkit", reason = "Same as TYPE_FALLING_DUST",
            replaceWith = "TYPE_FALLING_DUST")
    public static final int TYPE_DUST = TYPE_FALLING_DUST;
    
    public static final int TYPE_MOB_SPELL = dynamic(33);
    public static final int TYPE_MOB_SPELL_AMBIENT = dynamic(34);
    public static final int TYPE_MOB_SPELL_INSTANTANEOUS = dynamic(35);
    public static final int TYPE_INK = dynamic(36);
    public static final int TYPE_SLIME = dynamic(37);
    public static final int TYPE_RAIN_SPLASH = dynamic(38);
    public static final int TYPE_VILLAGER_ANGRY = dynamic(39);
    public static final int TYPE_VILLAGER_HAPPY = dynamic(40);
    public static final int TYPE_ENCHANTMENT_TABLE = dynamic(41);
    public static final int TYPE_TRACKING_EMITTER = dynamic(42);
    public static final int TYPE_NOTE = dynamic(43);
    
    @PowerNukkitOnly("Backward compatibility")
    @Deprecated @DeprecationDetails(since = "1.4.0.0-PN", by = "NukkitX", reason = "Removed from Nukkit")
    public static final int TYPE_NOTE_AND_DUST = TYPE_NOTE;
    
    public static final int TYPE_WITCH_SPELL = dynamic(44);
    public static final int TYPE_CARROT = dynamic(45);
    @Since("1.4.0.0-PN") public static final int TYPE_MOB_APPEARANCE  = dynamic(46);
    public static final int TYPE_END_ROD = dynamic(47);
    public static final int TYPE_RISING_DRAGONS_BREATH = dynamic(48);
    public static final int TYPE_SPIT = dynamic(49);
    public static final int TYPE_TOTEM = dynamic(50);
    public static final int TYPE_FOOD = dynamic(51);
    public static final int TYPE_FIREWORKS_STARTER = dynamic(52);
    public static final int TYPE_FIREWORKS_SPARK = dynamic(53);
    public static final int TYPE_FIREWORKS_OVERLAY = dynamic(54);
    public static final int TYPE_BALLOON_GAS = dynamic(55);
    public static final int TYPE_COLORED_FLAME = dynamic(56);
    public static final int TYPE_SPARKLER = dynamic(57);
    public static final int TYPE_CONDUIT = dynamic(58);
    public static final int TYPE_BUBBLE_COLUMN_UP = dynamic(59);
    public static final int TYPE_BUBBLE_COLUMN_DOWN = dynamic(60);
    public static final int TYPE_SNEEZE = dynamic(61);
    @Since("1.4.0.0-PN") public static final int TYPE_SHULKER_BULLET = dynamic(62);
    @Since("1.4.0.0-PN") public static final int TYPE_BLEACH = dynamic(63);
    public static final int TYPE_LARGE_EXPLOSION = dynamic(64);
    @Since("1.4.0.0-PN") public static final int TYPE_MYCELIUM_DUST = dynamic(65);
    public static final int TYPE_FALLING_RED_DUST = dynamic(66);
    public static final int TYPE_CAMPFIRE_SMOKE = dynamic(67);
    @Since("1.4.0.0-PN") public static final int TYPE_TALL_CAMPFIRE_SMOKE = dynamic(68);
    public static final int TYPE_FALLING_DRAGONS_BREATH = dynamic(69);
    public static final int TYPE_DRAGONS_BREATH = dynamic(70);
    @Since("1.4.0.0-PN") public static final int TYPE_BLUE_FLAME = dynamic(71);
    @Since("1.4.0.0-PN") public static final int TYPE_SOUL = dynamic(72);
    @Since("1.4.0.0-PN") public static final int TYPE_OBSIDIAN_TEAR = dynamic(73);
    @Since("1.4.0.0-PN") public static final int TYPE_PORTAL_REVERSE = dynamic(74);
    @Since("1.4.0.0-PN") public static final int TYPE_SNOWFLAKE = dynamic(75);
    @Since("1.4.0.0-PN") public static final int TYPE_VIBRATION_SIGNAL = dynamic(76);
    @Since("1.4.0.0-PN") public static final int TYPE_SCULK_SENSOR_REDSTONE = dynamic(77);
    @Since("1.4.0.0-PN") public static final int TYPE_SPORE_BLOSSOM_SHOWER = dynamic(78);
    @Since("1.4.0.0-PN") public static final int TYPE_SPORE_BLOSSOM_AMBIENT = dynamic(79);
    @Since("1.4.0.0-PN") public static final int TYPE_WAX = dynamic(80);
    @Since("1.4.0.0-PN") public static final int TYPE_ELECTRIC_SPARK = dynamic(81);

    @Since("1.4.0.0-PN")
    public static Integer getParticleIdByName(String name) {
        name = name.toUpperCase();

        try {
            Field field = Particle.class.getDeclaredField(name.startsWith("TYPE_") ? name : "TYPE_" + name);

            Class<?> type = field.getType();

            if(type==int.class) {
                return field.getInt(null);
            }
        } catch(NoSuchFieldException | IllegalAccessException e) {
            // ignore
        }
        return null;
    }

    @Since("1.4.0.0-PN")
    public static boolean particleExists(String name) {
        return getParticleIdByName(name) != null;   
    }

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

    abstract public DataPacket[] encode();
}
