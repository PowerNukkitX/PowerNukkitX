package cn.nukkit.utils;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class HumanStringComparator implements Comparator<String> {
    private static final HumanStringComparator INSTANCE = new HumanStringComparator();
    private static final int LEFT = -1;
    private static final int RIGHT = 1;
    private static final int EQUALS = 0;
    private static final String SYMBOLS = "[:.;,/\\]{}|=";

    @Override
    public int compare(String o1, String o2) {
        if (o1.equals(o2)) {
            return EQUALS;
        }

        List<String> l1 = splitSymbols(combineNegativeSign(split(o1)));
        List<String> l2 = splitSymbols(combineNegativeSign(split(o2)));

        return compare(l1, l2);
    }

    private List<String> splitSymbols(List<String> list) {
        boolean changed = false;
        List<String> result = list;
        int size = list.size();
        for (int i = size - 1; i >= 0; i--) {
            String str = result.get(i);
            int length = str.length();
            int lastPart = length;
            for (int j = length - 1; j >= 0; j--) {
                char c = str.charAt(j);
                if (SYMBOLS.indexOf(c) != -1) {
                    if (!changed) {
                        result = list instanceof ArrayList? list : new ArrayList<>(list);
                        changed = true;
                    }
                    int indexToAddLast;
                    if (j > 0) {
                        result.set(i, str.substring(0, j));
                        result.add(i + 1, Character.toString(c));
                        indexToAddLast = i + 2;
                    } else {
                        result.set(i, Character.toString(c));
                        indexToAddLast = i + 1;
                    }
                    if (j + 2 <= length) {
                        result.add(indexToAddLast, str.substring(j + 1, lastPart));
                    }
                    lastPart = j;
                }
            }
        }
        return result;
    }

    private int compare(List<String> l1, List<String> l2) {
        int len1 = l1.size();
        int len2 = l2.size();
        int minLen = Math.min(len1, len2);
        for (int i = 0; i < minLen; i++) {
            String str1 = l1.get(i);
            String str2 = l2.get(i);
            int strLen1 = str1.length();
            int strLen2 = str2.length();
            assert strLen1 > 0;
            assert strLen2 > 0;
            boolean isNum1 = Character.isDigit(str1.charAt(strLen1 - 1));
            boolean isNum2 = Character.isDigit(str2.charAt(strLen2 - 1));
            if (isNum1) {
                if (isNum2) {
                    int i1 = Integer.parseInt(str1);
                    int i2 = Integer.parseInt(str2);
                    int result = Integer.compare(i1, i2);
                    if (result != EQUALS) {
                        return result;
                    }
                    // Number with higher 0 padding goes before
                    result = Integer.compare(strLen1, strLen2);
                    if (result != EQUALS) {
                        return result;
                    }
                } else {
                    return RIGHT;
                }
            } else if (isNum2) {
                return LEFT;
            } else {
                if (strLen1 == strLen2) {
                    int result = str1.compareTo(str2);
                    if (result != EQUALS) {
                        return result;
                    }
                } else {
                    int minStrLen = Math.min(strLen1, strLen2);
                    String commonPart1 = str1.substring(0, minStrLen);
                    String commonPart2 = str2.substring(0, minStrLen);
                    int result = commonPart1.compareTo(commonPart2);
                    if (result != EQUALS) {
                        return result;
                    }

                    // Detect omitted number
                    if (strLen1 < strLen2) {
                        if (detectOmittedNumber(l1, len1, i, str2, strLen2, minStrLen, commonPart1)) {
                            return RIGHT;
                        }
                    } else if (detectOmittedNumber(l2, len2, i, str1, strLen1, minStrLen, commonPart2)) {
                        return LEFT;
                    }

                    return Integer.compare(strLen1, strLen2);
                }
            }
        }

        return Integer.compare(len1, len2);
    }

    private boolean detectOmittedNumber(List<String> l1, int len1, int i, String str2, int strLen2, int minStrLen, String commonPart1) {
        String combined;
        String comparingWith;
        String next1 = len1 > i + 1? l1.get(i + 1) : null;
        int nextLen1 = next1 == null? 0 : next1.length();
        boolean isDigit1 = next1 != null && Character.isDigit(next1.charAt(nextLen1 - 1));
        String afterNext1 = isDigit1 && len1 > i + 2? l1.get(i + 2) : null;
        int afterNextLen1 = afterNext1 != null? afterNext1.length() : 0;
        if (afterNextLen1 > 0) {
            combined = commonPart1 + afterNext1.substring(0, Math.min(afterNextLen1, strLen2 - minStrLen));
            comparingWith = str2;
            return combined.equals(comparingWith);
        }
        return false;
    }

    private List<String> combineNegativeSign(List<String> list) {
        int size = list.size();
        if (size < 2) {
            return list;
        }

        for (int i = size - 1; i > 0; i--) {
            String str1 = list.get(i);
            int strLen1 = str1.length();
            if (strLen1 > 0 && Character.isDigit(str1.charAt(strLen1 - 1))) {
                String str2 = list.get(i - 1);
                int strLen2 = str2.length();
                if (strLen2 > 0 && str2.charAt(strLen2 - 1) == '-') {
                    list.set(i, "-" + str1);
                    if (strLen2 == 1) {
                        list.remove(i - 1);
                        i -= 2;
                    } else {
                        list.set(i - 1, str2.substring(0, strLen2 - 1));
                        i--;
                    }
                }
            }
        }

        return list;
    }

    @Nonnull
    private List<String> split(String str) {
        int length = str.length();
        if (length == 0) {
            return Collections.emptyList();
        } else if (length == 1) {
            return Collections.singletonList(str);
        }

        List<String> list = null;
        boolean wasDigit = false;
        int start = -1;
        for (int i = 0; i < length; i++) {
            if (Character.isDigit(str.charAt(i))) {
                if (!wasDigit && start == -1) {
                    start = i;
                    wasDigit = true;
                } else if (!wasDigit) {
                    if (list == null) {
                        list = new ArrayList<>(2);
                    }
                    list.add(str.substring(start, i));
                    start = i;
                    wasDigit = true;
                }
            } else {
                if (wasDigit) {
                    if (list == null) {
                        list = new ArrayList<>(2);
                    }
                    list.add(str.substring(start, i));
                    start = i;
                    wasDigit = false;
                } else if (start == -1) {
                    start = i;
                }
            }
        }

        String substring = str.substring(start, length);
        if (list == null) {
            list = Collections.singletonList(substring);
        } else {
            list.add(substring);
        }

        return list;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static HumanStringComparator getInstance() {
        return INSTANCE;
    }
}
