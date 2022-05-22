package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityInteractable;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.form.dialog.window.FormWindowDialogue;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.NPCDialoguePacket;

/**
 * @author good777LUCKY
 */
@Since("1.4.0.0-PN")
@PowerNukkitOnly
public class EntityNPCEntity extends EntityLiving implements EntityNPC, EntityInteractable {

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    public static final int NETWORK_ID = 51;

    private FormWindowDialogue dialogue;

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
    public String getInteractButtonText() {
        return "action.interact.edit";
    }

    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    @Override
    public String getOriginalName() {
        return "NPC";
    }

    @Override
    public void initEntity() {
        super.initEntity();
        this.setMaxHealth(Integer.MAX_VALUE); // Should be Float max value
        this.setHealth(20);
        this.setNameTagVisible(true);
        this.setNameTagAlwaysVisible(true);
//        this.setDataProperty(new ByteEntityData(Entity.DATA_HAS_NPC_COMPONENT, 1));
//        this.setDataProperty(new StringEntityData(Entity.DATA_NPC_SKIN_DATA, "{\"picker_offsets\":{\"scale\":[1.70,1.70,1.70],\"translate\":[0,20,0]},\"portrait_offsets\":{\"scale\":[1.750,1.750,1.750],\"translate\":[-7,50,0]},\"skin_list\":[{\"variant\":0},{\"variant\":1},{\"variant\":2},{\"variant\":3},{\"variant\":4},{\"variant\":5},{\"variant\":6},{\"variant\":7},{\"variant\":8},{\"variant\":9},{\"variant\":10},{\"variant\":11},{\"variant\":12},{\"variant\":13},{\"variant\":14},{\"variant\":15},{\"variant\":16},{\"variant\":17},{\"variant\":18},{\"variant\":19},{\"variant\":20},{\"variant\":21},{\"variant\":22},{\"variant\":23},{\"variant\":24}]}"));
//        this.setDataProperty(new StringEntityData(Entity.DATA_NPC_ACTIONS, ""));
//        this.setDataProperty(new StringEntityData(Entity.DATA_INTERACTIVE_TAG, ""));
//        this.setDataProperty(new StringEntityData(Entity.DATA_NAME_RAW_TEXT, "NPC"));
    }

    //todo: remove this DEBUG
    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
//        if (this.dialogue == null) {
//            this.dialogue = new FormWindowDialogue("TestTitle", "TestContent");
//            this.dialogue.addButton("TestButton");
//            this.dialogue.setEntityNPCEntity(this);
//        }
//        this.dialogue.sendToPlayer(player);
        String actionJson = "[{\"button_name\":\"TestButton\",\"text\":\"Test123\",\"data\":[{\"cmd_line\":\"Test123\",\"cmd_ver\":17}],\"mode\":0,\"type\":1}]";
//        EntityNPCEntity entityNPCEntity = this;
        EntityNPCEntity entityNPCEntity = new EntityNPCEntity(player.getChunk(), EntityNPCEntity.getDefaultNBT(player));
        entityNPCEntity.getDataProperties().putByte(Entity.DATA_HAS_NPC_COMPONENT, 1);
        entityNPCEntity.getDataProperties().putString(Entity.DATA_INTERACTIVE_TAG, "Content");
        entityNPCEntity.getDataProperties().putString(Entity.DATA_NPC_ACTIONS, actionJson);
        entityNPCEntity.spawnTo(player);

        NPCDialoguePacket npcDialoguePacket = new NPCDialoguePacket();
        npcDialoguePacket.setRuntimeEntityId(entityNPCEntity.getId());
        npcDialoguePacket.setAction(NPCDialoguePacket.NPCDialogAction.OPEN);
        npcDialoguePacket.setDialogue("Content");
        npcDialoguePacket.setSceneName("Scene");
        npcDialoguePacket.setNpcName("Title");
        npcDialoguePacket.setActionJson(actionJson);
        player.dataPacket(npcDialoguePacket);
        player.sendMessage("Open...");
        return true;
    }

    @PowerNukkitDifference(info = "Using new method to play sounds", since = "1.4.0.0-PN")
    @Override
    public boolean attack(EntityDamageEvent source) {
        if (source instanceof EntityDamageByEntityEvent event && event.getDamager() instanceof Player damager && damager.isCreative()) {
            this.kill();
        }
        return false;
    }
}
