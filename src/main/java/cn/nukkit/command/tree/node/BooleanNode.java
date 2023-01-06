package cn.nukkit.command.tree.node;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.data.CommandEnum;
import com.google.common.collect.Sets;

import java.util.Set;

/**
 * 解析对应参数为{@link Boolean}值
 * <br>
 * 没有默认的对应参数类型，需要手动指定
 */
@PowerNukkitXOnly
@Since("1.19.50-r4")
public class BooleanNode extends ParamNode<Boolean> {
    private final static Set<String> ENUM_BOOLEAN = Sets.newHashSet(CommandEnum.ENUM_BOOLEAN.getValues());

    @Override
    public void fill(String arg) {
        if (ENUM_BOOLEAN.contains(arg)) this.value = Boolean.parseBoolean(arg);
        else this.error();
    }
}
