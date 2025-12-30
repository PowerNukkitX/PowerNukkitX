package cn.nukkit.command;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.passive.EntityNpc;
import cn.nukkit.lang.CommandOutputContainer;
import cn.nukkit.lang.TextContainer;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.permission.PermissibleBase;
import cn.nukkit.permission.Permission;
import cn.nukkit.permission.PermissionAttachment;
import cn.nukkit.permission.PermissionAttachmentInfo;
import cn.nukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Represents a command sender for NPCs (non-player characters) that can execute commands in the server.
 * <p>
 * This class allows NPCs to act as command senders, providing context about the NPC and the player who initiated
 * the command. It implements the {@link CommandSender} interface and delegates permission management to a
 * {@link PermissibleBase} instance. The NPCCommandSender provides entity and location context for command execution,
 * and supports permission checks, attachments, and operator status.
 * <p>
 * Features:
 * <ul>
 *   <li>Associates the command sender with an NPC entity and the player who initiated the command.</li>
 *   <li>Delegates permission management to {@link PermissibleBase}.</li>
 *   <li>Provides entity and location context for command execution.</li>
 *   <li>Supports permission checks, attachments, and operator status.</li>
 *   <li>Returns the NPC's name as the sender name.</li>
 * </ul>
 * <p>
 * Usage:
 * <ul>
 *   <li>Instantiate with an {@link EntityNpc} and the initiating {@link Player}.</li>
 *   <li>Use {@link #getNpc()} and {@link #getInitiator()} to access the NPC and player context.</li>
 *   <li>Use {@link #getPosition()} and {@link #getLocation()} for location context.</li>
 *   <li>Use permission methods to manage and check permissions for the NPC.</li>
 *   <li>Use {@link #getName()} to get the sender name (NPC's name).</li>
 * </ul>
 * <p>
 * The NPC is not a player, so {@code isPlayer()} returns false. Operator status is always true and cannot be changed.
 *
 * @author PowerNukkitX Project Team
 * @see CommandSender
 * @since PowerNukkitX 2.0.0
 */
public class NPCCommandSender implements CommandSender {
    /**
     * Permission management base for the NPC sender.
     */
    protected PermissibleBase perm = new PermissibleBase(this);
    /**
     * The player who initiated the command (may be null).
     */
    private final Player initiator;
    /**
     * The NPC entity acting as the command sender.
     */
    private final EntityNpc npc;

    /**
     * Constructs a new NPCCommandSender with the given NPC and initiating player.
     *
     * @param npc the NPC entity acting as the sender
     * @param initiator the player who initiated the command
     */
    public NPCCommandSender(EntityNpc npc, Player initiator) {
        this.npc = npc;
        this.initiator = initiator;
    }

    /**
     * Gets the player who initiated the command.
     *
     * @return the initiating player
     */
    public Player getInitiator() {
        return initiator;
    }

    /**
     * Gets the NPC entity acting as the command sender.
     *
     * @return the NPC entity
     */
    public EntityNpc getNpc() {
        return npc;
    }

    /**
     * Sends a plain text message to the NPC sender. (No-op for NPCs)
     *
     * @param message the message to send
     */
    @Override
    public void sendMessage(String message) {}

    /**
     * Sends a formatted or translatable message to the NPC sender. (No-op for NPCs)
     *
     * @param message the TextContainer message to send
     */
    @Override
    public void sendMessage(TextContainer message) {}

    /**
     * Sends a command output container to the NPC sender. (No-op for NPCs)
     *
     * @param container the command output container to send
     */
    @Override
    public void sendCommandOutput(CommandOutputContainer container) {}

    /**
     * Gets the server instance associated with the NPC sender.
     *
     * @return the server instance
     */
    @Override
    public Server getServer() {
        return npc.getServer();
    }

    /**
     * Gets the name of the NPC sender (NPC's name).
     *
     * @return the NPC's name
     */
    @Override
    @NotNull public String getName() {
        return npc.getName();
    }

    /**
     * Checks if the sender is a player. (Always false for NPCs)
     *
     * @return false always
     */
    @Override
    public boolean isPlayer() {
        return false;
    }

    /**
     * Checks if the sender is an entity. (Always true for NPCs)
     *
     * @return true always
     */
    @Override
    public boolean isEntity() {
        return true;
    }

    /**
     * Gets the entity context for this command sender (the NPC).
     *
     * @return the NPC entity
     */
    @Override
    public @Nullable Entity asEntity() {
        return npc;
    }

    /**
     * Gets the player context for this command sender. (Always null for NPCs)
     *
     * @return null always
     */
    @Override
    public @Nullable Player asPlayer() {
        return null;
    }

    /**
     * Gets the position of the NPC in the world.
     *
     * @return the NPC's position
     */
    @Override
    @NotNull public Position getPosition() {
        return npc.getPosition();
    }

    /**
     * Gets the location of the NPC in the world.
     *
     * @return the NPC's location
     */
    @Override
    @NotNull public Location getLocation() {
        return npc.getLocation();
    }

    /**
     * Checks if the NPC has a specific permission set by name.
     *
     * @param name the permission name
     * @return true if the permission is set, false otherwise
     */
    @Override
    public boolean isPermissionSet(String name) {
        return this.perm.isPermissionSet(name);
    }

    /**
     * Checks if the NPC has a specific permission set by Permission object.
     *
     * @param permission the Permission object
     * @return true if the permission is set, false otherwise
     */
    @Override
    public boolean isPermissionSet(Permission permission) {
        return this.perm.isPermissionSet(permission);
    }

    /**
     * Checks if the NPC has a specific permission by name.
     *
     * @param name the permission name
     * @return true if the NPC has the permission, false otherwise
     */
    @Override
    public boolean hasPermission(String name) {
        return this.perm.hasPermission(name);
    }

    /**
     * Checks if the NPC has a specific permission by Permission object.
     *
     * @param permission the Permission object
     * @return true if the NPC has the permission, false otherwise
     */
    @Override
    public boolean hasPermission(Permission permission) {
        return this.perm.hasPermission(permission);
    }

    /**
     * Adds a permission attachment for the NPC sender.
     *
     * @param plugin the plugin requesting the attachment
     * @return the created PermissionAttachment
     */
    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        return this.perm.addAttachment(plugin);
    }

    /**
     * Adds a permission attachment for the NPC sender with a specific permission name.
     *
     * @param plugin the plugin requesting the attachment
     * @param name the permission name
     * @return the created PermissionAttachment
     */
    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name) {
        return this.perm.addAttachment(plugin, name);
    }

    /**
     * Adds a permission attachment for the NPC sender with a specific permission name and value.
     *
     * @param plugin the plugin requesting the attachment
     * @param name the permission name
     * @param value the value to set
     * @return the created PermissionAttachment
     */
    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, Boolean value) {
        return this.perm.addAttachment(plugin, name, value);
    }

    /**
     * Removes a permission attachment from the NPC sender.
     *
     * @param attachment the PermissionAttachment to remove
     */
    @Override
    public void removeAttachment(PermissionAttachment attachment) {
        this.perm.removeAttachment(attachment);
    }

    /**
     * Recalculates permissions for the NPC sender.
     */
    @Override
    public void recalculatePermissions() {
        this.perm.recalculatePermissions();
    }

    /**
     * Gets the effective permissions for the NPC sender.
     *
     * @return a map of permission names to PermissionAttachmentInfo
     */
    @Override
    public Map<String, PermissionAttachmentInfo> getEffectivePermissions() {
        return this.perm.getEffectivePermissions();
    }

    /**
     * Returns true because the NPC always has operator status.
     *
     * @return true always
     */
    @Override
    public boolean isOp() {
        return true;
    }

    /**
     * Does nothing because the NPC's operator status cannot be changed.
     *
     * @param value ignored
     */
    @Override
    public void setOp(boolean value) {}
}
