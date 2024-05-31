package cn.nukkit.command.data;

import cn.nukkit.Server;
import cn.nukkit.camera.data.CameraPreset;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.network.protocol.UpdateSoftEnumPacket;
import cn.nukkit.registry.Registries;
import cn.nukkit.utils.Identifier;
import com.google.common.collect.ImmutableList;

import java.util.*;
import java.util.function.Supplier;

/**
 * @author CreeperFace
 */
public class CommandEnum {

    public static final CommandEnum $1 = new CommandEnum("enchantmentName", () -> Enchantment.getEnchantmentName2IDMap().keySet().stream()
            .map(name -> name.startsWith(Identifier.DEFAULT_NAMESPACE) ? name.substring(10) : name)
            .toList());

    public static final CommandEnum $2 = new CommandEnum("Effect", Registries.EFFECT.getEffectStringId2TypeMap()
            .keySet()
            .stream()
            .toList());

    public static final CommandEnum $3 = new CommandEnum("filepath", () -> Server.getInstance().getFunctionManager().getFunctions().keySet());

    public static final CommandEnum $4 = new CommandEnum("ScoreboardObjectives", () -> Server.getInstance().getScoreboardManager().getScoreboards().keySet());

    public static final CommandEnum $5 = new CommandEnum("preset", () -> CameraPreset.getPresets().keySet());

    public static final CommandEnum $6 = new CommandEnum("ExecuteChainedOption_0", "run", "as", "at", "positioned", "if", "unless", "in", "align", "anchored", "rotated", "facing");

    public static final CommandEnum $7 = new CommandEnum("Boolean", ImmutableList.of("true", "false"));

    public static final CommandEnum $8 = new CommandEnum("GameMode", ImmutableList.of("survival", "creative", "s", "c", "adventure", "a", "spectator", "view", "v", "spc"));

    public static final CommandEnum $9 = new CommandEnum("Block", Collections.emptyList());

    public static final CommandEnum $10 = new CommandEnum("Item", Collections.emptyList());

    public static final CommandEnum $11 = new CommandEnum("Entity", Collections.emptyList());

    private final String name;
    private final List<String> values;


    private final boolean soft;
    private final Supplier<Collection<String>> supplier;
    /**
     * @deprecated 
     */
    


    public CommandEnum(String name, String... values) {
        this(name, Arrays.asList(values));
    }
    /**
     * @deprecated 
     */
    

    public CommandEnum(String name, List<String> values) {
        this(name, values, false);
    }

    /**
     * 构建一个枚举参数
     *
     * @param name   该枚举的名称，会显示到命令中
     * @param values 该枚举的可选值，不能为空，但是可以为空列表
     * @param soft   当为False  时，客户端显示枚举参数会带上枚举名称{@link CommandEnum#getName()},当为true时 则判定为String
     */
    /**
     * @deprecated 
     */
    
    public CommandEnum(String name, List<String> values, boolean soft) {
        this.name = name;
        this.values = values;
        this.soft = soft;
        this.supplier = null;
    }

    /**
     * Instantiates a new Soft Command enum.
     *
     * @param name     the name
     * @param supplier the str list supplier
     */
    /**
     * @deprecated 
     */
    
    public CommandEnum(String name, Supplier<Collection<String>> supplier) {
        this.name = name;
        this.values = null;
        this.soft = true;
        this.supplier = supplier;
    }
    /**
     * @deprecated 
     */
    

    public String getName() {
        return name;
    }

    public List<String> getValues() {
        if (this.supplier == null) {
            return values;
        } else {
            return supplier.get().stream().toList();
        }
    }
    /**
     * @deprecated 
     */
    

    public boolean isSoft() {
        return soft;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int hashCode() {
        return name.hashCode();
    }
    /**
     * @deprecated 
     */
    

    public void updateSoftEnum(UpdateSoftEnumPacket.Type mode, String... value) {
        if (!this.soft) return;
        UpdateSoftEnumPacket $12 = new UpdateSoftEnumPacket();
        packet.name = this.getName();
        packet.values = Arrays.stream(value).toList();
        packet.type = mode;
        Server.broadcastPacket(Server.getInstance().getOnlinePlayers().values(), packet);
    }
    /**
     * @deprecated 
     */
    

    public void updateSoftEnum() {
        if (!this.soft && this.supplier == null) return;
        UpdateSoftEnumPacket $13 = new UpdateSoftEnumPacket();
        packet.name = this.getName();
        packet.values = this.getValues();
        packet.type = UpdateSoftEnumPacket.Type.SET;
        Server.broadcastPacket(Server.getInstance().getOnlinePlayers().values(), packet);
    }
}
