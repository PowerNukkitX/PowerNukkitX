package org.powernukkitx.entity.ai.route.finder.impl;

import org.jetbrains.annotations.NotNull;
import org.powernukkitx.entity.passive.EntityParrot;
import org.powernukkitx.entity.ai.route.posevaluator.ParrotPosEvaluator;
import org.powernukkitx.entity.ai.route.data.Node;
import org.powernukkitx.math.Vector3;

/** Checks flight shortcuts at the parrot's feet rather than at the block below it. */
public class ParrotRouteFinder extends SimpleSpaceAStarRouteFinder {
    private Vector3 neighborOrigin;

    /**
     * Creates a route finder restricted to safe positions in loaded chunks.
     * @param parrot the bird whose collision box is checked
     */
    public ParrotRouteFinder(@NotNull EntityParrot parrot) {
        super(new ParrotPosEvaluator(), parrot);
    }

    @Override
    protected void putNeighborNodeIntoOpen(@NotNull Node node) {
        Vector3 origin = node.getVector3();
        neighborOrigin = evalPos(origin) ? origin : null;
        try {
            super.putNeighborNodeIntoOpen(node);
        } finally {
            neighborOrigin = null;
        }
    }

    @Override
    protected boolean evalGridPos(Vector3 position) {
        return super.evalGridPos(position) && (neighborOrigin == null || !hasBarrier(neighborOrigin, position));
    }

    @Override
    protected boolean hasBarrier(Vector3 from, Vector3 to) {
        double dx = to.x - from.x;
        double dy = to.y - from.y;
        double dz = to.z - from.z;
        int steps = Math.max(1, (int) Math.ceil(Math.sqrt(dx * dx + dy * dy + dz * dz) * 4));
        for (int i = 0; i <= steps; i++) {
            double fraction = (double) i / steps;
            if (!evalPos(new Vector3(from.x + dx * fraction, from.y + dy * fraction, from.z + dz * fraction))) {
                return true;
            }
        }
        return false;
    }
}
