package cn.nukkit.camera.data;

import lombok.Getter;

/**
 * @author daoge_cmd (PowerNukkitX Project)
 * @since 2023/6/10
 */


@Getter
public enum EaseType {
    LINEAR("linear"),
    SPRING("spring"),
    IN_SINE("in_sine"),
    OUT_SINE("out_sine"),
    IN_OUT_SINE("in_out_sine"),
    IN_QUAD("in_quad"),
    OUT_QUAD("out_quad"),
    IN_OUT_QUAD("in_out_quad"),
    IN_CUBIC("in_cubic"),
    OUT_CUBIC("out_cubic"),
    IN_OUT_CUBIC("in_out_cubic"),
    IN_QUART("in_quart"),
    OUT_QUART("out_quart"),
    IN_OUT_QUART("in_out_quart"),
    IN_QUINT("in_quint"),
    OUT_QUINT("out_quint"),
    IN_OUT_QUINT("in_out_quint"),
    IN_EXPO("in_expo"),
    OUT_EXPO("out_expo"),
    IN_OUT_EXPO("in_out_expo"),
    IN_CIRC("in_circ"),
    OUT_CIRC("out_circ"),
    IN_OUT_CIRC("in_out_circ"),
    IN_BACK("in_back"),
    OUT_BACK("out_back"),
    IN_OUT_BACK("in_out_back"),
    IN_ELASTIC("in_elastic"),
    OUT_ELASTIC("out_elastic"),
    IN_OUT_ELASTIC("in_out_elastic"),
    IN_BOUNCE("in_bounce"),
    OUT_BOUNCE("out_bounce"),
    IN_OUT_BOUNCE("in_out_bounce");

    private final String type;

    EaseType(String type) {
        this.type = type;
    }

    public String toString() {
        return "EaseType(type=" + this.type + ")";
    }
}
