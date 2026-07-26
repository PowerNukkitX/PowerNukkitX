package org.powernukkitx.event.player;

import org.powernukkitx.Player;
import org.powernukkitx.event.Cancellable;
import org.powernukkitx.event.HandlerList;
import org.powernukkitx.event.entity.EntityDeathEvent;
import org.powernukkitx.item.Item;
import org.powernukkitx.lang.TextContainer;
import org.powernukkitx.lang.TranslationContainer;

public class PlayerDeathEvent extends EntityDeathEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private TextContainer deathMessage;
    private boolean keepInventory = false;
    private boolean keepExperience = false;
    private int experience;

    public PlayerDeathEvent(Player player, Item[] drops, TextContainer deathMessage, int experience) {
        super(player, drops);
        this.deathMessage = deathMessage;
        this.experience = experience;
    }

    public PlayerDeathEvent(Player player, Item[] drops, String deathMessage, int experience) {
        this(player, drops, new TextContainer(deathMessage), experience);
    }

    @Override
    public Player getEntity() {
        return (Player) super.getEntity();
    }

    public TextContainer getDeathMessage() {
        return deathMessage;
    }

    public TranslationContainer getTranslationDeathMessage() {
        return this.deathMessage instanceof TranslationContainer ?
                (TranslationContainer) this.deathMessage :
                new TranslationContainer(this.deathMessage.getText());
    }

    public void setDeathMessage(TranslationContainer deathMessage) {
        this.deathMessage = deathMessage;
    }

    public void setDeathMessage(TextContainer deathMessage) {
        this.deathMessage = deathMessage;
    }

    public void setDeathMessage(String deathMessage) {
        this.deathMessage = new TextContainer(deathMessage);
    }

    public boolean getKeepInventory() {
        return keepInventory;
    }

    public void setKeepInventory(boolean keepInventory) {
        this.keepInventory = keepInventory;
    }

    public boolean getKeepExperience() {
        return keepExperience;
    }

    public void setKeepExperience(boolean keepExperience) {
        this.keepExperience = keepExperience;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }
}
