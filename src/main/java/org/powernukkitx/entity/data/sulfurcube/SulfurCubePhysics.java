package org.powernukkitx.entity.data.sulfurcube;

import org.powernukkitx.Player;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.math.Vector3;

import lombok.RequiredArgsConstructor;

import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class SulfurCubePhysics {

    private static final double MAX_HORIZONTAL_SPEED = 1.5;
    private static final double REST_THRESHOLD = 0.003;
    private static final double PUSH_RANGE = 1.3;
    private static final double EPSILON = 1e-4;

    private final Entity cube;

    public void damp(@NotNull SulfurCubeArchetype archetype) {
        double speed = Math.sqrt(cube.motionX * cube.motionX + cube.motionZ * cube.motionZ);
        if (speed > MAX_HORIZONTAL_SPEED) {
            double clamp = MAX_HORIZONTAL_SPEED / speed;
            cube.motionX *= clamp;
            cube.motionZ *= clamp;
            speed = MAX_HORIZONTAL_SPEED;
        }

        if (speed > REST_THRESHOLD) {
            if (!cube.onGround) {
                cube.motionX *= archetype.getAirKeepPerTick();
                cube.motionZ *= archetype.getAirKeepPerTick();
            }
        } else if (cube.onGround) {
            cube.motionX = 0;
            cube.motionZ = 0;
        }
    }

    /**
     * Kicks the cube away from {@code striker}. Where the hit lands on the cube decides how much of the
     * blow turns into lift instead of forward travel, and how far the cube veers off the striker's aim.
     */
    public void launch(@NotNull SulfurCubeArchetype archetype, @NotNull Entity striker, float damage) {
        double dirX = striker.x - cube.x;
        double dirZ = striker.z - cube.z;
        if (Math.sqrt(dirX * dirX + dirZ * dirZ) < EPSILON) return;

        Vector3 eye = new Vector3(striker.x, striker.y + striker.getEyeHeight(), striker.z);
        Vector3 look = striker.getDirectionVector();
        Vector3 center = new Vector3(cube.x,
                (cube.getBoundingBox().getMinY() + cube.getBoundingBox().getMaxY()) / 2, cube.z);

        Vector3 curved = curveAroundAim(eye, look, center, dirX, dirZ);
        dirX = curved.x;
        dirZ = curved.z;

        double lift = verticalBias(eye, look, center);
        double horizontal = archetype.getHorizontalPower() * (1 - lift);
        double vertical = archetype.getVerticalPower() * (1 + lift);

        // aiming down at a cube below you drives it along the ground, aiming up pops it into the air
        double positionAngle = Math.atan2(cube.y - striker.y,
                Math.sqrt(Math.pow(cube.x - striker.x, 2) + Math.pow(cube.z - striker.z, 2))) * 0.8;
        double cos = Math.cos(positionAngle);
        double sin = Math.sin(positionAngle);
        double rotatedHorizontal = horizontal * cos - vertical * sin;
        double rotatedVertical = horizontal * sin + vertical * cos;

        double scale = Math.sqrt(Math.max(0.5f, damage)) * archetype.getKnockbackFactor();
        horizontal = rotatedHorizontal * scale * 0.4;
        vertical = rotatedVertical * scale;

        double length = Math.sqrt(dirX * dirX + dirZ * dirZ);
        if (length < EPSILON) return;

        cube.setMotion(new Vector3(
                cube.motionX - dirX / length * horizontal,
                cube.motionY + Math.max(-0.5, Math.min(1.2, vertical * 1.2)),
                cube.motionZ - dirZ / length * horizontal
        ));
    }

    /**
     * Rolls the cube along when a player walks into it, scaled by how fast the player is moving.
     */
    public void shoveFromPlayers(@NotNull SulfurCubeArchetype archetype) {
        double knockbackFactor = archetype.getKnockbackFactor();
        if (knockbackFactor <= 0) return;

        double cubeTop = cube.y + cube.getHeight() * cube.getScale();
        for (Entity entity : cube.level.getNearbyEntitiesSafe(
                cube.getBoundingBox().grow(PUSH_RANGE, 0.1, PUSH_RANGE), cube)) {
            if (!(entity instanceof Player player) || !player.isAlive() || player.isSpectator()) continue;

            double dx = cube.x - player.x;
            double dz = cube.z - player.z;
            double distance = Math.sqrt(dx * dx + dz * dz);
            if (distance >= PUSH_RANGE || distance < EPSILON) continue;
            if (player.y > cubeTop || player.y + player.getHeight() <= cube.y) continue;

            double movedX = player.x - player.lastX;
            double movedY = player.y - player.lastY;
            double movedZ = player.z - player.lastZ;
            double moved = Math.sqrt(movedX * movedX + movedY * movedY + movedZ * movedZ);
            double push = Math.max(0, Math.min(0.5, moved * 2 * 0.3));
            if (push < 1e-3) continue;

            cube.setMotion(new Vector3(
                    cube.motionX + dx / distance * knockbackFactor * push,
                    cube.motionY + (cube.onGround ? knockbackFactor * 0.3 * push : 0),
                    cube.motionZ + dz / distance * knockbackFactor * push
            ));
        }
    }

    /**
     * Bends the launch direction away from where the striker was aiming, so glancing hits send the
     * cube off to the side rather than straight back.
     */
    private Vector3 curveAroundAim(Vector3 eye, Vector3 look, Vector3 center, double dirX, double dirZ) {
        double toCubeX = center.x - eye.x;
        double toCubeZ = center.z - eye.z;
        double toCubeLength = Math.sqrt(toCubeX * toCubeX + toCubeZ * toCubeZ);
        double lookLength = Math.sqrt(look.x * look.x + look.z * look.z);
        if (toCubeLength <= EPSILON || lookLength <= EPSILON) return new Vector3(dirX, 0, dirZ);

        toCubeX /= toCubeLength;
        toCubeZ /= toCubeLength;
        double lookX = look.x / lookLength;
        double lookZ = look.z / lookLength;

        double rotation = Math.atan2(lookX * toCubeZ - lookZ * toCubeX, lookX * toCubeX + lookZ * toCubeZ) * 1.6;
        double cos = Math.cos(rotation);
        double sin = Math.sin(rotation);
        return new Vector3(dirX * cos - dirZ * sin, 0, dirX * sin + dirZ * cos);
    }

    /**
     * Where the striker's aim crosses the cube, as -1 at the top face through +1 at the bottom face.
     * Hitting low lifts the cube, hitting high drives it into the ground.
     */
    private double verticalBias(Vector3 eye, Vector3 look, Vector3 center) {
        double halfHeight = cube.getHeight() * cube.getScale() / 2;
        Vector3 toTop = center.add(0, halfHeight, 0).subtract(eye.x, eye.y, eye.z).normalize();
        Vector3 toBottom = center.add(0, -halfHeight, 0).subtract(eye.x, eye.y, eye.z).normalize();
        double range = toBottom.y - toTop.y;
        if (Math.abs(range) < 1e-6) return 0;

        double crossing = Math.max(0, Math.min(1, (look.y - toTop.y) / range));
        return (-1 + crossing * 2) * 0.5;
    }
}
