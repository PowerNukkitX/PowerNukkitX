package cn.nukkit.metadata;

import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.plugin.Plugin;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class BlockMetadataStore extends MetadataStore {
    private final Level owningLevel;

    public BlockMetadataStore(Level owningLevel) {
        this.owningLevel = owningLevel;
    }

    @Override
    protected String disambiguate(Metadatable block, String metadataKey) {
        if (!(block instanceof Block b)) {
            throw new IllegalArgumentException("Argument must be a Block instance");
        }
        return b.getFloorX() + ":" + b.getFloorY() + ":" + b.getFloorZ() + ":" + metadataKey;
    }

    @Override
    public List<MetadataValue> getMetadata(Metadatable block, String metadataKey) {
        if (!(block instanceof Block b)) {
            throw new IllegalArgumentException("Object must be a Block");
        }
        if (b.getLevel() == this.owningLevel) {
            return super.getMetadata(block, metadataKey);
        } else {
            throw new IllegalStateException("Block does not belong to world " + this.owningLevel.getName());
        }
    }

    @Override
    public MetadataValue getMetadata(Metadatable block, String metadataKey, Plugin plugin) {
        if (!(block instanceof Block b)) {
            throw new IllegalArgumentException("Object must be a Block");
        }
        if (b.getLevel() == this.owningLevel) {
            return super.getMetadata(block, metadataKey, plugin);
        } else {
            throw new IllegalStateException("Block does not belong to world " + this.owningLevel.getName());
        }
    }

    @Override
    public boolean hasMetadata(Metadatable block, String metadataKey) {
        if (!(block instanceof Block b)) {
            throw new IllegalArgumentException("Object must be a Block");
        }
        if (b.getLevel() == this.owningLevel) {
            return super.hasMetadata(block, metadataKey);
        } else {
            throw new IllegalStateException("Block does not belong to world " + this.owningLevel.getName());
        }
    }

    @Override
    public boolean hasMetadata(Metadatable block, String metadataKey, Plugin plugin) {
        if (!(block instanceof Block b)) {
            throw new IllegalArgumentException("Object must be a Block");
        }
        if (b.getLevel() == this.owningLevel) {
            return super.hasMetadata(block, metadataKey, plugin);
        } else {
            throw new IllegalStateException("Block does not belong to world " + this.owningLevel.getName());
        }
    }

    @Override
    public void removeMetadata(Metadatable block, String metadataKey, Plugin owningPlugin) {
        if (!(block instanceof Block b)) {
            throw new IllegalArgumentException("Object must be a Block");
        }
        if (b.getLevel() == this.owningLevel) {
            super.removeMetadata(block, metadataKey, owningPlugin);
        } else {
            throw new IllegalStateException("Block does not belong to world " + this.owningLevel.getName());
        }
    }

    @Override
    public void setMetadata(Metadatable block, String metadataKey, MetadataValue newMetadataValue) {
        if (!(block instanceof Block b)) {
            throw new IllegalArgumentException("Object must be a Block");
        }
        if (b.getLevel() == this.owningLevel) {
            super.setMetadata(block, metadataKey, newMetadataValue);
        } else {
            throw new IllegalStateException("Block does not belong to world " + this.owningLevel.getName());
        }
    }

    public Map<String, Map<Plugin, MetadataValue>> getBlockMetadataMap() {
        return Collections.unmodifiableMap(metadataMap);
    }
}
