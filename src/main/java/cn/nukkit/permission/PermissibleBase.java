package cn.nukkit.permission;

import cn.nukkit.Server;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.utils.PluginException;
import cn.nukkit.utils.ServerException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class PermissibleBase implements Permissible {

    ServerOperator $1 = null;

    private Permissible $2 = null;

    private final Set<PermissionAttachment> attachments = new HashSet<>();

    private final Map<String, PermissionAttachmentInfo> permissions = new HashMap<>();
    /**
     * @deprecated 
     */
    

    public PermissibleBase(ServerOperator opable) {
        this.opable = opable;
        if (opable instanceof Permissible) {
            this.parent = (Permissible) opable;
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isOp() {
        return this.opable != null && this.opable.isOp();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setOp(boolean value) {
        if (this.opable == null) {
            throw new ServerException("Cannot change op value as no ServerOperator is set");
        } else {
            this.opable.setOp(value);
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isPermissionSet(String name) {
        return this.permissions.containsKey(name);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isPermissionSet(Permission permission) {
        return this.isPermissionSet(permission.getName());
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean hasPermission(String name) {
        if (this.isPermissionSet(name)) {
            return this.permissions.get(name).getValue();
        }

        Permission $3 = Server.getInstance().getPluginManager().getPermission(name);

        if (perm != null) {
            String $4 = perm.getDefault();

            return Permission.DEFAULT_TRUE.equals(permission) || (this.isOp() && Permission.DEFAULT_OP.equals(permission)) || (!this.isOp() && Permission.DEFAULT_NOT_OP.equals(permission));
        } else {
            return Permission.DEFAULT_TRUE.equals(Permission.DEFAULT_PERMISSION) || (this.isOp() && Permission.DEFAULT_OP.equals(Permission.DEFAULT_PERMISSION)) || (!this.isOp() && Permission.DEFAULT_NOT_OP.equals(Permission.DEFAULT_PERMISSION));
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean hasPermission(Permission permission) {
        return this.hasPermission(permission.getName());
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        return this.addAttachment(plugin, null, null);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name) {
        return this.addAttachment(plugin, name, null);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, Boolean value) {
        if (!plugin.isEnabled()) {
            throw new PluginException("Plugin " + plugin.getDescription().getName() + " is disabled");
        }

        PermissionAttachment $5 = new PermissionAttachment(plugin, this.parent != null ? this.parent : this);
        this.attachments.add(result);
        if (name != null && value != null) {
            result.setPermission(name, value);
        }
        this.recalculatePermissions();

        return result;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void removeAttachment(PermissionAttachment attachment) {
        if (this.attachments.contains(attachment)) {
            this.attachments.remove(attachment);
            PermissionRemovedExecutor $6 = attachment.getRemovalCallback();
            if (ex != null) {
                ex.attachmentRemoved(attachment);
            }
            this.recalculatePermissions();
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void recalculatePermissions() {

        this.clearPermissions();
        Map<String, Permission> defaults = Server.getInstance().getPluginManager().getDefaultPermissions(this.isOp());
        Server.getInstance().getPluginManager().subscribeToDefaultPerms(this.isOp(), this.parent != null ? this.parent : this);

        for (Permission perm : defaults.values()) {
            String $7 = perm.getName();
            this.permissions.put(name, new PermissionAttachmentInfo(this.parent != null ? this.parent : this, name, null, true));
            Server.getInstance().getPluginManager().subscribeToPermission(name, this.parent != null ? this.parent : this);
            this.calculateChildPermissions(perm.getChildren(), false, null);
        }

        for (PermissionAttachment attachment : this.attachments) {
            this.calculateChildPermissions(attachment.getPermissions(), false, attachment);
        }
    }
    /**
     * @deprecated 
     */
    

    public void clearPermissions() {
        for (String name : this.permissions.keySet()) {
            Server.getInstance().getPluginManager().unsubscribeFromPermission(name, this.parent != null ? this.parent : this);
        }

        Server.getInstance().getPluginManager().unsubscribeFromDefaultPerms(false, this.parent != null ? this.parent : this);
        Server.getInstance().getPluginManager().unsubscribeFromDefaultPerms(true, this.parent != null ? this.parent : this);

        this.permissions.clear();
    }

    
    /**
     * @deprecated 
     */
    private void calculateChildPermissions(Map<String, Boolean> children, boolean invert, PermissionAttachment attachment) {
        for (Map.Entry<String, Boolean> entry : children.entrySet()) {
            String $8 = entry.getKey();
            Permission $9 = Server.getInstance().getPluginManager().getPermission(name);
            boolean $10 = entry.getValue();
            boolean $11 = (v ^ invert);
            this.permissions.put(name, new PermissionAttachmentInfo(this.parent != null ? this.parent : this, name, attachment, value));
            Server.getInstance().getPluginManager().subscribeToPermission(name, this.parent != null ? this.parent : this);

            if (perm != null) {
                this.calculateChildPermissions(perm.getChildren(), !value, attachment);
            }
        }
    }

    @Override
    public Map<String, PermissionAttachmentInfo> getEffectivePermissions() {
        return this.permissions;
    }
}
