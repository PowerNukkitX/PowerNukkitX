package cn.nukkit.level.generator.populator.impl.structure.utils.template;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.math.BlockVector3;


public interface StructureTemplate {

    BlockVector3 getSize();

    boolean isInvalid();

    void clean();
}
