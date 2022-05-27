/*
 * https://PowerNukkit.org - The Nukkit you know but Powerful!
 * Copyright (C) 2021  José Roberto de Araújo Júnior
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package cn.nukkit.network.protocol;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

import javax.annotation.Nonnull;

/**
 * @author joserobjr
 * @since 2021-07-06
 */
@PowerNukkitOnly
@Since("1.5.2.0-PN")
public class NPCDialoguePacket extends DataPacket {
    @PowerNukkitOnly
    @Since("1.5.2.0-PN")
    public static final byte NETWORK_ID = ProtocolInfo.NPC_DIALOGUE_PACKET;
    
    private static final NPCDialogAction[] ACTIONS = NPCDialogAction.values();
    
    private long runtimeEntityId;
    private NPCDialogAction action = NPCDialogAction.OPEN;
    private String dialogue = "";
    private String sceneName = "";
    private String npcName = "";
    private String actionJson = "";

    @PowerNukkitOnly
    @Since("1.5.2.0-PN")
    public NPCDialoguePacket() {
        // Indicates when this public constructor were accessible
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        runtimeEntityId = getLLong();
        action = ACTIONS[getVarInt()];
        dialogue = getString();
        sceneName = getString();
        npcName = getString();
        actionJson = getString();
    }

    @Override
    public void encode() {
        reset();
        putLLong(runtimeEntityId);
        putVarInt(action.ordinal());
        putString(dialogue);
        putString(sceneName);
        putString(npcName);
        putString(actionJson);
    }

    @PowerNukkitOnly
    @Since("1.5.2.0-PN")
    public long getRuntimeEntityId() {
        return runtimeEntityId;
    }

    @PowerNukkitOnly
    @Since("1.5.2.0-PN")
    public void setRuntimeEntityId(long runtimeEntityId) {
        this.runtimeEntityId = runtimeEntityId;
    }

    @PowerNukkitOnly
    @Since("1.5.2.0-PN")
    @Nonnull
    public NPCDialogAction getAction() {
        return action;
    }

    @PowerNukkitOnly
    @Since("1.5.2.0-PN")
    public void setAction(@Nonnull NPCDialogAction action) {
        this.action = action;
    }

    @PowerNukkitOnly
    @Since("1.5.2.0-PN")
    @Nonnull
    public String getDialogue() {
        return dialogue;
    }

    @PowerNukkitOnly
    @Since("1.5.2.0-PN")
    public void setDialogue(@Nonnull String dialogue) {
        this.dialogue = dialogue;
    }

    @PowerNukkitOnly
    @Since("1.5.2.0-PN")
    @Nonnull
    public String getSceneName() {
        return sceneName;
    }

    @PowerNukkitOnly
    @Since("1.5.2.0-PN")
    public void setSceneName(@Nonnull String sceneName) {
        this.sceneName = sceneName;
    }

    @PowerNukkitOnly
    @Since("1.5.2.0-PN")
    @Nonnull
    public String getNpcName() {
        return npcName;
    }

    @PowerNukkitOnly
    @Since("1.5.2.0-PN")
    public void setNpcName(@Nonnull String npcName) {
        this.npcName = npcName;
    }

    @PowerNukkitOnly
    @Since("1.5.2.0-PN")
    @Nonnull
    public String getActionJson() {
        return actionJson;
    }

    @PowerNukkitOnly
    @Since("1.5.2.0-PN")
    public void setActionJson(@Nonnull String actionJson) {
        this.actionJson = actionJson;
    }

    @PowerNukkitOnly
    @Since("1.5.2.0-PN")
    public enum NPCDialogAction {
        @PowerNukkitOnly @Since("1.5.2.0-PN") OPEN,
        @PowerNukkitOnly @Since("1.5.2.0-PN") CLOSE
    }
}
