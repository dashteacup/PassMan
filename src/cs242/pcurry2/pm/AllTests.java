package cs242.pcurry2.pm;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Test suite for running all the tests in my Password Manager project.
 */
@RunWith(Suite.class)
@SuiteClasses({ JavaEncryptionSettingsTest.class, PasswordManagerTest.class })
public class AllTests {

}
