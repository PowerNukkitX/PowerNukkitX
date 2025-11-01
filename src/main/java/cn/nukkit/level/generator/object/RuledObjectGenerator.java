package cn.nukkit.level.generator.object;

import cn.nukkit.level.Location;
import cn.nukkit.utils.random.Xoroshiro128;

public abstract class RuledObjectGenerator extends ObjectGenerator {

    protected final Xoroshiro128 random = new Xoroshiro128();

    public abstract String getName();

    public abstract boolean canGenerateAt(Location location);

}
