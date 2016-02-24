/**
 * Created by RobbinNi on 2/24/16.
 */
public class Main {

    static public void main(String args[]) {
        GithubDBParams dbParams = new GithubDBParams("localhost" , "root" , "000000", "GitHubCrawler"
                , "jdbc" , "mysql" , "3306");
        GithubDatabase DB = new GithubDatabase(dbParams);
        DB.initDatabaseConnection();
        DB.createStatement();
        //String [] fields = {"1232","yanpei3"};
        System.err.println(DB.executeInsertRepoQuery(1233,"sdfds2","sdfsfd",2,"9999-12-31 23:59:59"));
        DB.destroyDatabaseConnection();

    }
}
