package cn.nukkit.entity;


import cn.nukkit.Player;

/**
 * @author Adam Matthew
 */
public interface EntityInteractable {

    // Todo: Passive entity?? i18n and boat leaving text
    String getInteractButtonText(Player player);

    boolean canDoInteraction();
}
