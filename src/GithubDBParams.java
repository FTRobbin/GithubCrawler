/**
 * Created by Ceno on 2/24/16.
 */
public class GithubDBParams {

    private String dbUserName; // User name for database
    private String dbPassword; //Password for database
    private String dbName;
    private String dbAdapter; // JDBC
    private String dbType; // MySQL, PGSQL
    private String dbHostAddr; // IP Address of host
    private String dbPort; // Port where db listens

    public GithubDBParams(String hostAddr, String dbUser, String dbPass, String dbName, String adapter
            , String dbType, String dbPort){

        this.dbUserName = dbUser;
        this.dbPassword = dbPass;
        this.dbName = dbName;
        this.dbAdapter = adapter;
        this.dbType = dbType;
        this.dbHostAddr = hostAddr;
        this.dbPort = dbPort;


    }

    public String getDBLink(){
        return this.dbAdapter + ":" + this.dbType + "://" + this.dbHostAddr + ":"
                + this.dbPort + "/" + this.dbName;
    }

    public String[] getParams(){

        String [] params= new String[7];

        params[0] = this.dbUserName;
        params[1] = this.dbPassword;
        params[2] = this.dbName;
        params[3] = this.dbAdapter;
        params[4] = this.dbType;
        params[5] = this.dbHostAddr;
        params[6] = this.dbPort;

        return params;
    }

    public String getDBUser(){
        return this.dbUserName;
    }

    public String getDBPass(){
        return this.dbPassword;
    }

    public String getDBName(){
        return this.dbName;
    }

    public String getDBAdapter(){
        return this.dbAdapter;
    }

    public String getDBType(){
        return this.dbType;
    }

    public String getDBHost(){
        return this.dbHostAddr;
    }

    public String getDBPort(){
        return this.dbPort;
    }
}
