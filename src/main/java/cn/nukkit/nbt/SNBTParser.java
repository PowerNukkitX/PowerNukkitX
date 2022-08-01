package cn.nukkit.nbt;

import cn.nukkit.nbt.tag.*;
import lombok.NonNull;

public class SNBTParser {
    private final String snbt;
    private int index = 0;

    SNBTParser(@NonNull String snbt) {
        this.snbt = snbt.replace(" ", "").replace("\n", "");
    }

    public CompoundTag deserializationSNBT() {
        String key = getKey();
        return parsingCompound(key);
    }

    private CompoundTag parsingCompound(String compoundKey) {
        if (compoundKey == null) compoundKey = "";
        var result = new CompoundTag(compoundKey);
        //每次循环读取一个key:value;
        do {
            if (snbt.charAt(index) == '}') {
                next(1);
                break;
            }
            //index移动到'"'才能调用使用getKey
            if (snbt.charAt(index) != '"') {
                next(1);
                continue;
            }
            //使用后index到达':',或者进入无key Compound递归
            String key = getKey();
            if (key == null) result.putCompound("", parsingCompound(""));
            //获取':'后第一个字符
            char type = peek(1);
            eval(type);
            //判断value类型
            if (type == '{') result.putCompound(key, parsingCompound(key));
            else if (type == '[') {//只字节数组或者List,List中元素类型必须相同
                if (snbt.charAt(index + 1) == 'B') {
                    int start = index + 3;
                    int end = snbt.indexOf("]", start);
                    var value = snbt
                            .substring(start, end)
                            .replace("b", "")
                            .split(",");
                    var byteArray = new byte[value.length];
                    for (int i = 0; i < value.length; ++i) {
                        byteArray[i] = Byte.parseByte(value[i]);
                    }
                    result.putByteArray(key, byteArray);
                    next(end - index + 1);//index前往']'后一个位置
                } else {
                    result.putList(parsingList(key));
                }
            } else if (type >= '0' && type <= '9' || type == '-') {
                StringBuilder str = new StringBuilder();
                while (snbt.charAt(index) != ',' && snbt.charAt(index) != '}') {
                    str.append(snbt.charAt(index));
                    next(1);//index直到','或者'}'
                }
                result.put(key, getNumber(key, str.toString()));
            } else if (type == '\"') {
                int start = index + 1;
                int end = snbt.indexOf("\"", start);
                var value = snbt.substring(start, end);
                result.put(key, new StringTag(key, value));
                next(end - index + 1);//index前往'"'后一个位置
            } else if (type == ',' || type == '}') {
                result.put(key, new EndTag());
            }
        } while (index <= snbt.length());
        return result;
    }

    private ListTag<Tag> parsingList(String listKey) {
        ListTag<Tag> result = new ListTag<>(listKey);
        //每次循环读取一个key:value;
        do {
            char charAtIndex = snbt.charAt(index);
            if (charAtIndex == ']') break;
            //获取'[' ',' 后第一个字符
            char type = charAtIndex;
            if (charAtIndex == ',' || charAtIndex == '[') type = peek(1);
            eval(type);
            //判断value类型
            if (type == '{') {
                result.add(parsingCompound(""));
            } else if (type == '[') {//只字节数组或者List,List中元素类型必须相同
                if (snbt.charAt(index + 1) == 'B') {
                    int start = index + 3;
                    int end = snbt.indexOf("]", start);
                    var value = snbt
                            .substring(start, end)
                            .replace("b", "")
                            .split(",");
                    var byteArray = new byte[value.length];
                    for (int i = 0; i < value.length; ++i) {
                        byteArray[i] = Byte.parseByte(value[i]);
                    }
                    result.add(new ByteArrayTag("", byteArray));
                    next(end - index + 1);//index前往']'后一个位置
                } else {
                    result.add(parsingList(""));
                    next(1);//index到达','或者']'
                }
            } else if (type >= '0' && type <= '9' || type == '-') {
                int start = index;
                int end = snbt.indexOf("]", start);
                var value = snbt
                        .substring(start, end)
                        .split(",");
                for (var tag : value) {
                    result.add(getNumber("", tag));
                }
                next(end - start);
            } else if (type == '\"') {
                int start = index;
                int end = snbt.indexOf("]", start);
                var value = snbt
                        .substring(start, end)
                        .split(",");
                for (var tag : value) {
                    result.add(new StringTag("", tag.replace("\"", "")));
                }
                next(end - start);
            } else if (type == ',' || type == ']') {
                int start = index;
                int end = snbt.indexOf("]", start);
                if (type == ']') {
                    result.add(new EndTag());
                } else {
                    var len = snbt.substring(start, end).length() + 1;
                    for (int i = 0; i < len; ++i) {
                        result.add(new EndTag());
                    }
                }
                next(end - start);
            }
        } while (index <= snbt.length());
        return result;
    }

    private void next(int num) {
        if (index + num > snbt.length()) throw new IndexOutOfBoundsException("snbt索引越界");
        index += num;
    }

    private char peek(int num) {
        if (index + num > snbt.length()) throw new IndexOutOfBoundsException("snbt索引越界");
        index += num;
        return snbt.charAt(index);
    }

    private String getKey() {
        if (snbt.charAt(index) == '\"') {
            int start = index + 1;
            int end = snbt.indexOf("\"", start);
            //index进入':'
            if (peek(end - index + 1) != ':') throw new IllegalArgumentException("snbt格式错误");
            return snbt.substring(start, end);
        } else if (snbt.charAt(index) == '{') {//为'{'意味着当前是无key Compound
            next(1);
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
        } else {
            int num = Integer.parseInt(value);
            return new IntTag(key, num);
        }
    }

    private void eval(char type) {
        if (!((type >= '0' && type <= '9') || type == ',' || type == '[' || type == '{'
                || type == ']' || type == '\"' || type == '}' || type == '-')) {
            throw new IllegalArgumentException("snbt格式错误");
        }
    }

    public void reset() {
        index = 0;
    }

    public static CompoundTag parseSNBT(String SNBT) {
        SNBTParser parser = new SNBTParser(SNBT);
        return parser.deserializationSNBT();
    }
}

