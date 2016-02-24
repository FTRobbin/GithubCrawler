/**
 * Created by Ceno on 2/24/16.
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class GithubDatabase {

    private Connection dbConn;
    private String dbURL;
    private Statement statement;
    private ResultSet results;
    private GithubDBParams dbParams;

    public GithubDatabase(GithubDBParams dbParams){
        this.dbParams = dbParams;

    }
    public static String trimQuery(String Query){
        String trim = Query.replace(',', ' ');
        trim = trim.replace('\'', ' ');
        return trim;
    }

    public boolean createStatement(){

        try {
            this.statement = this.dbConn.createStatement();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;
    }

    public ResultSet executeSelectQuery(String Query) throws SQLException{
        return this.statement.executeQuery( Query );
    }

    public int executeUpdateQuery(String Query) throws SQLException{
        return this.statement.executeUpdate(Query);
    }

    public int executeInsertUserQuery(int userID, String name){
        int count = 0;
        try {
            count = this.statement.executeUpdate("INSERT into user (id, name) values(" + userID + ",'" + name + "')");
        } catch(Exception e) {
            e.printStackTrace();
        }

        return count;
    }

    public int executeInsertRepoQuery(int id, String name, String git_url, int owner, String updated_at){
        int count = 0;
        String tmp = "";
        try {
            tmp ="INSERT into repo (id,name,git_url,owner,updated_at) values(" + id + ",'" + name + "','" + git_url + "'," + owner + ",'" + updated_at + "');";
            count = this.statement.executeUpdate(tmp);

        } catch(Exception e){
            System.err.println(tmp);
            e.printStackTrace();
        }

        return count;
    }

    private String createInsertQueryString(String SQLCommand , String tableName , String [] Values){
        String queryString = "";
        String queryVals = "";
        if( tableName != null )
            if( SQLCommand.equalsIgnoreCase("INSERT") ){
                queryString = queryString.concat( "INSERT into " + tableName + " VALUES ('" );
                System.out.println(Values.length + " Values");
                for( int i = 0; i < Values.length - 1; i++ )
                    queryVals = queryVals.concat( Values[i] + "','" );
                queryString = queryString.concat( queryVals + Values[Values.length - 1] + "')" );
                System.out.println(queryString);
            }

        return queryString;

    }

    public void initDatabaseConnection() {

        try {

            this.dbURL = this.dbParams.getDBLink();
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            //this.dbConn = DriverManager.getConnection(this.dbURL, this.dbParams.getDBUser(), this.dbParams.getDBPass());
            this.dbConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/GitHubCrawler?user=root&password=000000");
            System.out.println("DB conn established");

        } catch(Exception e) {
            System.err.println("Cannot connect to DB");
            System.err.println("Connection URL " + this.dbParams.getDBLink());
            System.err.println("Connection USER " + this.dbParams.getDBUser());
            System.err.println("Connection PASSWORD " + this.dbParams.getDBPass());
            System.err.println(e.getMessage());
        }

    }

    public void destroyDatabaseConnection(){
        try	{
            dbConn.close();
            System.out.println("DB conn closed");
        } catch(Exception e){}
    }
}
