package cn.nukkit.entity;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.item.Item;


/**
 * Functional predicate used to evaluate whether an {@link Entity} satisfies a given condition
 * under a specific interaction context.
 * <p>
 * The {@link Context} record provides optional runtime information such as the other entity,
 * target, damager, parent, player, block, or item involved in the evaluation.
 * <p>
 * Includes utility combinators ({@code all}, {@code any}, {@code not}) for composing filters.
 */
@FunctionalInterface
public interface EntityFilter {
    boolean test(Entity self, Context ctx);

    record Context(Entity other, Entity target, Entity damager, Entity parent, Player player, Block block, Item item) {
        public static Context of(Player player, Item item) {
            return new Context(null, null, null, null, player, null, item);
        }

        public Context withOther(Entity other)   { return new Context(other, target, damager, parent, player, block, item); }
        public Context withTarget(Entity target) { return new Context(other, target, damager, parent, player, block, item); }
        public Context withDamager(Entity d)     { return new Context(other, target, d, parent, player, block, item); }
        public Context withParent(Entity p)      { return new Context(other, target, damager, p, player, block, item); }
        public Context withBlock(Block b)        { return new Context(other, target, damager, parent, player, b, item); }
    }

    // Combinators
    static EntityFilter all(EntityFilter... filters) {
        return (self, ctx) -> {
            if (filters == null || filters.length == 0) return true;
            for (EntityFilter f : filters) {
                if (f != null && !f.test(self, ctx)) return false;
            }
            return true;
        };
    }

    static EntityFilter any(EntityFilter... filters) {
        return (self, ctx) -> {
            if (filters == null || filters.length == 0) return false;
            for (EntityFilter f : filters) {
                if (f != null && f.test(self, ctx)) return true;
            }
            return false;
        };
    }

    static EntityFilter not(EntityFilter f) {
        return (self, ctx) -> f == null || !f.test(self, ctx);
    }
}
