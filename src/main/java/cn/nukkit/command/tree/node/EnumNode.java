package cn.nukkit.command.tree.node;

import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.tree.ParamList;
import com.google.common.collect.Sets;

import java.util.Set;


/**
 * 解析为{@link String}值
 * <p>
 * 所有命令枚举类型如果没有手动指定{@link IParamNode},则会默认使用这个解析
 */
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
        this.paramList = parent;
        this.commandEnum = enumData;
        this.enums = Sets.newHashSet(this.commandEnum.getValues());
        this.optional = optional;
        return this;
    }

}
