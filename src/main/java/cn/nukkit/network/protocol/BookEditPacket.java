package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BookEditPacket extends DataPacket {
    public Action action;
    public int inventorySlot;
    public int pageNumber;
    public int secondaryPageNumber;

    public String text;
    public String photoName;

    public String title;
    public String author;
    public String xuid;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.inventorySlot = byteBuf.readVarInt();
        this.action = Action.values()[byteBuf.readUnsignedVarInt()];

        switch (this.action) {
            case REPLACE_PAGE:
            case ADD_PAGE:
                this.pageNumber = byteBuf.readVarInt();
                this.text = byteBuf.readString();
                this.photoName = byteBuf.readString();
                break;
            case DELETE_PAGE:
                this.pageNumber = byteBuf.readVarInt();
                break;
            case SWAP_PAGES:
                this.pageNumber = byteBuf.readVarInt();
                this.secondaryPageNumber = byteBuf.readVarInt();
                break;
            case SIGN_BOOK:
                this.title = byteBuf.readString();
                this.author = byteBuf.readString();
                this.xuid = byteBuf.readString();
                break;
        }
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {

    }

    public enum Action {
        REPLACE_PAGE,
        ADD_PAGE,
        DELETE_PAGE,
        SWAP_PAGES,
        SIGN_BOOK;
    }

    @Override
    public int pid() {
        return ProtocolInfo.BOOK_EDIT_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
