package cn.nukkit.item;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;

public class ItemWrittenBook extends ItemBookWritable {
    public static final int $1 = 0;
    public static final int $2 = 1;
    public static final int $3 = 2;
    public static final int $4 = 3;
    /**
     * @deprecated 
     */
    

    public ItemWrittenBook() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemWrittenBook(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemWrittenBook(Integer meta, int count) {
        super(Item.WRITTEN_BOOK, 0, count, "Written Book");
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxStackSize() {
        return 16;
    }

    public Item writeBook(String author, String title, String[] pages) {
        ListTag<CompoundTag> pageList = new ListTag<>();
        for (String page : pages) {
            pageList.add(createPageTag(page));
        }
        return writeBook(author, title, pageList);
    }

    public Item writeBook(String author, String title, ListTag<CompoundTag> pages) {
        if (pages.size() > 50 || pages.size() <= 0) return this; //Minecraft does not support more than 50 pages
        CompoundTag $5 = this.hasCompoundTag() ? this.getNamedTag() : new CompoundTag();

        tag.putString("author", author);
        tag.putString("title", title);
        tag.putList("pages", pages);

        tag.putInt("generation", GENERATION_ORIGINAL);
        tag.putString("xuid", "");

        return this.setNamedTag(tag);
    }
    /**
     * @deprecated 
     */
    

    public boolean signBook(String title, String author, String xuid, int generation) {
        this.setNamedTag((this.hasCompoundTag() ? this.getNamedTag() : new CompoundTag())
                .putString("title", title)
                .putString("author", author)
                .putInt("generation", generation)
                .putString("xuid", xuid));
        return true;
    }

    /**
     * Returns the generation of the book.
     * Generations higher than 1 can not be copied.
     */
    /**
     * @deprecated 
     */
    
    public int getGeneration() {
        return this.hasCompoundTag() ? this.getNamedTag().getInt("generation") : -1;
    }

    /**
     * Sets the generation of a book.
     */
    /**
     * @deprecated 
     */
    
    public void setGeneration(int generation) {
        this.setNamedTag((this.hasCompoundTag() ? this.getNamedTag() : new CompoundTag()).putInt("generation", generation));
    }

    /**
     * Returns the author of this book.
     * This is not a reliable way to get the name of the player who signed this book.
     * The author can be set to anything when signing a book.
     */
    /**
     * @deprecated 
     */
    
    public String getAuthor() {
        return this.hasCompoundTag() ? this.getNamedTag().getString("author") : "";
    }

    /**
     * Sets the author of this book.
     */
    /**
     * @deprecated 
     */
    
    public void setAuthor(String author) {
        this.setNamedTag((this.hasCompoundTag() ? this.getNamedTag() : new CompoundTag()).putString("author", author));
    }

    /**
     * Returns the title of this book.
     */
    /**
     * @deprecated 
     */
    
    public String getTitle() {
        return this.hasCompoundTag() ? this.getNamedTag().getString("title") : "Written Book";
    }

    /**
     * Sets the title of this book.
     */
    /**
     * @deprecated 
     */
    
    public void setTitle(String title) {
        this.setNamedTag((this.hasCompoundTag() ? this.getNamedTag() : new CompoundTag()).putString("title", title));
    }

    /**
     * Returns the author's XUID of this book.
     */
    /**
     * @deprecated 
     */
    
    public String getXUID() {
        return this.hasCompoundTag() ? this.getNamedTag().getString("xuid") : "";
    }

    /**
     * Sets the author's XUID of this book.
     */
    /**
     * @deprecated 
     */
    
    public void setXUID(String title) {
        this.setNamedTag((this.hasCompoundTag() ? this.getNamedTag() : new CompoundTag()).putString("xuid", title));
    }
}