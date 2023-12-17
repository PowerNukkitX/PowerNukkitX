package cn.nukkit.utils.functional;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;


@FunctionalInterface
public interface BlockPositionDataConsumer<D> {


    void accept(int x, int y, int z, D data);
}
