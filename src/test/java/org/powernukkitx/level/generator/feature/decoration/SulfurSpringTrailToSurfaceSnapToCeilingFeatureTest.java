package org.powernukkitx.level.generator.feature.decoration;

import org.cloudburstmc.protocol.bedrock.data.payload.structure.Rotation;
import org.junit.jupiter.api.Test;
import org.powernukkitx.utils.random.RandomSourceProvider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SulfurSpringTrailToSurfaceSnapToCeilingFeatureTest {

    @Test
    void selectsEachSupportedTemplateRotation() {
        RandomSourceProvider random = mock(RandomSourceProvider.class);
        Rotation[] expected = {
                Rotation.NONE,
                Rotation.ROTATE_90,
                Rotation.ROTATE_180,
                Rotation.ROTATE_270
        };

        for (int index = 0; index < expected.length; index++) {
            when(random.nextInt(expected.length)).thenReturn(index);
            assertEquals(expected[index], SulfurSpringTrailToSurfaceSnapToCeilingFeature.pickTemplateRotation(random));
        }
    }
}
