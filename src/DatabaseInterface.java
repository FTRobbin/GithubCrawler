import java.sql.SQLException;
import java.util.Queue;

/**
 * Created by RobbinNi on 2/25/16.
 */
public interface DatabaseInterface {

    public void updateUser(UserInfo user) throws SQLException;
    public void updateRepo(RepoInfo repo) throws SQLException;
    public void clonedRepo(RepoInfo repo) throws SQLException;
    public UserInfo getUser(int id) throws SQLException;
    public Queue<RepoInfo> getToCloneRepos() throws SQLException;
    public void initDatabaseConnection();
    public void destroyDatabaseConnection();
}
