package cn.nukkit.entity.ai.evaluator;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.item.Item;


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

        String controlItem = e.getItemControllable();
        if (controlItem == null || controlItem.isBlank()) return false;

        Item hand = player.getInventory().getItemInHand();
        if (hand.isNull()) return false;

        String wanted = normalizeControlId(controlItem);
        String inHand = normalizeItemId(hand);

        if (inHand == null) return false;

        return wanted.equalsIgnoreCase(inHand);
    }

    private static String normalizeItemId(Item item) {
        String ns = item.getId();
        if (ns == null || ns.isBlank()) return null;
        return ns.trim();
    }

    private static String normalizeControlId(String id) {
        String v = id.trim();
        if (v.isEmpty()) return null;

        if (v.indexOf(':') >= 0) { return v; }
        return "minecraft:" + v;
    }
}
