package org.powernukkitx.level.poi;

import org.powernukkitx.block.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.Predicate;

/**
 * A point-of-interest category (job site block, bed, bell...).
 * <p>
 * A block state belongs to this type when its identifier is in {@link #blockIds()}
 * and it passes {@link #stateFilter()} (e.g. only bed foot pieces are indexed).
 * {@code maxTickets} is the number of entities that can claim a POI of this type
 * at the same time (1 for beds and job sites, more for meeting points).
 *
 * @param jobSiteBlockId the block id of the profession work station, or null if this type is not a job site
 */
public record PoiType(String name, Set<String> blockIds, Predicate<BlockState> stateFilter, int maxTickets,
                      @Nullable String jobSiteBlockId) {

    public boolean matches(BlockState state) {
        return blockIds.contains(state.getIdentifier()) && stateFilter.test(state);
    }

    public boolean mayMatch(String blockId) {
        return blockIds.contains(blockId);
    }

    public boolean isJobSite() {
        return jobSiteBlockId != null;
    }
}
