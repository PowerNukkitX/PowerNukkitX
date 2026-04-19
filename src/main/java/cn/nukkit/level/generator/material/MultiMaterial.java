package cn.nukkit.level.generator.material;

import cn.nukkit.block.BlockState;
import cn.nukkit.level.generator.densityfunction.DensityFunction;

public record MultiMaterial(MaterialFiller[] materials) implements MaterialFiller {

    @Override
    public BlockState calculate(final DensityFunction.FunctionContext context) {
        for (MaterialFiller rule : this.materials) {
            if (rule == null) {
                continue;
            }
            BlockState state = rule.calculate(context);
            if (state != null) {
                return state;
            }
        }
        return null;
    }
}
