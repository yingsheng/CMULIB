package edu.cmu.cmulib.API.data;

import java.util.Comparator;
import java.util.PriorityQueue;

public class KNNDataImputationProcessor extends DataImputationProcessor {
	int N;

	class Pair {
		private double distance;
		private double[] instance;

		public Pair(double dis, double[] inst) {
			distance = dis;
			instance = inst;
		}

		public double getDistance() {
			return distance;
		}

		public double[] getInstance() {
			return instance;
		}
	}

	public KNNDataImputationProcessor(SamplingProcessor samProcessor) {
		super(samProcessor);
		// magic number, maybe should use config.ini
		N = 5;
	}

	@Override
	public String[] imputateInstance(String[] instance) {

		String[] imputatedInstance = instance.clone();
		Comparator<Pair> comparator = new Comparator<KNNDataImputationProcessor.Pair>() {
			public int compare(Pair p1, Pair p2) {
				if (p1.distance < p2.distance)
					return 1;
				else if (p2.distance < p1.distance)
					return -1;
				else
					return 0;

			}
		};

		PriorityQueue<Pair> heap = new PriorityQueue<>(N, comparator);

		// find k nearest neighbor i.e. the k smallest distance
		// hence this is a max heap

		// for each sampled data
		double tmpDistance = 0;
		for (int i = 0; i < this.getSamplingProcessor().getNumericalData()
				.size(); i++) {
			tmpDistance = calculateDistance(instance, this
					.getSamplingProcessor().getNumericalData().get(i));
			if (heap.size() < N) {
				heap.add(new Pair(tmpDistance, this.getSamplingProcessor()
						.getNumericalData().get(i)));
			} else {
				if (heap.peek().getDistance() <= tmpDistance)
					continue;
				else {
					heap.remove();
					heap.add(new Pair(tmpDistance, this.getSamplingProcessor()
							.getNumericalData().get(i)));

				}
			}
		}

		// impute missing value
		// for each feature
		double tmp = 0;

		for (int i = 0; i < imputatedInstance.length; i++) {
			if (!isNumerical(imputatedInstance[i])) {
				tmp = 0;
				for (Pair p : heap) {
					tmp += (1 / p.distance) * p.getInstance()[i];
				}
			}
			imputatedInstance[i] = "" + (tmp / N);
		}

		return imputatedInstance;

	}

	private double calculateDistance(String[] instance, double[] sampledInstance) {

		return 0;
	}

	private boolean isNumerical(String str) {
		return false;

	}

	public static void main(String args[]) {

	}

}
