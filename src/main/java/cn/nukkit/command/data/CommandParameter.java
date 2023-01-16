package cn.nukkit.command.data;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.tree.node.IParamNode;
import cn.nukkit.item.Item;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

public class CommandParameter implements Cloneable {

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final CommandParameter[] EMPTY_ARRAY = new CommandParameter[0];

    public String name;
    public CommandParamType type;
    public boolean optional;
    @Deprecated
    @DeprecationDetails(since = "1.19.20-r1", reason = "use CommandParamOption instead")
    public byte options = 0;
    @PowerNukkitXOnly
    @Since("1.19.20-r2")
    public List<CommandParamOption> paramOptions;
    public CommandEnum enumData;
    public String postFix;

    @PowerNukkitXOnly
    @Since("1.19.50-r4")
    public IParamNode<?> paramNode;

    /**
     * @deprecated use {@link #newType(String, boolean, CommandParamType)} instead
     */
    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "Deprecated by Cloudburst Nukkit", replaceWith = "newType(String, boolean, CommandParamType)")
    public CommandParameter(String name, String type, boolean optional) {
        this(name, fromString(type), optional);
    }

    /**
     * @deprecated use {@link #newType(String, boolean, CommandParamType)} instead
     */
    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "Deprecated by Cloudburst Nukkit", replaceWith = "newType(String, boolean, CommandParamType)")
    public CommandParameter(String name, CommandParamType type, boolean optional) {
        this.name = name;
        this.type = type;
        this.optional = optional;
    }

    /**
     * @deprecated use {@link #newType(String, boolean, CommandParamType)} instead
     */
    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "Deprecated by Cloudburst Nukkit", replaceWith = "newType(String, boolean, CommandParamType)")
    public CommandParameter(String name, boolean optional) {
        this(name, CommandParamType.RAWTEXT, optional);
    }

    /**
     * @deprecated use {@link #newType(String, CommandParamType)} instead
     */
    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "Deprecated by Cloudburst Nukkit", replaceWith = "newType(String, CommandParamType)")
    public CommandParameter(String name) {
        this(name, false);
    }

    /**
     * @deprecated use {@link #newEnum(String, boolean, String)} instead
     */
    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "Deprecated by Cloudburst Nukkit", replaceWith = "newEnum(String, boolean, String)")
    public CommandParameter(String name, boolean optional, String enumType) {
        this.name = name;
        this.type = CommandParamType.RAWTEXT;
        this.optional = optional;
        this.enumData = new CommandEnum(enumType, ENUM_TYPE_ITEM_LIST.equals(enumType) ? Item.getItemList() : new ArrayList<>());
    }

    /**
     * @deprecated use {@link #newEnum(String, boolean, String[])} instead
     */
    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "Deprecated by Cloudburst Nukkit", replaceWith = "newEnum(String, boolean, String[])")
    public CommandParameter(String name, boolean optional, String[] enumValues) {
        this.name = name;
        this.type = CommandParamType.RAWTEXT;
        this.optional = optional;
        this.enumData = new CommandEnum(name + "Enums", enumValues);
    }

    /**
     * @deprecated use {@link #newEnum(String, String)} instead
     */
    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "Deprecated by Cloudburst Nukkit", replaceWith = "newEnum(String, String)")
    public CommandParameter(String name, String enumType) {
        this(name, false, enumType);
    }

    /**
     * @deprecated use {@link #newEnum(String, String[])} instead
     */
    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "Deprecated by Cloudburst Nukkit", replaceWith = "newEnum(String, String[])")
    public CommandParameter(String name, String[] enumValues) {
        this(name, false, enumValues);
    }

    private CommandParameter(String name, boolean optional, CommandParamType type, CommandEnum enumData, String postFix) {
        this(name, optional, type, enumData, postFix, null);
    }

    @PowerNukkitXOnly
    @Since("1.19.50-r4")
    private CommandParameter(String name, boolean optional, CommandParamType type, CommandEnum enumData, String postFix, IParamNode<?> paramNode) {
        this.name = name;
        this.optional = optional;
        this.type = type;
        this.enumData = enumData;
        this.postFix = postFix;
        this.paramNode = paramNode;
    }


    /**
     * optional = false
     *
     * @see #newType(String name, boolean, CommandParamType type)
     */
    @Since("1.4.0.0-PN")
    public static CommandParameter newType(String name, CommandParamType type) {
        return newType(name, false, type);
    }


    /**
     * optional = false
     *
     * @see #newType(String, boolean, CommandParamType, IParamNode)
     */
    @PowerNukkitXOnly
    @Since("1.19.50-r3")
    public static CommandParameter newType(String name, CommandParamType type, IParamNode<?> paramNode) {
        return newType(name, false, type, paramNode);
    }


    /**
     * 创建一个命令参数,参数解析节点{@link IParamNode}依据参数类型{@link CommandParamType}自动选取
     *
     * @param name     参数名
     * @param optional 该参数是否为可选参数
     * @param type     类型{@link CommandParamType}
     * @return the command parameter
     */
    @Since("1.4.0.0-PN")
    public static CommandParameter newType(String name, boolean optional, CommandParamType type) {
        return new CommandParameter(name, optional, type, null, null);
    }

    /**
     * 创建一个命令参数,参数解析节点{@link IParamNode}依据参数类型{@link CommandParamType}自动选取
     *
     * @param name     参数名
     * @param optional 该参数是否为可选参数
     * @param type     类型{@link CommandParamType}
     * @return the command parameter
     */
    @Since("1.19.50-r4")
    public static CommandParameter newType(String name, boolean optional, CommandParamType type, CommandParamOption... options) {
        var result = new CommandParameter(name, optional, type, null, null);
        result.paramOptions = Lists.newArrayList(options);
        return result;
    }

    /**
     * 创建一个命令参数
     *
     * @param name      参数名
     * @param optional  该参数是否为可选参数
     * @param type      类型{@link CommandParamType}
     * @param paramNode 用于解析该参数的参数节点
     * @return the command parameter
     */
    @PowerNukkitXOnly
    @Since("1.19.50-r3")
    public static CommandParameter newType(String name, boolean optional, CommandParamType type, IParamNode<?> paramNode) {
        return new CommandParameter(name, optional, type, null, null, paramNode);
    }

    /**
     * optional = false
     *
     * @see #newEnum(String name, boolean optional, String[] values)
     */
    @Since("1.4.0.0-PN")
    public static CommandParameter newEnum(String name, String[] values) {
        return newEnum(name, false, values);
    }

    /**
     * {@link CommandEnum#getName()}为 {@code name+"Enums"}<p>
     * isSoft = false
     *
     * @see #newEnum(String name, boolean optional, CommandEnum data)
     */
    @Since("1.4.0.0-PN")
    public static CommandParameter newEnum(String name, boolean optional, String[] values) {
        return newEnum(name, optional, new CommandEnum(name + "Enums", values));
    }

    /**
     * optional = false
     *
     * @see #newEnum(String name, boolean optional, String type)
     */
    @Since("1.4.0.0-PN")
    public static CommandParameter newEnum(String name, String type) {
        return newEnum(name, false, type);
    }

    /**
     * isSoft = false
     *
     * @see #newEnum(String name, boolean optional, String type, boolean isSoft)
     */
    @Since("1.4.0.0-PN")
    public static CommandParameter newEnum(String name, boolean optional, String type) {
        return newEnum(name, optional, new CommandEnum(type, new ArrayList<>()));
    }


    /**
     * 创建一个枚举参数，其枚举数据{@link CommandEnum#getValues()}为空列表
     *
     * @param name     参数名(不会显示到命令中,仅作为标识)
     * @param optional 该枚举参数是否可选
     * @param type     枚举参数名(会显示到参数中)
     * @param isSoft   当为False时，客户端显示枚举参数会带上枚举名称{@link CommandEnum#getName()}
     * @return the command parameter
     */
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public static CommandParameter newEnum(String name, boolean optional, String type, boolean isSoft) {
        return newEnum(name, optional, new CommandEnum(type, new ArrayList<>(), isSoft));
    }

    /**
     * optional = false
     *
     * @see #newEnum(String, boolean, CommandEnum)
     */
    @Since("1.4.0.0-PN")
    public static CommandParameter newEnum(String name, CommandEnum data) {
        return newEnum(name, false, data);
    }

    /**
     * 创建一个枚举参数
     *
     * @param name     参数名(不会显示到命令中,仅作为标识)
     * @param optional 改参数是否可选
     * @param data     枚举数据{@link CommandEnum},其中的{@link CommandEnum#getName()}才是真正的枚举参数名
     * @return the command parameter
     */
    @Since("1.4.0.0-PN")
    public static CommandParameter newEnum(String name, boolean optional, CommandEnum data) {
        return new CommandParameter(name, optional, null, data, null);
    }

    /**
     * 创建一个枚举参数
     *
     * @param name     参数名称
     * @param optional 改参数是否可选
     * @param data     枚举数据{@link CommandEnum},其中的{@link CommandEnum#getName()}才是真正的枚举参数名
     * @param options  the options
     * @return the command parameter
     */
    @PowerNukkitXOnly
    @Since("1.19.50-r4")
    public static CommandParameter newEnum(String name, boolean optional, CommandEnum data, CommandParamOption... options) {
        var result = new CommandParameter(name, optional, null, data, null);
        result.paramOptions = Lists.newArrayList(options);
        return result;
    }


    /**
     * New enum command parameter.
     *
     * @param name      the name
     * @param optional  the optional
     * @param data      the data
     * @param paramNode the enum node
     * @return the command parameter
     */
    @PowerNukkitXOnly
    @Since("1.19.50-r4")
    public static CommandParameter newEnum(String name, boolean optional, CommandEnum data, IParamNode<?> paramNode) {
        return new CommandParameter(name, optional, null, data, null, paramNode);
    }

    /**
     * New enum command parameter.
     *
     * @param name      the name
     * @param optional  the optional
     * @param data      the data
     * @param paramNode the enum node
     * @param options   the options
     * @return the command parameter
     */
    @PowerNukkitXOnly
    @Since("1.19.50-r4")
    public static CommandParameter newEnum(String name, boolean optional, CommandEnum data, IParamNode<?> paramNode, CommandParamOption... options) {
        var result = new CommandParameter(name, optional, null, data, null, paramNode);
        result.paramOptions = Lists.newArrayList(options);
        return result;
    }

    //此方法使用不当会崩溃客户端，请谨慎使用！
    @Since("1.4.0.0-PN")
    public static CommandParameter newPostfix(String name, String postfix) {
        return newPostfix(name, false, postfix);
    }

    //此方法使用不当会崩溃客户端，请谨慎使用！
    @Since("1.4.0.0-PN")
    public static CommandParameter newPostfix(String name, boolean optional, String postfix) {
        return new CommandParameter(name, optional, null, null, postfix);
    }

    protected static CommandParamType fromString(String param) {
        switch (param) {
            case "string":
            case "stringenum":
                return CommandParamType.STRING;
            case "target":
                return CommandParamType.TARGET;
            case "blockpos":
                return CommandParamType.POSITION;
            case "rawtext":
                return CommandParamType.RAWTEXT;
            case "int":
                return CommandParamType.INT;
        }
        return CommandParamType.RAWTEXT;
    }

    @PowerNukkitXOnly
    @Since("1.19.50-r4")
    @Override
    public CommandParameter clone() throws CloneNotSupportedException {
        try {
            CommandParameter commandParameter = (CommandParameter) super.clone();
            commandParameter.type = this.type;
            commandParameter.name = this.name;
            commandParameter.enumData = this.enumData;
            commandParameter.optional = this.optional;
            commandParameter.paramOptions = this.paramOptions;
            commandParameter.postFix = this.postFix;
            commandParameter.paramNode = this.paramNode;
            return commandParameter;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "Removed from Cloudburst Nukkit")
    @PowerNukkitOnly("Re-added for backward compatibility")
    public final static String ARG_TYPE_STRING = "string";

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "Removed from Cloudburst Nukkit")
    @PowerNukkitOnly("Re-added for backward compatibility")
    public final static String ARG_TYPE_STRING_ENUM = "stringenum";

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "Removed from Cloudburst Nukkit")
    @PowerNukkitOnly("Re-added for backward compatibility")
    public final static String ARG_TYPE_BOOL = "bool";

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "Removed from Cloudburst Nukkit")
    @PowerNukkitOnly("Re-added for backward compatibility")
    public final static String ARG_TYPE_TARGET = "target";

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "Removed from Cloudburst Nukkit")
    @PowerNukkitOnly("Re-added for backward compatibility")
    public final static String ARG_TYPE_PLAYER = "target";

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "Removed from Cloudburst Nukkit")
    @PowerNukkitOnly("Re-added for backward compatibility")
    public final static String ARG_TYPE_BLOCK_POS = "blockpos";

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "Removed from Cloudburst Nukkit")
    @PowerNukkitOnly("Re-added for backward compatibility")
    public final static String ARG_TYPE_RAW_TEXT = "rawtext";

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "Removed from Cloudburst Nukkit")
    @PowerNukkitOnly("Re-added for backward compatibility")
    public final static String ARG_TYPE_INT = "int";

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "Removed from Cloudburst Nukkit")
    @PowerNukkitOnly("Re-added for backward compatibility")
    public static final String ENUM_TYPE_ITEM_LIST = "Item";

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "Removed from Cloudburst Nukkit")
    @PowerNukkitOnly("Re-added for backward compatibility")
    public static final String ENUM_TYPE_BLOCK_LIST = "blockType";

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "Removed from Cloudburst Nukkit")
    @PowerNukkitOnly("Re-added for backward compatibility")
    public static final String ENUM_TYPE_COMMAND_LIST = "commandName";

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "Removed from Cloudburst Nukkit")
    @PowerNukkitOnly("Re-added for backward compatibility")
    public static final String ENUM_TYPE_ENCHANTMENT_LIST = "enchantmentType";

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "Removed from Cloudburst Nukkit")
    @PowerNukkitOnly("Re-added for backward compatibility")
    public static final String ENUM_TYPE_ENTITY_LIST = "entityType";

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "Removed from Cloudburst Nukkit")
    @PowerNukkitOnly("Re-added for backward compatibility")
    public static final String ENUM_TYPE_EFFECT_LIST = "effectType";

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "Removed from Cloudburst Nukkit")
    @PowerNukkitOnly("Re-added for backward compatibility")
    public static final String ENUM_TYPE_PARTICLE_LIST = "particleType";
}
