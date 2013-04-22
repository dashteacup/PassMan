package cs242.pcurry2.pm;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * Custom file filter to restrict the openable files to .PMAN files
 */
public class PmanFileFilter extends FileFilter {

    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        String filename = f.getName();
        int extIndex = filename.lastIndexOf('.');
        String extension = filename.substring(extIndex + 1);
        if (extension.equals("pman") || extension.equals("PMAN")) {
            return true;
        }
        return false;
    }

    @Override
    public String getDescription() {
        return "PMAN file";
    }

}
