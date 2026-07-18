package org.powernukkitx.blockentity;

import org.powernukkitx.block.BlockAir;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemBlock;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.ItemHelper;
import org.powernukkitx.utils.RedstoneComponent;


public class BlockEntityLectern extends BlockEntitySpawnable {

    private int totalPages;


    public BlockEntityLectern(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        super.initBlockEntity();
        updateTotalPages();
    }

    @Override
    public void loadNBT() {
        super.loadNBT();
        if (!this.nbt.containsCompound("book")) {
            this.nbt.remove("book");
        }

        if (!this.nbt.containsInt("page")) {
            this.nbt.remove("page");
        }
    }

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag c = super.getSpawnCompound()
                .putBoolean("isMovable", this.movable);

        Item book = getBook();
        if (!book.isNull()) {
            c.putCompound("book", ItemHelper.write(book, null));
            c.putBoolean("hasBook", true);
            c.putInt("page", getRawPage());
            c.putInt("totalPages", totalPages);
        } else {
            c.putBoolean("hasBook", false);
        }

        return c;
    }

    @Override
    public boolean isBlockEntityValid() {
        return getBlock().getId() == BlockID.LECTERN;
    }

    @Override
    public void onBreak(boolean isSilkTouch) {
        level.dropItem(this, getBook());
    }

    public boolean hasBook() {
        return this.nbt.containsCompound("book");
    }

    public Item getBook() {
        if (!hasBook()) {
            return new ItemBlock(new BlockAir(), 0, 0);
        } else {
            return ItemHelper.read(this.getNbt().getCompound("book"));
        }
    }

    public void setBook(Item item) {
        if (item.getId().equals(Item.WRITTEN_BOOK) || item.getId().equals(Item.WRITABLE_BOOK)) {
            this.nbt.putCompound("book", ItemHelper.write(item, null));
        } else {
            this.nbt.remove("book");
            this.nbt.remove("page");
        }
        updateTotalPages();
    }

    public int getLeftPage() {
        return (getRawPage() * 2) + 1;
    }

    public int getRightPage() {
        return getLeftPage() + 1;
    }

    public void setLeftPage(int newLeftPage) {
        setRawPage((newLeftPage - 1) / 2);
    }

    public void setRightPage(int newRightPage) {
        setLeftPage(newRightPage - 1);
    }

    public void setRawPage(int page) {
        this.nbt.putInt("page", Math.min(page, totalPages));
        this.getLevel().updateAround(this);
    }

    public int getRawPage() {
        return this.getNbt().getInt("page");
    }

    public int getTotalPages() {
        return totalPages;
    }


    private void updateTotalPages() {
        Item book = getBook();
        if (book.isNull() || !book.hasNbt()) {
            totalPages = 0;
        } else {
            totalPages = book.getNbt().getList("pages", CompoundTag.class).size();
        }
        RedstoneComponent.updateAroundRedstone(this);
    }
}
