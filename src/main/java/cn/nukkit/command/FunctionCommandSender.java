package cn.nukkit.command;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.lang.TextContainer;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.permission.Permission;
import cn.nukkit.permission.PermissionAttachment;
import cn.nukkit.permission.PermissionAttachmentInfo;
import cn.nukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import java.util.Map;

public class FunctionCommandSender implements CommandSender {

    private CommandSender sender;

    public FunctionCommandSender(CommandSender sender) {
        if (sender instanceof FunctionCommandSender functionCommandSender) {
            this.sender = functionCommandSender.getSender();
        } else {
            this.sender = sender;
        }
    }

    @Override
    public void sendMessage(String message) {
    }

    @Override
    public void sendMessage(TextContainer message) {
    }

    @Override
    public Server getServer() {
        return sender.getServer();
    }

    @Nonnull
    @Override
    public String getName() {
        return sender.getName();
    }

    @Override
    public boolean isPlayer() {
        return sender.isPlayer();
    }

    @Override
    public boolean isEntity() {
        return sender.isEntity();
    }

    @Override
    public Entity asEntity() {
        return sender.asEntity();
    }

    @Override
    public Player asPlayer() {
        return sender.asPlayer();
    }

    @Override
    public Position getPosition() {
        return sender.getPosition();
    }

    @Since("1.6.0.0-PNX")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public Location getLocation() {
        return sender.getLocation();
    }

    @Override
    public boolean isOp() {
        return this.sender.isOp();
    }

    @Override
    public void setOp(boolean value) {
        this.sender.setOp(value);
    }

    @Override
    public boolean isPermissionSet(String name) {
        return sender.isPermissionSet(name);
    }

    @Override
    public boolean isPermissionSet(Permission permission) {
        return sender.isPermissionSet(permission);
    }

    @Override
    public boolean hasPermission(String name) {
        return sender.hasPermission(name);
    }

    @Override
    public boolean hasPermission(Permission permission) {
        return sender.hasPermission(permission);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        return sender.addAttachment(plugin);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name) {
        return sender.addAttachment(plugin, name);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, Boolean value) {
        return sender.addAttachment(plugin, name, value);
    }

    @Override
    public void removeAttachment(PermissionAttachment attachment) {
        sender.removeAttachment(attachment);
    }

    @Override
    public void recalculatePermissions() {
        sender.recalculatePermissions();
    }

    @Override
    public Map<String, PermissionAttachmentInfo> getEffectivePermissions() {
        return sender.getEffectivePermissions();
    }

    public CommandSender getSender() {
        return sender;
    }
}
