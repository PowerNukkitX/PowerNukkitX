package cn.nukkit.command.tree.node;

import cn.nukkit.command.exceptions.CommandSyntaxException;
import com.google.common.collect.Sets;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.StringJoiner;

public class ChainedCommandNode extends EnumNode {
    private static final HashSet<String> CHAINED = Sets.newHashSet("run", "as", "at", "positioned", "if", "unless", "in", "align", "anchored", "rotated", "facing");
    private boolean remain = false;

    protected final List<String> TMP = new ArrayList<>();

    @Override
    public void fill(String arg) throws CommandSyntaxException {
        if (!remain) {
            if (!CHAINED.contains(arg)) throw new CommandSyntaxException();
            TMP.add(arg);
            remain = true;
        } else {
            if (this.parent.getIndex() != this.parent.parent.getArgs().length) TMP.add(arg);
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
