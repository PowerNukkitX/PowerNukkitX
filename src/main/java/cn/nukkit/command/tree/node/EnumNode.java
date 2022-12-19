package cn.nukkit.command.tree.node;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.command.tree.ParamList;


/**
 * 会评估当前参数是否在枚举中，如果不在则抛出{@link CommandSyntaxException}<br>
 * 对应枚举参数类型{@link cn.nukkit.command.data.CommandEnum}
 */
@PowerNukkitXOnly
@Since("1.19.50-r4")
public class EnumNode extends ParamNode<String> {
    protected CommandEnum commandEnum;

    @Override
    public void fill(String arg) throws CommandSyntaxException {
        if (commandEnum.isSoft()) {
            this.value = arg;
            return;
        }
//        if (commandEnum.getName().equals("Block")) {
//            if (BlockStateRegistry.getBlockId(arg) != null || BlockStateRegistry.getBlockId("minecraft:" + arg) != null)
//                throw new CommandSyntaxException();
//        }
//        if (commandEnum.getName().equals("Item")) {
//            if (RuntimeItems.getRuntimeMapping().getNetworkIdByNamespaceId(arg).isPresent() || RuntimeItems.getRuntimeMapping().getNetworkIdByNamespaceId("minecraft:" + arg).isPresent()) {
//                throw new CommandSyntaxException();
//            }
//        }
        if (!commandEnum.getValues().contains(arg)) throw new CommandSyntaxException();
        this.value = arg;
    }

    @Override
    public IParamNode<String> init(ParamList parent, String name, boolean optional, CommandParamType type, CommandEnum enumData, String postFix) {
        this.parent = parent;
        this.commandEnum = enumData;
        this.optional = optional;
        return this;
    }

}
