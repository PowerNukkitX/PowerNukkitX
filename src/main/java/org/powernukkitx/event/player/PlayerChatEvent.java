package org.powernukkitx.event.player;

import org.powernukkitx.Player;
import org.powernukkitx.Server;
import org.powernukkitx.command.CommandSender;
import org.powernukkitx.event.Cancellable;
import org.powernukkitx.event.HandlerList;
import org.powernukkitx.permission.Permissible;

import java.util.HashSet;
import java.util.Set;

public class PlayerChatEvent extends PlayerMessageEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    protected Set<CommandSender> recipients = new HashSet<>();
    protected String format;

    public PlayerChatEvent(Player player, String message) {
        this(player, message, "chat.type.text", null);
    }

    public PlayerChatEvent(Player player, String message, String format, Set<CommandSender> recipients) {
        this.player = player;
        this.message = message;

        this.format = format;

        if (recipients == null) {
            for (Permissible permissible : Server.getInstance().getPluginManager().getPermissionSubscriptions(Server.BROADCAST_CHANNEL_USERS)) {
                if (permissible instanceof CommandSender) {
                    this.recipients.add((CommandSender) permissible);
                }
            }

        } else {
            this.recipients = recipients;
        }
    }

    /**
     * Changes the player that is sending the message
     *
     * @param player messenger
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getFormat() {
        return this.format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Set<CommandSender> getRecipients() {
        return this.recipients;
    }

    public void setRecipients(Set<CommandSender> recipients) {
        this.recipients = recipients;
    }
}
