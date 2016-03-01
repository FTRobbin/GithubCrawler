import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.Queue;

/**
 * Created by RobbinNi on 2/25/16.
 */
public class Cloner implements Runnable {

    private String localStorage;

    private DatabaseInterface db;

    private Queue<RepoInfo> toClone;

    public Cloner(DatabaseInterface db, Properties setup) {
        this.db = db;
        localStorage = setup.getProperty("local_path");
    }

    private void renewQueue() {
        toClone = db.getToCloneRepos();
    }

    private boolean cloneSingle(RepoInfo repo) {
        //return true if success false otherwise

        String url = repo.url;
        File localGit = new File(localStorage + url.replace("git://github.com/", "").replace(".git", "/.git"));

        if (localGit.exists()) {
            try (Git result = Git.open(localGit)) {
                result.close();
            } catch (IOException ie) {
                ie.printStackTrace();
                System.err.println("Unable to open local Git file : " + localGit.getPath());
                repo.printRepoInfo();
                System.exit(1);
                return false;
            }
        } else {
            File localPath = new File(localStorage + url.replace("git://github.com/", "").replace(".git", ""));
            localPath.mkdirs();
            try (Git result = Git.cloneRepository()
                    .setURI(url)
                    .setDirectory(localPath)
                    .call()) {
                result.close();
            } catch (GitAPIException ge) {
                ge.printStackTrace();
                System.err.println("Error when pulling remote repository to : " + localPath.getPath());
                repo.printRepoInfo();
                System.exit(1);
                return false;
            }
        }

        repo.clonedAt = new Date();
        db.clonedRepo(repo);
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
