package edu.cmu.cmulib.API.data;

import java.util.ArrayList;
import java.util.Iterator;

import org.netlib.util.booleanW;
import org.netlib.util.doubleW;

import sun.launcher.resources.launcher;

public class SamplingProcessor {
	protected ArrayList<String[]> sampleInstances;
	protected ArrayList<double[]> numericalData;
	protected double samplingProb;

	public SamplingProcessor(double prob) {
		assert (prob > 0 && prob <= 1);
		sampleInstances = new ArrayList<String[]>();
		numericalData = new ArrayList<double[]>();
	}

	public ArrayList<String[]> getSampleInstances() {
		return sampleInstances;
	}

	public ArrayList<double[]> getNumericalData() {
		return numericalData;
	}

	public double getSamplingProb() {
		return samplingProb;
	}

	public boolean sampling(String[] instance) {
		double rand = Math.random();

		// add instance
		if (rand < samplingProb) {
			sampleInstances.add(instance);
			numericalData.add(convertStringArray2DoubleArray(instance));
		} else
			return true;
		return false;
	}

	private double[] convertStringArray2DoubleArray(String[] strs) {
		double[] result = new double[strs.length];
		for (int i = 0; i < strs.length; i++) {
			result[i] = Double.parseDouble(strs[i]);
		}

		return result;

	}
}
