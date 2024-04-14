package cn.nukkit.command.tree.node;

import java.util.ArrayList;
import java.util.List;

/**
 * 解析全部剩余参数拼接为{@code String}值
 * <p>
 * 所有命令参数类型为{@link cn.nukkit.command.data.CommandParamType#JSON JSON}的
 * 如果没有手动指定{@link IParamNode},则会默认使用这个解析
 */
public class RemainStringNode extends ParamNode<String> {
    private final List<String> TMP = new ArrayList<>();

    @Override
    public void fill(String arg) {
        if (this.paramList.getIndex() != paramList.getParamTree().getArgs().length) TMP.add(arg);
        else {
            TMP.add(arg);
            this.value = String.join("", TMP);
        }
    }

    @Override
    public void reset() {
        super.reset();
        TMP.clear();
    }
}
