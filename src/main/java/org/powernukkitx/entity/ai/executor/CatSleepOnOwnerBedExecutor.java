package org.powernukkitx.entity.ai.executor;

import org.powernukkitx.Player;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.item.Item;
import org.powernukkitx.utils.Utils;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;

/**
 * Sleeps in the owner's bed like {@link SleepOnOwnerBedExecutor} does, and leaves a gift next to
 * the owner on waking up.
 */
public class CatSleepOnOwnerBedExecutor extends SleepOnOwnerBedExecutor {

    private static final float GIFT_CHANCE = 0.7f;

    /**
     * The gift pool, each item repeated as many times as its weight. The phantom membrane is
     * five times rarer than the six other entries.
     */
    private static final String[] GIFTS = {
            Item.RABBIT_HIDE, Item.RABBIT_HIDE, Item.RABBIT_HIDE, Item.RABBIT_HIDE, Item.RABBIT_HIDE,
            Item.RABBIT_FOOT, Item.RABBIT_FOOT, Item.RABBIT_FOOT, Item.RABBIT_FOOT, Item.RABBIT_FOOT,
            Item.CHICKEN, Item.CHICKEN, Item.CHICKEN, Item.CHICKEN, Item.CHICKEN,
            Item.FEATHER, Item.FEATHER, Item.FEATHER, Item.FEATHER, Item.FEATHER,
            Item.ROTTEN_FLESH, Item.ROTTEN_FLESH, Item.ROTTEN_FLESH, Item.ROTTEN_FLESH, Item.ROTTEN_FLESH,
            Item.STRING, Item.STRING, Item.STRING, Item.STRING, Item.STRING,
            Item.PHANTOM_MEMBRANE
    };

    private boolean slept;

    @Override
    public boolean execute(EntityIntelligent entity) {
        boolean running = super.execute(entity);
        if (entity.getDataFlag(ActorFlags.RESTING)) {
            this.slept = true;
        }
        return running;
    }

    @Override
    protected void stop(EntityIntelligent entity) {
        boolean gift = this.slept;
        this.slept = false;
        super.stop(entity);

        if (gift && Utils.rand(0f, 1f) < GIFT_CHANCE) {
            dropGift(entity);
        }
    }

    protected void dropGift(EntityIntelligent entity) {
        Player owner = entity.getOwner();
        if (owner == null) {
            return;
        }
        entity.getLevel().dropItem(owner, Item.get(GIFTS[Utils.rand(0, GIFTS.length - 1)]));
    }
}
