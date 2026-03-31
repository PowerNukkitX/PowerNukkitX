package cn.nukkit.level.generator.object;

import cn.nukkit.level.Location;

public interface RuledObjectGenerator {

    String getName();

    boolean canGenerateAt(Location location);
}
