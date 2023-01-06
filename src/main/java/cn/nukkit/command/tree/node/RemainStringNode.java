package cn.nukkit.command.tree.node;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

import java.util.ArrayList;
import java.util.List;

@PowerNukkitXOnly
@Since("1.19.50-r4")
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
