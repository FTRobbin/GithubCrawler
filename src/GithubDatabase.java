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
    private GithubDBParams dbParams;
    private DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public GithubDatabase(GithubDBParams dbParams){
        this.dbParams = dbParams;
    }

    public static String trimQuery(String Query){
        String trim = Query.replace(',', ' ');
        trim = trim.replace('\'', ' ');
        return trim;
    }

    @Override
    public synchronized void updateUser(UserInfo user) {
        String lastCommand = "";
        try {
            String SELECTUSER = "SELECT * FROM user WHERE id = " + user.id + ";";
            lastCommand = SELECTUSER;
            ResultSet results = statement.executeQuery(SELECTUSER);
            lastCommand = "results.next()";
            if (results.next()) {
                String UPDATEUSER = "UPDATE user SET "
                        + "crawled_at = '" + format.format(user.crawledAt) + "' "
                        + "WHERE id = " + user.id
                        + ";";
                lastCommand = UPDATEUSER;
                statement.executeUpdate(UPDATEUSER);
            } else {
                String INSERTUSER = "INSERT INTO user VALUES("
                        + user.id + ", '"
                        + user.name + "', '"
                        + format.format(user.crawledAt) + "');";
                lastCommand = INSERTUSER;
                statement.executeUpdate(INSERTUSER);
            }
        } catch (SQLException se) {
            se.printStackTrace();
            System.err.println("When executing  : " + lastCommand);
            System.exit(1);
        }
    }

    @Override
    public synchronized void updateRepo(RepoInfo repo) {
        String lastCommand = "";
        try {
            String SELECTREPO = "SELECT * FROM repo WHERE id = " + repo.id + ";";
            lastCommand = SELECTREPO;
            ResultSet results = statement.executeQuery(SELECTREPO);
            lastCommand = "results.next()";
            if (results.next()) {
                String UPDATEREPO = "UPDATE repo SET "
                                + "crawled_at = '" + format.format(repo.crawledAt) + "' "
                                + ", owner_id = " + repo.ownerid
                                + ", url : '" + repo.url + "' "
                                + ", updated_at = '" + format.format(repo.updatedAt) + "' "
                                + "WHERE id = " + repo.id
                                + ";";
                lastCommand = UPDATEREPO;
                statement.executeUpdate(UPDATEREPO);
            } else {
                String INSERTREPO = "INSERT INTO repo VALUES(" + repo.id + ", '" + repo.name + "', '" + repo.url
                                + "', '" + format.format(repo.crawledAt)
                                + "', '" + format.format(repo.updatedAt)
                                + "', '" + format.format(repo.clonedAt)
                                + "', " + repo.ownerid
                                + ");";
                lastCommand = INSERTREPO;
                statement.executeUpdate(INSERTREPO);
            }
        } catch (SQLException se) {
            se.printStackTrace();
            System.err.println("When executing  : " + lastCommand);
            System.exit(1);
        }
    }

    @Override
    public synchronized UserInfo getUser(int id) {
        String lastCommand = "";
        try {
            String SELECTUSER = "SELECT * FROM user WHERE id = " + id;
            lastCommand = SELECTUSER;
            ResultSet results = statement.executeQuery(SELECTUSER);
            lastCommand = "results.next";
            if (results.next()) {
                String name = results.getString(2);
                Date crawledAt = results.getDate(3);
                UserInfo user = new UserInfo(id, name, crawledAt);
                return user;
            } else {
                return null;
            }
        } catch (SQLException se) {
            se.printStackTrace();
            System.err.println("When executing  : " + lastCommand);
            System.exit(1);
            return null;
        }
    }

    @Override
    public synchronized void clonedRepo(RepoInfo repo) {
        String lastCommand = "";
        try {
            String UPDATEREPO = "UPDATE repo SET cloned_at = '" + format.format(repo.clonedAt)
                            + "' WHERE id = " + repo.id
                            + ";";
            lastCommand = UPDATEREPO;
            statement.executeUpdate(UPDATEREPO);
        } catch (SQLException se) {
            se.printStackTrace();
            System.err.println("When executing  : " + lastCommand);
            System.exit(1);
        }
    }

    @Override
    public synchronized Queue<RepoInfo> getToCloneRepos() {
        String lastCommand = "";
        try {
            Queue<RepoInfo> q = new LinkedList<>();
            String SELECTREPO = "SELECT * FROM repo WHERE cloned_at < updated_at;";
            lastCommand = SELECTREPO;
            ResultSet results = statement.executeQuery(SELECTREPO);
            lastCommand = "results.next()";
            while (results.next()) {
                lastCommand = "results.getxxx()";
                int id = results.getInt(1);
                String name = results.getString(2);
                String url = results.getString(3);
                java.util.Date crawledAt = results.getDate(4);
                java.util.Date updatedAt = results.getDate(5);
                java.util.Date clonedAt = results.getDate(6);
                int ownerId = results.getInt(7);
                q.add(new RepoInfo(id, ownerId, name, url, crawledAt, updatedAt, clonedAt));
                lastCommand = "results.next()";
            }
            return q;
        } catch (SQLException se) {
            se.printStackTrace();
            System.err.println("When executing  : " + lastCommand);
            System.exit(1);
            return null;
        }
    }

    public void initDatabaseConnection() {
        try {
            this.dbURL = this.dbParams.getDBLink();
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            this.dbConn = DriverManager.getConnection(this.dbURL, this.dbParams.getDBUser(), this.dbParams.getDBPass());
            this.statement = this.dbConn.createStatement();
            System.out.println("DB conn established");
        } catch (ClassNotFoundException ce) {
            System.err.println("Class \"com.mysql.jdbc.Driver\" not found. Please check runtime libraries.");
            System.exit(1);
        } catch (InstantiationException ie) {
            System.err.println("Failed to load mysql.jdbc.Driver.");
            System.exit(1);
        } catch (IllegalAccessException ie) {
            System.err.println("Failed to access mysql.jdbc.Driver.");
            System.exit(1);
        } catch (SQLException se) {
            System.err.println("Cannot connect to DB");
            System.err.println("Connection URL " + this.dbParams.getDBLink());
            System.err.println("Connection USER " + this.dbParams.getDBUser());
            System.err.println("Connection PASSWORD " + this.dbParams.getDBPass());
            System.err.println(se.getMessage());
            System.exit(1);
        }
        setupDatabaseAndTables();
    }

    public void setupDatabaseAndTables() {
        String lastCommand = "";
        try {
            String CREATEDB = "CREATE DATABASE IF NOT EXISTS GithubCrawler;";
            lastCommand = CREATEDB;
            statement.execute(CREATEDB);
            String USEDB = "USE GithubCrawler";
            lastCommand = USEDB;
            statement.execute(USEDB);
            String CREATETABLE = "CREATE TABLE IF NOT EXISTS user ("
                                + "id INT(11) NOT NULL PRIMARY KEY,"
                                + "name VARCHAR(255) NOT NULL,"
                                + "crawled_at DATETIME NOT NULL"
                                + ");";
            lastCommand = CREATETABLE;
            statement.execute(CREATETABLE);
            String CREATETABLE2 = "CREATE TABLE IF NOT EXISTS repo ("
                                + "id INT(11) NOT NULL PRIMARY KEY,"
                                + "name VARCHAR(255) NOT NULL,"
                                + "git_url VARCHAR(255) NOT NULL,"
                                + "crawled_at DATETIME NOT NULL,"
                                + "updated_at DATETIME NOT NULL,"
                                + "cloned_at DATETIME NOT NULL,"
                                + "owner int(11) NOT NULL,"
                                + "CONSTRAINT to_owner FOREIGN KEY (owner) REFERENCES user(id)"
                                + ");";
            lastCommand = CREATETABLE2;
            statement.execute(CREATETABLE2);
        } catch (SQLException se) {
            se.printStackTrace();
            System.err.println("When executing  : " + lastCommand);
            System.exit(1);
        }
        System.out.println("Database created");
    }

    public void destroyDatabaseConnection(){
        try	{
            dbConn.close();
            System.out.println("DB conn closed");
        } catch (SQLException se) {
            System.exit(1);
        }
    }

}
