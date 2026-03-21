package cn.nukkit.entity.ai.evaluator;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.item.Item;
import cn.nukkit.utils.Identifier;

/**
 * Evaluates whether the entity is currently being ridden by a player
 * holding the configured control item in hand. <p>
 *
 * Used for item-based rider control mechanics (e.g. carrot on a stick,
 * warped fungus on a stick), matching the item ID.
 * 
 * @author Curse
 */
public class RiderItemControllableEvaluator implements IBehaviorEvaluator {

    @Override
    public boolean evaluate(EntityIntelligent entity) {
        if (!(entity instanceof Entity e)) return false;

        Entity rider = e.getPassenger();
        if (!(rider instanceof Player player)) return false;

        String wanted = normalizeId(e.getItemControllable());
        if (wanted == null) return false;

        Item hand = player.getInventory().getItemInHand();
        if (hand.isNull()) return false;

        String inHand = normalizeId(hand.getId());
        if (inHand == null) return false;

        return wanted.equals(inHand);
    }

    private static String normalizeId(String id) {
        if (id == null || id.isBlank()) return null;

        Identifier identifier = Identifier.tryParse(id.trim());
        return identifier != null ? identifier.toString() : null;
    }
}
