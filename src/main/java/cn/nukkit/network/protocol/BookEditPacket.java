package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.ToString;

@ToString
public class BookEditPacket extends DataPacket {

    public static final int NETWORK_ID = ProtocolInfo.BOOK_EDIT_PACKET;

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
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.action = Action.values()[byteBuf.readByte()];
        this.inventorySlot = byteBuf.readByte();

        switch (this.action) {
            case REPLACE_PAGE:
            case ADD_PAGE:
                this.pageNumber = byteBuf.readByte();
                this.text = byteBuf.readString();
                this.photoName = byteBuf.readString();
                break;
            case DELETE_PAGE:
                this.pageNumber = byteBuf.readByte();
                break;
            case SWAP_PAGES:
                this.pageNumber = byteBuf.readByte();
                this.secondaryPageNumber = byteBuf.readByte();
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
        SIGN_BOOK
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
