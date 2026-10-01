package org.powernukkitx.camera;

import org.cloudburstmc.protocol.bedrock.data.camera.CameraSplineType;

/**
 * Smooth Catmull-Rom camera spline.
 *
 * @author Curse
 */
public final class CatmullRomSpline extends CameraSpline {
    public CatmullRomSpline() {
        super(CameraSplineType.CATMULL_ROM, 4);
    }
}
