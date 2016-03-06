import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;

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

    private int waitTime;

    public Cloner(DatabaseInterface db, Properties setup) {
        this.db = db;
        localStorage = setup.getProperty("local_path");
        waitTime = Integer.valueOf(setup.getProperty("waittime"));
    }

    private void renewQueue() {
        toClone = db.getToCloneRepos();
        System.out.println("Get " + toClone.size() + " repos to clone.");
    }

    private boolean cloneSingle(RepoInfo repo) {
        //return true if success false otherwise

        String url = repo.url;
        File localGit = new File(localStorage + url.replace("git://github.com/", "").replace(".git", "/.git"));

        if (localGit.exists()) {
            try (Git result = Git.open(localGit)) {
                result.pull().call();
                result.close();
            } catch (JGitInternalException je) {
                je.printStackTrace();
                System.err.println("Error when pulling remote repository to : " + localGit.getPath());
                repo.printRepoInfo();
                //System.exit(1);
                return false;
            } catch (GitAPIException ge) {
                ge.printStackTrace();
                System.err.println("Error when pulling remote repository to : " + localGit.getPath());
                repo.printRepoInfo();
                return false;
            } catch (IOException ie) {
                ie.printStackTrace();
                System.err.println("Unable to open local Git file : " + localGit.getPath());
                repo.printRepoInfo();
                //System.exit(1);
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
            } catch (JGitInternalException je) {
                je.printStackTrace();
                System.err.println("Error when cloning remote repository to : " + localPath.getPath());
                repo.printRepoInfo();
                //System.exit(1);
                return false;
            } catch (GitAPIException ge) {
                ge.printStackTrace();
                System.err.println("Error when cloning remote repository to : " + localPath.getPath());
                repo.printRepoInfo();
                //System.exit(1);
                return false;
            }
        }

        repo.clonedAt = new Date();
        db.clonedRepo(repo);
        System.out.println("Successfully cloned " + repo.url);
        return true;
    }

    private void cloneQueue() {
        while (!toClone.isEmpty()) {
            cloneSingle(toClone.poll());
        }
    }

    public void run() {
        while (true) {
            db.checkDatabaseStatus();
            renewQueue();
            cloneQueue();
            try {
                Thread.sleep(waitTime);
            } catch (InterruptedException ie) {
                //Do nothing
            }
        }
    }
}
