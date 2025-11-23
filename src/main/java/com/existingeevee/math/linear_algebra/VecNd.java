package com.existingeevee.math.linear_algebra;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import com.existingeevee.math.number.ComplexDouble;

import net.minecraft.util.math.Vec3d;

public class VecNd implements Collection<Double> {
	private final static double EPSILON = 1E-8;

	private final List<Double> values;

	public VecNd(double... vals) {
		List<Double> tempList = new ArrayList<>();

		for (double d : vals) {
			tempList.add(d);
		}

		values = Collections.unmodifiableList(tempList);
	}

	public static VecNd fromAngle(double theta) {
		return new VecNd(Math.cos(theta), Math.sin(theta));
	}

	public static VecNd fromAngle(double theta, boolean degrees) {
		return fromAngle(degrees ? (theta / 180 * Math.PI) : theta);
	}

	public VecNd normalize() {
		double len = this.length();
		return len < 1E-4D ? VecNd.zeroVecWithSize(this.size()) : this.scale(1 / len);
	}

	public double dotProduct(VecNd vec) {
		verifyVectorSize(vec);

		double ret = 0;
		for (int i = 0; i < this.size(); i++) {
			ret += get(i) * vec.get(i);
		}
		return ret;
	}

	public VecNd subtract(VecNd vec) {
		return this.add(vec.scale(-1));
	}

	public VecNd subtract(double... vals) {
		return this.subtract(new VecNd(vals));
	}

	public VecNd subtractReverse(VecNd vec) {
		return vec.subtract(this);
	}

	public VecNd subtractReverse(double... vals) {
		return subtractReverse(new VecNd(vals));
	}

	public VecNd add(VecNd vec) {
		verifyVectorSize(vec);
		double[] newVals = new double[this.size()];
		for (int i = 0; i < newVals.length; i++) {
			newVals[i] = vec.get(i) + this.get(i);
		}
		return new VecNd(newVals);
	}

	public VecNd add(double... vals) {
		return add(new VecNd(vals));
	}

	public double distanceTo(VecNd vec) {
		return Math.sqrt(squareDistanceTo(vec));
	}

	public double squareDistanceTo(VecNd vec) {
		this.verifyVectorSize(vec);

		double ret = 0;
		for (int i = 0; i < size(); i++) {
			double dv = values.get(i) - vec.values.get(i);
			ret += dv * dv;
		}
		return ret;
	}

	public double squareDistanceTo(double... vals) {
		return squareDistanceTo(new VecNd(vals));
	}

	public VecNd scale(double factor) {
		double[] newVals = new double[this.size()];
		for (int i = 0; i < newVals.length; i++) {
			newVals[i] = this.values.get(i) * factor;
		}
		return new VecNd(newVals);
	}

	public double length() {
		return Math.sqrt(lengthSquared());
	}

	public double lengthSquared() {
		return this.dotProduct(this);
	}

	public double[] asDoubleArray() {
		return values.stream().mapToDouble(Double::doubleValue).toArray();
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		} else if (!(other instanceof VecNd)) {
			return false;
		} else {
			VecNd vec3d = (VecNd) other;

			if (this.size() != vec3d.size()) {
				return false;
			}

			for (int i = 0; i < size(); i++)
				if (this.get(i) != vec3d.get(i))
					return false;

			return true;
		}
	}

	public int hashCode() {
		int hashCode = 1;
		for (Double e : this)
			hashCode = 31 * hashCode + e.hashCode();
		return hashCode;
	}

	public static VecNd zeroVecWithSize(int vectorSize) {
		return new VecNd(new double[vectorSize]);
	}

	// Keep in mind that my impl starts at 0, so what typically is e1 will be e0
	public static VecNd standardBasisVector(int size, int at) {
		return zeroVecWithSize(size).with(at, 1);
	}

	public VecNd with(int at, int i) {
		double[] arr = this.asDoubleArray();
		arr[at] = i;
		return new VecNd(arr);
	}

	public double get(int index) {
		return values.get(index);
	}

	@Override
	public int size() {
		return values.size();
	}

	public void verifyVectorSize(VecNd other) {
		if (other.size() != this.size()) {
			throw new IllegalArgumentException("Vector sizes do not match");
		}
	}

	public String toString() {
		if (size() == 0) {
			return "VecNd()";
		} else {
			String ret = "VecNd(";
			for (int i = 0; i < size() - 1; i++) {
				ret += get(i) + ", ";
			}

			return ret + get(size() - 1) + ")";
		}
	}

	@Override
	public Iterator<Double> iterator() {
		return new Iterator<Double>() {
			private final Iterator<Double> i = values.iterator();

			@Override
			public boolean hasNext() {
				return i.hasNext();
			}

			@Override
			public Double next() {
				return i.next();
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

			@Override
			public void forEachRemaining(Consumer<? super Double> action) {
				i.forEachRemaining(action);
			}
		};
	}

	@Override
	public boolean isEmpty() {
		return values.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return values.contains(o);
	}

	@Override
	public Double[] toArray() {
		return values.toArray(new Double[0]);
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return values.toArray(a);
	}

	@Override
	public boolean add(Double e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return values.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends Double> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	public double firstNonZero() {
		for (Double d : this) {
			if (Math.abs(d) > EPSILON) {
				return d;
			}
		}
		return 0;
	}

	public int posFirstNonZero() {
		int i = 0;
		for (Double d : this) {
			if (Math.abs(d) > EPSILON) {
				return i;
			}
			i++;
		}
		return -1;
	}

	public Vec3d asVec3d() {
		if (this.size() != 3) {
			throw new IllegalStateException("Size does not equal 3");
		}
		return new Vec3d(get(0), get(1), get(2));
	}

	public VecNd rot2d(double theta) {
		if (this.size() != 2) {
			throw new IllegalStateException("Size does not equal 2");
		}
		ComplexDouble complex = ComplexDouble.fromVecNd(this);
		return complex.rotate(theta).asVecNd();
	}

	public static VecNd fromVec3d(Vec3d vec3d) {
		return new VecNd(vec3d.x, vec3d.y, vec3d.z);
	}
}
