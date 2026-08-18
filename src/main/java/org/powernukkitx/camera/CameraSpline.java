package org.powernukkitx.camera;

import com.google.common.base.Preconditions;
import org.cloudburstmc.protocol.bedrock.data.camera.CameraSplineType;
import org.powernukkitx.math.Vector3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Base class for a camera spline.
 *
 * @author Curse
 */
public abstract class CameraSpline {
    private final CameraSplineType type;
    private final int minimumControlPoints;
    private final List<Vector3> controlPoints = new ArrayList<>();

    CameraSpline(CameraSplineType type, int minimumControlPoints) {
        this.type = Objects.requireNonNull(type, "type");
        this.minimumControlPoints = minimumControlPoints;
    }

    /**
     * Returns the spline control points.
     *
     * @return immutable view of the control points
     */
    public List<Vector3> getControlPoints() {
        return Collections.unmodifiableList(this.controlPoints);
    }

    /**
     * Replaces all spline control points.
     *
     * @param controlPoints control points
     */
    public void setControlPoints(Collection<? extends Vector3> controlPoints) {
        List<? extends Vector3> copy = new ArrayList<>(
                Objects.requireNonNull(controlPoints, "controlPoints")
        );

        this.controlPoints.clear();

        for (Vector3 controlPoint : copy) {
            this.addControlPoint(controlPoint);
        }
    }

    /**
     * Adds a control point to the spline.
     *
     * @param controlPoint control point
     */
    public void addControlPoint(Vector3 controlPoint) {
        this.controlPoints.add(
                Objects.requireNonNull(controlPoint, "controlPoint")
        );
    }

    CameraSplineType getType() {
        return this.type;
    }

    void validate() {
        Preconditions.checkArgument(
                this.controlPoints.size() >= this.minimumControlPoints,
                "%s requires at least %s control points",
                this.getClass().getSimpleName(),
                this.minimumControlPoints
        );
    }
}
