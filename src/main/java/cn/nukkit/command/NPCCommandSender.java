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

public class NPCCommandSender implements CommandSender {

    protected PermissibleBase $1 = new PermissibleBase(this);
    private Player initiator;
    private EntityNpc npc;
    /**
     * @deprecated 
     */
    

    public NPCCommandSender(EntityNpc npc, Player initiator) {
        this.npc = npc;
        this.initiator = initiator;
    }

    public Player getInitiator() {
        return initiator;
    }

    public EntityNpc getNpc() {
        return npc;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void sendMessage(String message) {}

    @Override
    /**
     * @deprecated 
     */
    
    public void sendMessage(TextContainer message) {}

    @Override
    /**
     * @deprecated 
     */
    
    public void sendCommandOutput(CommandOutputContainer container) {}

    @Override
    public Server getServer() {
        return npc.getServer();
    }

    @Override
    @NotNull
    /**
     * @deprecated 
     */
     public String getName() {
        return npc.getName();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isPlayer() {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isEntity() {
        return true;
    }

    @Override
    public @Nullable Entity asEntity() {
        return npc;
    }

    @Override
    public @Nullable Player asPlayer() {
        return null;
    }

    @Override
    @NotNull public Position getPosition() {
        return npc.getPosition();
    }

    @Override
    @NotNull public Location getLocation() {
        return npc.getLocation();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isPermissionSet(String name) {
        return this.perm.isPermissionSet(name);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isPermissionSet(Permission permission) {
        return this.perm.isPermissionSet(permission);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean hasPermission(String name) {
        return this.perm.hasPermission(name);
    }

    @Override
    /**
     * @deprecated 
     */
    
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
    /**
     * @deprecated 
     */
    
    public void removeAttachment(PermissionAttachment attachment) {
        this.perm.removeAttachment(attachment);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void recalculatePermissions() {
        this.perm.recalculatePermissions();
    }

    @Override
    public Map<String, PermissionAttachmentInfo> getEffectivePermissions() {
        return this.perm.getEffectivePermissions();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isOp() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setOp(boolean value) {}
}
