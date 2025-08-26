package in.wynk.secret.manager.utils.encryption;

import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.SignatureException;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

/**
 * Created with IntelliJ IDEA. User: bhuvangupta Date: 13/01/13 Time: 1:00 PM To change this
 * template use File | Settings | File Templates.
 */
public class EncryptUtils {

    private static final String ALGORITHM = "AES";
    private static final String ALGORITHM_256 = "AES/ECB/PKCS7Padding";
    private static final String ENCRYPTIONKEY ="BSB$PORTAL@2014#";

    public static final String ENCRYPTION_KEY_FOR_THANKS ="THANKS$";
    private static Cipher ecipher;
    private static Cipher dcipher;
    private static final int shiftKey=3;
    private static final int fullPhoneLength=10;
    private static SecretKey key;

    static {
        try {

            // generate secret key using DES algorithm
            key = KeyGenerator.getInstance("DES").generateKey();

            ecipher = Cipher.getInstance("DES");
            dcipher = Cipher.getInstance("DES");

            // initialize the ciphers with the given key

            ecipher.init(Cipher.ENCRYPT_MODE, key);

            dcipher.init(Cipher.DECRYPT_MODE, key);

        }
        catch (NoSuchAlgorithmException e) {
            System.out.println("No Such Algorithm:" + e.getMessage());
        }
        catch (NoSuchPaddingException e) {
            System.out.println("No Such Padding:" + e.getMessage());
        }
        catch (InvalidKeyException e) {
            System.out.println("Invalid Key:" + e.getMessage());
        }
    }

    public static String encodeShiftAlpha(String data) {
    	data = getRotatedString(data);
    	String resultString = "";
    	for(int i=0;i<data.length();i++) {
    		char c = data.charAt(i);
    		if(c >= '0' && c <= '9') {
    			int digit = Character.getNumericValue(c);
    			char dig = (char)(((int)'A')+digit);
    			resultString+=dig;
    		}
    	}
    	return resultString;
    }

	public static String getRotatedString(String data) {
		String rotatePart = data.substring(fullPhoneLength-shiftKey);
    	rotatePart = new StringBuffer(rotatePart).reverse().toString();
    	data = rotatePart + data.substring(0,fullPhoneLength-shiftKey);
		return data;
	}

    public static String decodeShiftAlpha(String data) {
    	String number = "";
    	for(int i=0;i<data.length();i++) {
    		char c = data.charAt(i);
    		int digit = ((int)c) - ((int)'A');
    		number += digit;
    	}
    	String rotatePart = number.substring(0, shiftKey);
    	rotatePart = new StringBuilder(rotatePart).reverse().toString();
    	String result = number.substring(shiftKey) + rotatePart;
    	return result;
    }

    // BASE 64 - encode and decode
    public static String encodeBase64(String data, boolean urlsafe) {
        if(urlsafe) {
            return new String(Base64.encodeBase64URLSafe(data.getBytes()));
        }
        else {
            return new String(Base64.encodeBase64(data.getBytes()));
        }
    }

    public static String decodeBase64(String data) {
        return new String(Base64.decodeBase64(data));
    }

    /**
     * Create an MD5 hash of a string.
     *
     * @param input Input string.
     * @return Hash of input.
     * @throws IllegalArgumentException if {@code input} is blank.
     */
    public static String md5(String input) {
        if (input == null || input.length() == 0) {
            throw new IllegalArgumentException("Input string must not be blank.");
        }
        try {
            MessageDigest algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(input.getBytes());
            byte[] messageDigest = algorithm.digest();

            StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < messageDigest.length; i++) {
                hexString.append(Integer.toHexString((messageDigest[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(String.format("Cannot generate md5 for string [%s]", input));
        }
    }




    /**
     * Convert input string's characters to corresponding hexcode value if char range is outside [0-127]
     *
     * @param input
     * @return
     */
    public static Object[] convertTohexString(String input, boolean xmlEncode)
    {
        if (input == null)
        {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        if (xmlEncode)
        {
            input = xmlEncode(input);
        }
        boolean containNonAscii = false;
        for (int i = 0; i < input.length(); i++)
        {
            char ch = input.charAt(i);
            if (ch >= 0 && ch <= 127)
            {
                sb.append(ch);
            }
            else
            {
                containNonAscii = true;
                sb.append("&");
                sb.append(Integer.toHexString((int) ch));
                sb.append(";");
            }
        }
        String msg = sb.toString();
        if (containNonAscii)
        {
            msg = msg.replaceAll("&#xa;", " ");
        }
        Object[] obj = new Object[2];
        obj[0] = containNonAscii;
        obj[1] = msg;
        return obj;
    }


    public static String xmlEncode(String s)
    {
        StringBuffer str = new StringBuffer(new String("".getBytes(), Charset.forName("UTF-8")));
        int len = (s != null) ? s.length() : 0;
        for (int i = 0; i < len; i++)
        {
            char ch = s.charAt(i);
            switch (ch)
            {
                case '<':
                    str.append("&lt;");
                    break;
                case '>':
                    str.append("&gt;");
                    break;
                case '&':
                    str.append("&amp;");
                    break;
                case '"':
                    str.append("&quot;");
                    break;
                case '\r':
                case '\n':
                    str.append("&#x" + Integer.toHexString(ch) + ';');
                    break;
                default:
                    str.append(ch);
            }
        }

        return str.toString();
    }


    /**
     * Computes RFC 2104-compliant HMAC signature.
     *
     * @param data
     *            The data to be signed.
     * @param secretKey
     *            The signing key.
     * @return The Base64-encoded RFC 2104-compliant HMAC signature.
     * @throws java.security.SignatureException
     *             when signature generation fails
     */
    public static String calculateRFC2104HMAC(String data, String secretKey) throws java.security.SignatureException {
        String result;
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(), "HmacSHA1");
            mac.init(key);
            byte[] authentication = mac.doFinal(data.getBytes());
            result = new String(org.apache.commons.codec.binary.Base64.encodeBase64(authentication));

        } catch (Exception e) {
            throw new SignatureException("Failed to generate HMAC : " + e.getMessage());
        }
        return result;
    }



    public static String decrypt(String valueToDeenc, String encKey) throws Exception {
    	Key key = generateKey(encKey.getBytes("utf-8"));

    	byte[] content = Base64.decodeBase64(valueToDeenc.getBytes());
//    	String encryptedData = content.toString();
    	Cipher c= Cipher.getInstance(ALGORITHM);
    	c.init(Cipher.DECRYPT_MODE, key);
        byte[] data = c.doFinal(content);
//        String result = Base64.decodeBase64String(data);
        String res = new String(data);
        return res;
    }


    private static Key generateKey(byte[] keyBytes) throws Exception {
        Key key = new SecretKeySpec(keyBytes, ALGORITHM);
        return key;
    }





    private static Key generateKey(String password) throws Exception {
	    byte[] keyValue = password.getBytes("UTF8");
	    Key key = new SecretKeySpec(keyValue, ALGORITHM_256);
	    return key;
    }

    /**
     * Hmac-sha1 & base64URLEncode
     *
     * @param key
     * @param in
     * @param max
     * @return
     * @throws Exception
     */
    public static String hmacSha1Enc(String key, String in, int max) throws Exception {
        if (max > 0) {
            String Base64Data = base64(hmacsha1(key, in));
            String Base64urlencodeData = Base64Data.replace("+", "-").replace("/", "_").replace("=", "");
            if (Base64urlencodeData.length() > max) {
                return Base64urlencodeData.substring(0, max);
            } else {
                return Base64urlencodeData;
            }
        } else {
            return "";
        }
    }

    /**
     * HMAC-SHA1 process(Attestation code generation)
     *
     * @param key
     * @param in
     * @return
     * @throws Exception
     */
    private static byte[] hmacsha1(String key, String in) throws Exception {
        try {
            SecretKeySpec sk = new SecretKeySpec(key.getBytes(), "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(sk);
            byte[] HMACSHA1Data = mac.doFinal(in.getBytes());
            return HMACSHA1Data;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Base64 encode process
     *
     * @param data data of input
     * @return String base64Encode
     */
    private static String base64(byte[] data) throws Exception {
        //Base64 encode
        String Base64EncodeData = new String(Base64.encodeBase64((data)));
        return Base64EncodeData;
    }


    public static void main(String[] args) {
//        String encryptedvalue = "0wCX/jtOFvKpP+D6CQv9d+Bhu85qd/92ssKzoaoTVq92s3gjHZWjkDXzi1z+lx9Isrud0N7dp0SYfdinWSORFQmZVBSK2B/7pFOPNmRvrK7kvXfqiE9rZwjjOq629NkM31yuVLt215Q8jtNkuHX7nYTk7C1Pi+124ZFusudj6G2ylK7txIb3z5Wb5vzBid/8zxEEFM6ojBh/ViiM0qsC4S9Rp55WdQlVb5Mz50G16fE=";
//    							 //0wCX/jtOFvKpP+D6CQv9d+Bhu85qd/92ssKzoaoTVq92s3gjHZWjkDXzi1z+lx9Isrud0N7dp0SYfdinWSORFQmZVBSK2B/7pFOPNmRvrK7kvXfqiE9rZwjjOq629NkM31yuVLt215Q8jtNkuHX7nYTk7C1Pi+124ZFusudj6G2ylK7txIb3z5Wb5vzBid/8zxEEFM6ojBh/ViiM0qsC4S9Rp55WdQlVb5Mz50G16fE=
//        String key = "BSB$PORTAL@2014#";
//        try {
//            String encrypted = EncryptUtils.decrypt(encryptedvalue, key);
//            System.out.println("encrypted value is " + encrypted);
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        } 


        try {
        	decrypt(
                "q6ddFDEWsL7Z6NwGC_39mMHM1LpP5gp7mimVRaRNWpDNfawgFW1uZBCVWPZVvM6WIFZATmFlIt5AtVHWMjN7yA"
                , ENCRYPTIONKEY);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        

    }


}