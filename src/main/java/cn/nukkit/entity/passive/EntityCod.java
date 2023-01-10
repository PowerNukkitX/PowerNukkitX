package cn.nukkit.entity.passive;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.controller.SpaceMoveController;
import cn.nukkit.entity.ai.evaluator.ProbabilityEvaluator;
import cn.nukkit.entity.ai.executor.FlatRandomRoamExecutor;
import cn.nukkit.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.SwimmingPosEvaluator;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Utils;

import java.util.Set;

/**
 * @author PetteriM1
 */
public class EntityCod extends EntityFish {

    public static final int NETWORK_ID = 112;

    public EntityCod(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    @Override
    public String getOriginalName() {
        return "Cod";
    }

    @Override
    public float getWidth() {
        return 0.6f;
    }

    @Override
    public float getHeight() {
        return 0.3f;
    }

    @Override
    public void initEntity() {
        this.setMaxHealth(3);
        super.initEntity();
    }

    @Override
    public Item[] getDrops() {
        //只能25%获得骨头
        if (Utils.rand(0, 3) == 1) {
            return new Item[]{Item.get(Item.BONE, 0, Utils.rand(1, 2)), Item.get(((this.isOnFire()) ? Item.COOKED_FISH : Item.RAW_FISH))};
        }
        return new Item[]{Item.get(((this.isOnFire()) ? Item.COOKED_FISH : Item.RAW_FISH))};
    }
}
