package cn.nukkit.dialog.response;

import cn.nukkit.network.protocol.NPCRequestPacket;

public class FormResponseDialog {

    private final NPCRequestPacket packet;

    public FormResponseDialog(NPCRequestPacket packet) {
        this.packet = packet;
    }
}
