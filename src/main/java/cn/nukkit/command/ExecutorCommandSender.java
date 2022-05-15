package cn.nukkit.command;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockentity.ICommandBlock;
import cn.nukkit.entity.Entity;
import cn.nukkit.lang.TextContainer;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.permission.Permission;
import cn.nukkit.permission.PermissionAttachment;
import cn.nukkit.permission.PermissionAttachmentInfo;
import cn.nukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import java.util.Map;

//used for executing commands in place of an entity
@PowerNukkitOnly
@Since("1.6.0.0-PNX")
public class ExecutorCommandSender implements CommandSender {

    private CommandSender executor;
    private Entity entity;
    private Location executeLocation;

    public ExecutorCommandSender(CommandSender executor, Entity entity, Location executeLocation) {
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
        if (this.executor instanceof ICommandBlock && !executor.getPosition().level.getGameRules().getBoolean(GameRule.COMMAND_BLOCK_OUTPUT))
            return;
        if(entity instanceof Player){
            ((Player) entity).sendMessage(message);
        }
    }

    @Override
    public void sendMessage(TextContainer message) {
        if (this.executor instanceof ICommandBlock && !executor.getPosition().level.getGameRules().getBoolean(GameRule.COMMAND_BLOCK_OUTPUT))
            return;
        if(entity instanceof Player){
            ((Player) entity).sendMessage(message);
        }
    }

    @Override
    public Server getServer() {
        return entity.getServer();
    }

    @Nonnull
    @Override
    public String getName() {
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
    public Position getPosition() {
        return (executeLocation == null ? entity : executeLocation).clone();
    }

    @Since("1.6.0.0-PNX")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public Location getLocation() {
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
        return executor;
    }
}
