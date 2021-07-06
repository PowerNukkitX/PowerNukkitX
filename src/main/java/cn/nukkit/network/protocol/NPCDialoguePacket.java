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

/**
 * @author joserobjr
 * @since 2021-07-06
 */
@PowerNukkitOnly
@Since("FUTURE")
public class NPCDialoguePacket extends DataPacket {
    @PowerNukkitOnly
    @Since("FUTURE")
    public static final byte NETWORK_ID = ProtocolInfo.NPC_DIALOGUE_PACKET;
    
    private static final Action[] ACTIONS = Action.values();
    
    private long runtimeEntityId;
    private Action action;
    private String dialogue;
    private String sceneName;
    private String npcName;
    private String actionJson;

    @PowerNukkitOnly
    @Since("FUTURE")
    public NPCDialoguePacket() {
        // Indicates when this public constructor were accessible
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        runtimeEntityId = getUnsignedVarLong();
        action = ACTIONS[getVarInt()];
        dialogue = getString();
        sceneName = getString();
        npcName = getString();
        actionJson = getString();
    }

    @Override
    public void encode() {
        reset();
        putUnsignedVarLong(runtimeEntityId);
        putVarInt(action.ordinal());
        putString(actionJson);
        putString(dialogue);
        putString(sceneName);
        putString(npcName);
        putString(actionJson);
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public long getRuntimeEntityId() {
        return runtimeEntityId;
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public void setRuntimeEntityId(long runtimeEntityId) {
        this.runtimeEntityId = runtimeEntityId;
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public Action getAction() {
        return action;
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public void setAction(Action action) {
        this.action = action;
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public String getDialogue() {
        return dialogue;
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public void setDialogue(String dialogue) {
        this.dialogue = dialogue;
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public String getSceneName() {
        return sceneName;
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public void setSceneName(String sceneName) {
        this.sceneName = sceneName;
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public String getNpcName() {
        return npcName;
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public void setNpcName(String npcName) {
        this.npcName = npcName;
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public String getActionJson() {
        return actionJson;
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public void setActionJson(String actionJson) {
        this.actionJson = actionJson;
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public enum Action {
        @PowerNukkitOnly @Since("FUTURE") OPEN,
        @PowerNukkitOnly @Since("FUTURE") CLOSE
    }
}
