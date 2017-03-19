package kmeans;

public class KmeansDataPoint {
    private final DataPoint point;
    private KmeansCluster cluster;
    
    public KmeansDataPoint(DataPoint point) {
        this.point = point;
    }
    
    public void setCluster(KmeansCluster cluster) throws KmeansException {
        if(this.cluster != null)
            this.cluster.removeDataPoint(this);
        
        this.cluster = cluster;
        this.cluster.addDataPoint(this);
    }
    
    public KmeansCluster getCluster() {
        return this.cluster;
    }
    
    public DataPoint getDataPoint() {
        return this.point;
    }
}
