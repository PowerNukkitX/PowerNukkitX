package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.level.particle.WaxOffParticle;
import cn.nukkit.level.particle.WaxOnParticle;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author joserobjr
 * @since 2021-06-14
 */
@PowerNukkitOnly
@Since("FUTURE")
public interface Waxable {
    @PowerNukkitOnly
    @Since("FUTURE")
    @Nonnull
    Location getLocation();

    @PowerNukkitOnly
    @Since("FUTURE")
    default boolean onActivate(@Nonnull Item item, @Nullable Player player) {
        boolean waxed = isWaxed();
        if ((item.getId() != ItemID.HONEYCOMB || waxed) && (!item.isAxe() || !waxed)) {
            return false;
        }

        waxed = !waxed;
        if (!setWaxed(waxed)) {
            return false;
        }

        Position location = this instanceof Block? (Position) this : getLocation();
        if (player == null || !player.isCreative()) {
            if (waxed) {
                item.count--;
            } else {
                item.useOn(this instanceof Block? (Block) this : location.getLevelBlock());
            }
        }
        location.getValidLevel().addParticle(waxed? new WaxOnParticle(location) : new WaxOffParticle(location));
        return true;
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    boolean isWaxed();

    @PowerNukkitOnly
    @Since("FUTURE")
    boolean setWaxed(boolean waxed);
}
