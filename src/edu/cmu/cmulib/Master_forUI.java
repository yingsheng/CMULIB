package edu.cmu.cmulib;

import edu.cmu.cmulib.API.gui.UI;
import edu.cmu.cmulib.API.data.DataFileProcesser;
import edu.cmu.cmulib.API.data.WrongDataTypeStrategy;
import edu.cmu.cmulib.Communication.CommonPacket;
import edu.cmu.cmulib.CoolMatrixUtility.core.Mat;
import edu.cmu.cmulib.CoolMatrixUtility.decomp.svd.Master_SVD;
import edu.cmu.cmulib.CoolMatrixUtility.decomp.svd.Master_Spliter;
import edu.cmu.cmulib.DistributedSVD;
import edu.cmu.cmulib.FileSystemAdaptor.*;
import edu.cmu.cmulib.MasterMiddleWare;
import edu.cmu.cmulib.Utils.BinaryDataGenerator;
import edu.cmu.cmulib.Utils.ConfParameter;
import edu.cmu.cmulib.Utils.JsonGenerator;
import edu.cmu.cmulib.Utils.JsonParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

public class Master_forUI {
	private Mat score;
	private Mat Like;
	private Master_Spliter split;
	private Master_SVD svd;
	public MasterMiddleWare commu;
	private LinkedList<Double[]> mList;
	public int slaveNum;
	public String dir;
	public String fileName;
	public int port;
	public FileSystemType mFsType;
	public double[] test;

	public int columnNum, rowNum;
	private UI gui;
	DataFileProcesser processor = new DataFileProcesser();

	/**
	 * @return the processor
	 */
	public DataFileProcesser getProcessor() {
		return processor;
	}

	private final List<WrongDataTypeStrategy> wrongDataStrategies = new ArrayList<WrongDataTypeStrategy>();

	/**
	 * @return the wrongDataStrategies
	 */
	public List<WrongDataTypeStrategy> getWrongDataStrategies() {
		return wrongDataStrategies;
	}

	public void registerDataStrategy(WrongDataTypeStrategy stra) {
		this.wrongDataStrategies.add(stra);
	}

	public void setUI(UI inputUI) {
		gui = inputUI;
	}

	public Master_forUI() {
		// TODO: change slaveNum for demo
		this.slaveNum = 2;
		this.dir = "./resource";
		this.fileName = "/BinData";
		this.port = 8888;
		this.mFsType = FileSystemType.LOCAL;
	}

	public Master_forUI(ConfParameter conf) {
		this.slaveNum = conf.minSlaveNum;
		this.dir = conf.fileDir;
		this.fileName = conf.fileName;
		this.port = conf.masterPort;
		this.mFsType = conf.fsType;
	}

	public Master_forUI(String filePath) throws IOException, ParseException {
		JsonParser jp = new JsonParser();
		ConfParameter conf = jp.parseFile(filePath);

		this.slaveNum = conf.minSlaveNum;
		this.dir = conf.fileDir;
		this.fileName = conf.fileName;
		this.port = conf.masterPort;
		this.mFsType = conf.fsType;
	}

	public int getCurSlaveNum() {
		return commu.slaveNum();
	}

	public void setPara(String para) throws ParseException {
		JsonParser jp = new JsonParser();
		ConfParameter conf = jp.parseString(para);

		this.slaveNum = conf.minSlaveNum;
		this.dir = conf.fileDir;
		this.fileName = conf.fileName;
		this.port = conf.masterPort;
		this.mFsType = conf.fsType;
	}

	public String sayHi() {
		return "HHHHHHHHHHHHHH";
	}

	public String init() throws IOException {

		/*
		 * test = new double[1000 * 1000]; LinkedList<Double[]> mList = new
		 * LinkedList<Double[]>();
		 * 
		 * 
		 * //String dir = "tachyon://localhost:19998"; //String fileName =
		 * "/BinData"; String dir = "./resource"; String fileName = "/BinData";
		 * try { FileSystemInitializer fs =
		 * FileSystemAdaptorFactory.BuildFileSystemAdaptor(FileSystemType.LOCAL,
		 * dir); DataHandler t =
		 * DataHandlerFactory.BuildDataHandler(FileSystemType.LOCAL); test =
		 * t.getDataInDouble(fs.getFsHandler(), fileName, 1000 * 1000);
		 * System.out.println(test[1000 * 1000 - 1]); } catch (IOException e) {
		 * }
		 */

		this.commu = new MasterMiddleWare(this.port);
		commu.startMaster();

		return "success!";
	}

	public String execute() {
		String output = "";
		if (commu.slaveNum() < this.slaveNum) {
			return "Not enoght slaves";
		}

		DistributedSVD svd_t = new DistributedSVD(commu, this.slaveNum, test);
		Thread t = new Thread(svd_t);
		t.run();

		while (true) {
			if (svd_t.isFinished)
				break;
		}
		output = svd_t.getOutput();
		gui.updateprogressArea(output);
		return output;
	}

	public boolean isCompleted() {
		return svd.isPerformed(Like);
	}

	public String dispFinal() {
		String finalout = "final  " + dispArray(this.Like.data); // final
																	// information
		return finalout;
	}

	public void startRun(String input, String output) {

		System.out.println(gui.numberOfSalveNodes.getText());
		this.slaveNum = Integer.parseInt(gui.numberOfSalveNodes.getText());
		int rows = this.rowNum;
		int cols = this.columnNum;
		double[] test = new double[rows * cols];
		LinkedList<Double[]> mList = new LinkedList<Double[]>();

		// String dir = "./resource";
		// String fileName = "/BinData";

		try {
			FileSystemInitializer fs = FileSystemAdaptorFactory
					.BuildFileSystemAdaptor(FileSystemType.LOCAL, this.dir);
			DataHandler t = DataHandlerFactory
					.BuildDataHandler(FileSystemType.LOCAL);
			test = t.getDataInDouble(fs.getFsHandler(), this.fileName, rows
					* cols);
			System.out.println(test[rows * cols - 1]);
		} catch (IOException e) {
		}

		DistributedSVD_forUI svd = new DistributedSVD_forUI(commu,
				this.slaveNum, test, gui, this.rowNum);

		while (commu.slaveNum() < this.slaveNum) {
			gui.updateprogressArea(commu.slaveNum()
					+ "slave(s) is found, need " + this.slaveNum + " slaves\n");
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
		gui.updateprogressArea(this.slaveNum
				+ " slaves found! Start calculating...\n");
		Thread t = new Thread(svd);
		t.start();
	}

	public void generateBinDataWithErrorHandler(String input, String output) throws Exception {
		gui.updateprogressArea("Start loading data....\n");
		gui.updateprogressArea("Input file found: " + input + "\n");

		String[][] stringMat = processor.processingData(input, ",", "dataType",
				gui.getSamplingRate(), output + "/resource/sampleData.txt");
		this.rowNum = stringMat.length;
		this.columnNum = stringMat[0].length;
		String tmpFile = output + "/resource/tmpColumnWiseData.txt"; // store a 1-column
																// format of the
																// matrix

		Path inPath = Paths.get(input);
		this.dir = inPath.getParent().toString();
		this.dir = "./resource";
		this.fileName = "/BinData1";

		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
				tmpFile)));
		for (int j = 0; j < this.columnNum; j++) {
			for (int i = 0; i < this.rowNum; i++) {
				writer.write(stringMat[i][j] + "\n");
			}
		}
		writer.close();
		BinaryDataGenerator g = new BinaryDataGenerator();
		try {
			double[] data = g.read(tmpFile);
			g.write(data, output + "/resource/"+ this.fileName); // hardcoded as
														// "./resource/BinData.bin"
		} catch (IOException e) {
			e.printStackTrace();
		}
		// File f = new File(tmpFile);
		// f.delete();
		// start generate config file

		JsonGenerator generator = new JsonGenerator();
		initConfigFile(generator);
		generator.put("columnNum", new Integer(this.columnNum));
		generator.put("rowNum", new Integer(this.rowNum));
		generator.put("fileName", this.fileName);
		generator.put("fileDir", this.dir);

		FileWriter file = new FileWriter(output + "/resource/conf.json");
		file.write(generator.getJsonString());
		file.flush();
		file.close();
		gui.updateprogressArea("generating configuration file...\n");
		gui.updateprogressArea("Data successfully loaded\n\n");
		return;
	}

	public void initConfigFile(JsonGenerator generator) {
		// all the default settings
		generator.put("masterAddress", "localhost");
		generator.put("masterPort", new Integer(8888));
		generator.put("minSlaveNum", new Integer(1));
		generator.put("fsType", "local");
	}

	public static void printArray(double[] arr) {
		for (double i : arr)
			System.out.print(i + " ");
		System.out.println();
	}

	private String dispArray(double[] arr) {
		String s = "";
		for (double i : arr)
			s += i + " ";
		s += "\n";
		return s;
	}

	public static Mat getMat(LinkedList<Double[]> mList) {
		Double[] temp = mList.peek();
		double row = temp[0];
		double col = temp[1];
		double[] arr = new double[temp.length - 2];
		for (int k = 0; k < arr.length; k++) {
			arr[k] = temp[k + 2];
		}
		Mat mat = new Mat((int) row, (int) col, arr);
		mList.remove();
		return mat;
	}

	public static void sendMat(Mat mat, int id, MasterMiddleWare m) {
		Double[] array = new Double[mat.data.length + 2];
		array[0] = Double.valueOf(mat.rows);
		array[1] = Double.valueOf(mat.cols);

		for (int k = 0; k < mat.data.length; k++)
			array[k + 2] = Double.valueOf(mat.data[k]);
		CommonPacket packet = new CommonPacket(-1, array);

		m.sendPacket(id, packet);
	}
}
