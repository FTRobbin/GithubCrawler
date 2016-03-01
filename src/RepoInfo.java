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

    public void printRepoInfo() {
        System.out.println("id : " + id);
        System.out.println("owner_id : " + ownerid);
        System.out.println("name : " + name);
        System.out.println("url : " + url);
        System.out.println("crawled_at : " + crawledAt.toString());
        System.out.println("updated_at : " + updatedAt.toString());
        System.out.println("cloned_at : " + clonedAt.toString());
    }
}
