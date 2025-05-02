package com.existingeevee.moretcon.mathtest;

import com.existingeevee.math.linear_algebra.LinearAlgebraUtils;
import com.existingeevee.math.linear_algebra.VecNd;

import scala.actors.threadpool.Arrays;

public class MathTest {
	public static void main(String... args) {
		System.out.println(Arrays.toString(LinearAlgebraUtils.gramSchmidt(new VecNd(0, 0, 1, 1), new VecNd(0, 1, 1, 0), new VecNd(1, 1, 0, 0))));
		System.out.println(Arrays.toString(new VecNd[] { new VecNd(0, 0, 1, 1).scale(1. / Math.sqrt(2)), new VecNd(0, 1, 1. / 2, -1. / 2).scale(Math.sqrt(2d / 3)), new VecNd(3, 1, -1, 1).scale(1d / (2 * Math.sqrt(3))) }));

	}
}
