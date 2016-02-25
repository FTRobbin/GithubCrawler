import java.util.Queue;

/**
 * Created by RobbinNi on 2/25/16.
 */
public class Cloner implements Runnable {

    private DatabaseInterface db;

    private Queue<RepoInfo> toClone;

    public Cloner(DatabaseInterface db) {
        this.db = db;
    }

    private void renewQueue() {
        toClone = db.getToCloneRepos();
    }

    private void cloneSingle(RepoInfo repo) {
        //TODO
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
