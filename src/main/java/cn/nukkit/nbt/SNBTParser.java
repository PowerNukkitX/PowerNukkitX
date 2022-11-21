package cn.nukkit.nbt;

import cn.nukkit.nbt.tag.*;
import com.dfsek.terra.lib.commons.lang3.StringUtils;

public class SNBTParser {
    private String SNBT;
    private int index = 0;
    private int nestLimit = 50;
    private int compoundNestNum = 0;
    private int listNestNum = 0;

    SNBTParser() {
        this.SNBT = null;
    }

    SNBTParser(String SNBT) {
        this.SNBT = preProcessing(SNBT);
    }

    public static CompoundTag parseSNBT(String SNBT) {
        SNBTParser parser = new SNBTParser(SNBT);
        return parser.deserializationSNBT();
    }

    public CompoundTag deserializationSNBT() {
        String key = getKey();
        return parsingCompound(key);
    }

    public SNBTParser setNestLimit(int nestLimit) {
        this.nestLimit = nestLimit;
        return this;
    }

    public static SNBTParser build() {
        return new SNBTParser();
    }

    public SNBTParser setSNBT(String SNBT) {
        this.reset();
        this.SNBT = preProcessing(SNBT);
        return this;
    }

    private void reset() {
        index = 0;
        nestLimit = 50;
        compoundNestNum = 0;
        listNestNum = 0;
    }

    private String preProcessing(String SNBT) {
        return SNBT.trim();
    }

    private CompoundTag parsingCompound(String compoundKey) {
        if (compoundKey == null) compoundKey = "";
        var result = new CompoundTag(compoundKey);
        compoundNestNum++;
        //每次循环读取一个key:value;
        do {
            if (SNBT.charAt(index) == '}') {
                move(1);
                break;
            }
            //index移动到'"'才能调用使用getKey
            if (SNBT.charAt(index) != '"') {
                move(1);
            } else {
                String key = getKey();
                if (key == null) result.putCompound("", parsingCompound(""));
                //获取':'后第一个字符
                char type = peek(1);
                while (type == ' ' || type == '\n') {
                    type = peek(1);
                }
                eval(type);
                //判断value类型
                if (type == '{') result.putCompound(key, parsingCompound(key));
                else if (type == '[') {//只字节数组或者List,List中元素类型必须相同
                    if (SNBT.charAt(index + 1) == 'B') {
                        result.putByteArray(key, parseByteArray(type));
                    } else {
                        move(1);
                        result.putList(parsingList(key));
                    }
                } else if (type >= '0' && type <= '9' || type == '-') {
                    StringBuilder str = new StringBuilder();
                    while (SNBT.charAt(index) != ',' && SNBT.charAt(index) != '}') {
                        str.append(SNBT.charAt(index));
                        move(1);//index直到','或者'}'
                    }
                    result.put(key, getNumber(key, str.toString().replace(" ", "").replace("\n", "")));
                } else if (type == '\"') {
                    int start = index + 1;
                    int end = SNBT.indexOf("\"", start);
                    var value = SNBT.substring(start, end);
                    result.put(key, new StringTag(key, value));
                    move(end - index + 1);//index前往'"'后一个位置
                } else if (type == ',' || type == '}') {
                    result.put(key, new EndTag());
                }
            }
        } while (index <= SNBT.length());
        compoundNestNum--;
        return result;
    }

    private ListTag<Tag> parsingList(String listKey) {
        ListTag<Tag> result = new ListTag<>(listKey);
        listNestNum++;
        //每次循环读取一个key:value;
        do {
            char type = SNBT.charAt(index);
            while (type == ' ' || type == '\n') {
                type = peek(1);
            }
            if (type == ']') break;
            eval(type);
            //判断value类型
            if (type == '{') {
                result.add(parsingCompound(""));
            } else if (type == '[') {//只字节数组或者List,List中元素类型必须相同
                move(1);
                var tmp = parsingList("");
                if (tmp.size() == 1 && tmp.get(0) instanceof ByteArrayTag) {
                    result.add(tmp.get(0));
                } else result.add(tmp);
            } else if (type == 'B') {
                result.add(new ByteArrayTag("", parseByteArray(type)));
            } else if (type >= '0' && type <= '9' || type == '-') {
                parseNumberArray(result, type);
            } else if (type == '\"') {
                parseStringArray(result, type);
            } else if (type == ',') {
                parseNullArray(result, type);
            }
            if (SNBT.charAt(index) == ',') move(1);
        } while (index <= SNBT.length());
        move(1);
        listNestNum--;
        return result;
    }

    private byte[] parseByteArray(char type) {
        int start = index + 3;
        int end = SNBT.indexOf("]", start);
        var value = SNBT
                .substring(start, end)
                .replace(" ", "")
                .replace("\n", "")
                .replace("b", "")
                .split(",");
        var byteArray = new byte[value.length];
        for (int i = 0; i < value.length; ++i) {
            byteArray[i] = Byte.parseByte(value[i]);
        }
        move(end - index);//index前往']'后一个位置
        return byteArray;
    }

    private void parseNumberArray(ListTag<Tag> result, char type) {
        int start = index;
        int end = SNBT.indexOf("]", start);
        var value = SNBT
                .substring(start, end)
                .replace(" ", "")
                .replace("\n", "")
                .split(",");
        for (var tag : value) {
            result.add(getNumber("", tag));
        }
        move(end - start);
    }

    private void parseStringArray(ListTag<Tag> result, char type) {
        int start = index;
        int end = SNBT.indexOf("]", start);
        var value = SNBT
                .substring(start, end)
                .split("\",");
        for (var tag : value) {
            var str = StringUtils.removeEnd(tag.trim(), "\"");
            str = StringUtils.removeStart(str, "\"");
            result.add(new StringTag("", str));
        }
        move(end - start);
    }

    private void parseNullArray(ListTag<Tag> result, char type) {
        int start = index;
        int end = SNBT.indexOf("]", start);
        if (type == ']') {
            result.add(new EndTag());
        } else {
            var len = SNBT.substring(start, end).replace(" ", "").replace("\n", "").length() + 1;
            for (int i = 0; i < len; ++i) {
                result.add(new EndTag());
            }
        }
        move(end - start);
    }

    private void move(int num) {
        if (index + num > SNBT.length()) throw new IndexOutOfBoundsException("snbt索引越界");
        index += num;
    }

    private char peek(int num) {
        if (index + num > SNBT.length()) throw new IndexOutOfBoundsException("snbt索引越界");
        index += num;
        return SNBT.charAt(index);
    }

    private String getKey() {
        if (SNBT.charAt(index) == '\"') {
            int start = index + 1;
            int end = SNBT.indexOf("\"", start);
            //index进入':'
            if (peek(end - index + 1) != ':') throw new IllegalArgumentException("snbt格式错误");
            return SNBT.substring(start, end);
        } else if (SNBT.charAt(index) == '{') {//为'{'意味着当前是无key Compound
            move(1);
            return null;
        }
        return null;
    }

    private Tag getNumber(String key, String value) {
        if (value.indexOf('L') != -1) {
            long num = Long.parseLong(value.replace("L", ""));
            return new LongTag(key, num);
        } else if (value.indexOf('s') != -1) {
            short num = Short.parseShort(value.replace("s", ""));
            return new ShortTag(key, num);
        } else if (value.indexOf('b') != -1) {
            byte num = Byte.parseByte(value.replace("b", ""));
            return new ByteTag(key, num);
        } else if (value.indexOf('d') != -1) {
            double num = Double.parseDouble(value.replace("d", ""));
            return new DoubleTag(key, num);
        } else if (value.indexOf('f') != -1) {
            float num = Float.parseFloat(value.replace("f", ""));
            return new FloatTag(key, num);
        } else if (value.indexOf('i') != -1) {
            int num = Integer.parseInt(value.replace("i", ""));
            return new IntTag(key, num);
        } else {
            int num = Integer.parseInt(value);
            return new IntTag(key, num);
        }
    }

    private void eval(char type) {
        if (!((type >= '0' && type <= '9') || type == ',' || type == '[' || type == '{'
                || type == ']' || type == '\"' || type == '}' || type == '-' || type == 'B')) {
            throw new IllegalArgumentException("snbt格式错误");
        }
        if (compoundNestNum > nestLimit || listNestNum > nestLimit) {
            throw new StackOverflowError("NBT嵌套层数过深，大于限制层数" + nestLimit);
        }
    }
}

