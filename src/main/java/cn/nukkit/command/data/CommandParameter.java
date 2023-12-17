package cn.nukkit.command.data;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.tree.node.IParamNode;
import cn.nukkit.item.Item;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandParameter {


    public static final CommandParameter[] EMPTY_ARRAY = new CommandParameter[0];

    public String name;
    public CommandParamType type;
    public boolean optional;
    @Deprecated
    @DeprecationDetails(since = "1.19.20-r1", reason = "use CommandParamOption instead")
    public byte options = 0;


    public List<CommandParamOption> paramOptions;
    public CommandEnum enumData;
    public String postFix;


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

    public static CommandParameter newType(String name, CommandParamType type) {
        return newType(name, false, type);
    }


    /**
     * optional = false,CommandParamOption=[]
     *
     * @see #newType(String, boolean, CommandParamType, IParamNode, CommandParamOption...)
     */


    public static CommandParameter newType(String name, CommandParamType type, IParamNode<?> paramNode) {
        return newType(name, false, type, paramNode);
    }


    /**
     * paramNode = null , CommandParamOption=[]
     *
     * @see #newType(String, boolean, CommandParamType, IParamNode, CommandParamOption...)
     */

    public static CommandParameter newType(String name, boolean optional, CommandParamType type) {
        return newType(name, optional, type, null, new CommandParamOption[]{});
    }

    /**
     * paramNode = null
     *
     * @see #newType(String, boolean, CommandParamType, IParamNode, CommandParamOption...)
     */


    public static CommandParameter newType(String name, boolean optional, CommandParamType type, CommandParamOption... options) {
        return newType(name, optional, type, null, options);
    }

    /**
     * 创建一个命令参数
     *
     * @param name      参数名
     * @param optional  该参数是否为可选参数
     * @param type      类型{@link CommandParamType}
     * @param paramNode 用于解析该参数的参数节点
     * @param options   the options
     * @return the command parameter
     */


    public static CommandParameter newType(String name, boolean optional, CommandParamType type, IParamNode<?> paramNode, CommandParamOption... options) {
        var result = new CommandParameter(name, optional, type, null, null, paramNode);
        if (options.length != 0) {
            result.paramOptions = Lists.newArrayList(options);
        }
        return result;
    }

    /**
     * optional = false
     *
     * @see #newEnum(String name, boolean optional, String[] values)
     */

    public static CommandParameter newEnum(String name, String[] values) {
        return newEnum(name, false, values);
    }

    /**
     * {@link CommandEnum#getName()}为 {@code name+"Enums"}<p>
     * isSoft = false
     *
     * @see #newEnum(String name, boolean optional, CommandEnum data)
     */

    public static CommandParameter newEnum(String name, boolean optional, String[] values) {
        return newEnum(name, optional, new CommandEnum(name + "Enums", values));
    }

    /**
     * @see #newEnum(String name, boolean optional, CommandEnum data)
     */


    public static CommandParameter newEnum(String name, boolean optional, String[] values, boolean isSoft) {
        return newEnum(name, optional, new CommandEnum(name + "Enums", Arrays.asList(values), isSoft));
    }

    /**
     * optional = false
     *
     * @see #newEnum(String, boolean, CommandEnum, IParamNode, CommandParamOption...)
     */

    public static CommandParameter newEnum(String name, String type) {
        return newEnum(name, false, type);
    }

    /**
     * optional = false
     *
     * @see #newEnum(String, boolean, CommandEnum, IParamNode, CommandParamOption...)
     */

    public static CommandParameter newEnum(String name, boolean optional, String type) {
        return newEnum(name, optional, new CommandEnum(type, new ArrayList<>()));
    }

    /**
     * optional = false
     *
     * @see #newEnum(String, boolean, CommandEnum)
     */

    public static CommandParameter newEnum(String name, CommandEnum data) {
        return newEnum(name, false, data);
    }

    /**
     * optional = false
     *
     * @see #newEnum(String, boolean, CommandEnum, IParamNode, CommandParamOption...)
     */

    public static CommandParameter newEnum(String name, boolean optional, CommandEnum data) {
        return new CommandParameter(name, optional, null, data, null);
    }

    /**
     * optional = false
     *
     * @see #newEnum(String, boolean, CommandEnum, IParamNode, CommandParamOption...)
     */


    public static CommandParameter newEnum(String name, boolean optional, CommandEnum data, CommandParamOption... options) {
        return newEnum(name, optional, data, null, options);
    }


    /**
     * optional = false
     *
     * @see #newEnum(String, boolean, CommandEnum, IParamNode, CommandParamOption...)
     */


    public static CommandParameter newEnum(String name, boolean optional, CommandEnum data, IParamNode<?> paramNode) {
        return newEnum(name, optional, data, paramNode, new CommandParamOption[]{});
    }

    /**
     * 创建一个枚举参数
     *
     * @param name      参数名称
     * @param optional  改参数是否可选
     * @param data      枚举数据{@link CommandEnum},其中的{@link CommandEnum#getName()}才是真正的枚举参数名
     * @param paramNode 该参数对应的{@link IParamNode}
     * @param options   the options
     * @return the command parameter
     */


    public static CommandParameter newEnum(String name, boolean optional, CommandEnum data, IParamNode<?> paramNode, CommandParamOption... options) {
        var result = new CommandParameter(name, optional, null, data, null, paramNode);
        if (options.length != 0) {
            result.paramOptions = Lists.newArrayList(options);
        }
        return result;
    }

    //此方法使用不当会崩溃客户端，请谨慎使用！
    //todo 注释掉创建Postfix的方法直到能够稳定运行
    /*
    public static CommandParameter newPostfix(String name, String postfix) {
        return newPostfix(name, false, postfix);
    }

    //此方法使用不当会崩溃客户端，请谨慎使用！

    public static CommandParameter newPostfix(String name, boolean optional, String postfix) {
        return new CommandParameter(name, optional, null, null, postfix);
    }*/

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

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "Removed from Cloudburst Nukkit")
    ("Re-added for backward compatibility")
    public final static String ARG_TYPE_STRING = "string";

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "Removed from Cloudburst Nukkit")
    ("Re-added for backward compatibility")
    public final static String ARG_TYPE_STRING_ENUM = "stringenum";

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "Removed from Cloudburst Nukkit")
    ("Re-added for backward compatibility")
    public final static String ARG_TYPE_BOOL = "bool";

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "Removed from Cloudburst Nukkit")
    ("Re-added for backward compatibility")
    public final static String ARG_TYPE_TARGET = "target";

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "Removed from Cloudburst Nukkit")
    ("Re-added for backward compatibility")
    public final static String ARG_TYPE_PLAYER = "target";

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "Removed from Cloudburst Nukkit")
    ("Re-added for backward compatibility")
    public final static String ARG_TYPE_BLOCK_POS = "blockpos";

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "Removed from Cloudburst Nukkit")
    ("Re-added for backward compatibility")
    public final static String ARG_TYPE_RAW_TEXT = "rawtext";

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "Removed from Cloudburst Nukkit")
    ("Re-added for backward compatibility")
    public final static String ARG_TYPE_INT = "int";

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "Removed from Cloudburst Nukkit")
    ("Re-added for backward compatibility")
    public static final String ENUM_TYPE_ITEM_LIST = "Item";

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "Removed from Cloudburst Nukkit")
    ("Re-added for backward compatibility")
    public static final String ENUM_TYPE_BLOCK_LIST = "blockType";

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "Removed from Cloudburst Nukkit")
    ("Re-added for backward compatibility")
    public static final String ENUM_TYPE_COMMAND_LIST = "commandName";

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "Removed from Cloudburst Nukkit")
    ("Re-added for backward compatibility")
    public static final String ENUM_TYPE_ENCHANTMENT_LIST = "enchantmentType";

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "Removed from Cloudburst Nukkit")
    ("Re-added for backward compatibility")
    public static final String ENUM_TYPE_ENTITY_LIST = "entityType";

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "Removed from Cloudburst Nukkit")
    ("Re-added for backward compatibility")
    public static final String ENUM_TYPE_EFFECT_LIST = "effectType";

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "Removed from Cloudburst Nukkit")
    ("Re-added for backward compatibility")
    public static final String ENUM_TYPE_PARTICLE_LIST = "particleType";
}
