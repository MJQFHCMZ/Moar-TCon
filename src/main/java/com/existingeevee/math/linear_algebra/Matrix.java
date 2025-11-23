package com.existingeevee.math.linear_algebra;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function; 

public class Matrix implements Function<VecNd, VecNd> {

	private final double[][] data;

	public Matrix(int rows, int cols) {
		this.data = new double[rows][cols];
	}

	public Matrix(double[][] data) {
		this.data = copy2DArray(data);

		if (!isValid()) {
			throw new IllegalArgumentException("Matrix data length is not consistant");
		}
	}

	public static Matrix withRows(VecNd... rows) {
		Matrix matrix = new Matrix(rows.length, rows[0].size());
		for (int i = 0; i < rows.length; i++) {
			matrix = matrix.withRow(i, rows[i]);
		}
		return matrix;
	}

	public static Matrix withColumns(VecNd... cols) {
		Matrix matrix = new Matrix(cols[0].size(), cols.length);
		for (int i = 0; i < cols.length; i++) {
			matrix = matrix.withColumn(i, cols[i]);
		}
		return matrix;
	}
	
	public boolean isValid() {
		return Arrays.stream(data).allMatch(d -> d.length == data[0].length);
	}

	public int getNumRows() {
		return data.length;
	}

	public int getNumCols() {
		return data[0].length;
	}

	public double get(int row, int col) {
		return getRow(row).get(col);
	}

	public VecNd getRow(int row) {
		if (!isRowInBound(row)) {
			throw new IllegalArgumentException("Row (" + row + ") not in bound");
		}

		return new VecNd(data[row]);
	}

	public VecNd getColumn(int col) {
		if (!isColInBound(col)) {
			throw new IllegalArgumentException("Column (" + col + ")  not in bound");
		}

		double[] ret = new double[data.length];
		for (int i = 0; i < data.length; i++) {
			ret[i] = data[i][col];
		}

		return new VecNd(ret);
	}

	public boolean isRowInBound(int row) {
		return row >= 0 && row < getNumRows();
	}

	public boolean isColInBound(int col) {
		return col >= 0 && col < getNumCols();
	}

	public Matrix withRow(int row, VecNd newRow) {
		return this.withRow(row, newRow.asDoubleArray());
	}

	public Matrix with(int row, int col, double newVal) {
		if (!isRowInBound(row)) {
			throw new IllegalArgumentException("Row (" + row + ") not in bound");
		}
		if (!isColInBound(col)) {
			throw new IllegalArgumentException("Column (" + col + ")  not in bound");
		}
		double[][] copy = copy2DArray(data);
		copy[row][col] = newVal;
		return new Matrix(copy);
	}

	public Matrix withRow(int row, double... newRow) {
		if (!isRowInBound(row)) {
			throw new IllegalArgumentException("Row (" + row + ") not in bound");
		}

		double[][] copy = copy2DArray(data);
		copy[row] = newRow;
		return new Matrix(copy);
	}

	public Matrix withColumn(int col, VecNd newRow) {
		return this.withColumn(col, newRow.asDoubleArray());
	}

	public Matrix withColumn(int col, double... newCol) {
		if (!isColInBound(col)) {
			throw new IllegalArgumentException("Column (" + col + ")  not in bound");
		}
		if (newCol.length != getNumRows()) {
			throw new IllegalArgumentException("Matrix data length is not consistant");
		}

		double[][] copy = copy2DArray(data);
		for (int i = 0; i < newCol.length; i++) {
			copy[i][col] = newCol[i];
		}

		return new Matrix(copy);
	}

	public double[][] getData() {
		return copy2DArray(data);
	}

	public Matrix swapRows(int row1, int row2) {
		if (row1 == row2) {
			return this;
		}
		return this.withRow(row2, this.getRow(row1)).withRow(row1, this.getRow(row2));
	}

	public Matrix replaceSumRow(int row, int scaledRow, double scaleFactor) {
		VecNd rowVec = this.getRow(row);
		return this.withRow(row, rowVec.add(this.getRow(scaledRow).scale(scaleFactor)));
	}

	protected static double[][] copy2DArray(double[][] array) {
		double[][] deepcopy = new double[array.length][];

		for (int i = 0; i < array.length; i++) {
			deepcopy[i] = Arrays.copyOf(array[i], array[i].length);
		}

		return deepcopy;
	}

	public String display() {
		String ret = "";
		for (int i = 0; i < this.getNumRows(); i++) {
			for (int j = 0; j < this.getNumCols(); j++) {
				ret += String.format("%10.5f ", get(i, j));

				if (j == this.getNumCols() - 1)
					ret += "\n";
			}
		}
		return ret;
	}

	@Override
	public String toString() {
		return "Matrix" + Arrays.deepToString(data);
	}

	public Matrix scaleRow(int row, double d) {
		return this.withRow(row, this.getRow(row).scale(d));
	}

	public Matrix scaleColumn(int col, double d) {
		return this.withColumn(col, this.getColumn(col).scale(d));
	}

	@Override
	public VecNd apply(VecNd t) {
		double[] ret = new double[getNumRows()];
		for (int i = 0; i < getNumRows(); i++) {
			ret[i] = this.getRow(i).dotProduct(t);
		}
		return new VecNd(ret);
	}

	public Matrix multiply(Matrix other) {
		double[][] productData = new double[getNumRows()][other.getNumCols()];

		for (int i = 0; i < productData.length; i++) {
			for (int j = 0; j < productData[0].length; j++) {
				productData[i][j] = this.getRow(i).dotProduct(other.getColumn(j));
			}
		}

		return new Matrix(productData);
	}

	public Matrix toAugmentedMatrix(VecNd b) {
		if (b.size() != this.getNumRows()) {
			throw new IllegalArgumentException("Vector does not match size of matrix");
		}

		double[][] newData = new double[this.getNumRows()][this.getNumCols() + 1];

		// copy the data to new matrix
		for (int i = 0; i < this.getNumRows(); i++) {
			for (int j = 0; j < this.getNumCols(); j++) {
				newData[i][j] = this.get(i, j);
			}
		}

		// put in the the b vector
		for (int i = 0; i < this.getNumRows(); i++) {
			newData[i][this.getNumCols()] = b.get(i);
		}

		return new Matrix(newData);
	}

	public boolean isInvertible() {
		if (getNumCols() != getNumRows()) {
			return false;
		}

		return this.getDeterminant() != 0;
	}

	public Matrix invert() {
		if (!isInvertible()) {
			throw new IllegalStateException("Matrix is not invertible");
		}

		Matrix inverted = new Matrix(this.getNumRows(), this.getNumCols());

		// Yes i know this is inefficient, but i dont want to rewrite the entirety of my
		// gaussian elim code lmfao
		for (int i = 0; i < this.getNumCols(); i++) {
			VecNd e_i = VecNd.standardBasisVector(this.getNumRows(), i);
			Matrix augmented = LinearAlgebraUtils.toRREF(this.toAugmentedMatrix(e_i));
			inverted = inverted.withColumn(i, augmented.getColumn(augmented.getNumCols() - 1));
		}

		return inverted;
	}

	public Matrix inverseMultiply(Matrix other) {
		return other.multiply(this);
	}

	public Matrix scale(double scalar) {
		double[][] scaled = copy2DArray(data);

		for (int i = 0; i < scaled.length; i++) {
			for (int j = 0; j < scaled[0].length; j++) {
				scaled[i][j] *= scalar;
			}
		}

		return new Matrix(scaled);
	}

	public static Matrix identity(int size) {
		double[][] identity = new double[size][size];

		for (int i = 0; i < size; i++) {
			identity[i][i] = 1;
		}

		return new Matrix(identity);
	}

	public Matrix subtract(Matrix other) {
		return add(other.scale(-1));
	}

	public Matrix add(Matrix other) {
		if (this.getNumCols() != other.getNumCols() || this.getNumRows() != other.getNumRows())
			throw new IllegalArgumentException("Matrix data length is not consistant");

		double[][] added = copy2DArray(data);

		for (int i = 0; i < added.length; i++) {
			for (int j = 0; j < added[0].length; j++) {
				added[i][j] += other.get(i, j);
			}
		}

		return new Matrix(added);
	}
	
	public double getDeterminant() {
		if (this.getNumRows() != this.getNumCols()) {
			throw new IllegalStateException("Matrix rows and columns do not equal");
		}
		
		if (this.getNumRows() == 1 && this.getNumCols() == 1) {
			return this.get(0, 0);
		}
		
		double total = 0;
		
		for (int z = 0; z < this.getNumCols(); z++) {
			if (this.get(0, z) == 0) {
				continue; //its gonna be zero lmao
			}
			
			double[][] newData = new double[this.getNumCols() - 1][this.getNumRows() - 1];
						
			int x = 0; 
			
			for (int i = 0; i < this.getNumCols(); i++) {
				if (i == z)
					continue;
				
				for (int j = 1; j < this.getNumRows(); j++) { //we skip the first one
					newData[j - 1][x] = this.get(j, i);
				}
				x++;
			}
			
			Matrix newMatrix = new Matrix(newData);
			total += (z % 2 == 0 ? 1 : -1) * this.get(0, z) * newMatrix.getDeterminant();

		}
		return total;
	}
	
	public List<VecNd> getRows() {
		List<VecNd> list = new ArrayList<>();
		
		for (int i = 0; i < this.getNumRows(); i++) {
			list.add(this.getRow(i));
		}
		return list;
	}

	public List<VecNd> getColumns() {
		List<VecNd> list = new ArrayList<>();
		
		for (int i = 0; i < this.getNumCols(); i++) {
			list.add(this.getColumn(i));
		}
		return list;
	}
}
