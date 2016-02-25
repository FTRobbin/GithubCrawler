import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.SimpleFormatter;

/**
 * Created by RobbinNi on 2/25/16.
 */
public class DummyDatabase implements DatabaseInterface {

    private RepoInfo testRepo = null;

    public DummyDatabase() {
        //For debug usage
        int id = 23736423, ownerid = 7616107;
        String name = "002_Enkannokotowari",
                url = "git://github.com/Necrololicon/002_Enkannokotowari.git";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd'T'hh:mm:ss'Z'");
        Date updatedAt = new Date();
        try {
            updatedAt = format.parse("2014-09-06T14:28:42Z");
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        testRepo = new RepoInfo(id, ownerid, name, url, new Date(), updatedAt, new Date(0));
    }

    @Override
    public void updateUser(UserInfo user) {
        System.out.println("Update User Info:");
        System.out.println("id : " + user.id);
        System.out.println("name : " + user.name);
        System.out.println("crawled_at : " + user.crawledAt.toString());
    }

    @Override
    public void updateRepo(RepoInfo repo) {
        System.out.println("Update Repo Info:");
        System.out.println("id : " + repo.id);
        System.out.println("owner_id : " + repo.ownerid);
        System.out.println("name : " + repo.name);
        System.out.println("url : " + repo.url);
        System.out.println("crawled_at : " + repo.crawledAt.toString());
        System.out.println("updated_at : " + repo.updatedAt.toString());
        System.out.println("cloned_at : " + repo.clonedAt.toString());
    }

    @Override
    public UserInfo getUser(int id) {
        return null;
    }

    @Override
    public void clonedRepo(RepoInfo repo) {
        System.out.println("Cloned Repo:");
        System.out.println("id : " + repo.id);
        System.out.println("owner_id : " + repo.ownerid);
        System.out.println("name : " + repo.name);
        System.out.println("url : " + repo.url);
        System.out.println("crawled_at : " + repo.crawledAt.toString());
        System.out.println("updated_at : " + repo.updatedAt.toString());
        System.out.println("cloned_at : " + repo.clonedAt.toString());
    }

    @Override
    public Queue<RepoInfo> getToCloneRepos() {
        Queue<RepoInfo> q = new LinkedList<>();
        q.add(testRepo);
        return q;
    }
}
