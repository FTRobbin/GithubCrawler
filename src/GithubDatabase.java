import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Ceno on 2/25/16.
 */
public class GithubDatabase implements DatabaseInterface {
    private Connection dbConn;
    private String dbURL;
    private Statement statement;
    private ResultSet results;
    private GithubDBParams dbParams;
    private DateFormat formart = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public GithubDatabase(GithubDBParams dbParams){
        this.dbParams = dbParams;
    }

    public static String trimQuery(String Query){
        String trim = Query.replace(',', ' ');
        trim = trim.replace('\'', ' ');
        return trim;
    }

    @Override
    public synchronized void updateUser(UserInfo user) throws SQLException {
        /*
        System.out.println("Update User Info:");
        System.out.println("id : " + user.id);
        System.out.println("name : " + user.name);
        System.out.println("crawled_at : " + user.crawledAt.toString());
        */
        results = statement.executeQuery("SELECT * FROM user WHERE id = " + user.id + ";");
        if (results.next()) {
            statement.executeUpdate("UPDATE user SET "
                    + "crawled_at = '" + formart.format(user.crawledAt) + "' "
                    + "WHERE id = " + user.id
                    + ";");
        } else {
            statement.executeUpdate("INSERT INTO user VALUES(" + user.id + ", '" + user.name + "', '" + formart.format(user.crawledAt) + "');");
        }
    }

    @Override
    public synchronized void updateRepo(RepoInfo repo) throws SQLException {
        /*
        System.out.println("Update Repo Info:");
        System.out.println("id : " + repo.id);
        System.out.println("owner_id : " + repo.ownerid);
        System.out.println("name : " + repo.name);
        System.out.println("url : " + repo.url);
        System.out.println("crawled_at : " + repo.crawledAt.toString());
        System.out.println("updated_at : " + repo.updatedAt.toString());
        System.out.println("cloned_at : " + repo.clonedAt.toString());
        */
        results = statement.executeQuery("SELECT * FROM repo WHERE id = " + repo.id + ";");
        if (results.next()) {
            statement.executeUpdate("UPDATE repo SET "
                    + "crawled_at = '" + formart.format(repo.crawledAt) + "' "
                    + ", owner_id = " + repo.ownerid
                    + ", url : '" + repo.url + "' "
                    + ", updated_at = '" + formart.format(repo.updatedAt) + "' "
                    + "WHERE id = " + repo.id
                    + ";");
        } else {
            statement.executeUpdate("INSERT INTO repo VALUES(" + repo.id + ", '" + repo.name + "', '" + repo.url
                    + "', '" + formart.format(repo.crawledAt)
                    + "', '" + formart.format(repo.updatedAt)
                    + "', '" + formart.format(repo.clonedAt)
                    + "', " + repo.ownerid
                    + ");");
        }
    }

    @Override
    public synchronized UserInfo getUser(int id) throws SQLException {
        results = statement.executeQuery("SELECT * FROM user WHERE id = " + id);
        if (results.next()) {
            String name = results.getString(2);
            Date crawledAt = results.getDate(3);
            UserInfo user = new UserInfo(id, name, crawledAt);
            return user;
        } else return null;
    }

    @Override
    public synchronized void clonedRepo(RepoInfo repo) throws SQLException {
        /*
        System.out.println("Cloned Repo:");
        System.out.println("id : " + repo.id);
        System.out.println("owner_id : " + repo.ownerid);
        System.out.println("name : " + repo.name);
        System.out.println("url : " + repo.url);
        System.out.println("crawled_at : " + repo.crawledAt.toString());
        System.out.println("updated_at : " + repo.updatedAt.toString());
        System.out.println("cloned_at : " + repo.clonedAt.toString());
        */
        statement.executeUpdate("UPDATE repo SET cloned_at = '" + formart.format(repo.clonedAt)
                + "' WHERE id = " + repo.id
                + ";");
    }

    @Override
    public synchronized Queue<RepoInfo> getToCloneRepos() throws SQLException {
        Queue<RepoInfo> q = new LinkedList<>();
        results = statement.executeQuery("SELECT * FROM repo WHERE cloned_at < updated_at;");
        while (results.next()) {
            int id = results.getInt(1);
            String name = results.getString(2);
            String url = results.getString(3);
            java.util.Date crawledAt = results.getDate(4);
            java.util.Date updatedAt = results.getDate(5);
            java.util.Date clonedAt = results.getDate(6);
            int ownerId = results.getInt(7);
            q.add(new RepoInfo(id, ownerId, name, url, crawledAt, updatedAt, clonedAt));
        }
        return q;
    }

    public void initDatabaseConnection() {
        try {
            this.dbURL = this.dbParams.getDBLink();
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            this.dbConn = DriverManager.getConnection(this.dbURL, this.dbParams.getDBUser(), this.dbParams.getDBPass());
            System.out.println("DB conn established");
        } catch(Exception e) {
            System.err.println("Cannot connect to DB");
            System.err.println("Connection URL " + this.dbParams.getDBLink());
            System.err.println("Connection USER " + this.dbParams.getDBUser());
            System.err.println("Connection PASSWORD " + this.dbParams.getDBPass());
            System.err.println(e.getMessage());
        }
        try {
            this.statement = this.dbConn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void destroyDatabaseConnection(){
        try	{
            dbConn.close();
            System.out.println("DB conn closed");
        } catch(Exception e){}
    }

}
