package cn.nukkit.command.tree.node;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

import java.util.ArrayList;
import java.util.List;

/**
 * 解析全部剩余参数为{@code String[]}值
 * <p>
 * 所有命令参数类型为{@link cn.nukkit.command.data.CommandParamType#MESSAGE MESSAGE} , {@link cn.nukkit.command.data.CommandParamType#JSON JSON}的
 * 如果没有手动指定{@link IParamNode},则会默认使用这个解析
 */
@PowerNukkitXOnly
@Since("1.19.50-r4")
public class RemainStringNode extends ParamNode<String[]> {
    private final String defaultStr;

    public RemainStringNode() {
        defaultStr = null;
    }

    public RemainStringNode(String defaultStr) {
        this.defaultStr = defaultStr;
    }

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
        if (defaultStr != null) this.value = new String[]{defaultStr};
        else super.reset();
        TMP.clear();
    }
}
