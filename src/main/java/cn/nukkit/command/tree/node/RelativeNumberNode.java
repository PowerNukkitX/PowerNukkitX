package cn.nukkit.command.tree.node;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

/**
 * @author daoge_cmd <br>
 * Date: 2023/6/11 <br>
 * PowerNukkitX Project <br>
 */


public abstract class RelativeNumberNode<T extends Number> extends ParamNode<T> {
    @Override
    public <E> E get() {
        throw new UnsupportedOperationException();
    }

    public abstract <E> E get(T base);
}
