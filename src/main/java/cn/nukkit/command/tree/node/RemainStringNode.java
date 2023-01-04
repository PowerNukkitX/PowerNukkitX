package cn.nukkit.command.tree.node;

import java.util.ArrayList;
import java.util.List;

public class RemainStringNode extends ParamNode<String[]> {
    private final List<String> TMP = new ArrayList<>();

    @Override
    public void fill(String arg) {
        if (this.parent.getIndex() != this.parent.parent.getArgs().length) TMP.add(arg);
        else {
            TMP.add(arg);
            this.value = TMP.toArray(new String[]{});
        }
    }

    @Override
    public void reset() {
        super.reset();
        TMP.clear();
    }
}
