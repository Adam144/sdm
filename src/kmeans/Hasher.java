/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kmeans;
import java.util.Random;

/**
 *
 * @author adam.vondracek
 */
public class Hasher {
    
    private DataPoint[] dataPoints;
    private DataPoint[] hashes;
    private double[][] hashFunctions;
    private int numberOfHashes;
    private int dimensionOfPoints;
    
    public Hasher(DataPoint[] dataPoints, int numberOfHashes) throws KmeansException {
        this.dataPoints = dataPoints;
        this.hashes = new DataPoint[this.dataPoints.length];
        for (int i = 0; i < this.dataPoints.length; i++){
            this.hashes[i] = new DataPoint(numberOfHashes);
        }
        if (numberOfHashes < 1) {
            throw new KmeansException("Number of hashes must be greater then 0.");
        }
        this.numberOfHashes = numberOfHashes;// how many hash functions I will use
        this.dimensionOfPoints = dataPoints.length > 0 ? dataPoints[0].getDimensions() : 0;
        this.hashFunctions = new double[this.numberOfHashes][this.dimensionOfPoints];
        generateHashFunction();
    }
    
    private double getHashOfPoint(DataPoint point, double[] hashFunction) throws KmeansException {
        double hashValue = 0;
        for (int i = 0; i < this.dimensionOfPoints; i++){
            hashValue += point.get(i)*hashFunction[i];
        }
        return hashValue;
    }
    
    public DataPoint getAllHashesOfPoint(DataPoint point) throws KmeansException { //vector of hashes
        DataPoint hash = new DataPoint(this.numberOfHashes);
        for (int j = 0; j < this.numberOfHashes; j++){
            hash.set(j, getHashOfPoint(point, hashFunctions[j]));
        }
        return hash;
    }
    
    private void generateHashFunction(){//hash function is vector of coeficients from N(0,1)
        Random random = new Random();
        for (int j = 0; j < this.numberOfHashes; j++){
            for (int i = 0; i < this.dimensionOfPoints; i++){
                this.hashFunctions[j][i] = random.nextGaussian();
            }
        }
    }
    
    public DataPoint[] getHashes() throws KmeansException {
        for (int j = 0; j < this.dataPoints.length; j++){
            DataPoint hash = getAllHashesOfPoint(this.dataPoints[j]);
            this.hashes[j] = hash;
        }
        return this.hashes;
    }
    
    public double[] getHashFunction(int n){
        return this.hashFunctions[n];
    }
}
