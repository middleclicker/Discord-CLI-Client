import java.io.File;
import java.io.IOException;

public class testclass {
    public  static void main (String [] args) {
        try {
            // create a new file object
            File directory = new File("data/middleclicker person's server");

            deleteDirectory(directory);

        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    public static void deleteDirectory(File directory) {
        if(directory.isDirectory()) {
            File[] files = directory.listFiles();
            if(files != null) {
                for(File file : files) {
                    deleteDirectory(file);
                }
            }
        }
        directory.delete();
    }
}
