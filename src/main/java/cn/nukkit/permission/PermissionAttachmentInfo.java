package cn.nukkit.permission;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class PermissionAttachmentInfo {

    private Permissible permissible;

    private String permission;

    private PermissionAttachment attachment;

    private boolean value;
    /**
     * @deprecated 
     */
    

    public PermissionAttachmentInfo(Permissible permissible, String permission, PermissionAttachment attachment, boolean value) {
        if (permission == null) {
            throw new IllegalStateException("Permission may not be null");
        }

        this.permissible = permissible;
        this.permission = permission;
        this.attachment = attachment;
        this.value = value;
    }

    public Permissible getPermissible() {
        return permissible;
    }
    /**
     * @deprecated 
     */
    

    public String getPermission() {
        return permission;
    }

    public PermissionAttachment getAttachment() {
        return attachment;
    }
    /**
     * @deprecated 
     */
    

    public boolean getValue() {
        return value;
    }
}
