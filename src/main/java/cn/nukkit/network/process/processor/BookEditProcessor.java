package cn.nukkit.network.process.processor;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.event.player.PlayerEditBookEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemWritableBook;
import cn.nukkit.item.ItemWrittenBook;
import cn.nukkit.network.process.DataPacketProcessor;
import org.cloudburstmc.protocol.bedrock.packet.BookEditPacket;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class BookEditProcessor extends DataPacketProcessor<BookEditPacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull BookEditPacket pk) {
        Player player = playerHandle.player;

        Item oldBook = player.getInventory().getItem(pk.getInventorySlot());
        if (!oldBook.getId().equals(Item.WRITABLE_BOOK)) {
            return;
        }

        if (pk.getAction() != BookEditPacket.Action.SIGN_BOOK) {
            if (pk.getText() == null || pk.getText().length() > 512) {
                return;
            }
        }

        Item newBook = oldBook.clone();
        boolean success;
        switch (pk.getAction()) {
            case REPLACE_PAGE:
                success = ((ItemWritableBook) newBook).setPageText(pk.getPageNumber(), pk.getText());
                break;
            case ADD_PAGE:
                success = ((ItemWritableBook) newBook).insertPage(pk.getPageNumber(), pk.getText());
                break;
            case DELETE_PAGE:
                success = ((ItemWritableBook) newBook).deletePage(pk.getPageNumber());
                break;
            case SWAP_PAGES:
                success = ((ItemWritableBook) newBook).swapPages(pk.getPageNumber(), pk.getSecondaryPageNumber());
                break;
            case SIGN_BOOK:
                if (pk.getTitle() == null || pk.getAuthor() == null || pk.getXuid() == null || pk.getTitle().length() > 64 || pk.getAuthor().length() > 64 || pk.getXuid().length() > 64) {
                    log.debug("{}: Invalid BookEditPacket action SIGN_BOOK: title/author/xuid is too long", playerHandle.getUsername());
                    return;
                }
                newBook = Item.get(Item.WRITTEN_BOOK, 0, 1, oldBook.getCompoundTag());
                success = ((ItemWrittenBook) newBook).signBook(pk.getTitle(), pk.getAuthor(), pk.getXuid(), ItemWrittenBook.GENERATION_ORIGINAL);
                break;
            default:
                return;
        }

        if (success) {
            PlayerEditBookEvent editBookEvent = new PlayerEditBookEvent(player, oldBook, newBook, pk.getAction());
            player.getServer().getPluginManager().callEvent(editBookEvent);
            if (!editBookEvent.isCancelled()) {
                player.getInventory().setItem(pk.getInventorySlot(), editBookEvent.getNewBook());
            }
        }
    }

    @Override
    public Class<BookEditPacket> getPacketClass() {
        return BookEditPacket.class;
    }
}
