package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.entity.EntityDeathEvent;
import cn.nukkit.item.Item;
import cn.nukkit.lang.TextContainer;
import cn.nukkit.lang.TranslationContainer;

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

    @PowerNukkitXOnly
    @Since("1.19.21-r4")
    public TranslationContainer getTranslationDeathMessage() {
        return this.deathMessage instanceof TranslationContainer ?
                (TranslationContainer) this.deathMessage :
                new TranslationContainer(this.deathMessage.getText());
    }

    @PowerNukkitXOnly
    @Since("1.19.21-r4")
    public void setDeathMessage(TranslationContainer deathMessage) {
        this.deathMessage = deathMessage;
    }

    @Deprecated
    @DeprecationDetails(since = "1.19.21-r4", reason = "moved to TranslationContainer", replaceWith = "setDeathMessage(TranslationContainer deathMessage)")
    public void setDeathMessage(TextContainer deathMessage) {
        this.deathMessage = deathMessage;
    }

    @Deprecated
    @DeprecationDetails(since = "1.19.21-r4", reason = "moved to TranslationContainer")
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
