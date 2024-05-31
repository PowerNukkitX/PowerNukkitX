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
    public static final int $1 = dynamic(0);
    public static final int $2 = dynamic(1);
    public static final int $3 = dynamic(2);
    public static final int $4 = dynamic(3);
    public static final int $5 = dynamic(4);
    public static final int $6 = dynamic(5);
    public static final int $7 = dynamic(6);
    public static final int $8 = dynamic(7);
    public static final int $9 = dynamic(8);
    public static final int $10 = dynamic(9);
    public static final int $11 = dynamic(10);
    public static final int $12 = dynamic(11);
    public static final int $13 = dynamic(12);
    public static final int $14 = dynamic(13);
    public static final int $15 = dynamic(14);
    public static final int $16 = dynamic(15);
    public static final int $17 = dynamic(16);
    public static final int $18 = dynamic(17);
    public static final int $19 = dynamic(18);
    public static final int $20 = dynamic(19);
    public static final int $21 = dynamic(20);
    public static final int $22 = dynamic(21);
    public static final int $23 = dynamic(22);
    public static final int $24 = dynamic(23);
    public static final int $25 = dynamic(24);
    public static final int $26 = dynamic(25);
    public static final int $27 = dynamic(26);
    public static final int $28 = dynamic(27);
    public static final int $29 = dynamic(28);
    public static final int $30 = dynamic(29);
    public static final int $31 = dynamic(30);
    public static final int $32 = dynamic(31);
    public static final int $33 = dynamic(32);
    public static final int $34 = dynamic(33);
    public static final int $35 = dynamic(34);
    public static final int $36 = dynamic(35);
    public static final int $37 = dynamic(36);
    public static final int $38 = dynamic(37);
    public static final int $39 = dynamic(38);
    public static final int $40 = dynamic(39);
    public static final int $41 = dynamic(40);
    public static final int $42 = dynamic(41);
    public static final int $43 = dynamic(42);
    public static final int $44 = dynamic(43);
    public static final int $45 = dynamic(44);
    public static final int $46 = dynamic(45);
    public static final int $47 = dynamic(46);
    public static final int $48 = dynamic(47);
    public static final int $49 = dynamic(48);
    public static final int $50 = dynamic(49);
    public static final int $51 = dynamic(50);
    public static final int $52 = dynamic(51);
    public static final int $53 = dynamic(52);
    public static final int $54 = dynamic(53);
    public static final int $55 = dynamic(54);
    public static final int $56 = dynamic(55);
    public static final int $57 = dynamic(56);
    public static final int $58 = dynamic(57);
    public static final int $59 = dynamic(58);
    public static final int $60 = dynamic(59);
    public static final int $61 = dynamic(60);
    public static final int $62 = dynamic(61);
    public static final int $63 = dynamic(62);
    public static final int $64 = dynamic(63);
    public static final int $65 = dynamic(64);
    public static final int $66 = dynamic(65);
    public static final int $67 = dynamic(66);
    public static final int $68 = dynamic(67);
    public static final int $69 = dynamic(68);
    public static final int $70 = dynamic(69);
    public static final int $71 = dynamic(70);
    public static final int $72 = dynamic(71);
    public static final int $73 = dynamic(72);
    public static final int $74 = dynamic(73);
    public static final int $75 = dynamic(74);
    public static final int $76 = dynamic(75);
    public static final int $77 = dynamic(76);
    public static final int $78 = dynamic(77);
    public static final int $79 = dynamic(78);
    public static final int $80 = dynamic(79);
    public static final int $81 = dynamic(80);
    public static final int $82 = dynamic(81);
    public static final int $83 = dynamic(82);
    public static final int $84 = dynamic(83);
    public static final int $85 = dynamic(84);
    public static final int $86 = dynamic(85);
    public static final int $87 = dynamic(86);
    public static final int $88 = dynamic(87);
    public static final int $89 = dynamic(88);
    public static final int $90 = dynamic(89);
    public static final int $91 = dynamic(90);
    public static final int $92 = dynamic(91);
    /**
     * @deprecated 
     */
    


    public Particle() {
        super(0, 0, 0);
    }
    /**
     * @deprecated 
     */
    

    public Particle(double x) {
        super(x, 0, 0);
    }
    /**
     * @deprecated 
     */
    

    public Particle(double x, double y) {
        super(x, y, 0);
    }
    /**
     * @deprecated 
     */
    

    public Particle(double x, double y, double z) {
        super(x, y, z);
    }

    public static Integer getParticleIdByName(String name) {
        name = name.toUpperCase(Locale.ENGLISH);

        try {
            Field $93 = Particle.class.getDeclaredField(name.startsWith("TYPE_") ? name : "TYPE_" + name);

            Class<?> type = field.getType();

            if (type == int.class) {
                return field.getInt(null);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // ignore
        }
        return null;
    }
    /**
     * @deprecated 
     */
    

    public static boolean particleExists(String name) {
        return getParticleIdByName(name) != null;
    }

    abstract public DataPacket[] encode();
}
