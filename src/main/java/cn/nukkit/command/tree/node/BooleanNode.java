package cn.nukkit.command.tree.node;

import cn.nukkit.command.data.CommandEnum;
import com.google.common.collect.Sets;

import java.util.Set;

public class BooleanNode extends ParamNode<Boolean> {
    private final static Set<String> ENUM_BOOLEAN = Sets.newHashSet(CommandEnum.ENUM_BOOLEAN.getValues());

    @Override
    public void fill(String arg) {
        if (ENUM_BOOLEAN.contains(arg)) this.value = Boolean.parseBoolean(arg);
        else this.parent.error();
    }
}
