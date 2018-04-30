/*
 * ------------------------------------------------------------------------
 * Copyright 2014 Korea Electronics Technology Institute
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------------------
 */

package kr.re.keti.ncube.resource;

/**
 * Class for base64 encoding and decoding.
 * @author NakMyoung Sung (nmsung@keti.re.kr)
 */
public class Base64 {

    private static final char[] BASE64CHARS = { 'A', 'B', 'C', 'D', 'E', 'F',
            'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',
            'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
            'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's',
            't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', '+', '/' };

    private static final char BASE64PAD = '=';

    private static final byte[] DECODETABLE = new byte[128];

    static {
        for (int i = 0; i < DECODETABLE.length; i++) {
            DECODETABLE[i] = Byte.MAX_VALUE; // 127
        }
        for (int i = 0; i < BASE64CHARS.length; i++) {
            DECODETABLE[BASE64CHARS[i]] = (byte) i; // 0 to 63
        }
    }

    private static int _decode(char[] ibuf, byte[] obuf, int wp) {
        int outlen = 3;
        if (ibuf[3] == BASE64PAD)
            outlen = 2;
        if (ibuf[2] == BASE64PAD)
            outlen = 1;
        int b0 = DECODETABLE[ibuf[0]];
        int b1 = DECODETABLE[ibuf[1]];
        int b2 = DECODETABLE[ibuf[2]];
        int b3 = DECODETABLE[ibuf[3]];
        switch (outlen) {
        case 1:
            obuf[wp] = (byte) (b0 << 2 & 0xfc | b1 >> 4 & 0x3);
            return 1;
        case 2:
            obuf[wp++] = (byte) (b0 << 2 & 0xfc | b1 >> 4 & 0x3);
            obuf[wp] = (byte) (b1 << 4 & 0xf0 | b2 >> 2 & 0xf);
            return 2;
        case 3:
            obuf[wp++] = (byte) (b0 << 2 & 0xfc | b1 >> 4 & 0x3);
            obuf[wp++] = (byte) (b1 << 4 & 0xf0 | b2 >> 2 & 0xf);
            obuf[wp] = (byte) (b2 << 6 & 0xc0 | b3 & 0x3f);
            return 3;
        default:
            throw new RuntimeException("Internal Error");
        }
    }

    public static String decode(String data) {
        char[] ibuf = new char[4];
        int ibufcnt = 0;
        String returnString = null;
        byte[] obuf = new byte[data.length() / 4 * 3 + 3];
        int obufcnt = 0;
        for (int i = 0; i < data.length(); i++) {
            char ch = data.charAt(i);
            if (ch == BASE64PAD || ch < DECODETABLE.length
                    && DECODETABLE[ch] != Byte.MAX_VALUE) {
                ibuf[ibufcnt++] = ch;
                if (ibufcnt == ibuf.length) {
                    ibufcnt = 0;
                    obufcnt += _decode(ibuf, obuf, obufcnt);
                }
            }
        }
        if (obufcnt == obuf.length) {
        	returnString = new String(obuf, 0, obuf.length);
            return returnString;	
        }
        byte[] ret = new byte[obufcnt];
        System.arraycopy(obuf, 0, ret, 0, obufcnt);
        returnString = new String(ret, 0, ret.length);
        returnString = returnString.replaceAll("%3A", ":");
        returnString = returnString.replaceAll("%2F", "/");
        returnString = returnString.replaceAll("%3F", "?");
        returnString = returnString.replaceAll("%3D", "=");
        returnString = returnString.replaceAll("%26", "&");
        return returnString;
    }

    public static String encode(byte[] data) {
        int len = data.length;
        if (len <= 0)
            return "";
        char[] out = new char[len / 3 * 4 + 4];
        int ridx = 0;
        int widx = 0;
        int rest = len;
        while (rest >= 3) {
            int i = ((data[ridx] & 0xff) << 16)
                    + ((data[ridx + 1] & 0xff) << 8) + (data[ridx + 2] & 0xff);
            out[widx++] = BASE64CHARS[i >> 18];
            out[widx++] = BASE64CHARS[(i >> 12) & 0x3f];
            out[widx++] = BASE64CHARS[(i >> 6) & 0x3f];
            out[widx++] = BASE64CHARS[i & 0x3f];
            ridx += 3;
            rest -= 3;
        }
        if (rest == 1) {
            int i = data[ridx] & 0xff;
            out[widx++] = BASE64CHARS[i >> 2];
            out[widx++] = BASE64CHARS[(i << 4) & 0x3f];
            out[widx++] = BASE64PAD;
            out[widx++] = BASE64PAD;
        } else if (rest == 2) {
            int i = ((data[ridx] & 0xff) << 8) + (data[ridx + 1] & 0xff);
            out[widx++] = BASE64CHARS[i >> 10];
            out[widx++] = BASE64CHARS[(i >> 4) & 0x3f];
            out[widx++] = BASE64CHARS[(i << 2) & 0x3f];
            out[widx++] = BASE64PAD;
        }
        return new String(out, 0, widx);
    }

    public static boolean isValidBase64Encoding(String data) {
        for (int i = 0; i < data.length(); i++) {
            char ch = data.charAt(i);
            if (ch == BASE64PAD || ch < DECODETABLE.length
                    && DECODETABLE[ch] != Byte.MAX_VALUE) {
                // do nothing
            } else if (ch == '\r' || ch == '\n') {
                // do nothing
            } else {
                return false;
            }
        }
        return true;
    }
}