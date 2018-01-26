//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package java.lang;

import sun.misc.VM;

public final class Integer extends Number implements Comparable<Integer> {
    public static final int MIN_VALUE = -2147483648;
    public static final int MAX_VALUE = 2147483647;
    public static final Class<Integer> TYPE = (Class<Integer>)Class.getPrimitiveClass("int");
    static final char[] digits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    static final char[] DigitTens = new char[]{'0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '2', '2', '2', '2', '2', '2', '2', '2', '2', '2', '3', '3', '3', '3', '3', '3', '3', '3', '3', '3', '4', '4', '4', '4', '4', '4', '4', '4', '4', '4', '5', '5', '5', '5', '5', '5', '5', '5', '5', '5', '6', '6', '6', '6', '6', '6', '6', '6', '6', '6', '7', '7', '7', '7', '7', '7', '7', '7', '7', '7', '8', '8', '8', '8', '8', '8', '8', '8', '8', '8', '9', '9', '9', '9', '9', '9', '9', '9', '9', '9'};
    static final char[] DigitOnes = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    static final int[] sizeTable = new int[]{9, 99, 999, 9999, 99999, 999999, 9999999, 99999999, 999999999, 2147483647};
    private final int value;
    public static final int SIZE = 32;
    public static final int BYTES = 4;
    private static final long serialVersionUID = 1360826667806852920L;

    public static String toString(int var0, int var1) {
        if (var1 < 2 || var1 > 36) {
            var1 = 10;
        }

        if (var1 == 10) {
            return toString(var0);
        } else {
            char[] var2 = new char[33];
            boolean var3 = var0 < 0;
            int var4 = 32;
            if (!var3) {
                var0 = -var0;
            }

            while(var0 <= -var1) {
                var2[var4--] = digits[-(var0 % var1)];
                var0 /= var1;
            }

            var2[var4] = digits[-var0];
            if (var3) {
                --var4;
                var2[var4] = '-';
            }

            return new String(var2, var4, 33 - var4);
        }
    }

    public static String toUnsignedString(int var0, int var1) {
        return Long.toUnsignedString(toUnsignedLong(var0), var1);
    }

    public static String toHexString(int var0) {
        return toUnsignedString0(var0, 4);
    }

    public static String toOctalString(int var0) {
        return toUnsignedString0(var0, 3);
    }

    public static String toBinaryString(int var0) {
        return toUnsignedString0(var0, 1);
    }

    private static String toUnsignedString0(int var0, int var1) {
        int var2 = 32 - numberOfLeadingZeros(var0);
        int var3 = Math.max((var2 + (var1 - 1)) / var1, 1);
        char[] var4 = new char[var3];
        formatUnsignedInt(var0, var1, var4, 0, var3);
        return new String(var4, true);
    }

    static int formatUnsignedInt(int var0, int var1, char[] var2, int var3, int var4) {
        int var5 = var4;
        int var6 = 1 << var1;
        int var7 = var6 - 1;

        do {
            --var5;
            var2[var3 + var5] = digits[var0 & var7];
            var0 >>>= var1;
        } while(var0 != 0 && var5 > 0);

        return var5;
    }

    public static String toString(int var0) {
        if (var0 == -2147483648) {
            return "-2147483648";
        } else {
            int var1 = var0 < 0 ? stringSize(-var0) + 1 : stringSize(var0);
            char[] var2 = new char[var1];
            getChars(var0, var1, var2);
            return new String(var2, true);
        }
    }

    public static String toUnsignedString(int var0) {
        return Long.toString(toUnsignedLong(var0));
    }

    static void getChars(int var0, int var1, char[] var2) {
        int var5 = var1;
        byte var6 = 0;
        if (var0 < 0) {
            var6 = 45;
            var0 = -var0;
        }

        int var3;
        int var4;
        while(var0 >= 65536) {
            var3 = var0 / 100;
            var4 = var0 - ((var3 << 6) + (var3 << 5) + (var3 << 2));
            var0 = var3;
            --var5;
            var2[var5] = DigitOnes[var4];
            --var5;
            var2[var5] = DigitTens[var4];
        }

        do {
            var3 = var0 * '쳍' >>> 19;
            var4 = var0 - ((var3 << 3) + (var3 << 1));
            --var5;
            var2[var5] = digits[var4];
            var0 = var3;
        } while(var3 != 0);

        if (var6 != 0) {
            --var5;
            var2[var5] = (char)var6;
        }

    }

    static int stringSize(int var0) {
        int var1;
        for(var1 = 0; var0 > sizeTable[var1]; ++var1) {
            ;
        }

        return var1 + 1;
    }

    public static int parseInt(String var0, int var1) throws NumberFormatException {
        if (var0 == null) {
            throw new NumberFormatException("null");
        } else if (var1 < 2) {
            throw new NumberFormatException("radix " + var1 + " less than Character.MIN_RADIX");
        } else if (var1 > 36) {
            throw new NumberFormatException("radix " + var1 + " greater than Character.MAX_RADIX");
        } else {
            int var2 = 0;
            boolean var3 = false;
            int var4 = 0;
            int var5 = var0.length();
            int var6 = -2147483647;
            if (var5 > 0) {
                char var9 = var0.charAt(0);
                if (var9 < '0') {
                    if (var9 == '-') {
                        var3 = true;
                        var6 = -2147483648;
                    } else if (var9 != '+') {
                        throw NumberFormatException.forInputString(var0);
                    }

                    if (var5 == 1) {
                        throw NumberFormatException.forInputString(var0);
                    }

                    ++var4;
                }

                int var8;
                for(int var7 = var6 / var1; var4 < var5; var2 -= var8) {
                    var8 = Character.digit(var0.charAt(var4++), var1);
                    if (var8 < 0) {
                        throw NumberFormatException.forInputString(var0);
                    }

                    if (var2 < var7) {
                        throw NumberFormatException.forInputString(var0);
                    }

                    var2 *= var1;
                    if (var2 < var6 + var8) {
                        throw NumberFormatException.forInputString(var0);
                    }
                }

                return var3 ? var2 : -var2;
            } else {
                throw NumberFormatException.forInputString(var0);
            }
        }
    }

    public static int parseInt(String var0) throws NumberFormatException {
        return parseInt(var0, 10);
    }

    public static int parseUnsignedInt(String var0, int var1) throws NumberFormatException {
        if (var0 == null) {
            throw new NumberFormatException("null");
        } else {
            int var2 = var0.length();
            if (var2 > 0) {
                char var3 = var0.charAt(0);
                if (var3 == '-') {
                    throw new NumberFormatException(String.format("Illegal leading minus sign on unsigned string %s.", var0));
                } else if (var2 <= 5 || var1 == 10 && var2 <= 9) {
                    return parseInt(var0, var1);
                } else {
                    long var4 = Long.parseLong(var0, var1);
                    if ((var4 & -4294967296L) == 0L) {
                        return (int)var4;
                    } else {
                        throw new NumberFormatException(String.format("String value %s exceeds range of unsigned int.", var0));
                    }
                }
            } else {
                throw NumberFormatException.forInputString(var0);
            }
        }
    }

    public static int parseUnsignedInt(String var0) throws NumberFormatException {
        return parseUnsignedInt(var0, 10);
    }

    public static Integer valueOf(String var0, int var1) throws NumberFormatException {
        return parseInt(var0, var1);
    }

    public static Integer valueOf(String var0) throws NumberFormatException {
        return parseInt(var0, 10);
    }

    public static Integer valueOf(int var0) {
        return var0 >= -128 && var0 <= Integer.IntegerCache.high ? Integer.IntegerCache.cache[var0 + 128] : new Integer(var0);
    }

    public Integer(int var1) {
        this.value = var1;
    }

    public Integer(String var1) throws NumberFormatException {
        this.value = parseInt(var1, 10);
    }

    public byte byteValue() {
        return (byte)this.value;
    }

    public short shortValue() {
        return (short)this.value;
    }

    public int intValue() {
        return this.value;
    }

    public long longValue() {
        return (long)this.value;
    }

    public float floatValue() {
        return (float)this.value;
    }

    public double doubleValue() {
        return (double)this.value;
    }

    public String toString() {
        return toString(this.value);
    }

    public int hashCode() {
        return hashCode(this.value);
    }

    public static int hashCode(int var0) {
        return var0;
    }

    public boolean equals(Object var1) {
        if (var1 instanceof Integer) {
            return this.value == ((Integer)var1).intValue();
        } else {
            return false;
        }
    }

    public static Integer getInteger(String var0) {
        return getInteger(var0, (Integer)null);
    }

    public static Integer getInteger(String var0, int var1) {
        Integer var2 = getInteger(var0, (Integer)null);
        return var2 == null ? var1 : var2;
    }

    public static Integer getInteger(String var0, Integer var1) {
        String var2 = null;

        try {
            var2 = System.getProperty(var0);
        } catch (NullPointerException | IllegalArgumentException var4) {
            ;
        }

        if (var2 != null) {
            try {
                return decode(var2);
            } catch (NumberFormatException var5) {
                ;
            }
        }

        return var1;
    }

    public static Integer decode(String var0) throws NumberFormatException {
        byte var1 = 10;
        int var2 = 0;
        boolean var3 = false;
        if (var0.length() == 0) {
            throw new NumberFormatException("Zero length string");
        } else {
            char var5 = var0.charAt(0);
            if (var5 == '-') {
                var3 = true;
                ++var2;
            } else if (var5 == '+') {
                ++var2;
            }

            if (!var0.startsWith("0x", var2) && !var0.startsWith("0X", var2)) {
                if (var0.startsWith("#", var2)) {
                    ++var2;
                    var1 = 16;
                } else if (var0.startsWith("0", var2) && var0.length() > 1 + var2) {
                    ++var2;
                    var1 = 8;
                }
            } else {
                var2 += 2;
                var1 = 16;
            }

            if (!var0.startsWith("-", var2) && !var0.startsWith("+", var2)) {
                Integer var4;
                try {
                    var4 = valueOf(var0.substring(var2), var1);
                    var4 = var3 ? -var4.intValue() : var4;
                } catch (NumberFormatException var8) {
                    String var7 = var3 ? "-" + var0.substring(var2) : var0.substring(var2);
                    var4 = valueOf(var7, var1);
                }

                return var4;
            } else {
                throw new NumberFormatException("Sign character in wrong position");
            }
        }
    }

    public int compareTo(Integer var1) {
        return compare(this.value, var1.value);
    }

    public static int compare(int var0, int var1) {
        return var0 < var1 ? -1 : (var0 == var1 ? 0 : 1);
    }

    public static int compareUnsigned(int var0, int var1) {
        return compare(var0 + -2147483648, var1 + -2147483648);
    }

    public static long toUnsignedLong(int var0) {
        return (long)var0 & 4294967295L;
    }

    public static int divideUnsigned(int var0, int var1) {
        return (int)(toUnsignedLong(var0) / toUnsignedLong(var1));
    }

    public static int remainderUnsigned(int var0, int var1) {
        return (int)(toUnsignedLong(var0) % toUnsignedLong(var1));
    }

    public static int highestOneBit(int var0) {
        var0 |= var0 >> 1;
        var0 |= var0 >> 2;
        var0 |= var0 >> 4;
        var0 |= var0 >> 8;
        var0 |= var0 >> 16;
        return var0 - (var0 >>> 1);
    }

    public static int lowestOneBit(int var0) {
        return var0 & -var0;
    }

    public static int numberOfLeadingZeros(int var0) {
        if (var0 == 0) {
            return 32;
        } else {
            int var1 = 1;
            if (var0 >>> 16 == 0) {
                var1 += 16;
                var0 <<= 16;
            }

            if (var0 >>> 24 == 0) {
                var1 += 8;
                var0 <<= 8;
            }

            if (var0 >>> 28 == 0) {
                var1 += 4;
                var0 <<= 4;
            }

            if (var0 >>> 30 == 0) {
                var1 += 2;
                var0 <<= 2;
            }

            var1 -= var0 >>> 31;
            return var1;
        }
    }

    public static int numberOfTrailingZeros(int var0) {
        if (var0 == 0) {
            return 32;
        } else {
            int var2 = 31;
            int var1 = var0 << 16;
            if (var1 != 0) {
                var2 -= 16;
                var0 = var1;
            }

            var1 = var0 << 8;
            if (var1 != 0) {
                var2 -= 8;
                var0 = var1;
            }

            var1 = var0 << 4;
            if (var1 != 0) {
                var2 -= 4;
                var0 = var1;
            }

            var1 = var0 << 2;
            if (var1 != 0) {
                var2 -= 2;
                var0 = var1;
            }

            return var2 - (var0 << 1 >>> 31);
        }
    }

    public static int bitCount(int var0) {
        var0 -= var0 >>> 1 & 1431655765;
        var0 = (var0 & 858993459) + (var0 >>> 2 & 858993459);
        var0 = var0 + (var0 >>> 4) & 252645135;
        var0 += var0 >>> 8;
        var0 += var0 >>> 16;
        return var0 & 63;
    }

    public static int rotateLeft(int var0, int var1) {
        return var0 << var1 | var0 >>> -var1;
    }

    public static int rotateRight(int var0, int var1) {
        return var0 >>> var1 | var0 << -var1;
    }

    public static int reverse(int var0) {
        var0 = (var0 & 1431655765) << 1 | var0 >>> 1 & 1431655765;
        var0 = (var0 & 858993459) << 2 | var0 >>> 2 & 858993459;
        var0 = (var0 & 252645135) << 4 | var0 >>> 4 & 252645135;
        var0 = var0 << 24 | (var0 & '\uff00') << 8 | var0 >>> 8 & '\uff00' | var0 >>> 24;
        return var0;
    }

    public static int signum(int var0) {
        return var0 >> 31 | -var0 >>> 31;
    }

    public static int reverseBytes(int var0) {
        return var0 >>> 24 | var0 >> 8 & '\uff00' | var0 << 8 & 16711680 | var0 << 24;
    }

    public static int sum(int var0, int var1) {
        return var0 + var1;
    }

    public static int max(int var0, int var1) {
        return Math.max(var0, var1);
    }

    public static int min(int var0, int var1) {
        return Math.min(var0, var1);
    }

    private static class IntegerCache {
        static final int low = -128;
        static final int high;
        static final Integer[] cache;

        private IntegerCache() {
        }

        static {
            int var0 = 127;
            String var1 = VM.getSavedProperty("java.lang.Integer.IntegerCache.high");
            int var2;
            if (var1 != null) {
                try {
                    var2 = Integer.parseInt(var1);
                    var2 = Math.max(var2, 127);
                    var0 = Math.min(var2, 2147483518);
                } catch (NumberFormatException var4) {
                    ;
                }
            }

            high = var0;
            cache = new Integer[high - -128 + 1];
            var2 = -128;

            for(int var3 = 0; var3 < cache.length; ++var3) {
                cache[var3] = new Integer(var2++);
            }

            assert high >= 127;

        }
    }
}
