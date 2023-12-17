package cn.nukkit.level.generator.populator.impl.structure.utils.template;

import cn.nukkit.math.BlockVector3;


public abstract class AbstractStructureTemplate implements StructureTemplate {

    protected BlockVector3 size = new BlockVector3();

    @Override
    public BlockVector3 getSize() {
        return this.size;
    }

    @Override
    public boolean isInvalid() {
        return this.size.getX() < 1 || this.size.getY() < 1 || this.size.getZ() < 1;
    }
}
