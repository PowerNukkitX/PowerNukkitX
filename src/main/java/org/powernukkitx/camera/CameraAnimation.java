package org.powernukkitx.camera;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Defines progress and rotation key frames for a camera spline animation.
 *
 * @author Curse
 */
public final class CameraAnimation {
    private final List<CameraProgressKeyFrame> progressKeyFrames = new ArrayList<>();
    private final List<CameraRotationKeyFrame> rotationKeyFrames = new ArrayList<>();

    public List<CameraProgressKeyFrame> getProgressKeyFrames() {
        return Collections.unmodifiableList(this.progressKeyFrames);
    }

    public void setProgressKeyFrames(Collection<? extends CameraProgressKeyFrame> keyFrames) {
        List<? extends CameraProgressKeyFrame> copy = new ArrayList<>(
                Objects.requireNonNull(keyFrames, "keyFrames")
        );

        this.progressKeyFrames.clear();

        for (CameraProgressKeyFrame keyFrame : copy) {
            this.addProgressKeyFrame(keyFrame);
        }
    }

    public CameraAnimation addProgressKeyFrame(CameraProgressKeyFrame keyFrame) {
        this.progressKeyFrames.add(
                Objects.requireNonNull(keyFrame, "keyFrame")
        );

        return this;
    }

    public List<CameraRotationKeyFrame> getRotationKeyFrames() {
        return Collections.unmodifiableList(this.rotationKeyFrames);
    }

    public void setRotationKeyFrames(Collection<? extends CameraRotationKeyFrame> keyFrames) {
        List<? extends CameraRotationKeyFrame> copy = new ArrayList<>(
                Objects.requireNonNull(keyFrames, "keyFrames")
        );

        this.rotationKeyFrames.clear();

        for (CameraRotationKeyFrame keyFrame : copy) {
            this.addRotationKeyFrame(keyFrame);
        }
    }

    public CameraAnimation addRotationKeyFrame(CameraRotationKeyFrame keyFrame) {
        this.rotationKeyFrames.add(
                Objects.requireNonNull(keyFrame, "keyFrame")
        );

        return this;
    }
}
