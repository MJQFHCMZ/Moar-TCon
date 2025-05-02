package com.existingeevee.math.linear_algebra;

public class LinearAlgebraUtils {

	public static boolean debugPrintFlag = false;

	private final static double EPSILON = 1E-6;

//	public static Vec3d[] gromSchmit(Vec3d... vecs) {
//		
//	}
	
	public static VecNd closestInSpan(VecNd vector, VecNd... orthonormalBasis) {
		VecNd ret = VecNd.zeroVecWithSize(vector.size());
		for (VecNd u : orthonormalBasis) {
			ret.add(u.scale(vector.dotProduct(u)));
		}
		return ret;
	}
			
	public static VecNd[] gramSchmidt(VecNd... basis) {
		VecNd[] orthonormalBasis = new VecNd[basis.length];
		for (int i = 0; i < basis.length; i++) {
			VecNd cur = basis[i];
			for (int j = 0; j < i; j++) {
				VecNd partialProjection = orthonormalBasis[j].scale(orthonormalBasis[j].dotProduct(basis[i]));
				cur = cur.subtract(partialProjection);
			}
			orthonormalBasis[i] = cur.normalize();
		}
		return orthonormalBasis;
	}
	
	public static Matrix toRREF(Matrix augmentedMatrix) {
		if (!isInEF(augmentedMatrix)) {
			if (debugPrintFlag)
				System.out.println("converting to REF");
			augmentedMatrix = toEF(augmentedMatrix); 
		}
		
		int rows = augmentedMatrix.getNumRows();

		for (int cur = rows - 1; cur >= 0; cur--) {
			double pivot = augmentedMatrix.getRow(cur).firstNonZero();
			int pos = augmentedMatrix.getRow(cur).posFirstNonZero();

			if (pos >= 0) {
				if (Math.abs(pivot - 1) > EPSILON) {
					if (debugPrintFlag)
						System.out.println("scaleRow(" + cur + ", " + (1 / pivot) + ")");
					augmentedMatrix = augmentedMatrix.scaleRow(cur, 1 / pivot);
					pivot = 1;
					if (debugPrintFlag)
						System.out.println(augmentedMatrix.display());
				}
				
				for (int i = cur - 1; i >= 0; i--) {
					double j = augmentedMatrix.get(i, pos);
					
					if (Math.abs(j) < EPSILON) {
						continue;
					}
					
					if (debugPrintFlag)
						System.out.println("replaceSumRow(" + i + ", " + cur + ", " + (-j / pivot) + ") - " + j);

					augmentedMatrix = augmentedMatrix.replaceSumRow(i, cur, -j / pivot);

					if (debugPrintFlag)
						System.out.println(augmentedMatrix.display());
				}
			}
		}
		
		return augmentedMatrix;
	}

	public static Matrix toEF(Matrix augmentedMatrix) {
		if (isInEF(augmentedMatrix)) {
			return augmentedMatrix;
		}

		int rows = augmentedMatrix.getNumRows();
		int cols = augmentedMatrix.getNumCols();

		int prevLead = -1;

		for (int cur = 0; cur < cols && cur < rows; cur++) {

			// find pivot row and swap
			// we use cols - 1 because we dont want to mess with the b vector
			int pivotRow = cur;

			for (int i = prevLead + 1; i < rows; i++) {
				if (Math.abs(augmentedMatrix.get(i, cur)) > EPSILON) {
					pivotRow = i;
					break;
				}
			}

			if (debugPrintFlag)
				System.out.println("swapRows(" + pivotRow + ", " + cur + ")");

			augmentedMatrix = augmentedMatrix.swapRows(pivotRow, cur);

			if (debugPrintFlag)
				System.out.println(augmentedMatrix.display());

			double pivot = augmentedMatrix.getRow(cur).firstNonZero();
			int pos = augmentedMatrix.getRow(cur).posFirstNonZero();

			prevLead = pos;

			if (pos >= 0) {
				for (int i = cur + 1; i < rows; i++) {
					double j = augmentedMatrix.get(i, pos);

					if (Math.abs(j) < EPSILON) {
						continue;
					}
					if (debugPrintFlag)
						System.out.println("replaceSumRow(" + i + ", " + cur + ", " + (-j / pivot) + ")");

					augmentedMatrix = augmentedMatrix.replaceSumRow(i, cur, -j / pivot);

					if (debugPrintFlag)
						System.out.println(augmentedMatrix.display());
				}
			}
		}

		return augmentedMatrix;
	}

	public static boolean isInEF(Matrix augmentedMatrix) {
		int largestLead = -1;

		for (int i = 0; i < augmentedMatrix.getNumRows(); i++) {
			int lead = 0;
			for (double d : augmentedMatrix.getRow(i)) {
				if (Math.abs(d) < EPSILON) {
					lead++;
				} else {
					break;
				}
			}

			if (lead <= largestLead) {
				return false;
			} else {
				largestLead = lead;
			}
		}

		return true;
	}
	
//	//oh cool all we need to do is scale it
//	for (int i = 0; i < augmentedMatrix.getNumRows(); i++) {
//		for (int j = i; j < augmentedMatrix.getNumCols(); j++) {
//			double num = augmentedMatrix.get(i, j);
//			if (Math.abs(num - 0) < EPSILON) {
//				continue;
//			}
//			augmentedMatrix.scale(1 / num);
//		}
//	}
}
