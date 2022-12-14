package cn.nukkit.entity.passive;

import cn.nukkit.Player;
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
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Utils;

import java.util.Set;

/**
 * @author PetteriM1
 */
public class EntitySalmon extends EntitySwimmingAnimal {

    public static final int NETWORK_ID = 109;
    private IBehaviorGroup behaviorGroup;

    public EntitySalmon(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public IBehaviorGroup getBehaviorGroup() {
        if (behaviorGroup == null) {
            behaviorGroup = new BehaviorGroup(
                    this.tickSpread,
                    Set.of(
                            new Behavior((entity) -> {
                                //刷新随机播放音效
                                if (!this.isInsideOfWater())
                                    this.getLevel().addSound(this, Sound.MOB_FISH_FLOP, 1.0f, 1.0f);
                                return false;
                            }, (entity) -> true, 1, 1, 20)
                    ),
                    Set.of(
                            new Behavior(new FlatRandomRoamExecutor(0.2f, 12, 150, false, -1, true, 20), new ProbabilityEvaluator(5, 10), 1, 1, 25)
                    ),
                    Set.of(),
                    Set.of(new SpaceMoveController(), new LookController(true, true)),
                    new SimpleFlatAStarRouteFinder(new SwimmingPosEvaluator(), this)
            );
        }
        return behaviorGroup;
    }


    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    @Override
    public String getOriginalName() {
        return "Salmon";
    }

    @Override
    public float getWidth() {
        if (this.isBaby()) {
            return 0.25f;
        } else if (this.isLarge()) {
            return 0.75f;
        }
        return 0.5f;
    }

    @Override
    public float getHeight() {
        if (this.isBaby()) {
            return 0.25f;
        } else if (this.isLarge()) {
            return 0.75f;
        }
        return 0.5f;
    }

    @Override
    public void initEntity() {
        this.setMaxHealth(3);
        super.initEntity();
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        if (item.getId() == Item.BUCKET && (item.getDamage() == 0 || item.getDamage() == 8) && Utils.entityInsideWaterFast(this)) {
            this.close();
            if (item.getCount() <= 1) {
                player.getInventory().setItemInHand(Item.get(Item.BUCKET, this.getBucketMeta(), 1));
                return false;
            } else {
                if (!player.isCreative()) {
                    player.getInventory().decreaseCount(player.getInventory().getHeldItemIndex());
                }
                player.getInventory().addItem(Item.get(Item.BUCKET, this.getBucketMeta(), 1));
                return true;
            }
        }
        return super.onInteract(player, item, clickedPos);
    }

    int getBucketMeta() {
        return 0;
    }

    @Override
    public Item[] getDrops() {
        int rand = Utils.rand(0, 3);
        if (this.isLarge()) {
            //只有25%获得骨头 来自wiki https://minecraft.fandom.com/zh/wiki/%E9%B2%91%E9%B1%BC
            if (rand == 1) {
                return new Item[]{Item.get(Item.BONE, 0, Utils.rand(1, 2)), Item.get(((this.isOnFire()) ? Item.COOKED_SALMON : Item.RAW_SALMON))};
            }
        } else if (!this.isLarge()) {
            //只有25%获得骨头 来自wiki https://minecraft.fandom.com/zh/wiki/%E9%B2%91%E9%B1%BC
            if (rand == 1) {
                return new Item[]{Item.get(Item.BONE), Item.get(((this.isOnFire()) ? Item.COOKED_SALMON : Item.RAW_SALMON))};
            }
        }
        return new Item[]{Item.get(((this.isOnFire()) ? Item.COOKED_SALMON : Item.RAW_SALMON))};
    }
}
