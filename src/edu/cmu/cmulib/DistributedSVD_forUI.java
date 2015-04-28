package edu.cmu.cmulib;

import edu.cmu.cmulib.API.gui.UI;
import edu.cmu.cmulib.Communication.CommonPacket;
import edu.cmu.cmulib.CoolMatrixUtility.core.Mat;
import edu.cmu.cmulib.CoolMatrixUtility.core.MatOp;
import edu.cmu.cmulib.CoolMatrixUtility.decomp.svd.Master_Spliter;
import edu.cmu.cmulib.CoolMatrixUtility.decomp.svd.Master_kSVD;
import edu.cmu.cmulib.CoolMatrixUtility.help.Tag;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by handixu on 4/18/15.
 */
public class DistributedSVD_forUI implements Runnable {
    MasterMiddleWare commu;
    double[] test;
    int slaveNum;
    LinkedList<Double[]> mList = new LinkedList<Double[]>();
    String output;
    boolean isFinished;
    private UI gui;
    public DistributedSVD_forUI(MasterMiddleWare middleWare, int slaveNum, double[] test, UI gui) {
        commu = middleWare;
        this.test = test;
        this.slaveNum = slaveNum;
        commu.register(Double[].class, mList);
        this.output = "";
        this.isFinished = false;
        this.gui = gui;
    }

    @Override
    public void run() {
        //double[] test = new double[1000 * 1000];
        int q = 0;
        output = "";

        int rows = 1000;
        int cols = 1000;
        Mat score = new Mat(rows, cols, test);
        Tag tag;
        Mat Like, slaveL;

        Master_Spliter split = new Master_Spliter(score, slaveNum);
        Master_kSVD svd = new Master_kSVD(score, slaveNum, 2);
        if(commu.slaveNum() < slaveNum) {
            System.out.println(commu.slaveNum()+ " is less than required number");
            System.exit(1);
        }
        Like = svd.initL();
        slaveL = null;
        isFinished = false;

        // compute the first eigenvector iterately
        int count = 0;
        do {
            int remain = slaveNum;
            svd.setL(Like);
            //output += printArray(Like.data);
            // send L
            for (int i = 1; i <= slaveNum; i++) {
                sendMat(Like, i, commu);
            }
            //send Tag
            ArrayList<Tag> index = split.split();
            for (int i = 0; i < index.size(); i++) {
                tag = index.get(i);
                CommonPacket packet = new CommonPacket(-1, tag);
                commu.sendPacket(i + 1, packet);
            }
            // receive L and update
            while (remain > 0) {
                synchronized (mList) {
                    if (mList.size() > 0) {
                        slaveL = getMat(mList);
                        svd.update_SVD(slaveL);
                        remain--;
                    }
                }
            }

            Like = svd.getUpdateL();
            MatOp.vectorNormalize(Like, MatOp.NormType.NORM_L2);
            gui.updateprogressArea("iteration " + count + "\n");
            gui.updateprogressArea(printArrayMid(Like.data));
            count ++;
        } while (!svd.isPerformed(Like));     //termination of iteration
        System.out.println("final  ");
        output += "final  ";
        output += printArrayMid(Like.data);
        System.out.println(output);
        gui.updateprogressArea("Computation finished, svd result is: \n");
        gui.updateprogressArea(printArrayMid(Like.data) + "\n");
        gui.updateprogressArea("Writing to file ....\n");
        try {
            String outFileName = gui.outputFolder.getCanonicalPath() + "/result.txt";
            gui.updateprogressArea("outputFile name:" + outFileName + "\n");
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outFileName)));
            writer.write(printArray(Like.data));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        gui.updateprogressArea("************* Finish Job Successfully *************\n\n");
        isFinished = true;

    }

    public String getOutput() {
        return this.output;
    }

    public static String printArray(double[] arr) {
        String ret = "";
        for (double i : arr) {
            //System.out.print(i + " ");
            ret += i + "\n";
        }
        //System.out.println();
        ret += "\n";
        return ret;
    }

    public static String printArrayMid(double[] arr) {
        String ret = "";
        for (double i : arr) {
            //System.out.print(i + " ");
            ret += i + " ";
        }
        //System.out.println();
        ret += "\n";
        return ret;
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
