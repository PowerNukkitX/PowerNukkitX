package cn.nukkit.command.tree.node;

import cn.nukkit.Server;

import java.util.ArrayList;
import java.util.List;

/**
 * 将全部剩余的参数以空格为分隔符合并，解析为{@link String}值
 * <p>
 * 所有命令参数类型为{@link cn.nukkit.command.data.CommandParamType#COMMAND COMMAND}如果没有手动指定{@link IParamNode},则会默认使用这个解析
 */
public class CommandNode extends ParamNode<String> {
    private final List<String> TMP = new ArrayList<>();

    private boolean first = true;

    @Override
    public void fill(String arg) {
        if (arg.contains(" ")) {
            arg = "\"" + arg + "\"";
        }
        if (first && !Server.getInstance().getCommandMap().getCommands().containsKey(arg)) {
            this.error("commands.generic.unknown", arg);
            return;
        }
        if (this.paramList.getIndex() != this.paramList.getParamTree().getArgs().length) {
            first = false;
            TMP.add(arg);
        } else {
            TMP.add(arg);
            this.value = String.join(" ", TMP);
            first = true;
        }
    }

    @Override
    public void reset() {
        super.reset();
        TMP.clear();
    }
}
