package cs242.pcurry2.pm;

import java.awt.Insets;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

/**
 * View class for the Password Management (PM) application.
 */
public class PMView {

    /**
     * The main window for this application.
     */
    private JFrame mainWindow;

    /**
     * Menu item for opening a new password file.
     */
    private JMenuItem newPasswordFile;

    /**
     * Menu item for opening an existing password file.
     */
    private JMenuItem openPasswordFile;

    /**
     * Menu item for closing a password file.
     */
    private JMenuItem closePasswordFile;

    /**
     * Menu item for saving a password file.
     */
    private JMenuItem savePasswordFile;

    /**
     * Menu item for saving a password file as a chosen file name.
     */
    private JMenuItem saveAsPasswordFile;

    /**
     * Text area where the password file will be displayed.
     */
    private JTextArea textArea;

    /**
     * Constructor builds the view
     */
    public PMView() {
        try {
            // Use the native look and feel if possible
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            // Put the Mac Menubar in the right place.
            // TODO: Check what this does on non-macs
            System.setProperty("apple.laf.useScreenMenuBar", "true");
        } catch(Exception e) {
            // It's not a big deal if you can't access the system look and feel
            System.err.println("Couldn't get native look and feel.");
        }
        mainWindow = new JFrame("CS242 Password Manager");
        setUpMenu(mainWindow);
        setUpFileChooser(mainWindow);
        setUpTextArea(mainWindow);
        mainWindow.pack();
        mainWindow.setVisible(true);
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * Get the main window for the password manager app.
     * @return the mainWindow
     */
    public JFrame getMainWindow() {
        return mainWindow;
    }

    /**
     * Adds an action listener to the open new password file menu option.
     * @param action listener to be attached to the menu item.
     */
    public void addNewFileListener(ActionListener action) {
        newPasswordFile.addActionListener(action);
    }

    /**
     * Adds an action listener to the open a preexisting password file menu
     * option.
     * @param action listener to be attached to the menu item.
     */
    public void addOpenFileListener(ActionListener action) {
        openPasswordFile.addActionListener(action);
    }

    /**
     * Adds an action listener to the close password file menu option.
     * @param action listener to be attached to the menu item
     */
    public void addCloseFileListener(ActionListener action) {
        closePasswordFile.addActionListener(action);
    }

    /**
     * Adds an action listener to the save password file menu option.
     * @param action listener to be attached to the menu item
     */
    public void addSaveFileListener(ActionListener action) {
        savePasswordFile.addActionListener(action);
    }

    /**
     * Adds an action listner to the save as file menu option.
     * @param action listener to be attached to the menu item
     */
    public void addSaveAsFileListener(ActionListener action) {
        saveAsPasswordFile.addActionListener(action);
    }

    /**
     * Displays a string in the main window's text area.
     * @param text to be added
     */
    public void addText(String text) {
        textArea.append(text);
    }

    /**
     * Set the main windows text area to display the given source text.
     * @param text to be shown.
     */
    public void setText(String text) {
        textArea.setText(text);
    }

    /**
     * Clear the main window's text area.
     */
    public void resetText() {
        textArea.setText("");
    }

    /**
     * Show a message dialog box attached to the main window of the application.
     * @param message to be displayed to the user.
     */
    public void showMessageDialog(String message) {
        // TODO: figure out what I'm going to do with this dialog here.
        JOptionPane.showMessageDialog(mainWindow, message, "errhere", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Obtain a password from the user.
     * @return char[] with a potential password or null if they cancel.
     */
    public char[] showPasswordDialog() {
        PasswordDialog dialog = new PasswordDialog(mainWindow);
        return dialog.getPasswordFromUser();
    }

    /**
     * Create the PM's menu bar.
     * @param window to put the menu in.
     */
    private void setUpMenu(JFrame window) {
        JMenuBar menubar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        newPasswordFile = new JMenuItem("New Password File");
        openPasswordFile = new JMenuItem("Open File");
        closePasswordFile = new JMenuItem("Close");
        savePasswordFile = new JMenuItem("Save");
        saveAsPasswordFile = new JMenuItem("Save As");

        fileMenu.add(newPasswordFile);
        fileMenu.add(openPasswordFile);
        fileMenu.add(closePasswordFile);
        fileMenu.add(savePasswordFile);
        fileMenu.add(saveAsPasswordFile);
        menubar.add(fileMenu);
        window.setJMenuBar(menubar);
    }

    /**
     * Create the text area where the password file will be displayed.
     * @param window to put the text area in
     */
    private void setUpTextArea(JFrame window) {
        textArea = new JTextArea(30, 40);
        textArea.setMargin(new Insets(5, 5, 5, 5));
        textArea.setEditable(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        window.add(scrollPane);
    }

    /**
     * Adds a file chooser component to the window.
     * @param window to connect the file chooser to
     */
    private void setUpFileChooser(JFrame window) {

    }


}
