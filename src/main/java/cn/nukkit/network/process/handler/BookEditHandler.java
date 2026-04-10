package cn.nukkit.network.process.handler;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.Server;
import cn.nukkit.event.player.PlayerEditBookEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemWritableBook;
import cn.nukkit.item.ItemWrittenBook;
import cn.nukkit.network.process.PacketHandler;
import cn.nukkit.network.process.PlayerSessionHolder;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.protocol.bedrock.data.BookEditAction;
import org.cloudburstmc.protocol.bedrock.data.BookEditOperation;
import org.cloudburstmc.protocol.bedrock.packet.BookEditPacket;

/**
 * @author Kaooot
 */
@Slf4j
public class BookEditHandler implements PacketHandler<BookEditPacket> {

    @Override
    public void handle(BookEditPacket packet, PlayerSessionHolder holder, Server server) {
        final PlayerHandle playerHandle = holder.getPlayerHandle();
        Player player = playerHandle.player;

        Item oldBook = player.getInventory().getItem(packet.getBookSlot());
        if (!oldBook.getId().equals(Item.WRITABLE_BOOK)) {
            return;
        }

        final BookEditAction action = packet.getOperation();
        if (!action.getType().equals(BookEditOperation.FINALIZE)) {
            final BookEditAction.Finalize finalize = (BookEditAction.Finalize) action;
            if (finalize.getTitle() == null || finalize.getTitle().length() > 512) {
                return;
            }
        }

        Item newBook = oldBook.clone();
        boolean success;
        switch (packet.getOperation().getType()) {
            case REPLACE_PAGE:
                final BookEditAction.ReplacePage replacePage = (BookEditAction.ReplacePage) action;
                success = ((ItemWritableBook) newBook).setPageText(replacePage.getPageIndex(), replacePage.getPageText());
                break;
            case ADD_PAGE:
                final BookEditAction.AddPage addPage = (BookEditAction.AddPage) action;
                success = ((ItemWritableBook) newBook).insertPage(addPage.getPageIndex(), addPage.getPageText());
                break;
            case DELETE_PAGE:
                final BookEditAction.DeletePage deletePage = (BookEditAction.DeletePage) action;
                success = ((ItemWritableBook) newBook).deletePage(deletePage.getPageIndex());
                break;
            case SWAP_PAGES:
                final BookEditAction.SwapPages swapPages = (BookEditAction.SwapPages) action;
                success = ((ItemWritableBook) newBook).swapPages(swapPages.getPageIndex(), swapPages.getSwapWithIndex());
                break;
            case FINALIZE:
                final BookEditAction.Finalize finalize = (BookEditAction.Finalize) action;
                if (finalize.getTitle() == null || finalize.getAuthor() == null || finalize.getXuid() == null || finalize.getTitle().length() > 64 || finalize.getAuthor().length() > 64 || finalize.getXuid().length() > 64) {
                    log.debug("{}: Invalid BookEditPacket action SIGN_BOOK: title/author/xuid is too long", playerHandle.getUsername());
                    return;
                }
                newBook = Item.get(Item.WRITTEN_BOOK, 0, 1, oldBook.getCompoundTag());
                success = ((ItemWrittenBook) newBook).signBook(finalize.getTitle(), finalize.getAuthor(), finalize.getXuid(), ItemWrittenBook.GENERATION_ORIGINAL);
                break;
            default:
                return;
        }

        if (success) {
            PlayerEditBookEvent editBookEvent = new PlayerEditBookEvent(player, oldBook, newBook, action);
            player.getServer().getPluginManager().callEvent(editBookEvent);
            if (!editBookEvent.isCancelled()) {
                player.getInventory().setItem(packet.getBookSlot(), editBookEvent.getNewBook());
            }
        }
    }
}