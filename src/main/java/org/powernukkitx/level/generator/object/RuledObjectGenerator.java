package org.powernukkitx.level.generator.object;

import org.powernukkitx.level.Location;

@Deprecated(forRemoval = true)
public interface RuledObjectGenerator {

    String getName();

    boolean canGenerateAt(Location location);
}
