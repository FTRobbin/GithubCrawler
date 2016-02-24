import org.apache.http.client.fluent.Request;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by RobbinNi on 2/24/16.
 */
public final class Crawler {

    public void crawlAllUsers() {
        try {
            int lastID = 0;
            do {
                String usersAPI = "https://api.github.com/users?since=" + lastID;
                String raw = Request.Get(usersAPI).execute().returnContent().asString();
                JSONArray array = new JSONArray(raw);
                int n = array.length();
                if (n == 0) {
                    //All users have been crawled
                    break;
                }
                for (int i = 0; i < n; ++i) {
                    JSONObject user = array.getJSONObject(i);
                    String username = user.getString("login");
                    int id = user.getInt("id");
                    System.out.println(username + " " + id);
                    lastID = id;
                }
            } while (lastID < 100);
        } catch (IOException ie) {
            ie.printStackTrace();
            System.exit(1);
        }
    }
}
