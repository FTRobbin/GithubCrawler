/**
 * Created by RobbinNi on 2/24/16.
 */
public class Main {

    static public void main(String args[]) {
        GithubDBParams params = new GithubDBParams("localhost", "myuser", "000000", "GithubCrawler", "jdbc", "mysql", "3306");
        DatabaseInterface db = new GithubDatabase(params);
        db.initDatabaseConnection();
        Thread crawler = new Thread(new Crawler(db));
        Thread cloner = new Thread(new Cloner(db));
        crawler.run();
        cloner.run();
    }
}
