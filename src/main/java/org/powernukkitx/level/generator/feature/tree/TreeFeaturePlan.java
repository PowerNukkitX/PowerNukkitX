package org.powernukkitx.level.generator.feature.tree;

import org.powernukkitx.level.generator.feature.placement.PlacementFeature;
import org.powernukkitx.level.generator.feature.placement.PlacementPredicate;
import org.powernukkitx.level.generator.feature.placement.vegetation.TallGrassAroundTreePlacementFeature;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.Nullable;

/**
 * Compiled execution plan for one chunk-level tree rule.
 *
 * @author Curse
 */
public final class TreeFeaturePlan {
    private final TreeCountPolicy countPolicy;
    private final TreeCandidatePolicy candidatePolicy;
    private final PlacementPredicate candidatePredicate;
    private final TreeFeatureSelector selector;
    private final PlacementFeature postAttempt;

    /**
     * Creates a tree feature plan.
     */
    public TreeFeaturePlan(
            TreeCountPolicy countPolicy,
            TreeCandidatePolicy candidatePolicy,
            PlacementPredicate candidatePredicate,
            TreeFeatureSelector selector,
            @Nullable PlacementFeature postAttempt
    ) {
        this.countPolicy = Preconditions.checkNotNull(countPolicy, "countPolicy");
        this.candidatePolicy = Preconditions.checkNotNull(candidatePolicy, "candidatePolicy");
        this.candidatePredicate = Preconditions.checkNotNull(candidatePredicate, "candidatePredicate");
        this.selector = Preconditions.checkNotNull(selector, "selector");
        this.postAttempt = postAttempt;
    }

    /**
     * Creates a vanilla LegacyTreeFeature plan with its common local tall-grass child.
     */
    public static TreeFeaturePlan legacy(
            float amount,
            PlacementPredicate candidatePredicate,
            TreeFeatureSelector selector
    ) {
        return new TreeFeaturePlan(
                TreeCountPolicy.legacy(amount),
                TreeCandidatePolicy.legacySurface(),
                candidatePredicate,
                selector,
                TallGrassAroundTreePlacementFeature.scatter()
        );
    }

    /**
     * Creates a vanilla legacy tree plan with a post-attempt feature.
     */
    public static TreeFeaturePlan legacy(
            float amount,
            PlacementPredicate candidatePredicate,
            TreeFeatureSelector selector,
            PlacementFeature postAttempt
    ) {
        return new TreeFeaturePlan(
                TreeCountPolicy.legacy(amount),
                TreeCandidatePolicy.legacySurface(),
                candidatePredicate,
                selector,
                Preconditions.checkNotNull(postAttempt, "postAttempt")
        );
    }

    /**
     * Returns the attempt-count policy.
     */
    public TreeCountPolicy countPolicy() {
        return this.countPolicy;
    }

    /**
     * Returns the candidate-position policy.
     */
    public TreeCandidatePolicy candidatePolicy() {
        return this.candidatePolicy;
    }

    /**
     * Returns the candidate filter.
     */
    public PlacementPredicate candidatePredicate() {
        return this.candidatePredicate;
    }

    /**
     * Returns the primary tree selector.
     */
    public TreeFeatureSelector selector() {
        return this.selector;
    }

    /**
     * Returns the post-attempt feature, if configured.
     */
    @Nullable
    public PlacementFeature postAttempt() {
        return this.postAttempt;
    }
}
