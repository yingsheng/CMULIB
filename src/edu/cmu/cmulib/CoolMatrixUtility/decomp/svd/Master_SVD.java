package edu.cmu.cmulib.CoolMatrixUtility.decomp.svd;

import edu.cmu.cmulib.CoolMatrixUtility.core.Mat;
import edu.cmu.cmulib.CoolMatrixUtility.core.MatOp;

/**
 * 
 * @author team3
 *
 */
public class Master_SVD {
    private Mat L, updateL, src;          // original L, updated L, and source Matrix
    public int subNum;    				 // number of parts to split
    private static double EPS = 1e-7;     // The termination of iteration
    private static int MAX_ITER = 500;    // The max loop of iteration
    public int iter;						 // current numbers of loop for iteration
    private static double ALPHA = 0.90;   // coenfficient of iteration
    private int updateNum;                // number of updates, need to equal to subNum

    /**
     * Master_SVD Constructor Initialize parameters.
     *
     */
    public Master_SVD (Mat matrix, int subNum) {
        this.src = matrix;
        this.subNum = subNum;
        this.L = new Mat(src.rows,1);
        this.updateL = new Mat(src.rows,1);
        this.iter = 0;
        this.updateNum = 0;
    }

    /**
     * initL
     * initialize L for start.
     */
    public Mat initL() {
        Mat initL = new Mat(src.rows, 1);
        for (int i = 0; i < src.rows; i++){
            initL.data[i] = Math.random();
        }
        MatOp.vectorNormalize(initL, MatOp.NormType.NORM_L2);
        return initL;
    }

    /**
     * setL
     * setL and updateL after each iteration
     */
    public void setL(Mat Like) {
        this.L = Like;
        this.updateL = Like.clone().mul(ALPHA);
        this.iter++;
    }

    /**
     * getUpdateL
     * return updateL for next iteration.
     */
    public Mat getUpdateL () {
        return this.updateL;
    }

    /**
     * isPerformed
     * compare original L with updated newL, to check if complete the iteration
     */
    public boolean isPerformed (Mat newL) {
        if (MatOp.dist(newL, this.L, MatOp.NormType.NORM_L2) < EPS || iter > MAX_ITER)
            return true;
        else
            return false;
    }
    /**
     * isPerformed no parameter
     * compare original L with updated L, to check if complete the iteration
     */
    public boolean isPerformed () {
        if (updateNum < subNum) {
            System.out.println("@@@@@");
            return false;
        }
        this.updateL = MatOp.vectorNormalize(this.updateL, MatOp.NormType.NORM_L2);
        if (MatOp.dist(this.updateL, this.L, MatOp.NormType.NORM_L2) < EPS || iter > MAX_ITER)
            return true;
        else
            return false;
    }

    /**
     * update_SVD
     * update L when new slaveL come
     */
    public void update_SVD (Mat slaveL) {
//        if (updateNum >= subNum) {
//            updateNum = 0;
//        }
        slaveL = slaveL.mul((1-ALPHA)/subNum);
        this.updateL = MatOp.add(this.updateL, slaveL);
       // updateNum++;
    }
  
}
