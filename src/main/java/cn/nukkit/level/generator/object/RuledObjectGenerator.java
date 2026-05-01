package cn.nukkit.level.generator.object;

import cn.nukkit.level.Location;

@Deprecated(forRemoval = true)
public interface RuledObjectGenerator {

    String getName();

    boolean canGenerateAt(Location location);
}
