package cn.nukkit.command.tree.node;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.tree.ParamList;
import com.google.common.collect.Sets;

import java.util.Set;


/**
 * 会评估当前参数是否在枚举中，如果不在则标记错误<br>
 * 对应枚举参数类型{@link cn.nukkit.command.data.CommandEnum}
 */
@PowerNukkitXOnly
@Since("1.19.50-r4")
public class EnumNode extends ParamNode<String> {
    protected CommandEnum commandEnum;
    protected Set<String> enums;

    @Override
    public void fill(String arg) {
        if (commandEnum.isSoft()) {
            this.value = arg;
            return;
        }
        if (enums.contains(arg)) this.value = arg;
        else this.error();
    }

    @Override
    public IParamNode<String> init(ParamList parent, String name, boolean optional, CommandParamType type, CommandEnum enumData, String postFix) {
        this.parent = parent;
        this.commandEnum = enumData;
        this.enums = Sets.newHashSet(this.commandEnum.getValues());
        this.optional = optional;
        return this;
    }

}
