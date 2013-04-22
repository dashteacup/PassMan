package cs242.pcurry2.pm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;

/**
 * Controller class for the Password Management (PM) Application.
 */
public class PMController {

    private static final String defaultDirectory = "testfiles";

    /**
     * The Application's view.
     */
    private PMView view;

    /**
     * The application's model.
     */
    private PasswordManager model;

    /**
     * File chooser for selecting password files to manipulate.
     */
    private JFileChooser fileChooser;

    /**
     * Runner method
     * @param args not currently used
     */
    public static void main(String[] args) {
        new PMController();
    }

    /**
     * Create the Application's Controller.
     */
    public PMController() {
        view = new PMView();
        model = new PasswordManager();
        fileChooser = new JFileChooser(defaultDirectory);
        fileChooser.setFileFilter(new PmanFileFilter());
        addListeners();
    }

    public void addListeners() {
        view.addNewFileListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                // TODO Auto-generated method stub

            }
        });

        view.addOpenFileListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                int returnStatus = fileChooser.showOpenDialog(view.getMainWindow());
                if (returnStatus == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    try {
                        // TODO: Actually make a proper password dialog box...
                        // Right now I'm keying it to only a single password.
                        model.openPasswordFile(file, "mommy".toCharArray());
                        view.setText(model.getText());
                    }
                    catch (BadPasswordException e) {
                        view.showMessageDialog("Invalid password for file: " + file.getName());
                    }
                    catch (IOException e) {
                        // TODO: Do something more appropriate when there is a file
                        // IO error.
                        e.printStackTrace();
                        return;
                    }

                }
            }
        });

        view.addCloseFileListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                // TODO: Add save dialog if unsaved changes.
                model = new PasswordManager();
                view.resetText();
            }
        });

        view.addSaveFileListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                // TODO Auto-generated method stub

            }
        });

        view.addSaveAsFileListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                // TODO Auto-generated method stub

            }
        });
    }

}
