package com.existingeevee.math.number;

import java.text.DecimalFormat;

import com.existingeevee.math.linear_algebra.VecNd;

public class ComplexDouble {

	protected static DecimalFormat format = new DecimalFormat("0.0###############################");

	public static final ComplexDouble ONE = new ComplexDouble(1, 0);
	public static final ComplexDouble I = new ComplexDouble(0, 1);
	public static final ComplexDouble ZERO = new ComplexDouble(0, 0);

	private final static double EPSILON = 1E-9;

	protected final double real, imaginary;

	public ComplexDouble(double real) {
		this(real, 0);
	}

	public ComplexDouble(double real, double imaginary) {
		this.real = real;
		this.imaginary = imaginary;
	}

	public static ComplexDouble fromVecNd(VecNd vec) {
		if (vec.size() != 2) {
			throw new IllegalArgumentException("Vector length must be 2");
		}
		return new ComplexDouble(vec.get(0), vec.get(1));
	}
	
	public static ComplexDouble fromRTheta(double radius, double theta, boolean degrees) {
		if (degrees) {
			return fromRTheta(radius, theta / 180 * Math.PI, false);
		}
		return new ComplexDouble(radius * Math.cos(theta), radius * Math.sin(theta));
	}

	private Double magnitudeCached;

	public double magnitude() {
		if (magnitudeCached == null) {
			magnitudeCached = Math.sqrt(magnitudeSquared());
		}
		return magnitudeCached;
	}

	public double magnitudeSquared() {
		return real * real + imaginary * imaginary;
	}

	public boolean isUnit() {
		return Math.abs(magnitude() - 1) < EPSILON;
	}

	public ComplexDouble scale(double scale) {
		return new ComplexDouble(real * scale, imaginary * scale);
	}

	public ComplexDouble multiply(double real) {
		return scale(real); // lmao speedrun
	}

	public ComplexDouble multiply(double real, double imaginary) {
		return multiply(new ComplexDouble(real, imaginary));
	}

	public ComplexDouble multiply(ComplexDouble number) {
		// (a_1 + b_1i)(a_2 + b_2i) =
		// (a_1)(a_2) + (a_1)(b_2)i + (b_1)(a_2)i - (b_1)(b_2) =
		// ((a_1)(a_2) - (b_1)(b_2)) + ((a_1)(b_2) + (b_1)(a_2))i
		return new ComplexDouble(real * number.getReal() - imaginary * number.getImaginary(), real * number.getImaginary() + imaginary * number.real);

		// we could also
		// return fromRTheta(this.magnitude() * number.magnitude(), this.angle() + number.angle(), false);
	}

	public ComplexDouble reciprical() {
		double magSq = this.magnitudeSquared();
		return conjugate().scale(1 / magSq);
	}

	public ComplexDouble rotate(double angle) {
		return this.multiply(fromRTheta(1, angle, false));
	}

	public ComplexDouble normalize() {
		return scale(1 / magnitude());
	}

	public ComplexDouble conjugate() {
		return new ComplexDouble(real, -imaginary);
	}

	// A line through the origin in the complex plane can be represented by a single nonzero complex number just like a vector.
	public ComplexDouble reflectAcrossLine(ComplexDouble line) {
		double angle = line.angle();
		return this.rotate(-angle).conjugate().rotate(angle);
	}

	public ComplexDouble inverseSubtract(double real) {
		return inverseSubtract(new ComplexDouble(real));
	}

	public ComplexDouble inverseSubtract(double real, double imaginary) {
		return inverseSubtract(new ComplexDouble(real, imaginary));
	}

	public ComplexDouble inverseSubtract(ComplexDouble number) {
		return number.subtract(this);
	}

	public ComplexDouble subtract(double real) {
		return subtract(new ComplexDouble(real));
	}

	public ComplexDouble subtract(double real, double imaginary) {
		return subtract(new ComplexDouble(real, imaginary));
	}

	public ComplexDouble subtract(ComplexDouble number) {
		return add(number.scale(-1));
	}

	public ComplexDouble add(double real) {
		return add(new ComplexDouble(real));
	}

	public ComplexDouble add(double real, double imaginary) {
		return add(new ComplexDouble(real, imaginary));
	}

	public ComplexDouble add(ComplexDouble number) {
		return new ComplexDouble(real + number.getReal(), imaginary + number.getImaginary());
	}

	public ComplexDouble inverseDivide(double real) {
		return inverseSubtract(new ComplexDouble(real));
	}

	public ComplexDouble inverseDivide(double real, double imaginary) {
		return inverseSubtract(new ComplexDouble(real, imaginary));
	}

	public ComplexDouble inverseDivide(ComplexDouble number) {
		return number.divide(this);
	}

	public ComplexDouble divide(double real) {
		return divide(new ComplexDouble(real));
	}

	public ComplexDouble divide(double real, double imaginary) {
		return divide(new ComplexDouble(real, imaginary));
	}

	public ComplexDouble divide(ComplexDouble number) {
		return multiply(number.reciprical());
	}

	public double getImaginary() {
		return imaginary;
	}

	public double getReal() {
		return real;
	}

	public ComplexDouble pow(double power) {
		return new ComplexDouble(Math.cos(power * angle()), Math.sin(power * angle())).scale(Math.pow(magnitude(), power));

	}

	@Override
	public String toString() {
		return getClass().getName() + "(" + displayStandard() + ")";
	}

	public String displayStandard() {
		
		if (Double.isNaN(real) || Double.isNaN(imaginary)) 
			return "NaN";
		
		double real = Double.parseDouble(format.format(this.real));
		double imaginary = Double.parseDouble(format.format(this.imaginary));
		
		if (real != 0) {
			String ret = "" + real;			
			if (imaginary > 0) {
				ret += " + " + imaginary + "i";
			} else if (imaginary < 0) {
				ret += " - " + -imaginary + "i";
			}
			return ret;
		} else {
			return imaginary == 0 ? "0.0" : format.format(real) + "i";
		}
	}

	public VecNd asVecNd() {
		return new VecNd(real, imaginary);
	}
	
	public double angle() {
		return Math.atan2(imaginary, real);
	}

	public String displayPolar(boolean withPi) {
		if (Double.isNaN(real) || Double.isNaN(imaginary)) 
			return "NaN";
		
		double angle = withPi ? angle() / Math.PI : angle();
		angle = Double.parseDouble(format.format(angle));
		
		return (isUnit() ? "" : "" + this.magnitude()) + "e^(" + (withPi ? angle + "π" : angle) + "i)";
	}
}
