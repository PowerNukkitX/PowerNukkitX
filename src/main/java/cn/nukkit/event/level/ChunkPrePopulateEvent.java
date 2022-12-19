package cn.nukkit.event.level;

import cn.nukkit.api.ImmutableCollection;
import cn.nukkit.event.HandlerList;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.type.Populator;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class ChunkPrePopulateEvent extends ChunkEvent {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    @NotNull
    @ImmutableCollection
    private List<Populator> terrainPopulators;
    @NotNull
    @ImmutableCollection
    private List<Populator> biomePopulators;

    public ChunkPrePopulateEvent(FullChunk chunk, @NotNull List<Populator> terrainPopulators, @NotNull List<Populator> biomePopulators) {
        super(chunk);
        this.terrainPopulators = Collections.unmodifiableList(terrainPopulators);
        this.biomePopulators = Collections.unmodifiableList(biomePopulators);
    }

    @NotNull
    @ImmutableCollection
    public List<Populator> getTerrainPopulators() {
        return terrainPopulators;
    }

    public void setTerrainPopulators(@NotNull List<Populator> terrainPopulators) {
        this.terrainPopulators = terrainPopulators;
    }

    @NotNull
    @ImmutableCollection
    public List<Populator> getBiomePopulators() {
        return biomePopulators;
    }

    public void setBiomePopulators(@NotNull List<Populator> biomePopulators) {
        this.biomePopulators = biomePopulators;
    }
}
