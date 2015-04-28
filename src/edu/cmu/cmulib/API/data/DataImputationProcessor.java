package edu.cmu.cmulib.API.data;

abstract class DataImputationProcessor {

	protected SamplingProcessor samplingProcessor;

	public SamplingProcessor getSamplingProcessor() {
		return samplingProcessor;
	}

	public void setSamplingProcessor(SamplingProcessor samplingProcessor) {
		this.samplingProcessor = samplingProcessor;
	}

	public DataImputationProcessor(SamplingProcessor samProcessor) {
		samplingProcessor = samProcessor;
	}

	abstract public String[] imputateInstance(String[] instance);

}