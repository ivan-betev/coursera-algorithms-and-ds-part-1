/* *****************************************************************************
 *  Name:              Ivan Betev
 *  Last modified:     05/08/2023
 *  Description:       Percolation Stats with Improved Structure and Logic
 *  Credit:            https://github.com/MeghaJakhotia/AlgorithmsPart-I-Coursera
 **************************************************************************** */

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

// Class to compute statistics for the percolation model
public class PercolationStats {

    // Constant value for the 95% confidence interval computation
    private static final double CONFIDENCE = 1.96;

    // Array to store the percolation threshold from each trial
    private final double[] thresholds;

    // Cached values for mean and standard deviation to avoid redundant computations
    private Double meanValue = null;
    private Double stdDeviation = null;

    // Constructor initializes and runs the experiments
    public PercolationStats(int n, int trials) {
        if (n <= 0 || trials <= 0) {
            throw new IllegalArgumentException("Dimensions and trials should be greater than 0.");
        }

        // Initialize the thresholds array to the number of trials
        thresholds = new double[trials];

        // Run each trial and store the result in the thresholds array
        for (int i = 0; i < trials; i++) {
            thresholds[i] = runExperiment(n);
        }
    }

    // Helper method to execute a single percolation experiment and return the percolation threshold
    private double runExperiment(int n) {
        Percolation matrix = new Percolation(n);

        // Continue opening sites until the system percolates
        while (!matrix.percolates()) {
            int row = StdRandom.uniformInt(1, n + 1);
            int col = StdRandom.uniformInt(1, n + 1);
            matrix.open(row, col);
        }

        // Return the ratio of opened sites to total sites as the percolation threshold
        return (double) matrix.numberOfOpenSites() / (n * n);
    }

    // Compute or retrieve the mean of the percolation thresholds
    public double mean() {
        if (meanValue == null) {
            meanValue = StdStats.mean(thresholds);
        }
        return meanValue;
    }

    // Compute or retrieve the standard deviation of the percolation thresholds
    public double stddev() {
        if (stdDeviation == null) {
            stdDeviation = StdStats.stddev(thresholds);
        }
        return stdDeviation;
    }

    // Compute the low endpoint of the 95% confidence interval
    public double confidenceLo() {
        return mean() - (CONFIDENCE * stddev() / Math.sqrt(thresholds.length));
    }

    // Compute the high endpoint of the 95% confidence interval
    public double confidenceHi() {
        return mean() + (CONFIDENCE * stddev() / Math.sqrt(thresholds.length));
    }

    // Print out the computed statistics
    private void displayStats() {
        StdOut.printf("mean                    = %f\n", mean());
        StdOut.printf("stddev                  = %f\n", stddev());
        StdOut.printf("95%% confidence interval = [%f, %f]", confidenceLo(), confidenceHi());
    }

    // Main method to run the program
    public static void main(String[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException("Please provide valid dimensions and trial count.");
        }

        // Parse command line arguments for grid size and trial count
        int n = Integer.parseInt(args[0]);
        int t = Integer.parseInt(args[1]);

        // Create a PercolationStats object and display results
        PercolationStats perc = new PercolationStats(n, t);
        perc.displayStats();
    }
}
