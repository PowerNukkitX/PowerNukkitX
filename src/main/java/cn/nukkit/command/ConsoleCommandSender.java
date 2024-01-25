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
 * @author MagicDroidX (Nukkit Project)
 */
@Slf4j
public class ConsoleCommandSender implements CommandSender {

    private final PermissibleBase perm;

    public ConsoleCommandSender() {
        this.perm = new PermissibleBase(this);
    }

    @Override
    public boolean isPermissionSet(String name) {
        return this.perm.isPermissionSet(name);
    }

    @Override
    public boolean isPermissionSet(Permission permission) {
        return this.perm.isPermissionSet(permission);
    }

    @Override
    public boolean hasPermission(String name) {
        return this.perm.hasPermission(name);
    }

    @Override
    public boolean hasPermission(Permission permission) {
        return this.perm.hasPermission(permission);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        return this.perm.addAttachment(plugin);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name) {
        return this.perm.addAttachment(plugin, name);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, Boolean value) {
        return this.perm.addAttachment(plugin, name, value);
    }

    @Override
    public void removeAttachment(PermissionAttachment attachment) {
        this.perm.removeAttachment(attachment);
    }

    @Override
    public void recalculatePermissions() {
        this.perm.recalculatePermissions();
    }

    @Override
    public Map<String, PermissionAttachmentInfo> getEffectivePermissions() {
        return this.perm.getEffectivePermissions();
    }

    @Override
    public boolean isPlayer() {
        return false;
    }

    @Override
    public Server getServer() {
        return Server.getInstance();
    }

    @Override
    public void sendMessage(String message) {
        for (String line : message.trim().split("\n")) {
            log.info(line);
        }
    }

    @Override
    public void sendMessage(TextContainer message) {
        this.sendMessage(this.getServer().getLanguage().tr(message));
    }

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

    @Override
    @NotNull public String getName() {
        return "CONSOLE";
    }

    @Override
    public boolean isOp() {
        return true;
    }

    @Override
    public void setOp(boolean value) {

    }
}
