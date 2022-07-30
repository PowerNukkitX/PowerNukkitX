package cn.nukkit.nbt.tag;

import cn.nukkit.nbt.stream.NBTInputStream;
import cn.nukkit.nbt.stream.NBTOutputStream;

import java.io.IOException;
import java.util.Arrays;

public class StringArrayTag extends Tag{
    public String[] data;

    public StringArrayTag(String name) {
        super(name);
    }

    public StringArrayTag(String name, String[] data) {
        super(name);
        this.data = data;
    }

    @Override
    void write(NBTOutputStream dos) throws IOException {
        dos.writeInt(data.length);
        for (String aData : data) {
            dos.writeUTF(aData);
        }
    }

    @Override
    void load(NBTInputStream dis) throws IOException {
        int length = dis.readInt();
        data = new String[length];
        for (int i = 0; i < length; i++) {
            data[i] = dis.readUTF();
        }
    }

    public String[] getData() {
        return data;
    }

    @Override
    public String[] parseValue() {
        return this.data;
    }

    @Override
    public byte getId() {
        return TAG_String_Array;
    }

    @Override
    public String toString() {
        return "StringArrayTag " + this.getName() + " [" + data.length + " lengths]";
    }

    @Override
    public String toSnbt() {
        return "\"" + this.getName() + "\":" + Arrays.toString(data).replace("[", "[I;");
    }

    @Override
    public String toSnbt(int space) {
        return "\"" + this.getName() + "\": " + Arrays.toString(data).replace("[", "[I;");
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            StringArrayTag stringArrayTag = (StringArrayTag) obj;
            return ((data == null && stringArrayTag.data == null) || (data != null && Arrays.equals(data, stringArrayTag.data)));
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Arrays.hashCode(data);
        return result;
    }

    @Override
    public Tag copy() {
        String[] cp = new String[data.length];
        System.arraycopy(data, 0, cp, 0, data.length);
        return new StringArrayTag(getName(), cp);
    }
}
