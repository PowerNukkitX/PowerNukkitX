package cn.nukkit.entity.ai.route;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;

/**
 * @author zzz1999 daoge_cmd @ MobPlugin
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class SimpleRouteFinder extends RouteFinder{

    public SimpleRouteFinder(EntityIntelligent entity) {
        super(entity);
    }

    @Override
    public boolean search() {
        this.resetNodes();
        this.addNode(new Node(this.destination));
        return true;
    }
}