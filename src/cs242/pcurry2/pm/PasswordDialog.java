package cs242.pcurry2.pm;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.SwingUtilities;

/**
 * Dialog box for recieving a user's password to unlock a password file.
 */
public class PasswordDialog {

    /**
     * The frame this dialog box is attached to.
     */
    private JFrame parentWindow;

    /**
     * Password field that will be filled in by the user.
     */
    private JPasswordField passwordField;


    /**
     * Create a new dialog box for accepting a password.
     * @param window the parent window this dialog box is attached to.
     */
    public PasswordDialog(JFrame window) {
        parentWindow = window;
        passwordField = new JPasswordField();

        // Ridiculous workaround necessary to ensure that the password input
        // field receives focus. For some reason pf.requestFocusInWindow doesn't
        // work well with the JOptionPane.showXxxDialog methods. I might write a
        // full custom dialog later.
        // Taken from: http://tips4java.wordpress.com/2010/03/14/dialog-focus/#comment-1352
        passwordField.addHierarchyListener(new HierarchyListener() {
            @Override
            public void hierarchyChanged(HierarchyEvent e) {
                final Component c = e.getComponent();
                if (c.isShowing() && (e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0) {
                    Window toplevel = SwingUtilities.getWindowAncestor(c);
                    toplevel.addWindowFocusListener(new WindowAdapter() {
                        @Override
                        public void windowGainedFocus(WindowEvent e) {
                            c.requestFocus();
                        }
                    });
                }
            }
        });


    }

    /**
     * Display a dialog box asking the user for a password and return the value
     * they provide.
     * @return the password entered by the user or null if cancelled.
     */
    public char[] getPasswordFromUser() {
        JComponent[] inputs = new JComponent[] { new JLabel("Enter Password"), passwordField };
        int approved = JOptionPane.showConfirmDialog(parentWindow, inputs, "Enter Password", JOptionPane.OK_CANCEL_OPTION);
        if (approved == JOptionPane.OK_OPTION) {
            return passwordField.getPassword();
        }
        return null;
    }

}
