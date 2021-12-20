package cn.nukkit.command;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.lang.TextContainer;
import cn.nukkit.permission.*;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.utils.TextFormat;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.function.Function;

/**
 * @since 1.2.1.0-PN
 */
@PowerNukkitOnly
@AllArgsConstructor(onConstructor = @__(@PowerNukkitOnly))
public class CapturingCommandSender implements CommandSender {
    private final StringBuilder captured = new StringBuilder();

    @NonNull
    private String name;

    private boolean isOp;
    
    @NonNull
    private final Permissible perms;

    @PowerNukkitOnly
    public CapturingCommandSender() {
        this("System");
    }

    @PowerNukkitOnly
    public CapturingCommandSender(@NonNull String name) {
        this.name = name;
        this.perms = new PermissibleBase(this);
    }

    @PowerNukkitOnly
    public CapturingCommandSender(@NonNull String name, boolean isOp) {
        this.name = name;
        this.isOp = isOp;
        this.perms = new PermissibleBase(this);
    }

    @PowerNukkitOnly
    public CapturingCommandSender(@NonNull String name, boolean isOp, @NonNull Function<ServerOperator, Permissible> permissibleFactory) {
        this.name = name;
        this.isOp = isOp;
        this.perms = permissibleFactory.apply(this);
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    @PowerNukkitOnly
    public void setName(@Nonnull String name) {
        this.name = name;
    }

    @Override
    public boolean isOp() {
        return isOp;
    }

    @Override
    public void setOp(boolean op) {
        isOp = op;
    }

    @PowerNukkitOnly
    public void resetCapture() {
        captured.setLength(0);
    }

    @PowerNukkitOnly
    public synchronized String getRawCapture() {
        return captured.toString();
    }

    @PowerNukkitOnly
    public synchronized String getCleanCapture() {
        return TextFormat.clean(captured.toString());
    }

    @Override
    public String toString() {
        return "CapturingCommandSender{" +
                "name='" + name + '\'' +
                '}';
    }

    @Override
    public synchronized void sendMessage(String message) {
        captured.append(message).append('\n');
    }

    @Override
    public void sendMessage(TextContainer message) {
        sendMessage(message.toString());
    }

    @Override
    public Server getServer() {
        return Server.getInstance();
    }

    @Override
    public boolean isPlayer() {
        return false;
    }

    @Override
    public boolean isPermissionSet(String name) {
        return perms.isPermissionSet(name);
    }

    @Override
    public boolean isPermissionSet(Permission permission) {
        return perms.isPermissionSet(permission);
    }

    @Override
    public boolean hasPermission(String name) {
        return perms.hasPermission(name);
    }

    @Override
    public boolean hasPermission(Permission permission) {
        return perms.hasPermission(permission);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        return perms.addAttachment(plugin);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name) {
        return perms.addAttachment(plugin, name);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, Boolean value) {
        return perms.addAttachment(plugin, name, value);
    }

    @Override
    public void removeAttachment(PermissionAttachment attachment) {
        perms.removeAttachment(attachment);
    }

    @Override
    public void recalculatePermissions() {
        perms.recalculatePermissions();
    }

    @Override
    public Map<String, PermissionAttachmentInfo> getEffectivePermissions() {
        return perms.getEffectivePermissions();
    }
}
