package cs242.pcurry2.pm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.Test;

public class PasswordManagerTest {
    private static final String shortText = "cats";
    private static final String midsizedText =
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Phasellus in " +
            "neque ut nisi interdum viverra at sit amet ipsum. Ut molestie eros vel " +
            "leo porttitor blandit.";
    private static final String salt16 = "1234567890123456";
    private static final String iv16 = "abcdefghijklmnop";

    @Test
    public void encryptTestFile() {
        String filePath = "testfiles/plainTextFile.txt";
        String password = "dogs";
        String salt = "12345";
        PasswordManager pm = new PasswordManager();
        byte[] encrypted = pm.encryptFile(filePath, password.toCharArray(), salt);
        assertNotNull(encrypted);
        try {
            Files.write(Paths.get(filePath + ".pman"), encrypted);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void encryptFileWithHeadersAndFooters() {
        String filename = "testfiles/textlines.pman";

        PasswordManager pm = new PasswordManager(shortText, salt16, iv16);
        try {
            pm.savePasswordFile(filename, "mark1".toCharArray());
            byte[] result = Files.readAllBytes(Paths.get(filename));
            assertEquals("PMFileVer001.000", new String(Arrays.copyOfRange(result, 0, 16)));
            assertEquals(salt16, new String(Arrays.copyOfRange(result, 16, 32)));
            assertEquals(iv16, new String(Arrays.copyOfRange(result, 32, 48)));
            assertEquals("PMBeginCipherTxt", new String(Arrays.copyOfRange(result, 48, 64)));
            assertEquals("PMFileEndCipherT", new String(Arrays.copyOfRange(result, 80, 96)));
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void encryptAndDecryptSmallFile() {
        String filename = "testfiles/encryptdecrypt.pman";
        String password = "helloworld";
        PasswordManager pm = new PasswordManager(shortText, salt16, iv16);
        try {
            // Since we destroy the password char[] we need to make a new one both times
            pm.savePasswordFile(filename, password.toCharArray());
            PasswordManager openpm = new PasswordManager();
            openpm.openPasswordFile(filename, password.toCharArray());
            assertEquals(shortText, openpm.getText());
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void encryptAndDecryptMidsizedFile() {
        String filename = "testfiles/midsize.pman";
        String password = "mommy";
        PasswordManager pm = new PasswordManager(midsizedText);
        try {
            pm.savePasswordFile(filename, password.toCharArray());
            PasswordManager openpm = new PasswordManager();
            openpm.openPasswordFile(filename, password.toCharArray());
            assertEquals(midsizedText, openpm.getText());
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void encryptAndDecryptLargeFile() {
        String filename = "testfiles/largefile.pman";
        String password = "bigone";
        try {
            byte[] rawInputFile = Files.readAllBytes(Paths.get("testfiles/largeTextFile.txt"));
            PasswordManager pm = new PasswordManager(new String(rawInputFile));
            pm.savePasswordFile(filename, password.toCharArray());
            PasswordManager openpm = new PasswordManager();
            openpm.openPasswordFile(filename, password.toCharArray());
            assertEquals(new String(rawInputFile), openpm.getText());
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void invalidDecryptPassword() {
        String filename = "testfiles/badpassword.pman";
        String password = "hithere";
        PasswordManager pm = new PasswordManager(midsizedText);
        try {
            pm.savePasswordFile(filename, password.toCharArray());
            PasswordManager openpm = new PasswordManager();
            openpm.openPasswordFile(filename, "wrong".toCharArray());
        }
        catch (BadPasswordException e) {
            assertTrue(true);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void manuallySetText() {
        String filename = "testfiles/manualset.pman";
        String password = "MANUAL1";
        PasswordManager pm = new PasswordManager();
        pm.setText(midsizedText);
        try {
            pm.savePasswordFile(filename, password.toCharArray());
            PasswordManager openpm = new PasswordManager();
            openpm.openPasswordFile(filename, password.toCharArray());
            assertEquals(midsizedText, openpm.getText());
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

}
