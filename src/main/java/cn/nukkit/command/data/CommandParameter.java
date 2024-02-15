package cn.nukkit.command.data;

import cn.nukkit.command.tree.node.IParamNode;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandParameter {
    public static final CommandParameter[] EMPTY_ARRAY = new CommandParameter[0];

    public final String name;
    public final boolean optional;
    public final CommandParamType type;
    public List<CommandParamOption> paramOptions;
    public final CommandEnum enumData;
    public final String postFix;
    public final IParamNode<?> paramNode;

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
    public static CommandParameter newEnum(String name, boolean optional, String[] values, boolean soft) {
        return newEnum(name, optional, new CommandEnum(name + "Enums", Arrays.asList(values), soft));
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
}
