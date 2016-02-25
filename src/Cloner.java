import org.eclipse.jgit.api.Git;

import java.io.File;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by RobbinNi on 2/25/16.
 */
public class Cloner implements Runnable {

    private String localStorage;

    private DatabaseInterface db;

    private Queue<RepoInfo> toClone;

    public Cloner(DatabaseInterface db) {
        this.db = db;
        localStorage = "Github/";
    }

    private void renewQueue() {
        try {
            toClone = db.getToCloneRepos();
        } catch (SQLException se) {
            se.printStackTrace();
            toClone = new LinkedList<>();
        }
    }

    private boolean cloneSingle(RepoInfo repo) {
        //return true if success false otherwise

        String url = repo.url;
        File localGit = new File(localStorage + url.replace("git://github.com/", "").replace(".git", "/.git"));

        if (localGit.exists()) {
            try (Git result = Git.open(localGit)) {
                //System.out.println("Pulling repository: " + result.getRepository().getDirectory());
                result.close();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            File localPath = new File(localStorage + url.replace("git://github.com/", "").replace(".git", ""));
            localPath.mkdirs();
            //System.out.println("Cloning from " + url + " to " + localPath);
            try (Git result = Git.cloneRepository()
                    .setURI(url)
                    .setDirectory(localPath)
                    .call()) {
                //System.out.println("Having repository: " + result.getRepository().getDirectory());
                result.close();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        repo.clonedAt = new Date();
        try {
            db.clonedRepo(repo);
        } catch (SQLException se) {
            se.printStackTrace();
            return false;
        }
        return true;
    }

    private void cloneQueue() {
        while (!toClone.isEmpty()) {
            cloneSingle(toClone.poll());
        }
    }

    public void run() {
        while (true) {
            renewQueue();
            cloneQueue();
        }
    }
}
