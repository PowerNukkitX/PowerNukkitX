package cn.nukkit.level.generator.material;

import cn.nukkit.block.BlockState;
import cn.nukkit.level.generator.densityfunction.DensityFunction;

import javax.annotation.Nullable;

@FunctionalInterface
public interface MaterialFiller {
    @Nullable
    BlockState calculate(DensityFunction.FunctionContext context);
}
