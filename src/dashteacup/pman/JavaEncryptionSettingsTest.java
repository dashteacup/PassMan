package dashteacup.pman;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.junit.Test;

/**
 * Class for making sure that I have my encryption library set up properly for
 * my application. These tests should not cover any of my code, they ensure the
 * java encryption functionality works as expected. These tests should be run
 * when checking for portability on other platforms. The tests also serve as a
 * helpful reminder of how to use the JCA.
 */
public class JavaEncryptionSettingsTest {

    /**
     * Size of the key used by the encryption algorithm.
     */
    private static final int KEY_SIZE = 256;

    /**
     * Algorithm used to generate the keys for my encryption algorithm.
     */
    private static final String KEYGEN_ALG = "PBKDF2WithHmacSHA1";

    /**
     * Transformation used by my ciphers to encrypt/decrypt data.
     */
    private static final String CIPHER_TRANSFORM = "AES/CBC/PKCS5PADDING";

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
        assertTrue(keyLength >= KEY_SIZE);
    }

    /**
     * Encrypt and then decrypt a message with AES. The result should be the
     * same as what you started with. This test only exists to verify that basic
     * AES functionality exists and does not reflect the way it will be used in
     * the program.
     */
    @Test
    public void basicAesEncryptAndDecrypt() {
        final String keyText = "1234567890123456"; // 16 bits
        final byte[] key =  keyText.getBytes(); // convert to bytes
        final String messageText = "The cat in the hat";
        final byte[] message = messageText.getBytes();

        try {
            // Create the encrypted message
            Cipher encrypt = Cipher.getInstance("AES");
            // I use SecretKeySpec to generate a SecretKey without worrying
            // about what provider to use.
            SecretKeySpec encryptKey = new SecretKeySpec(key, "AES");
            encrypt.init(Cipher.ENCRYPT_MODE, encryptKey);
            byte[] encryptedData = encrypt.doFinal(message);

            // Let's use another instance of all these things to simulate
            // doing this on another run of the application
            Cipher decrypt = Cipher.getInstance("AES");
            SecretKeySpec decryptKey = new SecretKeySpec(key, "AES");
            decrypt.init(Cipher.DECRYPT_MODE,  decryptKey);
            byte[] decryptedData = decrypt.doFinal(encryptedData);

            String decryptedMessage = new String(decryptedData);
            assertEquals(messageText, decryptedMessage);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Check Java's support for the Password-Based Key Derivation Function 2
     * (PBKDF2). More simply, check if I can generate a key from a password, use
     * that key to encrypt some text with 256-bit AES, and decrypt the message
     * with the same password.
     */
    @Test
    public void passwordEncryptionUsingPBKDF2() {
        final String messageText = "Hello, world!";
        final byte[] message = messageText.getBytes();
        // Note it's a char[] since PBEKey expects it. Also, although it doesn't
        // matter here, passwords shouldn't be stored as strings because strings
        // are immutable.
        final char[] password = "alpha1".toCharArray();
        // Salt is fixed in this test, but should not be so in the app
        final byte[] salt = "12345".getBytes();
        final int iterations = 60000; // number of times the password will be hashed

        try {
            // **** Build the encrypted message
            SecretKeySpec encryptKey = PBESecretKeySpec(password, salt, iterations, KEY_SIZE);
            Cipher encryptor = Cipher.getInstance(CIPHER_TRANSFORM);
            encryptor.init(Cipher.ENCRYPT_MODE, encryptKey);
            byte[] cypherText = encryptor.doFinal(message);
            byte[] IV = encryptor.getIV();

            // **** Decrypt the message
            // I don't reuse keys to simulate separate invocations of the App.
            SecretKeySpec decryptKey = PBESecretKeySpec(password, salt, iterations, KEY_SIZE);
            Cipher decryptor = Cipher.getInstance(CIPHER_TRANSFORM);
            decryptor.init(Cipher.DECRYPT_MODE, decryptKey, new IvParameterSpec(IV));
            byte[] plainText = decryptor.doFinal(cypherText);

            String decryptedMessage = new String(plainText);
            assertEquals(messageText, decryptedMessage);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    /**
     * Confirm that the generated IV (Initialization Vector) is the appropriate
     * size for my chosen algorithm. It should be 128 bits, the same as the
     * block size for AES.
     */
    @Test
    public void aesIVSize() {
        try {
            SecretKeySpec key =
                    PBESecretKeySpec("cat".toCharArray(), "dog".getBytes(), 6000, KEY_SIZE);
            Cipher encryptor = Cipher.getInstance(CIPHER_TRANSFORM);
            encryptor.init(Cipher.ENCRYPT_MODE, key);
            byte[] IV = encryptor.getIV();
            // IV should be 16 bytes long (128 bits)
            assertEquals(IV.length, 16);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    /**
     * Confirm that I can securely generate a 16-bit blob of random data. I will
     * use this to generate salts for each password file.
     */
    @Test
    public void secureRandomFunctionality() {
        SecureRandom rand = new SecureRandom();
        byte[] arr = new byte[16];
        rand.nextBytes(arr);
        String str = new String(arr);
        assertEquals(16, str.length());
    }

    /**
     * Generates a new {@link SecretKeySpec} via Password Based Encryption. This
     * interface mirrors {@link PBEKeySpec}, but returns the right kind of
     * {@link KeySpec} for my algorithm.
     * @param password - the password.
     * @param salt - the salt.
     * @param iterationCount - the iteration count.
     * @param keyLength - the to-be-derived key length.
     * @return a {@link SecretKeySpec} suitable for use with my encryption algorithm.
     * @throws NoSuchAlgorithmException - if it can't use the key generating algorithm.
     * @throws InvalidKeySpecException - if something goes horribly wrong with the JCA.
     */
    private SecretKeySpec PBESecretKeySpec(char[] password,
                                           byte[] salt,
                                           int iterationCount,
                                           int keyLength)
                                                   throws
                                                   NoSuchAlgorithmException,
                                                   InvalidKeySpecException {
        // PBEKeySpec won't return a SecretKey, so I have to jump through
        // hoops to get everything to the right type.
        KeySpec baseKey = new PBEKeySpec(password, salt, iterationCount, keyLength);
        // Cannot use AES with SecretKeyFactory.getInstance.
        // I'd prefer to use something like PBKDF2WithHmacSHA256 or
        // PBEWithHmacSHA256AndAES_256 instead of PBKDF2WithHmacSHA1 but
        // the last is what my version of java supports out of the box.
        SecretKeyFactory factory = SecretKeyFactory.getInstance(KEYGEN_ALG);
        SecretKey secretKey = factory.generateSecret(baseKey);
        return new SecretKeySpec(secretKey.getEncoded(), "AES");
    }
}
