package cn.nukkit.entity.ai.memory;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.math.Vector3;
import lombok.Getter;

/**
 * MoveDestinationMemory用于存储实体的寻路过程中的某一时刻的目标移动方向
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
@Getter
public class MoveDirectionMemory implements IMemory<Vector3> {

    protected Vector3 start;
    protected Vector3 end;
    protected Vector3 direction;
    /**
     *
     * @param start 方向向量起点
     * @param end 方向向量终点
     */
    public MoveDirectionMemory(Vector3 start, Vector3 end){
        this.start = start;
        this.end = end;
        //构建方向向量
        this.direction = new Vector3(end.x - start.x, end.y - start.y, end.z - start.z);
    }

    /**
     * 返回方向向量
     */
    @Override
    public Vector3 getData() {
        return direction;
    }
}
