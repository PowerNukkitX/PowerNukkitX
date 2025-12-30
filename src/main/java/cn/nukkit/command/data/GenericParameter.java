package cn.nukkit.command.data;

import cn.nukkit.command.tree.node.ChainedCommandNode;
import cn.nukkit.command.tree.node.ItemNode;

/**
 * Provides generic parameter suppliers for common command argument types in PowerNukkitX.
 * <p>
 * This interface defines reusable suppliers for frequently used command parameters, such as objectives,
 * items, chained commands, and origins. Each supplier produces a {@link CommandParameter} instance with
 * appropriate configuration for its use case, supporting optionality and advanced argument nodes.
 * <p>
 * Features:
 * <ul>
 *   <li>Suppliers for scoreboard objectives, target objectives, item names, chained commands, and origin selectors.</li>
 *   <li>Supports enum-based parameters and advanced argument nodes for auto-completion and validation.</li>
 *   <li>Facilitates consistent command registration and argument definition across the codebase.</li>
 * </ul>
 * <p>
 * Usage:
 * <ul>
 *   <li>Use the provided suppliers to create {@link CommandParameter} instances for command registration.</li>
 *   <li>Pass the <code>optional</code> flag to control whether the parameter is required or optional.</li>
 *   <li>Extend or implement additional suppliers for custom parameter types as needed.</li>
 * </ul>
 * <p>
 * Example:
 * <pre>
 * CommandParameter param = GenericParameter.ITEM_NAME.get(true);
 * </pre>
 *
 * @author PowerNukkitX Project Team
 * @see CommandParameter
 * @see CommandEnum
 * @see CommandParamOption
 * @see cn.nukkit.command.tree.node.ItemNode
 * @see cn.nukkit.command.tree.node.ChainedCommandNode
 */
public interface GenericParameter {
    /**
     * Supplier for scoreboard objective parameters.
     * Produces a parameter named "objective" using {@link CommandEnum#SCOREBOARD_OBJECTIVES}.
     */
    CommandParameterSupplier<CommandParameter> OBJECTIVES = (optional) -> CommandParameter.newEnum("objective", optional, CommandEnum.SCOREBOARD_OBJECTIVES);
    /**
     * Supplier for target scoreboard objective parameters.
     * Produces a parameter named "targetObjective" using {@link CommandEnum#SCOREBOARD_OBJECTIVES}.
     */
    CommandParameterSupplier<CommandParameter> TARGET_OBJECTIVES = (optional) -> CommandParameter.newEnum("targetObjective", optional, CommandEnum.SCOREBOARD_OBJECTIVES);
    /**
     * Supplier for item name parameters.
     * Produces a parameter named "itemName" using {@link CommandEnum#ENUM_ITEM} and {@link cn.nukkit.command.tree.node.ItemNode} for advanced parsing.
     */
    CommandParameterSupplier<CommandParameter> ITEM_NAME = (optional) -> CommandParameter.newEnum("itemName", optional, CommandEnum.ENUM_ITEM, new ItemNode());
    /**
     * Supplier for chained command parameters.
     * Produces a parameter named "chainedCommand" using {@link CommandEnum#CHAINED_COMMAND_ENUM}, {@link cn.nukkit.command.tree.node.ChainedCommandNode}, and {@link CommandParamOption#ENUM_AS_CHAINED_COMMAND}.
     */
    CommandParameterSupplier<CommandParameter> CHAINED_COMMAND = (optional) -> CommandParameter.newEnum("chainedCommand", optional, CommandEnum.CHAINED_COMMAND_ENUM, new ChainedCommandNode(), CommandParamOption.ENUM_AS_CHAINED_COMMAND);
    /**
     * Supplier for origin selector parameters.
     * Produces a parameter named "origin" using {@link CommandParamType#TARGET}.
     */
    CommandParameterSupplier<CommandParameter> ORIGIN = (optional) -> CommandParameter.newType("origin", optional, CommandParamType.TARGET);

    /**
     * Functional interface for supplying command parameters with optionality.
     * <p>
     * Implementations should return a configured {@link CommandParameter} based on the <code>optional</code> flag.
     *
     * @param <T> the type of parameter supplied
     */
    @FunctionalInterface
    interface CommandParameterSupplier<T> {
        /**
         * Returns a command parameter instance, configured as optional or required.
         *
         * @param optional true if the parameter should be optional, false if required
         * @return the configured command parameter
         */
        T get(boolean optional);
    }
}
