/* *****************************************************************************
 *  Name:              Ivan Betev
 *  Last modified:     05/08/2023
 *  Description:       Percolation with Improved Structure and Logic
 *  Credit:            https://github.com/MeghaJakhotia/AlgorithmsPart-I-Coursera
 **************************************************************************** */

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {

    // Grid's size
    private final int size;
    // Virtual top site's index
    private final int rootIndex;
    // Virtual bottom site's index
    private final int sinkIndex;
    // Tracking array for open sites
    private final boolean[] openSites;
    // Number of sites currently open
    private int numberOfOpenSites;
    // Union-find structure for connectivity and percolation check
    private final WeightedQuickUnionUF matrix;
    // Union-find excluding the sink (to solve backwash)
    private final WeightedQuickUnionUF fullMatrix;

    // Initialize the grid and data structures
    public Percolation(int n) {
        validateSize(n);

        this.size = n;
        this.matrix = new WeightedQuickUnionUF(size * size + 2);
        this.fullMatrix = new WeightedQuickUnionUF(size * size + 1);
        this.rootIndex = 0;
        this.sinkIndex = size * size + 1;
        this.openSites = new boolean[size * size];
    }

    // Open a site and connect to its open neighbors
    public void open(int row, int col) {
        validateIndices(row, col);

        int index = xyTo1D(row, col);

        if (!openSites[index]) {
            openSite(row, col, index);
        }
    }

    // Check if a site is open
    public boolean isOpen(int row, int col) {
        validateIndices(row, col);
        return openSites[xyTo1D(row, col)];
    }

    // Check if a site is connected to the top (full)
    public boolean isFull(int row, int col) {
        validateIndices(row, col);
        return isOpen(row, col) && connectedToRoot(row, col);
    }

    // Return total number of open sites
    public int numberOfOpenSites() {
        return numberOfOpenSites;
    }

    // Check if the system percolates (i.e., top is connected to bottom)
    public boolean percolates() {
        return matrix.find(rootIndex) == matrix.find(sinkIndex);
    }

    // Convert 2D grid coordinates to a 1D array index
    private int xyTo1D(int row, int col) {
        return (row - 1) * size + (col - 1);
    }

    // Validate that the given indices are within the grid's boundaries
    private void validateIndices(int row, int col) {
        if (row <= 0 || col <= 0 || row > size || col > size) {
            throw new IllegalArgumentException("Indices out of bounds.");
        }
    }

    // Validate the given size is greater than 0
    private void validateSize(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("Size should be positive.");
        }
    }

    // Mark a site as open and connect it to its open neighbors
    private void openSite(int row, int col, int index) {
        openSites[index] = true;
        numberOfOpenSites++;

        connectToOpenNeighbors(row, col, index);

        if (isTopRow(row)) {
            unionWithRoot(index);
        }

        if (isBottomRow(row)) {
            matrix.union(index, sinkIndex);
        }
    }

    // Check if a site is connected to the virtual top
    private boolean connectedToRoot(int row, int col) {
        return fullMatrix.find(rootIndex) == fullMatrix.find(xyTo1D(row, col));
    }

    // Connect a site to its open neighbors
    private void connectToOpenNeighbors(int row, int col, int index) {
        int[][] directions = { { 0, 1 }, { 0, -1 }, { 1, 0 }, { -1, 0 } };

        for (int[] direction : directions) {
            int newRow = row + direction[0];
            int newCol = col + direction[1];

            if (isValidPosition(newRow, newCol) && isOpen(newRow, newCol)) {
                int neighborIndex = xyTo1D(newRow, newCol);
                matrix.union(index, neighborIndex);
                fullMatrix.union(index, neighborIndex);
            }
        }
    }

    // Check if the given position is within the grid boundaries
    private boolean isValidPosition(int row, int col) {
        return row > 0 && col > 0 && row <= size && col <= size;
    }

    // Connect a site to the virtual top
    private void unionWithRoot(int index) {
        matrix.union(index, rootIndex);
        fullMatrix.union(index, rootIndex);
    }

    // Check if a row is the top row
    private boolean isTopRow(int row) {
        return row == 1;
    }

    // Check if a row is the bottom row
    private boolean isBottomRow(int row) {
        return row == size;
    }

    // test client
    public static void main(String[] args) {
        // Initialize the Percolation class with a matrix of size 5
        Percolation perc = new Percolation(5);

        // Check if the system percolates initially
        StdOut.println("Should be false: " + perc.percolates());

        // Open a few sites
        perc.open(1, 3);
        perc.open(2, 3);
        perc.open(3, 3);
        perc.open(4, 3);
        perc.open(5, 3);

        // Check if the system percolates now
        StdOut.println("Should be true: " + perc.percolates());

        // Check number of open sites
        StdOut.println("Should be 5: " + perc.numberOfOpenSites());

        // Check if a particular site is open
        StdOut.println("Should be true: " + perc.isOpen(3, 3));
        StdOut.println("Should be false: " + perc.isOpen(3, 2));

        // Check if a particular site is full
        StdOut.println("Should be true: " + perc.isFull(3, 3));
        StdOut.println("Should be false: " + perc.isFull(5, 2));

        // Validate exceptions
        try {
            perc.open(6, 3);
            StdOut.println("This line shouldn't be printed.");
        }
        catch (IllegalArgumentException e) {
            StdOut.println("Successfully caught exception for out-of-bound open.");
        }

        try {
            perc.isFull(6, 3);
            StdOut.println("This line shouldn't be printed.");
        }
        catch (IllegalArgumentException e) {
            StdOut.println("Successfully caught exception for out-of-bound isFull check.");
        }
    }
}
