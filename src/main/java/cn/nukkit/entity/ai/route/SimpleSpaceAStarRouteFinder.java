package cn.nukkit.entity.ai.route;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.route.blockevaluator.IBlockEvaluator;
import cn.nukkit.entity.ai.route.data.Node;
import org.jetbrains.annotations.NotNull;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class SimpleSpaceAStarRouteFinder extends SimpleFlatAStarRouteFinder {
    public SimpleSpaceAStarRouteFinder(IBlockEvaluator blockEvaluator, EntityIntelligent entity) {
        super(blockEvaluator, entity);
    }

    @Override
    protected void putNeighborNodeIntoOpen(@NotNull Node node) {

    }
}
