package dashteacup.pman;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.junit.Test;

/**
 * Class for making sure that I have my encryption library set up properly
 * for my application.
 */
public class JavaEncryptionSettingsTest {

    /**
     * My application will be using 256 bit AES keys, so I need to verify that the
     * java installation supports this. If you don't have the JCE Unlimited Strength
     * Jurisdiction Policy Files installed then you will be limited to 128 bit
     * AES because of US export restrictions.
     */
    @Test
    public void aesKeyLengthIs256BitsOrMore() {
        int keyLength = 0;
        try {
            keyLength = Cipher.getMaxAllowedKeyLength("AES");
        } catch (NoSuchAlgorithmException e) {
            fail(e.getMessage());
        }
        assertTrue(keyLength >= 256);
    }

    /**
     * Encrypt and then decrypt a message with AES. The result should be the
     * same as what you started with. This test only exists to verify that
     * basic AES functionality exists and does not reflect the way it will
     * be used in the program.
     */
    @Test
    public void basicAesEncryptAndDecrypt() {
        String keyText = "1234567890123456"; // 16 bits
        byte[] key =  keyText.getBytes(); // convert to bytes
        String messageText = "The cat in the hat";
        byte[] message = messageText.getBytes();

        try {
            // create the encrypted message
            Cipher encrypt = Cipher.getInstance("AES");
            SecretKeySpec encryptKey = new SecretKeySpec(key, "AES");
            encrypt.init(Cipher.ENCRYPT_MODE, encryptKey);
            byte[] encryptedData = encrypt.doFinal(message);

            // Let's use another instance of all these things to simulate
            // doing this on another run of the application
            Cipher decrypt = Cipher.getInstance("AES");
            SecretKeySpec decryptKey = new SecretKeySpec(keyText.getBytes(), "AES");
            decrypt.init(Cipher.DECRYPT_MODE,  decryptKey);
            byte[] decryptedData = decrypt.doFinal(encryptedData);

            String decryptedMessage = new String(decryptedData);
            assertEquals(messageText, decryptedMessage);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void passwordEncryptionUsingPBKDF2() {
        String messageText = "Hello, world!";
        byte[] message = messageText.getBytes();
        // Note it's a char[] since PBEKey
        char[] password = "alpha1".toCharArray();
        // Salt is fixed in this test, but should not be so in the app
        byte[] salt = "12345".getBytes();
        int iterations = 6000; // number of times the password will be hashed

        try {
            // **** Build the encrypted message
            // PBEKeySpec won't return a SecretKey, so I have to jump through
            // hoops to get everything to the right type.
            KeySpec baseKey = new PBEKeySpec(password, salt, iterations, 256);

            // Cannot use AES with SecretKeyFactory.getInstance
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            SecretKey seckey = factory.generateSecret(baseKey);
            SecretKeySpec finalKey = new SecretKeySpec(seckey.getEncoded(), "AES");

            Cipher encryptor = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            encryptor.init(Cipher.ENCRYPT_MODE, finalKey);
            byte[] cyphertext = encryptor.doFinal(message);
            byte[] IV = encryptor.getIV();

            // **** Decrypt the message
            Cipher decryptor = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            decryptor.init(Cipher.DECRYPT_MODE, finalKey, new IvParameterSpec(IV));
            byte[] plaintext = decryptor.doFinal(cyphertext);

            String decryptedMessage = new String(plaintext);
            assertEquals(messageText, decryptedMessage);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void aesIVSize() {
        try {
            KeySpec baseKey = new PBEKeySpec("cat".toCharArray(), "dog".getBytes(), 6000, 256);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            SecretKey seckey = factory.generateSecret(baseKey);
            SecretKeySpec finalKey = new SecretKeySpec(seckey.getEncoded(), "AES");

            Cipher encryptor = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            encryptor.init(Cipher.ENCRYPT_MODE, finalKey);
            byte[] IV = encryptor.getIV();
            // IV should be 16 bytes long (128 bits), the block size for AES
            assertEquals(IV.length, 16);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void secureRandomFunctionality() {
        SecureRandom rand = new SecureRandom();
        byte[] arr = new byte[16];
        rand.nextBytes(arr);
        String str = new String(arr);
        // System.out.println("random string(" + str + ")");
        assertEquals(16, str.length());
    }
}
