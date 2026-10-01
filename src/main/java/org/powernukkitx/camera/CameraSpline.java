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
        Objects.requireNonNull(controlPoint, "controlPoint");

        Preconditions.checkArgument(
                Double.isFinite(controlPoint.x)
                        && Double.isFinite(controlPoint.y)
                        && Double.isFinite(controlPoint.z),
                "Camera control point coordinates must be finite"
        );

        this.controlPoints.add(controlPoint);
    }

    CameraSplineType getType() {
        return this.type;
    }

    /**
     * Returns the approximate normalized progress of a control point along this spline.
     * <p>
     * Progress is calculated from the cumulative straight-line distance between
     * control points and normalized to the range {@code [0, 1]}.
     * </p>
     *
     * @param controlPointIndex control point index
     * @return approximate normalized spline progress
     */
    public float getApproximateProgressAtControlPoint(int controlPointIndex) {
        Preconditions.checkElementIndex(
                controlPointIndex,
                this.controlPoints.size(),
                "controlPointIndex"
        );

        if (controlPointIndex == 0) {
            return 0.0f;
        }

        double totalDistance = 0.0d;
        double targetDistance = 0.0d;

        for (int i = 1; i < this.controlPoints.size(); i++) {
            Vector3 previous = this.controlPoints.get(i - 1);
            Vector3 current = this.controlPoints.get(i);

            double deltaX = current.x - previous.x;
            double deltaY = current.y - previous.y;
            double deltaZ = current.z - previous.z;

            double distance = Math.sqrt(
                    deltaX * deltaX +
                    deltaY * deltaY +
                    deltaZ * deltaZ
            );

            totalDistance += distance;

            if (i <= controlPointIndex) {
                targetDistance += distance;
            }
        }

        Preconditions.checkState(
                totalDistance > 0.0d,
                "Camera spline requires a non-zero path distance"
        );

        return (float) (
                targetDistance /
                totalDistance
        );
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
