package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.NPCCommandSender;
import cn.nukkit.dialog.element.ElementDialogButton;
import cn.nukkit.dialog.window.FormWindowDialog;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityInteractable;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.NPCRequestPacket;

import java.util.Set;
import org.jetbrains.annotations.NotNull;

/**
 * @author good777LUCKY
 */
public class EntityNpc extends EntityLiving implements IEntityNPC, EntityInteractable {
    @Override
    @NotNull
    public String getIdentifier() {
        return NPC;
    }

    //todo: Implement automatic steering of NPC entities
    public static final String KEY_DIALOG_TITLE = "DialogTitle";
    public static final String KEY_DIALOG_CONTENT = "DialogContent";
    public static final String KEY_DIALOG_SKINDATA = "DialogSkinData";
    public static final String KEY_DIALOG_BUTTONS = "DialogButtons";

    protected FormWindowDialog dialog;

    protected int variant = 0;


    public EntityNpc(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
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

    @Override
    public String getOriginalName() {
        return "NPC";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("npc", "mob");
    }

    @Override
    public boolean isPersistent() {
        return true;
    }

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
                    this.setDataProperty(Entity.ACTIONS, response.getData());
                }
            }
            if (response.getRequestType() == NPCRequestPacket.RequestType.SET_INTERACTION_TEXT) {
                this.dialog.setContent(response.getData());
                this.setDataProperty(Entity.INTERACT_TEXT, response.getData());
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

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putString(KEY_DIALOG_TITLE, this.dialog.getTitle());
        this.namedTag.putString(KEY_DIALOG_CONTENT, this.dialog.getContent());
        this.namedTag.putString(KEY_DIALOG_SKINDATA, this.dialog.getSkinData());
        this.namedTag.putString(KEY_DIALOG_BUTTONS, this.dialog.getButtonJSONData());
        this.namedTag.putInt("Variant", this.variant);
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        //For Creative Mode players, the sceneName of the dialog sent by the NPC must be empty; otherwise, the client will not allow modification of the dialog content.
        //Additionally, we do not need to record dialogs sent to Creative Mode players, firstly because we cannot clear them, and secondly because it is unnecessary.
        player.showDialogWindow(this.dialog, !player.isCreative());
        return true;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (source instanceof EntityDamageByEntityEvent event && event.getDamager() instanceof Player damager && damager.isCreative()) {
            this.kill();
        }
        return false;
    }

    public int getVariant() {
        return this.variant;
    }

    public void setVariant(int variant) {
        this.variant = variant;
        this.setDataProperty(VARIANT, variant);
    }

    public FormWindowDialog getDialog() {
        return dialog;
    }
}
