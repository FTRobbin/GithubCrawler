/**
 * Created by RobbinNi on 2/24/16.
 */
public class Main {

    static public void main(String args[]) {
        DatabaseInterface db = new DummyDatabase();
        Thread crawler = new Thread(new Crawler(db));
        Thread cloner = new Thread(new Cloner(db));
        //crawler.run();
        cloner.run();
    }
}
