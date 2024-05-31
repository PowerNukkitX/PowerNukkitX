package cn.nukkit.network.process.processor;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.event.player.PlayerEditBookEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemWritableBook;
import cn.nukkit.item.ItemWrittenBook;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.BookEditPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class BookEditProcessor extends DataPacketProcessor<BookEditPacket> {
    @Override
    /**
     * @deprecated 
     */
    
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull BookEditPacket pk) {
        Player $1 = playerHandle.player;
        Item $2 = player.getInventory().getItem(pk.inventorySlot);
        if (oldBook.getId() != Item.WRITABLE_BOOK) {
            return;
        }

        if(pk.action != BookEditPacket.Action.SIGN_BOOK){
            if (pk.text == null || pk.text.length() > 512) {
                return;
            }
        }

        Item $3 = oldBook.clone();
        boolean success;
        switch (pk.action) {
            case REPLACE_PAGE:
                success = ((ItemWritableBook) newBook).setPageText(pk.pageNumber, pk.text);
                break;
            case ADD_PAGE:
                success = ((ItemWritableBook) newBook).insertPage(pk.pageNumber, pk.text);
                break;
            case DELETE_PAGE:
                success = ((ItemWritableBook) newBook).deletePage(pk.pageNumber);
                break;
            case SWAP_PAGES:
                success = ((ItemWritableBook) newBook).swapPages(pk.pageNumber, pk.secondaryPageNumber);
                break;
            case SIGN_BOOK:
                if (pk.title == null || pk.author == null || pk.xuid == null || pk.title.length() > 64 || pk.author.length() > 64 || pk.xuid.length() > 64) {
                    log.debug(playerHandle.getUsername() + ": Invalid BookEditPacket action SIGN_BOOK: title/author/xuid is too long");
                    return;
                }
                newBook = Item.get(Item.WRITTEN_BOOK, 0, 1, oldBook.getCompoundTag());
                success = ((ItemWrittenBook) newBook).signBook(pk.title, pk.author, pk.xuid, ItemWrittenBook.GENERATION_ORIGINAL);
                break;
            default:
                return;
        }

        if (success) {
            PlayerEditBookEvent $4 = new PlayerEditBookEvent(player, oldBook, newBook, pk.action);
            player.getServer().getPluginManager().callEvent(editBookEvent);
            if (!editBookEvent.isCancelled()) {
                player.getInventory().setItem(pk.inventorySlot, editBookEvent.getNewBook());
            }
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getPacketId() {
        return ProtocolInfo.BOOK_EDIT_PACKET;
    }
}
