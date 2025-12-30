package cn.nukkit.command;

import cn.nukkit.Server;
import cn.nukkit.event.server.ConsoleCommandOutputEvent;
import cn.nukkit.lang.CommandOutputContainer;
import cn.nukkit.lang.TextContainer;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.GameRule;
import cn.nukkit.permission.PermissibleBase;
import cn.nukkit.permission.Permission;
import cn.nukkit.permission.PermissionAttachment;
import cn.nukkit.permission.PermissionAttachmentInfo;
import cn.nukkit.plugin.Plugin;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Represents the console as a command sender in the server.
 * <p>
 * This class provides methods for sending messages, handling permissions, and processing command output
 * specifically for the server console. It implements the {@link CommandSender} interface and is used whenever
 * commands are executed from the server console (not by players or entities).
 * <p>
 * Features:
 * <ul>
 *   <li>Always has operator permissions ({@code isOp()} returns true).</li>
 *   <li>Supports permission management via {@link PermissibleBase}.</li>
 *   <li>Logs messages to the server log using SLF4J.</li>
 *   <li>Handles command output events and respects game rules for command feedback.</li>
 *   <li>Returns "CONSOLE" as its name.</li>
 * </ul>
 * <p>
 * Usage:
 * <ul>
 *   <li>Use {@link #sendMessage(String)} or {@link #sendMessage(TextContainer)} to send messages to the console.</li>
 *   <li>Use {@link #sendCommandOutput(CommandOutputContainer)} to send advanced command output.</li>
 *   <li>Use permission methods to manage and check permissions for the console.</li>
 *   <li>Use {@link #getName()} to get the sender name (always "CONSOLE").</li>
 * </ul>
 * <p>
 * The console is not a player or entity, so {@code isPlayer()} returns false and op status cannot be changed.
 *
 * @author MagicDroidX (Nukkit Project)
 * @see CommandSender
 * @since Nukkit 1.0 | Nukkit API 1.0.0
 */
@Slf4j
public class ConsoleCommandSender implements CommandSender {
    /**
     * Permission management base for the console sender.
     */
    private final PermissibleBase perm;

    /**
     * Constructs a new ConsoleCommandSender instance.
     * Initializes the permission base for the console.
     */
    public ConsoleCommandSender() {
        this.perm = new PermissibleBase(this);
    }

    /**
     * Checks if the console has a specific permission set by name.
     *
     * @param name the permission name
     * @return true if the permission is set, false otherwise
     */
    @Override
    public boolean isPermissionSet(String name) {
        return this.perm.isPermissionSet(name);
    }

    /**
     * Checks if the console has a specific permission set by Permission object.
     *
     * @param permission the Permission object
     * @return true if the permission is set, false otherwise
     */
    @Override
    public boolean isPermissionSet(Permission permission) {
        return this.perm.isPermissionSet(permission);
    }

    /**
     * Checks if the console has a specific permission by name.
     *
     * @param name the permission name
     * @return true if the console has the permission, false otherwise
     */
    @Override
    public boolean hasPermission(String name) {
        return this.perm.hasPermission(name);
    }

    /**
     * Checks if the console has a specific permission by Permission object.
     *
     * @param permission the Permission object
     * @return true if the console has the permission, false otherwise
     */
    @Override
    public boolean hasPermission(Permission permission) {
        return this.perm.hasPermission(permission);
    }

    /**
     * Adds a permission attachment for the console sender.
     *
     * @param plugin the plugin requesting the attachment
     * @return the created PermissionAttachment
     */
    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        return this.perm.addAttachment(plugin);
    }

    /**
     * Adds a permission attachment for the console sender with a specific permission name.
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
     * Adds a permission attachment for the console sender with a specific permission name and value.
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
     * Removes a permission attachment from the console sender.
     *
     * @param attachment the PermissionAttachment to remove
     */
    @Override
    public void removeAttachment(PermissionAttachment attachment) {
        this.perm.removeAttachment(attachment);
    }

    /**
     * Recalculates permissions for the console sender.
     */
    @Override
    public void recalculatePermissions() {
        this.perm.recalculatePermissions();
    }

    /**
     * Gets the effective permissions for the console sender.
     *
     * @return a map of permission names to PermissionAttachmentInfo
     */
    @Override
    public Map<String, PermissionAttachmentInfo> getEffectivePermissions() {
        return this.perm.getEffectivePermissions();
    }

    /**
     * Returns false because the console is not a player.
     *
     * @return false always
     */
    @Override
    public boolean isPlayer() {
        return false;
    }

    /**
     * Gets the server instance associated with the console sender.
     *
     * @return the server instance
     */
    @Override
    public Server getServer() {
        return Server.getInstance();
    }

    /**
     * Sends a plain text message to the console, logging each line to the server log.
     *
     * @param message the message to send
     */
    @Override
    public void sendMessage(String message) {
        for (String line : message.trim().split("\n")) {
            log.info(line);
        }
    }

    /**
     * Sends a formatted or translatable message to the console.
     *
     * @param message the TextContainer message to send
     */
    @Override
    public void sendMessage(TextContainer message) {
        this.sendMessage(this.getServer().getLanguage().tr(message));
    }

    /**
     * Sends a command output container to the console, handling command feedback and events.
     * Only sends output if the SEND_COMMAND_FEEDBACK game rule is enabled.
     *
     * @param container the command output container to send
     */
    @Override
    public void sendCommandOutput(CommandOutputContainer container) {
        if (this.getLocation().getLevel().getGameRules().getBoolean(GameRule.SEND_COMMAND_FEEDBACK)) {
            for (var msg : container.getMessages()) {
                var text = this.getServer().getLanguage().tr(new TranslationContainer(msg.getMessageId(), msg.getParameters()));
                ConsoleCommandOutputEvent event = new ConsoleCommandOutputEvent(this, text);
                this.getServer().getPluginManager().callEvent(event);
                if (event.isCancelled()) continue;
                text = event.getMessage();
                this.sendMessage(text);
            }
        }
    }

    /**
     * Gets the name of the console sender (always "CONSOLE").
     *
     * @return the string "CONSOLE"
     */
    @Override
    @NotNull public String getName() {
        return "CONSOLE";
    }

    /**
     * Returns true because the console always has operator status.
     *
     * @return true always
     */
    @Override
    public boolean isOp() {
        return true;
    }

    /**
     * Does nothing because the console's operator status cannot be changed.
     *
     * @param value ignored
     */
    @Override
    public void setOp(boolean value) {
        // No-op: Console op status cannot be changed
    }
}
