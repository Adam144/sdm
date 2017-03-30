package kmeans;

import java.util.Arrays;
import java.util.stream.Collectors;

public class DataPoint {

    private final int dimensions;
    private final Double[] coordinates;
    private DataCluster cluster;
    
    public DataPoint(int dimensions) throws KmeansException {
        if(dimensions <= 0)
            throw new KmeansException("dimensionality must be greater then 0");
        
        this.dimensions = dimensions;

        this.coordinates = new Double[this.dimensions];
        
        for(int i = 0; i < dimensions; i++)
            set(i, 0d);
    }
    
    public DataPoint(double[] coordinates) throws KmeansException {
        
        if(coordinates == null || coordinates.length < 1)
            throw new KmeansException("coordinates must not be null or of dimensionality 0");

        this.dimensions = coordinates.length;

        this.coordinates = new Double[this.dimensions];
        
        for(int i = 0; i < this.dimensions; i++)
            this.set(i, coordinates[i]);
    }
        
    public void set(int dimension, double value) throws KmeansException {
        if(dimension > this.dimensions || dimension < 0)
            throw new KmeansException("invalid dimension passed");
        
        this.coordinates[dimension] = value;
    }
    
    public double get(int dimension) throws KmeansException {
        if(dimension > this.dimensions || dimension < 0)
            throw new KmeansException("invalid dimension passed");
        
        return this.coordinates[dimension];
    }
    
    public double getSafe(int dimension) {
        return this.coordinates[dimension];
    }
            
    public int getDimensions() {
        return this.dimensions;
    }
    
    public DataPoint add(DataPoint other) throws KmeansException {
        if(other.dimensions != this.dimensions)
            throw new KmeansException("cannot add two points with different dimensions");
        
        DataPoint dp = new DataPoint(this.dimensions);
        
        for (int dimension = 0; dimension < this.dimensions; dimension++) {
            dp.set(dimension, this.get(dimension) + other.get(dimension));
        }
        
        return dp;
    }

    @Override
    public String toString() {
        String result = Arrays.stream(this.coordinates).map(c -> c.toString()).collect(Collectors.joining("|"));
        return result;
    }
    
    public void setCluster(DataCluster cluster) throws KmeansException {
        if(this.cluster != null && this.cluster != cluster)
            throw new KmeansException("Connot set cluster because it is already set");
        
        this.cluster = cluster;
    }
    
    public DataCluster getCluster() {
        return this.cluster;
    }
    
    public double getDistance(DataPoint other) throws KmeansException {
        if(other.dimensions != this.dimensions)
            throw new KmeansException("cannot add two points with different dimensions");
        
        double sumSquaredDistances = 0;
        for(int dimension = 0; dimension < getDimensions(); dimension++) {
            double distance = this.get(dimension) - other.get(dimension);
            sumSquaredDistances += Math.pow(distance, 2d);
        }
        
        double result = Math.sqrt(sumSquaredDistances);
        return result;
    }
    
}
