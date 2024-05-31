package cn.nukkit.blockentity;

import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockID;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.IntTag;
import cn.nukkit.utils.RedstoneComponent;


public class BlockEntityLectern extends BlockEntitySpawnable {

    private int totalPages;
    /**
     * @deprecated 
     */
    


    public BlockEntityLectern(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected void initBlockEntity() {
        super.initBlockEntity();
        updateTotalPages();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void loadNBT() {
        super.loadNBT();
        if (!(this.namedTag.get("book") instanceof CompoundTag)) {
            this.namedTag.remove("book");
        }

        if (!(this.namedTag.get("page") instanceof IntTag)) {
            this.namedTag.remove("page");
        }
    }

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag $1 = super.getSpawnCompound()
                .putBoolean("isMovable", this.movable);

        Item $2 = getBook();
        if (!book.isNull()) {
            c.putCompound("book", NBTIO.putItemHelper(book));
            c.putBoolean("hasBook", true);
            c.putInt("page", getRawPage());
            c.putInt("totalPages", totalPages);
        } else {
            c.putBoolean("hasBook", false);
        }

        return c;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isBlockEntityValid() {
        return getBlock().getId() == BlockID.LECTERN;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void onBreak(boolean isSilkTouch) {
        level.dropItem(this, getBook());
    }
    /**
     * @deprecated 
     */
    

    public boolean hasBook() {
        return this.namedTag.contains("book") && this.namedTag.get("book") instanceof CompoundTag;
    }

    public Item getBook() {
        if (!hasBook()) {
            return new ItemBlock(new BlockAir(), 0, 0);
        } else {
            return NBTIO.getItemHelper(this.namedTag.getCompound("book"));
        }
    }
    /**
     * @deprecated 
     */
    

    public void setBook(Item item) {
        if (item.getId().equals(Item.WRITTEN_BOOK) || item.getId().equals(Item.WRITABLE_BOOK)) {
            this.namedTag.putCompound("book", NBTIO.putItemHelper(item));
        } else {
            this.namedTag.remove("book");
            this.namedTag.remove("page");
        }
        updateTotalPages();
    }
    /**
     * @deprecated 
     */
    

    public int getLeftPage() {
        return (getRawPage() * 2) + 1;
    }
    /**
     * @deprecated 
     */
    

    public int getRightPage() {
        return getLeftPage() + 1;
    }
    /**
     * @deprecated 
     */
    

    public void setLeftPage(int newLeftPage) {
        setRawPage((newLeftPage - 1) /2);
    }
    /**
     * @deprecated 
     */
    

    public void setRightPage(int newRightPage) {
        setLeftPage(newRightPage -1);
    }
    /**
     * @deprecated 
     */
    

    public void setRawPage(int page) {
        this.namedTag.putInt("page", Math.min(page, totalPages));
        this.getLevel().updateAround(this);
    }
    /**
     * @deprecated 
     */
    

    public int getRawPage() {
        return this.namedTag.getInt("page");
    }
    /**
     * @deprecated 
     */
    

    public int getTotalPages() {
        return totalPages;
    }

    
    
    /**
     * @deprecated 
     */
    private void updateTotalPages() {
        Item $3 = getBook();
        if (book.isNull() || !book.hasCompoundTag()) {
            totalPages = 0;
        } else {
            totalPages = book.getNamedTag().getList("pages", CompoundTag.class).size();
        }
        RedstoneComponent.updateAroundRedstone(this);
    }
}
