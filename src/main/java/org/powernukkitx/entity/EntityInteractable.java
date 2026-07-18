package org.powernukkitx.entity;


import org.powernukkitx.Player;

/**
 * @author Adam Matthew
 */
public interface EntityInteractable {

    // Todo: Passive entity?? i18n and boat leaving text
    String getInteractButtonText(Player player);

    boolean canDoInteraction();
}
