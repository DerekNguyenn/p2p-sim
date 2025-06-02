import java.util.HashSet;
import java.util.Set;

public abstract class PeerNode extends NetworkNode {
    protected Set<Integer> ownedChunks;
    protected double uploadSpeed;
    protected double downloadSpeed;
    protected int totalChunks;

    public PeerNode(int id, double x, double y, int totalChunks) {
        super(id, x, y);
        this.totalChunks = totalChunks;
        this.ownedChunks = new HashSet<Integer>();
        this.uploadSpeed = 50 + Math.random() * 100; // 50 - 150 KB/s
        this.downloadSpeed = 100 + Math.random() * 200; // 50 - 150 KB/s
    }

    public boolean hasChunk(int chunkIndex) {
        return ownedChunks.contains(chunkIndex);
    }

    public boolean hasCompleteFile() {
        return ownedChunks.size() == totalChunks;
    }

    public void receiveChunk(int chunkIndex) {
        ownedChunks.add(chunkIndex);
    }

    public Set<Integer> getOwnedChunks() {
        return ownedChunks;
    }

    public double getUploadSpeed() {
        return uploadSpeed;
    }

    public double getDownloadSpeed() {
        return downloadSpeed;
    }

    public abstract String getNodeType();
}
