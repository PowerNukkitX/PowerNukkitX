package cn.nukkit.command.data;

import cn.nukkit.command.tree.node.IParamNode;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a parameter definition for a command in PowerNukkitX.
 * <p>
 * This class is used to describe the properties, type, and options for a single command argument. It supports
 * both primitive types and enum-based parameters, optional arguments, postfixes, advanced parsing nodes, and
 * custom options for validation and auto-completion.
 * <p>
 * Features:
 * <ul>
 *   <li>Supports primitive types and enum-based parameters via {@link CommandParamType} and {@link CommandEnum}.</li>
 *   <li>Allows marking parameters as optional.</li>
 *   <li>Supports postfixes for argument formatting.</li>
 *   <li>Integrates with {@link IParamNode} for advanced argument parsing and validation.</li>
 *   <li>Allows custom options via {@link CommandParamOption} for auto-completion and validation.</li>
 *   <li>Provides static factory methods for creating type and enum parameters with various configurations.</li>
 * </ul>
 * <p>
 * Usage:
 * <ul>
 *   <li>Use static factory methods to create parameters for command registration.</li>
 *   <li>Configure optionality, type, enum data, postfix, param node, and options as needed.</li>
 *   <li>Use in {@link CommandInput} and {@link CommandOverload} to define command signatures.</li>
 * </ul>
 * <p>
 * Example:
 * <pre>
 * CommandParameter param = CommandParameter.newType("target", false, CommandParamType.PLAYER);
 * CommandParameter enumParam = CommandParameter.newEnum("mode", new String[] {"easy", "hard"});
 * </pre>
 *
 * @author PowerNukkitX Project Team
 * @see CommandParamType
 * @see CommandEnum
 * @see CommandParamOption
 * @see IParamNode
 */
public class CommandParameter {
    /**
     * An empty array of CommandParameter, used for commands with no arguments.
     */
    public static final CommandParameter[] EMPTY_ARRAY = new CommandParameter[0];

    /**
     * The name of the parameter (used for display and identification).
     */
    public final String name;
    /**
     * Indicates whether the parameter is optional.
     */
    public final boolean optional;
    /**
     * The primitive type of the parameter (may be null for enum parameters).
     */
    public final CommandParamType type;
    /**
     * The list of custom options for auto-completion and validation.
     */
    public List<CommandParamOption> paramOptions;
    /**
     * The enum data for enum-based parameters (may be null for primitive types).
     */
    public final CommandEnum enumData;
    /**
     * The postfix string for argument formatting (may be null).
     */
    public final String postFix;
    /**
     * The parameter node for advanced parsing and validation (may be null).
     */
    public final IParamNode<?> paramNode;

    /**
     * Constructs a CommandParameter with all fields specified.
     *
     * @param name the parameter name
     * @param optional whether the parameter is optional
     * @param type the primitive type
     * @param enumData the enum data
     * @param postFix the postfix string
     */
    private CommandParameter(String name, boolean optional, CommandParamType type, CommandEnum enumData, String postFix) {
        this(name, optional, type, enumData, postFix, null);
    }

    /**
     * Constructs a CommandParameter with all fields specified, including param node.
     *
     * @param name the parameter name
     * @param optional whether the parameter is optional
     * @param type the primitive type
     * @param enumData the enum data
     * @param postFix the postfix string
     * @param paramNode the parameter node for advanced parsing
     */
    private CommandParameter(String name, boolean optional, CommandParamType type, CommandEnum enumData, String postFix, IParamNode<?> paramNode) {
        this.name = name;
        this.optional = optional;
        this.type = type;
        this.enumData = enumData;
        this.postFix = postFix;
        this.paramNode = paramNode;
    }

    //region Static factory methods

    /**
     * Creates a primitive type parameter (not optional).
     *
     * @param name the parameter name
     * @param type the primitive type
     * @return the command parameter
     * @see #newType(String, boolean, CommandParamType)
     */
    public static CommandParameter newType(String name, CommandParamType type) {
        return newType(name, false, type);
    }

    /**
     * Creates a primitive type parameter with a param node (not optional).
     *
     * @param name the parameter name
     * @param type the primitive type
     * @param paramNode the parameter node
     * @return the command parameter
     * @see #newType(String, boolean, CommandParamType, IParamNode, CommandParamOption...)
     */
    public static CommandParameter newType(String name, CommandParamType type, IParamNode<?> paramNode) {
        return newType(name, false, type, paramNode);
    }

    /**
     * Creates a primitive type parameter with optionality and no param node.
     *
     * @param name the parameter name
     * @param optional whether the parameter is optional
     * @param type the primitive type
     * @return the command parameter
     * @see #newType(String, boolean, CommandParamType, IParamNode, CommandParamOption...)
     */
    public static CommandParameter newType(String name, boolean optional, CommandParamType type) {
        return newType(name, optional, type, null, new CommandParamOption[]{});
    }

    /**
     * Creates a primitive type parameter with optionality and custom options.
     *
     * @param name the parameter name
     * @param optional whether the parameter is optional
     * @param type the primitive type
     * @param options custom options for auto-completion/validation
     * @return the command parameter
     * @see #newType(String, boolean, CommandParamType, IParamNode, CommandParamOption...)
     */
    public static CommandParameter newType(String name, boolean optional, CommandParamType type, CommandParamOption... options) {
        return newType(name, optional, type, null, options);
    }

    /**
     * Creates a primitive type parameter with all fields specified.
     *
     * @param name the parameter name
     * @param optional whether the parameter is optional
     * @param type the primitive type
     * @param paramNode the parameter node
     * @param options custom options for auto-completion/validation
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
     * Creates an enum parameter (not optional).
     *
     * @param name the parameter name
     * @param values the enum values
     * @return the command parameter
     * @see #newEnum(String, boolean, String[])
     */
    public static CommandParameter newEnum(String name, String[] values) {
        return newEnum(name, false, values);
    }

    /**
     * Creates an enum parameter with optionality and values.
     *
     * @param name the parameter name
     * @param optional whether the parameter is optional
     * @param values the enum values
     * @return the command parameter
     * @see #newEnum(String, boolean, CommandEnum)
     */
    public static CommandParameter newEnum(String name, boolean optional, String[] values) {
        return newEnum(name, optional, new CommandEnum(name + "Enums", values));
    }

    /**
     * Creates an enum parameter with optionality, values, and soft flag.
     *
     * @param name the parameter name
     * @param optional whether the parameter is optional
     * @param values the enum values
     * @param soft true for soft enum, false for static
     * @return the command parameter
     * @see #newEnum(String, boolean, CommandEnum)
     */
    public static CommandParameter newEnum(String name, boolean optional, String[] values, boolean soft) {
        return newEnum(name, optional, new CommandEnum(name + "Enums", Arrays.asList(values), soft));
    }

    /**
     * Creates an enum parameter (not optional) with a type name.
     *
     * @param name the parameter name
     * @param type the enum type name
     * @return the command parameter
     * @see #newEnum(String, boolean, CommandEnum, IParamNode, CommandParamOption...)
     */
    public static CommandParameter newEnum(String name, String type) {
        return newEnum(name, false, type);
    }

    /**
     * Creates an enum parameter with optionality and a type name.
     *
     * @param name the parameter name
     * @param optional whether the parameter is optional
     * @param type the enum type name
     * @return the command parameter
     * @see #newEnum(String, boolean, CommandEnum, IParamNode, CommandParamOption...)
     */
    public static CommandParameter newEnum(String name, boolean optional, String type) {
        return newEnum(name, optional, new CommandEnum(type, new ArrayList<>()));
    }

    /**
     * Creates an enum parameter (not optional) with enum data.
     *
     * @param name the parameter name
     * @param data the enum data
     * @return the command parameter
     * @see #newEnum(String, boolean, CommandEnum)
     */
    public static CommandParameter newEnum(String name, CommandEnum data) {
        return newEnum(name, false, data);
    }

    /**
     * Creates an enum parameter with optionality and enum data.
     *
     * @param name the parameter name
     * @param optional whether the parameter is optional
     * @param data the enum data
     * @return the command parameter
     * @see #newEnum(String, boolean, CommandEnum, IParamNode, CommandParamOption...)
     */
    public static CommandParameter newEnum(String name, boolean optional, CommandEnum data) {
        return new CommandParameter(name, optional, null, data, null);
    }

    /**
     * Creates an enum parameter with optionality, enum data, and custom options.
     *
     * @param name the parameter name
     * @param optional whether the parameter is optional
     * @param data the enum data
     * @param options custom options for auto-completion/validation
     * @return the command parameter
     * @see #newEnum(String, boolean, CommandEnum, IParamNode, CommandParamOption...)
     */
    public static CommandParameter newEnum(String name, boolean optional, CommandEnum data, CommandParamOption... options) {
        return newEnum(name, optional, data, null, options);
    }

    /**
     * Creates an enum parameter with optionality, enum data, and param node.
     *
     * @param name the parameter name
     * @param optional whether the parameter is optional
     * @param data the enum data
     * @param paramNode the parameter node
     * @return the command parameter
     * @see #newEnum(String, boolean, CommandEnum, IParamNode, CommandParamOption...)
     */
    public static CommandParameter newEnum(String name, boolean optional, CommandEnum data, IParamNode<?> paramNode) {
        return newEnum(name, optional, data, paramNode, new CommandParamOption[]{});
    }

    /**
     * Creates an enum parameter with all fields specified.
     *
     * @param name the parameter name
     * @param optional whether the parameter is optional
     * @param data the enum data
     * @param paramNode the parameter node
     * @param options custom options for auto-completion/validation
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
