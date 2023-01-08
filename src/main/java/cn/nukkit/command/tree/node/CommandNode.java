package cn.nukkit.command.tree.node;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

import java.util.*;

/**
 * 会填充所有剩余参数以空格分隔合并<br>
 * {@link cn.nukkit.command.defaults.ExecuteCommand ExecuteCommand}命令的子命令节点<br>
 * 对应参数类型{@link cn.nukkit.command.data.CommandParamType#COMMAND COMMAND}
 */
@PowerNukkitXOnly
@Since("1.19.50-r4")
public class CommandNode extends ParamNode<String> {
    private final static Set<String> COMMAND_NAMES = new HashSet<>();

    private final List<String> TMP = new ArrayList<>();

    private boolean first = true;

    @Override
    public void fill(String arg) {
        if (first && !COMMAND_NAMES.contains(arg)) {
            this.error("commands.generic.unknown", arg);
            return;
        }
        if (this.parent.getIndex() != this.parent.parent.getArgs().length) {
            first = false;
            TMP.add(arg);
        } else {
            TMP.add(arg);
            var join = new StringJoiner(" ");
            TMP.forEach(join::add);
            this.value = join.toString();
            first = true;
        }
    }

    @Override
    public void reset() {
        super.reset();
        TMP.clear();
    }

    public static void setCommandNames(Set<String> names) {
        if (COMMAND_NAMES.isEmpty()) COMMAND_NAMES.addAll(names);
    }
}
