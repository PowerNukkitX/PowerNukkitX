package cn.nukkit.command.data;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.network.protocol.UpdateSoftEnumPacket;
import com.google.common.collect.ImmutableList;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author CreeperFace
 */
public class CommandEnum {
    @PowerNukkitXOnly
    @Since("1.19.50-r4")
    public static final CommandEnum SCOREBOARD_OBJECTIVES = new CommandEnum("ScoreboardObjectives", () -> Server.getInstance().getScoreboardManager().getScoreboards().keySet(), true);
    @PowerNukkitXOnly
    @Since("1.19.50-r4")
    public static final CommandEnum CHAINED_COMMAND_ENUM = new CommandEnum("ExecuteChainedOption_0", "run", "as", "at", "positioned", "if", "unless", "in", "align", "anchored", "rotated", "facing");
    @Since("1.4.0.0-PN")
    public static final CommandEnum ENUM_BOOLEAN = new CommandEnum("Boolean", ImmutableList.of("true", "false"));

    @Since("1.4.0.0-PN")
    public static final CommandEnum ENUM_GAMEMODE = new CommandEnum("GameMode",
            ImmutableList.of("survival", "creative", "s", "c", "adventure", "a", "spectator", "view", "v", "spc"));

    @Since("1.4.0.0-PN")
    public static final CommandEnum ENUM_BLOCK;

    @Since("1.4.0.0-PN")
    public static final CommandEnum ENUM_ITEM;

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public static final CommandEnum ENUM_ENTITY;

    static {
        /*ImmutableList.Builder<String> blocks = ImmutableList.builder();
        for (Field field : BlockID.class.getDeclaredFields()) {
            blocks.add(field.getName().toLowerCase());
        }*/
        ENUM_BLOCK = new CommandEnum("Block", /*blocks.build()*/ Collections.emptyList());

        ENUM_ITEM = new CommandEnum("Item", /*ImmutableList.copyOf(Arrays.stream(MinecraftItemID.values())
            .filter(it -> !it.isTechnical())
            .filter(it -> !it.isEducationEdition())
            .flatMap(it -> Stream.of(Stream.of(it.getNamespacedId())*//*, Arrays.stream(it.getAliases())*//*).flatMap(Function.identity()))
            .map(it-> it.substring(10).toLowerCase())
            .toArray(String[]::new)
        )*/ Collections.emptyList());

        ENUM_ENTITY = new CommandEnum("Entity", Collections.emptyList());
    }

    private final String name;
    private final List<String> values;

    @PowerNukkitXOnly
    @Since("1.19.50-r4")
    private final boolean isSoft;//softEnum

    @PowerNukkitXOnly
    @Since("1.19.50-r4")
    private final Supplier<Collection<String>> strListSupplier;

    @Since("1.4.0.0-PN")
    public CommandEnum(String name, String... values) {
        this(name, Arrays.asList(values));
    }

    public CommandEnum(String name, List<String> values) {
        this(name, values, false);
    }

    /**
     * 构建一个枚举参数
     *
     * @param name   该枚举的名称，会显示到命令中
     * @param values 该枚举的可选值，不能为空，但是可以为空列表
     * @param isSoft 当为False  时，客户端显示枚举参数会带上枚举名称{@link CommandEnum#getName()},当为true时 则判定为String
     */
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public CommandEnum(String name, List<String> values, boolean isSoft) {
        this.name = name;
        this.values = values;
        this.isSoft = isSoft;
        this.strListSupplier = null;
    }

    /**
     * 配合{@link cn.nukkit.Player#sendCommandData()}能实现动态枚举，性能可能很差，因为{@link cn.nukkit.network.protocol.AvailableCommandsPacket AvailableCommandsPacket}数据包量很大
     * Instantiates a new Command enum.<br>
     * todo 找到原版实现动态枚举的方法
     *
     * @param name            the name
     * @param strListSupplier the str list supplier
     * @param isSoft          the is soft
     */
    @PowerNukkitXOnly
    @Since("1.19.50-r4")
    public CommandEnum(String name, Supplier<Collection<String>> strListSupplier, boolean isSoft) {
        this.name = name;
        this.values = null;
        this.isSoft = isSoft;
        this.strListSupplier = strListSupplier;
    }

    public String getName() {
        return name;
    }

    public List<String> getValues() {
        if (this.strListSupplier == null) return values;
        else return strListSupplier.get().stream().toList();
    }

    public boolean isSoft() {
        return isSoft;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @PowerNukkitXOnly
    @Since("1.19.50-r4")
    public boolean updateSoftEnum(UpdateSoftEnumPacket.Type mode, String... value) {
        if (!this.isSoft) return false;
        UpdateSoftEnumPacket pk = new UpdateSoftEnumPacket();
        pk.name = this.getName();
        pk.values = Arrays.stream(value).toList();
        pk.type = mode;
        Server.broadcastPacket(Server.getInstance().getOnlinePlayers().values(), pk);
        return true;
    }

    @PowerNukkitXOnly
    @Since("1.19.50-r4")
    public boolean updateSoftEnum() {
        if (!this.isSoft && this.strListSupplier == null) return false;
        UpdateSoftEnumPacket pk = new UpdateSoftEnumPacket();
        pk.name = this.getName();
        pk.values = this.getValues();
        pk.type = UpdateSoftEnumPacket.Type.SET;
        Server.broadcastPacket(Server.getInstance().getOnlinePlayers().values(), pk);
        return true;
    }
}
