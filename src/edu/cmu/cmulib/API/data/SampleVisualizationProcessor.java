package edu.cmu.cmulib.API.data;

import java.io.IOException;

public class SampleVisualizationProcessor {

	private String visualizationMethod = "2D";
	private String sampleDataFileName = "data.txt";

	public void visualize(String method) {
		this.setVisualizationMethod(method);
		this.visualize();
	}

	public String getVisualizationMethod() {
		return visualizationMethod;
	}

	public void setVisualizationMethod(String visualizationMethod) {
		this.visualizationMethod = visualizationMethod;
	}

	public String getSampleDataFileName() {
		return sampleDataFileName;
	}

	public void setSampleDataFileName(String sampleDataFileName) {
		this.sampleDataFileName = sampleDataFileName;
	}

	public void visualize() {
		String commands = "matlab -nodesktop -nosplash -r data_visualization('"
				+ visualizationMethod + "','" + sampleDataFileName + "')";
		System.out.println(commands);
		try {
			Process process = Runtime.getRuntime().exec(commands);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static public void main(String args[]) {
		SampleVisualizationProcessor sampleVisualizationProcessor = new SampleVisualizationProcessor();
		sampleVisualizationProcessor.visualize("3D");

	}
}