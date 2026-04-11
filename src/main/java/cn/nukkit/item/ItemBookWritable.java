package cn.nukkit.item;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtType;

import java.util.List;

public abstract class ItemBookWritable extends Item {

    protected ItemBookWritable(String id) {
        super(id);
    }

    protected ItemBookWritable(String id, Integer meta) {
        super(id, meta);
    }

    protected ItemBookWritable(String id, Integer meta, int count) {
        super(id, meta, count);
    }

    protected ItemBookWritable(String id, Integer meta, int count, String name) {
        super(id, meta, count, name);
    }

    /**
     * Returns whether the given page exists in this book.
     */
    public boolean pageExists(int pageId) {
        Preconditions.checkArgument(pageId >= 0 && pageId < 50, "Page number " + pageId + " is out of range");
        if (this.hasCompoundTag()) {
            NbtMap tag = this.getNamedTag();
            if (tag.containsKey("pages") && tag.get("pages") instanceof List<?>) {
                return tag.getList("pages", NbtType.COMPOUND).size() > pageId;
            }
        }
        return false;
    }

    /**
     * Returns a string containing the content of a page (which could be empty), or null if the page doesn't exist.
     */
    public String getPageText(int pageId) {
        Preconditions.checkArgument(pageId >= 0 && pageId < 50, "Page number " + pageId + " is out of range");
        if (this.hasCompoundTag()) {
            NbtMap tag = this.getNamedTag();
            if (tag.containsKey("pages") && tag.get("pages") instanceof List<?>) {
                List<NbtMap> pages = tag.getList("pages", NbtType.COMPOUND);
                if (pages.size() > pageId) {
                    return pages.get(pageId).getString("text");
                }
            }
        }
        return null;
    }

    /**
     * Sets the text of a page in the book. Adds the page if the page does not yet exist.
     *
     * @return boolean indicating success
     */
    public boolean setPageText(int pageId, String pageText) {
        Preconditions.checkArgument(pageId >= 0 && pageId < 50, "Page number " + pageId + " is out of range");
        Preconditions.checkArgument(pageText.length() <= 256, "Text length " + pageText.length() + " is out of range");
        NbtMap tag;
        if (this.hasCompoundTag()) {
            tag = this.getNamedTag();
        } else if (pageText.isEmpty()) {
            return false;
        } else {
            tag = NbtMap.EMPTY;
        }
        List<NbtMap> pages;
        if (!tag.containsKey("pages") || !(tag.get("pages") instanceof List<?>)) {
            pages = new ObjectArrayList<>();
            tag = tag.toBuilder().putList("pages", NbtType.COMPOUND, pages).build();
        } else {
            pages = new ObjectArrayList<>(tag.getList("pages", NbtType.COMPOUND));
        }
        if (pages.size() <= pageId) {
            for (int current = pages.size(); current <= pageId; current++) {
                pages.add(createPageTag());
            }
        }

        final List<NbtMap> updatedPages = new ObjectArrayList<>();
        for (NbtMap page : pages) {
            updatedPages.add(pages.indexOf(page) == pageId ? page.toBuilder().putString("text", pageText).build() : page);
        }
        tag = tag.toBuilder().putList("pages", NbtType.COMPOUND, updatedPages).build();
        this.setCompoundTag(tag);
        return true;
    }

    /**
     * Adds a new page with the given page ID.
     * Creates a new page for every page between the given ID and existing pages that doesn't yet exist.
     *
     * @return boolean indicating success
     */
    public boolean addPage(int pageId) {
        Preconditions.checkArgument(pageId >= 0 && pageId < 50, "Page number " + pageId + " is out of range");
        NbtMap tag = this.hasCompoundTag() ? this.getNamedTag() : NbtMap.EMPTY;
        List<NbtMap> pages;
        if (!tag.containsKey("pages") || !(tag.get("pages") instanceof List<?>)) {
            pages = new ObjectArrayList<>();
            tag = tag.toBuilder().putList("pages", NbtType.COMPOUND, pages).build();
        } else {
            pages = new ObjectArrayList<>(tag.getList("pages", NbtType.COMPOUND));
        }

        for (int current = pages.size(); current <= pageId; current++) {
            pages.add(createPageTag());
        }
        this.setCompoundTag(tag);
        return true;
    }

    /**
     * Deletes an existing page with the given page ID.
     *
     * @return boolean indicating success
     */
    public boolean deletePage(int pageId) {
        Preconditions.checkArgument(pageId >= 0 && pageId < 50, "Page number " + pageId + " is out of range");
        if (this.hasCompoundTag()) {
            NbtMap tag = this.getNamedTag();
            if (tag.containsKey("pages") && tag.get("pages") instanceof List<?>) {
                List<NbtMap> pages = new ObjectArrayList<>(tag.getList("pages", NbtType.COMPOUND));
                if (pages.size() > pageId) {
                    pages.remove(pageId);
                    this.setCompoundTag(tag);
                }
            }
        }
        return true;
    }

    /**
     * Inserts a new page with the given text and moves other pages upwards.
     *
     * @return boolean indicating success
     */
    public boolean insertPage(int pageId) {
        return this.insertPage(pageId, "");
    }

    /**
     * Inserts a new page with the given text and moves other pages upwards.
     *
     * @return boolean indicating success
     */
    public boolean insertPage(int pageId, String pageText) {
        Preconditions.checkArgument(pageId >= 0 && pageId < 50, "Page number " + pageId + " is out of range");
        Preconditions.checkArgument(pageText.length() <= 256, "Text length " + pageText.length() + " is out of range");
        NbtMap tag = this.hasCompoundTag() ? this.getNamedTag() : NbtMap.EMPTY;
        List<NbtMap> pages;
        if (!tag.containsKey("pages") || !(tag.get("pages") instanceof List<?>)) {
            pages = new ObjectArrayList<>();
            tag = tag.toBuilder().putList("pages", NbtType.COMPOUND, pages).build();
        } else {
            pages = new ObjectArrayList<>(tag.getList("pages", NbtType.COMPOUND));
        }

        if (pages.size() <= pageId) {
            for (int current = pages.size(); current <= pageId; current++) {
                pages.add(createPageTag());
            }
            pages.set(pageId, pages.get(pageId).toBuilder().putString("text", pageText).build());
        } else {
            pages.add(pageId, createPageTag(pageText));
        }
        this.setCompoundTag(tag);
        return true;
    }

    /**
     * Switches the text of two pages with each other.
     *
     * @return boolean indicating success
     */
    public boolean swapPages(int pageId1, int pageId2) {
        Preconditions.checkArgument(pageId1 >= 0 && pageId1 < 50, "Page number " + pageId1 + " is out of range");
        Preconditions.checkArgument(pageId2 >= 0 && pageId2 < 50, "Page number " + pageId2 + " is out of range");
        if (this.hasCompoundTag()) {
            NbtMap tag = this.getNamedTag();
            if (tag.containsKey("pages") && tag.get("pages") instanceof List<?>) {
                List<NbtMap> pages = new ObjectArrayList<>(tag.getList("pages", NbtType.COMPOUND));
                if (pages.size() > pageId1 && pages.size() > pageId2) {
                    String pageContents1 = pages.get(pageId1).getString("text");
                    String pageContents2 = pages.get(pageId2).getString("text");
                    pages.set(pageId1, pages.get(pageId1).toBuilder().putString("text", pageContents2).build());
                    pages.set(pageId2, pages.get(pageId2).toBuilder().putString("text", pageContents1).build());
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns an list containing all pages of this book.
     */
    public List<NbtMap> getPages() {
        NbtMap tag = this.hasCompoundTag() ? this.getNamedTag() : NbtMap.EMPTY;
        List<NbtMap> pages;
        if (!tag.containsKey("pages") || !(tag.get("pages") instanceof List<?>)) {
            pages = new ObjectArrayList<>();
            tag = tag.toBuilder().putList("pages", NbtType.COMPOUND, pages).build();
            this.setNamedTag(tag);
        } else {
            pages = tag.getList("pages", NbtType.COMPOUND);
        }
        return pages;
    }

    protected static NbtMap createPageTag() {
        return createPageTag("");
    }

    protected static NbtMap createPageTag(String pageText) {
        return NbtMap.builder()
                .putString("text", pageText)
                .putString("photoname", "")
                .build();
    }
}
