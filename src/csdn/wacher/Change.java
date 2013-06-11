package csdn.wacher;


public class Change {

    private String changes;
    private long length;

    public Change(String changes, long length){
        this.changes = changes;
        this.length = length;
    }
    
    public String getChanges() {
        return changes;
    }

    public void setChanges(String changes) {
        this.changes = changes;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }
}
