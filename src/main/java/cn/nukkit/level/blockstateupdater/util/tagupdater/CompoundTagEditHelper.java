package cn.nukkit.level.blockstateupdater.util.tagupdater;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

import static java.util.Objects.requireNonNull;

@SuppressWarnings("unchecked")
public class CompoundTagEditHelper {
    private final Deque<Object> parentTag = new ArrayDeque<>();
    private final Deque<String> tagName = new ArrayDeque<>();
    private Map<String, Object> rootTag;
    private Object tag;

    public CompoundTagEditHelper(Map<String, Object> tag) {
        this.rootTag = tag;
        this.tag = tag;
    }

    public Map<String, Object> getRootTag() {
        return rootTag;
    }

    public Map<String, Object> getCompoundTag() {
        return (Map<String, Object>) tag;
    }

    public Object getTag() {
        return tag;
    }

    public Map<String, Object> getParent() {
        if (!this.parentTag.isEmpty()) {
            Object tag = this.parentTag.peekLast();
            if (tag instanceof Map) {
                return (Map<String, Object>) tag;
            }
        }
        return null;
    }

    public boolean canPopChild() {
        return !this.parentTag.isEmpty();
    }

    public void popChild() {
        if (!this.parentTag.isEmpty()) {
            this.tag = this.parentTag.pollLast();
            this.tagName.pollLast();
        }
    }

    public void pushChild(String name) {
        requireNonNull(name, "name");
        if (!(tag instanceof Map)) throw new IllegalStateException("Tag is not of Compound type");
        this.parentTag.addLast(this.tag);
        this.tagName.addLast(name);
        this.tag = ((Map<String, Object>) this.tag).get(name);
    }

    public void replaceWith(String name, Object newTag) {
        this.tag = newTag;
        if (this.parentTag.isEmpty()) {
            this.rootTag = ((Map<String, Object>) tag);
            return;
        }
        Object tag = this.parentTag.getLast();
        if (tag instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) tag;
            map.remove(this.tagName.pollLast());
            map.put(name, newTag);
            this.tagName.offerLast(name);
        }
    }
}
