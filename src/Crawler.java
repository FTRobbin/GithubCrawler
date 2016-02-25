import org.apache.http.client.HttpResponseException;
import org.apache.http.client.fluent.Request;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Created by RobbinNi on 2/24/16.
 */
public class Crawler implements Runnable {

    private DatabaseInterface db;

    private String OAuth;

    public Crawler(DatabaseInterface db) {
        OAuth = "&client_id=6625e55ca0d52666da40&client_secret=0b0d5d277dd798581418682266aeb0f07c756d60";
        this.db = db;
    }

    private String githubAPISafe(String URL) {
        while (true) {
            String ret;
            try {
                ret = Request.Get(URL).execute().returnContent().asString();
                return ret;
            } catch (HttpResponseException re) {
                //retry after 60 seconds
                if (re.getStatusCode() == 403) {
                    //Achieved rate limit
                    System.err.println("Rate limit achieved.");
                } else {
                    System.err.println("Unknown connection Error.");
                }
                try {
                    Thread.sleep(60000);
                } catch (InterruptedException ie) {
                    //Do nothing
                }
            } catch (IOException ie) {
                ie.printStackTrace();
                System.exit(1);
            }
        }
    }

    private void crawlUserRepo(UserInfo user) {
        String repoAPI = "https://api.github.com/users/" + user.name + "/repos";
        String raw = githubAPISafe(repoAPI);
        JSONArray array = new JSONArray(raw);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd'T'hh:mm:ss'Z'");
        int n = array.length();
        for (int i = 0; i < n; ++i) {
            JSONObject repo = array.getJSONObject(i);
            int id = repo.getInt("id");
            String name = repo.getString("name");
            String url = repo.getString("git_url");
            Date updatedAt = null;
            try {
                updatedAt = format.parse(repo.getString("updated_at"));
            } catch (ParseException pe) {
                pe.printStackTrace();
            }
            RepoInfo cur = new RepoInfo(id, user.id, name, url, new Date(), updatedAt, new Date(0));
            db.updateRepo(cur);
        }
    }

    private void crawlAllUsers() {
        int lastID = 0;
        do {
            String usersAPI = "https://api.github.com/users?since=" + lastID;
            String raw = githubAPISafe(usersAPI);
            JSONArray array = new JSONArray(raw);
            int n = array.length();
            if (n == 0) {
                //All users have been crawled
                break;
            }
            n = 5;//For debug usage
            for (int i = 0; i < n; ++i) {
                JSONObject user = array.getJSONObject(i);
                String username = user.getString("login");
                int id = user.getInt("id");
                UserInfo cur = new UserInfo(id, username, new Date()),
                         last = db.getUser(id);
                //if not updated in a week
                if (last == null || last.crawledAt.getTime() < cur.crawledAt.getTime() - (7 * 24 * 3600 * 1000)) {
                    db.updateUser(cur);
                    crawlUserRepo(cur);
                }
                lastID = id;
            }
        } while (false); //For debug usage
    }
    public void run() {
        while (true) {
            crawlAllUsers();
        }
    }
}
