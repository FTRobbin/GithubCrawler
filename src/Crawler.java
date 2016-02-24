import org.apache.http.client.HttpResponseException;
import org.apache.http.client.fluent.Request;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by RobbinNi on 2/24/16.
 */
public final class Crawler {

    private String OAuth;

    private int lastID;

    private static Crawler crawler = null;

    private Crawler() {
        OAuth = "&client_id=6625e55ca0d52666da40&client_secret=0b0d5d277dd798581418682266aeb0f07c756d60";
        lastID = 0;
    }

    static public Crawler getCrawler() {
        if (crawler == null) {
            crawler = new Crawler();
        }
        return crawler;
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

    public void crawlUserRepo(String username) {
        String repoAPI = "https://api.github.com/users/" + username + "/repos";
        String raw = githubAPISafe(repoAPI);
        JSONArray array = new JSONArray(raw);
        int n = array.length();
        for (int i = 0; i < n; ++i) {
            JSONObject repo = array.getJSONObject(i);
            int id = repo.getInt("id");
            String name = repo.getString("name");
            System.out.println(id + " : " + name);
        }
    }

    public void crawlAllUsers() {
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
                System.out.println(username + " " + id);
                crawlUserRepo(username);
                lastID = id;
            }
        } while (false); //For debug usage
    }
}
