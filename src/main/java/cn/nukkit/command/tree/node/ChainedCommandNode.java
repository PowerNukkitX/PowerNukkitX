package cn.nukkit.command.tree.node;

import com.google.common.collect.Sets;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.StringJoiner;


/**
 * {@link cn.nukkit.command.defaults.ExecuteCommand ExecuteCommand}命令的链命令节点
 */


public class ChainedCommandNode extends EnumNode {
    private static final HashSet<String> CHAINED = Sets.newHashSet("run", "as", "at", "positioned", "if", "unless", "in", "align", "anchored", "rotated", "facing");
    private boolean remain = false;

    private final List<String> TMP = new ArrayList<>();

    @Override
    public void fill(String arg) {
        if (arg.contains(" ")) {
            arg = "\"" + arg + "\"";
        }
        if (!remain) {
            if (!CHAINED.contains(arg)) {
                this.error();
                return;
            }
            TMP.add(arg);
            remain = true;
        } else {
            if (this.paramList.getIndex() != this.paramList.getParamTree().getArgs().length) TMP.add(arg);
            else {
                TMP.add(arg);
                var join = new StringJoiner(" ", "execute ", "");
                TMP.forEach(join::add);
                this.value = join.toString();
            }
        }
    }

    @Override
    public void reset() {
        super.reset();
        TMP.clear();
        this.remain = false;
    }
}
