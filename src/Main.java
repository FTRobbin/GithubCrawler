import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by RobbinNi on 2/24/16.
 */
public class Main {

    static Properties loadProperties() {
        Properties ret = new Properties();
        File setup = new File("setup.ini");
        try {
            InputStream ism = new FileInputStream(setup);
            ret.load(ism);
        } catch (IOException ie) {
            ie.printStackTrace();
            System.exit(1);
        }
        return ret;
    }

    static public void main(String args[]) {
        Properties setup = loadProperties();
        GithubDBParams params = new GithubDBParams(
                setup.getProperty("host"),
                setup.getProperty("user"),
                setup.getProperty("pass"),
                setup.getProperty("database"),
                setup.getProperty("adapter"),
                setup.getProperty("type"),
                setup.getProperty("port"));
        GithubDatabase db = new GithubDatabase(params);
        db.initDatabaseConnection();
        if (args.length > 0 && args[0].equals("-c")) {
            db.dropExistDatabase();
        }
        db.setupDatabaseAndTables();
        Thread crawler = new Thread(new Crawler(db, setup));
        Thread cloner = new Thread(new Cloner(db, setup));
        crawler.start();
        cloner.start();
    }
}
