package cn.nukkit.level.generator.object;

import cn.nukkit.level.generator.object.legacytree.LegacyTreeGenerator;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.random.RandomSourceProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ObjectLegacyObjectWrapper extends TreeGenerator {

    private LegacyTreeGenerator legacyTreeGenerator;

    @Override
    public boolean generate(BlockManager level, RandomSourceProvider rand, Vector3 position) {
        if(legacyTreeGenerator != null) {
            legacyTreeGenerator.placeObject(level, position.getFloorX(), position.getFloorY(), position.getFloorZ(), rand);
            return true;
        }
        return false;
    }

}