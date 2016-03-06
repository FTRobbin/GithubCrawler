import java.util.Queue;

/**
 * Created by RobbinNi on 2/25/16.
 */
public interface DatabaseInterface {

    void updateUser(UserInfo user);
    void updateRepo(RepoInfo repo);
    void clonedRepo(RepoInfo repo);
    UserInfo getUser(int id);
    Queue<RepoInfo> getToCloneRepos();
    void initDatabaseConnection();
    void destroyDatabaseConnection();
    void checkDatabaseStatus();
}
