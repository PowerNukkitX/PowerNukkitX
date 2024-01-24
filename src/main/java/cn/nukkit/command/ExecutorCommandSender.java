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

//used for executing commands in place of an entity


public class ExecutorCommandSender implements CommandSender {

    private final CommandSender executor;
    private final Entity entity;
    private final Location executeLocation;

    public ExecutorCommandSender(@NotNull CommandSender executor, @Nullable Entity entity, @Nullable Location executeLocation) {
        if (executor instanceof ExecutorCommandSender executorCommandSender) {
            this.executor = executorCommandSender.getExecutor();
        } else {
            this.executor = executor;
        }
        this.entity = entity;
        this.executeLocation = executeLocation;
    }

    @Override
    public void sendMessage(String message) {
        executor.sendMessage(message);
    }

    @Override
    public void sendMessage(TextContainer message) {
        executor.sendMessage(message);
    }

    public void sendCommandOutput(CommandOutputContainer container) {
        executor.sendCommandOutput(container);
    }

    @Override
    public Server getServer() {
        return executor.getServer();
    }

    @Override
    @NotNull public String getName() {
        return entity.getName();
    }

    @Override
    public boolean isPlayer() {
        return entity instanceof Player;
    }

    @Override
    public boolean isEntity() {
        return true;
    }

    @Override
    public Entity asEntity() {
        return this.entity;
    }

    @Override
    public Player asPlayer() {
        return isPlayer() ? (Player) this.entity : null;
    }

    @Override
    @NotNull public Position getPosition() {
        return (executeLocation == null ? entity : executeLocation).clone();
    }

    @Override
    @NotNull public Location getLocation() {
        return (executeLocation == null ? entity : executeLocation).clone();
    }

    @Override
    public boolean isOp() {
        return this.executor.isOp();
    }

    @Override
    public void setOp(boolean value) {
        this.executor.setOp(value);
    }

    @Override
    public boolean isPermissionSet(String name) {
        return executor.isPermissionSet(name);
    }

    @Override
    public boolean isPermissionSet(Permission permission) {
        return executor.isPermissionSet(permission);
    }

    @Override
    public boolean hasPermission(String name) {
        return executor.hasPermission(name);
    }

    @Override
    public boolean hasPermission(Permission permission) {
        return executor.hasPermission(permission);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        return executor.addAttachment(plugin);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name) {
        return executor.addAttachment(plugin, name);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, Boolean value) {
        return executor.addAttachment(plugin, name, value);
    }

    @Override
    public void removeAttachment(PermissionAttachment attachment) {
        executor.removeAttachment(attachment);
    }

    @Override
    public void recalculatePermissions() {
        executor.recalculatePermissions();
    }

    @Override
    public Map<String, PermissionAttachmentInfo> getEffectivePermissions() {
        return executor.getEffectivePermissions();
    }

    public CommandSender getExecutor() {
        if (this.executor instanceof ExecutorCommandSender executorCommandSender)
            return executorCommandSender.getExecutor();
        else return this.executor;
    }
}
