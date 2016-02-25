import java.util.Queue;

/**
 * Created by RobbinNi on 2/25/16.
 */
public interface DatabaseInterface {

    public void updateUser(UserInfo user);
    public void updateRepo(RepoInfo repo);
    public void clonedRepo(RepoInfo repo);
    public UserInfo getUser(int id);
    public Queue<RepoInfo> getToCloneRepos();
}
