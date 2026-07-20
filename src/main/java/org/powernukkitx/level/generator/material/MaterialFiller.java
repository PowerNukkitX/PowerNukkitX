package org.powernukkitx.level.generator.material;

import org.powernukkitx.block.BlockState;
import org.powernukkitx.level.generator.densityfunction.DensityFunction;

import javax.annotation.Nullable;

@FunctionalInterface
public interface MaterialFiller {
    @Nullable
    BlockState calculate(DensityFunction.FunctionContext context);
}
