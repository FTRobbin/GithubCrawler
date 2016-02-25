import java.util.Date;

/**
 * Created by RobbinNi on 2/25/16.
 */
public class RepoInfo {

    int id, ownerid;
    String name, url;
    Date crawledAt, updatedAt, clonedAt;

    public RepoInfo(int id, int ownerid, String name, String url, Date crawledAt, Date updatedAt, Date clonedAt) {
        this.id = id;
        this.ownerid = ownerid;
        this.name = name;
        this.url = url;
        this.crawledAt = crawledAt;
        this.updatedAt = updatedAt;
        this.clonedAt = clonedAt;
    }
}
