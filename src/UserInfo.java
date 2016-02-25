import java.util.Date;

/**
 * Created by RobbinNi on 2/25/16.
 */
public class UserInfo {
    public int id;
    public String name;
    public Date crawledAt;

    public UserInfo(int id, String name, Date crawledAt) {
        this.id = id;
        this.name = name;
        this.crawledAt = crawledAt;
    }
}
