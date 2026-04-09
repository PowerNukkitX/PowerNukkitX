package cn.nukkit.blockentity;

import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockID;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.utils.ItemHelper;
import cn.nukkit.utils.RedstoneComponent;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;
import org.cloudburstmc.nbt.NbtType;


public class BlockEntityLectern extends BlockEntitySpawnable {

    private int totalPages;


    public BlockEntityLectern(IChunk chunk, NbtMap nbt) {
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
        if (!(this.namedTag.get("book") instanceof NbtMap)) {
            this.namedTag.remove("book");
        }

        if (!(this.namedTag.get("page") instanceof Integer)) {
            this.namedTag.remove("page");
        }
    }

    @Override
    public NbtMap getSpawnCompound() {
        NbtMapBuilder c = super.getSpawnCompound().toBuilder()
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

        return c.build();
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
        return this.namedTag.containsKey("book") && this.namedTag.get("book") instanceof NbtMap;
    }

    public Item getBook() {
        if (!hasBook()) {
            return new ItemBlock(new BlockAir(), 0, 0);
        } else {
            return ItemHelper.read(this.namedTag.getCompound("book"));
        }
    }

    public void setBook(Item item) {
        if (item.getId().equals(Item.WRITTEN_BOOK) || item.getId().equals(Item.WRITABLE_BOOK)) {
            this.namedTag = this.namedTag.toBuilder().putCompound("book", ItemHelper.write(item, null)).build();
        } else {
            this.namedTag.remove("book");
            this.namedTag.remove("page");
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
        this.namedTag = this.namedTag.toBuilder().putInt("page", Math.min(page, totalPages)).build();
        this.getLevel().updateAround(this);
    }

    public int getRawPage() {
        return this.namedTag.getInt("page");
    }

    public int getTotalPages() {
        return totalPages;
    }


    private void updateTotalPages() {
        Item book = getBook();
        if (book.isNull() || !book.hasCompoundTag()) {
            totalPages = 0;
        } else {
            totalPages = book.getNamedTag().getList("pages", NbtType.COMPOUND).size();
        }
        RedstoneComponent.updateAroundRedstone(this);
    }
}
