package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.value.OxidizationLevel;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.level.particle.WaxOffParticle;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author joserobjr
 * @since 2021-06-14
 */
@PowerNukkitOnly
@Since("FUTURE")
public interface Oxidizable {
    @PowerNukkitOnly
    @Since("FUTURE")
    @Nonnull
    Location getLocation();

    @PowerNukkitOnly
    @Since("FUTURE")
    default boolean onActivate(@Nonnull Item item, @Nullable Player player) {
        if (!item.isAxe()) {
            return false;
        }

        OxidizationLevel oxidizationLevel = getOxidizationLevel();
        if (OxidizationLevel.UNAFFECTED.equals(oxidizationLevel)) {
            return false;
        }

        oxidizationLevel = OxidizationLevel.values()[oxidizationLevel.ordinal() - 1];
        if (!setOxidizationLevel(oxidizationLevel)) {
            return false;
        }

        Position location = this instanceof Block? (Position) this : getLocation();
        if (player == null || !player.isCreative()) {
            item.useOn(this instanceof Block? (Block) this : location.getLevelBlock());
        }
        location.getValidLevel().addParticle(new WaxOffParticle(location));
        return true;
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    @Nonnull
    OxidizationLevel getOxidizationLevel();

    @PowerNukkitOnly
    @Since("FUTURE")
    boolean setOxidizationLevel(@Nonnull OxidizationLevel oxidizationLevel);
}
