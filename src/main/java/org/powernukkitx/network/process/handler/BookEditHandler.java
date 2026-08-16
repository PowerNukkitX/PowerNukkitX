package org.powernukkitx.network.process.handler;

import org.powernukkitx.Player;
import org.powernukkitx.PlayerHandle;
import org.powernukkitx.Server;
import org.powernukkitx.event.player.PlayerEditBookEvent;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemWritableBook;
import org.powernukkitx.item.ItemWrittenBook;
import org.powernukkitx.network.process.PacketHandler;
import org.powernukkitx.network.process.PlayerSessionHolder;
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

        final BookEditAction action = packet.getOperation();
        if (action == null || action.getType() == null) {
            log.debug("{}: BookEditPacket with null operation", playerHandle.getUsername());
            return;
        }

        int bookSlot = packet.getBookSlot();
        int inventorySize = player.getInventory().getSize();
        if (bookSlot < 0 || bookSlot >= inventorySize) {
            log.warn("{}: BookEditPacket with invalid book slot {}", playerHandle.getUsername(), bookSlot);
            return;
        }

        Item oldBook = player.getInventory().getItem(bookSlot);
        if (oldBook.getId() == null || !oldBook.getId().equals(Item.WRITABLE_BOOK)) {
            return;
        }

        Item newBook = oldBook.clone();
        boolean success;
        try {
            switch (action.getType()) {
                case REPLACE_PAGE -> {
                    final BookEditAction.ReplacePage replacePage = (BookEditAction.ReplacePage) action;
                    success = ((ItemWritableBook) newBook).setPageText(replacePage.getPageIndex(), replacePage.getPageText());
                }
                case ADD_PAGE -> {
                    final BookEditAction.AddPage addPage = (BookEditAction.AddPage) action;
                    success = ((ItemWritableBook) newBook).insertPage(addPage.getPageIndex(), addPage.getPageText());
                }
                case DELETE_PAGE -> {
                    final BookEditAction.DeletePage deletePage = (BookEditAction.DeletePage) action;
                    success = ((ItemWritableBook) newBook).deletePage(deletePage.getPageIndex());
                }
                case SWAP_PAGES -> {
                    final BookEditAction.SwapPages swapPages = (BookEditAction.SwapPages) action;
                    success = ((ItemWritableBook) newBook).swapPages(swapPages.getPageIndex(), swapPages.getSwapWithIndex());
                }
                case FINALIZE -> {
                    final BookEditAction.Finalize finalize = (BookEditAction.Finalize) action;
                    if (finalize.getTitle() == null || finalize.getAuthor() == null || finalize.getXuid() == null
                        || finalize.getTitle().length() > 64 || finalize.getAuthor().length() > 64
                        || finalize.getXuid().length() > 64) {
                        log.debug("{}: Invalid BookEditPacket action SIGN_BOOK: title/author/xuid is too long", playerHandle.getUsername());
                        return;
                    }
                    newBook = Item.get(Item.WRITTEN_BOOK, 0, 1, oldBook.getNbtBytes());
                    success = ((ItemWrittenBook) newBook).signBook(finalize.getTitle(), finalize.getAuthor(), finalize.getXuid(), ItemWrittenBook.GENERATION_ORIGINAL);
                }
                default -> {
                    return;
                }
            }
        } catch (Exception e) {
            log.warn("{}: failed to process BookEditPacket action {}", playerHandle.getUsername(), action.getType(), e);
            return;
        }

        if (success) {
            PlayerEditBookEvent editBookEvent = new PlayerEditBookEvent(player, oldBook, newBook, action);
            player.getServer().getPluginManager().callEvent(editBookEvent);
            if (!editBookEvent.isCancelled()) {
                player.getInventory().setItem(bookSlot, editBookEvent.getNewBook());
            }
        }
    }
}
