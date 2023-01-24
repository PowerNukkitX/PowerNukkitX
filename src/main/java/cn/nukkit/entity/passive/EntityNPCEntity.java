package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.NPCCommandSender;
import cn.nukkit.dialog.element.ElementDialogButton;
import cn.nukkit.dialog.window.FormWindowDialog;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityInteractable;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.data.IntEntityData;
import cn.nukkit.entity.data.StringEntityData;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.NPCRequestPacket;

/**
 * @author good777LUCKY
 */
@Since("1.4.0.0-PN")
@PowerNukkitOnly
public class EntityNPCEntity extends EntityLiving implements EntityNPC, EntityInteractable {
    //todo: Implement automatic steering of NPC entities

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    public static final int NETWORK_ID = 51;

    public static final String KEY_DIALOG_TITLE = "DialogTitle";
    public static final String KEY_DIALOG_CONTENT = "DialogContent";
    public static final String KEY_DIALOG_SKINDATA = "DialogSkinData";
    public static final String KEY_DIALOG_BUTTONS = "DialogButtons";

    protected FormWindowDialog dialog;

    protected int variant = 0;

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    public EntityNPCEntity(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.6f;
    }

    @Override
    public float getHeight() {
        return 2.1f;
    }

    @Override
    public boolean canDoInteraction() {
        return true;
    }

    @Override
    public String getInteractButtonText(Player player) {
        return player.isCreative() ? "action.interact.edit" : "action.interact.talk";
    }

    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    @Override
    public String getOriginalName() {
        return "NPC";
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    @Override
    public void initEntity() {
        super.initEntity();
        this.setMaxHealth(Integer.MAX_VALUE); // Should be Float max value
        this.setHealth(20);
        this.setNameTagVisible(true);
        this.setNameTagAlwaysVisible(true);
        this.setVariant(this.namedTag.getInt("Variant"));
        this.dialog = new FormWindowDialog(this.namedTag.getString(KEY_DIALOG_TITLE).isEmpty() ? "NPC" : this.namedTag.getString(KEY_DIALOG_TITLE), this.namedTag.getString(KEY_DIALOG_CONTENT), this);
        this.setNameTag(this.dialog.getTitle());
        if (!this.namedTag.getString(KEY_DIALOG_SKINDATA).isEmpty())
            this.dialog.setSkinData(this.namedTag.getString(KEY_DIALOG_SKINDATA));
        if (!this.namedTag.getString(KEY_DIALOG_BUTTONS).isEmpty())
            this.dialog.setButtonJSONData(this.namedTag.getString(KEY_DIALOG_BUTTONS));
        this.dialog.addHandler((player, response) -> {
            if (response.getRequestType() == NPCRequestPacket.RequestType.SET_ACTIONS) {
                if (!response.getData().isEmpty()) {
                    this.dialog.setButtonJSONData(response.getData());
                    this.setDataProperty(new StringEntityData(Entity.DATA_NPC_ACTIONS, response.getData()));
                }
            }
            if (response.getRequestType() == NPCRequestPacket.RequestType.SET_INTERACTION_TEXT) {
                this.dialog.setContent(response.getData());
                this.setDataProperty(new StringEntityData(Entity.DATA_INTERACTIVE_TAG, response.getData()));
            }
            if (response.getRequestType() == NPCRequestPacket.RequestType.SET_NAME) {
                this.dialog.setTitle(response.getData());
                this.setNameTag(response.getData());
            }
            if (response.getRequestType() == NPCRequestPacket.RequestType.SET_SKIN) {
                this.setVariant(response.getSkinType());
            }
            if (response.getRequestType() == NPCRequestPacket.RequestType.EXECUTE_ACTION) {
                ElementDialogButton clickedButton = response.getClickedButton();
                for (ElementDialogButton.CmdLine line : clickedButton.getData()) {
                    Server.getInstance().executeCommand(new NPCCommandSender(this, player), line.cmd_line);
                }
            }
            if (response.getRequestType() == NPCRequestPacket.RequestType.EXECUTE_OPENING_COMMANDS) {
                for (ElementDialogButton button : this.dialog.getButtons()) {
                    if (button.getMode() == ElementDialogButton.Mode.ON_ENTER) {
                        for (ElementDialogButton.CmdLine line : button.getData()) {
                            Server.getInstance().executeCommand(new NPCCommandSender(this, player), line.cmd_line);
                        }
                    }
                }
            }
            if (response.getRequestType() == NPCRequestPacket.RequestType.EXECUTE_CLOSING_COMMANDS) {
                for (ElementDialogButton button : this.dialog.getButtons()) {
                    if (button.getMode() == ElementDialogButton.Mode.ON_EXIT) {
                        for (ElementDialogButton.CmdLine line : button.getData()) {
                            Server.getInstance().executeCommand(new NPCCommandSender(this, player), line.cmd_line);
                        }
                    }
                }
            }
        });
        this.dialog.setBindEntity(this);
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putString(KEY_DIALOG_TITLE, this.dialog.getTitle());
        this.namedTag.putString(KEY_DIALOG_CONTENT, this.dialog.getContent());
        this.namedTag.putString(KEY_DIALOG_SKINDATA, this.dialog.getSkinData());
        this.namedTag.putString(KEY_DIALOG_BUTTONS, this.dialog.getButtonJSONData());
        this.namedTag.putInt("Variant", this.variant);
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        //对于创造模式玩家，NPC发送过去的dialog的sceneName必须为空，否则客户端会不允许修改对话框内容
        //另外的，我们不需要记录发送给创造模式玩家的对话框，首先因为我们无法清除，其次没有必要
        player.showDialogWindow(this.dialog, !player.isCreative());
        return true;
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    @Override
    public boolean attack(EntityDamageEvent source) {
        if (source instanceof EntityDamageByEntityEvent event && event.getDamager() instanceof Player damager && damager.isCreative()) {
            this.kill();
        }
        return false;
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public int getVariant() {
        return this.variant;
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public void setVariant(int variant) {
        this.variant = variant;
        this.setDataProperty(new IntEntityData(DATA_VARIANT, variant));
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public FormWindowDialog getDialog() {
        return dialog;
    }
}
