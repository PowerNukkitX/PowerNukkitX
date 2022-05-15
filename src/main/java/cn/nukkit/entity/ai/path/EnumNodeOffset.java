package cn.nukkit.entity.ai.path;

/**
 * 每个寻路点都以其坐标地板整数值作为记录的xyz，外加此{@link EnumNodeOffset}记录相对于上述xyz的偏移量。
 * 此枚举中，若枚举量名称中含有x,y,z中的某个字母，说明就像这个字母代表的坐标轴正方向偏移0.5。
 * 对应到二进制中，若右起第一位为1，则x轴向正方向偏移0.5；若右起第二位为1，则y轴向正方向偏移0.5；若右起第三位为1，则z轴向正方向偏移0.5。
 */
public enum EnumNodeOffset {
    NONE, // 000
    X, // 001
    Y, // 010
    XY, // 011
    Z, // 100
    XZ, // 101
    YZ, // 110
    XYZ; // 111

    public static EnumNodeOffset fromBinary(int bin) {
        return switch (bin) {
            case 1 -> X;
            case 2 -> Y;
            case 3 -> XY;
            case 4 -> Z;
            case 5 -> XZ;
            case 6 -> YZ;
            case 7 -> XYZ;
            default -> NONE;
        };
    }
}
