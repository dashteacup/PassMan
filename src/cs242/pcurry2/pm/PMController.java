package cs242.pcurry2.pm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

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
                    String text = "";
                    try {
                        // I don't expect any of these files to be very large,
                        // so readAllBytes is OK for now.
                        text = new String(Files.readAllBytes(file.toPath()));
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }
                    view.addText(text);
                }
            }
        });

        view.addCloseFileListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
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
