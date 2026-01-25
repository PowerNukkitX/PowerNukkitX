package cn.nukkit.command;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.lang.CommandOutputContainer;
import cn.nukkit.lang.TextContainer;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.permission.Permission;
import cn.nukkit.permission.PermissionAttachment;
import cn.nukkit.permission.PermissionAttachmentInfo;
import cn.nukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Represents a command sender that executes commands on behalf of an entity at a specific location.
 * <p>
 * This class is used to allow entities (such as mobs, NPCs, or other non-player entities) to execute commands
 * as if they were a player or another command sender, optionally at a custom location. It wraps another
 * {@link CommandSender} (the executor), and delegates most operations to it, while providing entity and location context.
 * <p>
 * Features:
 * <ul>
 *   <li>Delegates all permission and messaging operations to the underlying executor.</li>
 *   <li>Provides entity and location context for command execution.</li>
 *   <li>Supports both player and non-player entities.</li>
 *   <li>Allows chaining of ExecutorCommandSender for nested delegation.</li>
 * </ul>
 * <p>
 * Usage:
 * <ul>
 *   <li>Instantiate with an executor, entity, and optional location.</li>
 *   <li>Use {@link #getExecutor()} to retrieve the original command sender.</li>
 *   <li>Use {@link #asEntity()} and {@link #asPlayer()} to access the entity or player context.</li>
 *   <li>Use {@link #getPosition()} and {@link #getLocation()} for location context.</li>
 * </ul>
 * <p>
 * If the executor is itself an ExecutorCommandSender, {@link #getExecutor()} will recursively return the base executor.
 *
 * @author smartcmd (PowerNukkitX Project)
 * @see CommandSender
 * @since PowerNukkitX 2.0.0
 */
public class ExecutorCommandSender implements CommandSender {
    /**
     * The underlying command sender that executes the command.
     */
    private final CommandSender executor;
    /**
     * The entity on whose behalf the command is executed (may be null).
     */
    private final Entity entity;
    /**
     * The location at which the command is executed (may be null).
     */
    private final Location executeLocation;

    /**
     * Constructs a new ExecutorCommandSender.
     * <p>
     * If the executor is already an ExecutorCommandSender, unwraps to the base executor.
     *
     * @param executor the underlying command sender
     * @param entity the entity executing the command (may be null)
     * @param executeLocation the location for command execution (may be null)
     */
    public ExecutorCommandSender(@NotNull CommandSender executor, @Nullable Entity entity, @Nullable Location executeLocation) {
        if (executor instanceof ExecutorCommandSender executorCommandSender) {
            this.executor = executorCommandSender.getExecutor();
        } else {
            this.executor = executor;
        }
        this.entity = entity;
        this.executeLocation = executeLocation;
    }

    /**
     * Sends a plain text message to the executor.
     *
     * @param message the message to send
     */
    @Override
    public void sendMessage(String message) {
        executor.sendMessage(message);
    }

    /**
     * Sends a formatted or translatable message to the executor.
     *
     * @param message the TextContainer message to send
     */
    @Override
    public void sendMessage(TextContainer message) {
        executor.sendMessage(message);
    }

    /**
     * Sends a command output container to the executor.
     *
     * @param container the command output container to send
     */
    public void sendCommandOutput(CommandOutputContainer container) {
        executor.sendCommandOutput(container);
    }

    /**
     * Gets the server instance associated with the executor.
     *
     * @return the server instance
     */
    @Override
    public Server getServer() {
        return executor.getServer();
    }

    /**
     * Gets the name of the entity executing the command.
     *
     * @return the entity's name
     */
    @Override
    @NotNull public String getName() {
        return entity.getName();
    }

    /**
     * Checks if the entity is a player.
     *
     * @return true if the entity is a player, false otherwise
     */
    @Override
    public boolean isPlayer() {
        return entity instanceof Player;
    }

    /**
     * Checks if the sender is an entity (always true for ExecutorCommandSender).
     *
     * @return true always
     */
    @Override
    public boolean isEntity() {
        return true;
    }

    /**
     * Gets the entity context for this command sender.
     *
     * @return the entity, or null if not set
     */
    @Override
    public Entity asEntity() {
        return this.entity;
    }

    /**
     * Gets the player context for this command sender, if the entity is a player.
     *
     * @return the player, or null if not a player
     */
    @Override
    public Player asPlayer() {
        return isPlayer() ? (Player) this.entity : null;
    }

    /**
     * Gets the position for command execution.
     * <p>
     * Returns the executeLocation if set, otherwise the entity's position.
     *
     * @return the position for command execution
     */
    @Override
    @NotNull public Position getPosition() {
        return (executeLocation == null ? entity : executeLocation).clone();
    }

    /**
     * Gets the location for command execution.
     * <p>
     * Returns the executeLocation if set, otherwise the entity's location.
     *
     * @return the location for command execution
     */
    @Override
    @NotNull public Location getLocation() {
        return (executeLocation == null ? entity : executeLocation).clone();
    }

    /**
     * Checks if the executor has operator status.
     *
     * @return true if the executor is op, false otherwise
     */
    @Override
    public boolean isOp() {
        return this.executor.isOp();
    }

    /**
     * Sets the operator status for the executor.
     *
     * @param value the operator status to set
     */
    @Override
    public void setOp(boolean value) {
        this.executor.setOp(value);
    }

    /**
     * Checks if the executor has a specific permission set by name.
     *
     * @param name the permission name
     * @return true if the permission is set, false otherwise
     */
    @Override
    public boolean isPermissionSet(String name) {
        return executor.isPermissionSet(name);
    }

    /**
     * Checks if the executor has a specific permission set by Permission object.
     *
     * @param permission the Permission object
     * @return true if the permission is set, false otherwise
     */
    @Override
    public boolean isPermissionSet(Permission permission) {
        return executor.isPermissionSet(permission);
    }

    /**
     * Checks if the executor has a specific permission by name.
     *
     * @param name the permission name
     * @return true if the executor has the permission, false otherwise
     */
    @Override
    public boolean hasPermission(String name) {
        return executor.hasPermission(name);
    }

    /**
     * Checks if the executor has a specific permission by Permission object.
     *
     * @param permission the Permission object
     * @return true if the executor has the permission, false otherwise
     */
    @Override
    public boolean hasPermission(Permission permission) {
        return executor.hasPermission(permission);
    }

    /**
     * Adds a permission attachment for the executor.
     *
     * @param plugin the plugin requesting the attachment
     * @return the created PermissionAttachment
     */
    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        return executor.addAttachment(plugin);
    }

    /**
     * Adds a permission attachment for the executor with a specific permission name.
     *
     * @param plugin the plugin requesting the attachment
     * @param name the permission name
     * @return the created PermissionAttachment
     */
    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name) {
        return executor.addAttachment(plugin, name);
    }

    /**
     * Adds a permission attachment for the executor with a specific permission name and value.
     *
     * @param plugin the plugin requesting the attachment
     * @param name the permission name
     * @param value the value to set
     * @return the created PermissionAttachment
     */
    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, Boolean value) {
        return executor.addAttachment(plugin, name, value);
    }

    /**
     * Removes a permission attachment from the executor.
     *
     * @param attachment the PermissionAttachment to remove
     */
    @Override
    public void removeAttachment(PermissionAttachment attachment) {
        executor.removeAttachment(attachment);
    }

    /**
     * Recalculates permissions for the executor.
     */
    @Override
    public void recalculatePermissions() {
        executor.recalculatePermissions();
    }

    /**
     * Gets the effective permissions for the executor.
     *
     * @return a map of permission names to PermissionAttachmentInfo
     */
    @Override
    public Map<String, PermissionAttachmentInfo> getEffectivePermissions() {
        return executor.getEffectivePermissions();
    }

    /**
     * Gets the base executor, unwrapping nested ExecutorCommandSender if necessary.
     *
     * @return the base CommandSender executor
     */
    public CommandSender getExecutor() {
        if (this.executor instanceof ExecutorCommandSender executorCommandSender)
            return executorCommandSender.getExecutor();
        else return this.executor;
    }
}
