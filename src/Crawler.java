import org.apache.http.client.HttpResponseException;
import org.apache.http.client.fluent.Request;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

/**
 * Created by RobbinNi on 2/24/16.
 */
public class Crawler implements Runnable {

    private DatabaseInterface db;

    private ArrayList<String> OAuths;
    private int keyNum;
    private int ReconnectTime;
    private int OutdateTime;

    public Crawler(DatabaseInterface db, Properties setup) {
        OAuths = new ArrayList<>();
        int n = Integer.valueOf(setup.getProperty("num_key"));
        for (int i = 0; i < n; ++i) {
            OAuths.add(setup.getProperty("oath" + (i + 1)));
        }
        keyNum = 0;
        ReconnectTime = Integer.valueOf(setup.getProperty("reconnect"));
        OutdateTime = Integer.valueOf(setup.getProperty("outdate"));
        this.db = db;
    }

    private String githubAPISafe(String URL) {
        while (true) {
            String ret;
            try {
                String OAuthURL;
                if (URL.contains("?")) {
                    OAuthURL = URL + "&" + OAuths.get(keyNum);
                } else {
                    OAuthURL = URL + "?" + OAuths.get(keyNum);
                }
                ret = Request.Get(OAuthURL).execute().returnContent().asString();
                return ret;
            } catch (HttpResponseException re) {
                //retry after 60 seconds
                if (re.getStatusCode() == 403) {
                    //Achieved rate limit
                    System.err.println("Rate limit achieved.");
                    keyNum = (keyNum + 1) % OAuths.size();
                } else {
                    System.err.println("Unknown connection Error. " + re.getStatusCode() + " " + re.getMessage());
                }
                try {
                    Thread.sleep(ReconnectTime);
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
        System.out.println("Crawled repos of " + user.name);
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
                System.out.println("All users have been crawled");
                break;
            }
            System.out.println("Received " + n + " usernames");
            for (int i = 0; i < n; ++i) {
                JSONObject user = array.getJSONObject(i);
                String username = user.getString("login");
                int id = user.getInt("id");
                UserInfo cur = new UserInfo(id, username, new Date()),
                        last = db.getUser(id);
                //if not updated in a week
                if (last == null || last.crawledAt.getTime() < cur.crawledAt.getTime() - OutdateTime) {
                    db.updateUser(cur);
                    crawlUserRepo(cur);
                }
                lastID = id;
            }
        } while (true);
    }
    public void run() {
        while (true) {
            crawlAllUsers();
        }
    }
}
