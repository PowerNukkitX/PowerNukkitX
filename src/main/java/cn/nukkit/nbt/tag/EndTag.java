package cn.nukkit.nbt.tag;

public class EndTag extends Tag {

    public EndTag() {
        super();
    }

    @Override
    public byte getId() {
        return TAG_End;
    }

    @Override
    public String toString() {
        return "EndTag";
    }

    @Override
    public String toSNBT() {
        return "";
    }

    @Override
    public String toSNBT(int space) {
        return "";
    }

    @Override
    public Tag copy() {
        return new EndTag();
    }

    @Override
    public Void parseValue() {
        return null;
    }
}
