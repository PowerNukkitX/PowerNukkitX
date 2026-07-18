package org.powernukkitx.entity.passive;

import org.powernukkitx.Player;
import org.powernukkitx.Server;
import org.powernukkitx.command.NPCCommandSender;
import org.powernukkitx.dialog.element.ElementDialogButton;
import org.powernukkitx.dialog.window.FormWindowDialog;
import org.powernukkitx.entity.EntityInteractable;
import org.powernukkitx.entity.EntityLiving;
import org.powernukkitx.entity.components.MovementComponent;
import org.powernukkitx.event.entity.EntityDamageByEntityEvent;
import org.powernukkitx.event.entity.EntityDamageEvent;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorDataTypes;
import org.cloudburstmc.protocol.bedrock.packet.NpcRequestPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

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
    protected @Nullable MovementComponent getComponentMovement() {
        return MovementComponent.value(0.1f);
    }

    @Override
    public boolean isPersistent() {
        return true;
    }

    @Override
    public void initEntity() {
        super.initEntity();
        this.setHealthMax(Integer.MAX_VALUE); // Should be Float max value
        this.setHealthCurrent(20);
        this.setNameTagVisible(true);
        this.setNameTagAlwaysVisible(true);

        final CompoundTag nbtMap = this.getNbt();

        this.setVariant(nbtMap.getInt("Variant"));
        this.dialog = new FormWindowDialog(nbtMap.getString(KEY_DIALOG_TITLE).isEmpty() ? "NPC" : nbtMap.getString(KEY_DIALOG_TITLE), nbtMap.getString(KEY_DIALOG_CONTENT), this);
        this.setNameTag(this.dialog.getTitle());
        if (!nbtMap.getString(KEY_DIALOG_SKINDATA).isEmpty())
            this.dialog.setSkinData(nbtMap.getString(KEY_DIALOG_SKINDATA));
        if (!nbtMap.getString(KEY_DIALOG_BUTTONS).isEmpty())
            this.dialog.setButtonJSONData(nbtMap.getString(KEY_DIALOG_BUTTONS));
        this.dialog.addHandler((player, response) -> {
            if (response.getRequestType() == NpcRequestPacket.RequestType.SET_ACTIONS) {
                if (!response.getData().isEmpty()) {
                    this.dialog.setButtonJSONData(response.getData());
                    this.setDataProperty(ActorDataTypes.ACTIONS, response.getData());
                }
            }
            if (response.getRequestType() == NpcRequestPacket.RequestType.SET_INTERACT_TEXT) {
                this.dialog.setContent(response.getData());
                this.setDataProperty(ActorDataTypes.INTERACT_TEXT, response.getData());
            }
            if (response.getRequestType() == NpcRequestPacket.RequestType.SET_NAME) {
                this.dialog.setTitle(response.getData());
                this.setNameTag(response.getData());
            }
            if (response.getRequestType() == NpcRequestPacket.RequestType.SET_SKIN) {
                this.setVariant(response.getActionIndex());
            }
            if (response.getRequestType() == NpcRequestPacket.RequestType.EXECUTE_ACTION) {
                ElementDialogButton clickedButton = response.getClickedButton();
                for (ElementDialogButton.CmdLine line : clickedButton.getData()) {
                    Server.getInstance().executeCommand(new NPCCommandSender(this, player), line.cmd_line);
                }
            }
            if (response.getRequestType() == NpcRequestPacket.RequestType.EXECUTE_OPENING_COMMANDS) {
                for (ElementDialogButton button : this.dialog.getButtons()) {
                    if (button.getMode() == ElementDialogButton.Mode.ON_ENTER) {
                        for (ElementDialogButton.CmdLine line : button.getData()) {
                            Server.getInstance().executeCommand(new NPCCommandSender(this, player), line.cmd_line);
                        }
                    }
                }
            }
            if (response.getRequestType() == NpcRequestPacket.RequestType.EXECUTE_CLOSING_COMMANDS) {
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
        this.nbt.putString(KEY_DIALOG_TITLE, this.dialog.getTitle())
                .putString(KEY_DIALOG_CONTENT, this.dialog.getContent())
                .putString(KEY_DIALOG_SKINDATA, this.dialog.getSkinData())
                .putString(KEY_DIALOG_BUTTONS, this.dialog.getButtonJSONData())
                .putInt("Variant", this.variant);
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
        this.setDataProperty(ActorDataTypes.VARIANT, variant);
    }

    public FormWindowDialog getDialog() {
        return dialog;
    }
}
