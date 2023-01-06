package cn.nukkit.command.data;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.tree.node.ChainedCommandNode;
import cn.nukkit.command.tree.node.StringNode;

@PowerNukkitXOnly
@Since("1.19.50-r4")
public interface GenericParameter {
    CommandParameterSupplier<CommandParameter> OBJECTIVES = (optional) -> CommandParameter.newEnum("objective", optional, CommandEnum.SCOREBOARD_OBJECTIVES);
    CommandParameterSupplier<CommandParameter> TARGET_OBJECTIVES = (optional) -> CommandParameter.newEnum("targetObjective", optional, CommandEnum.SCOREBOARD_OBJECTIVES);
    CommandParameterSupplier<CommandParameter> ITEM_NAME = (optional) -> CommandParameter.newEnum("itemName", optional, CommandEnum.ENUM_ITEM, new StringNode());
    CommandParameterSupplier<CommandParameter> CHAINED_COMMAND = (optional) -> CommandParameter.newEnum("chainedCommand", false, CommandEnum.CHAINED_COMMAND_ENUM, new ChainedCommandNode(), CommandParamOption.ENUM_AS_CHAINED_COMMAND);
    CommandParameterSupplier<CommandParameter> ORIGIN = (optional) -> CommandParameter.newType("origin", optional, CommandParamType.TARGET);

    @FunctionalInterface
    interface CommandParameterSupplier<T> {
        T get(boolean optional);
    }
}
