/* GraspSynergy.java
 * 
 * representation of a single grasp synergies with N eigenvectors
 * of length DOF, and (optionally) a given origin/mean.
 * 
 * 29.12.2012 - add get/setJointsUsedVector() to enable/disable joints
 * 27.12.2012 - add toEigenSpace( HandProxy )
 * 27.12.2012 - update moveToJointsClipped to also send "LFJ5 0.0"
 * 23.06.2012 - implement toEigenSpace()
 * 17.03.2012 - implement parseMatlab*(), improve toString()
 * 07.03.2012 - implement parseEigengraspFile()
 * 05.03.2012 - new class
 * 
 * (c) 2012 fnh
 */


package hdbt.shadow;

import java.io.*;
import java.util.*;

import hdbt.util.Array;
import hdbt.util.MathUtils;

/**
 * representaiton of a single grasp synergy with N eigenvectors of length DOF,
 * and (optionally) a given origin/mean. Parses eigengrasp files compatible with
 * GraspIt!.
 */
public class GraspSynergy {
  private int N; // number of eigenvectors
  private int NDOF; // length of eigenvector, NDOF of robot
  private String[] jointNames; // 0..NDOF-1
  private double[] origin;
  private double[] amplitudes;
  private double[][] eigengrasps; // [index][dof]
  private String name;
  private double[] jointsUsed; // 0 or 1 or weight for each joint


  /**
   * create the "trivial" (diagonal) GraspSynergy
   */
  public GraspSynergy(int NDOF) {
    this.NDOF = NDOF;
    this.N = NDOF;
    this.jointNames = null;
    origin = Array.constant(0.0, NDOF);
    amplitudes = Array.constant(1.0, NDOF);
    eigengrasps = Array.eye(NDOF, NDOF);
    jointsUsed = Array.ones( NDOF );
  }


  public GraspSynergy(double[] origin, double[] amplitudes,
      double[][] eigengrasps) {
    this.N = origin.length;
    this.NDOF = eigengrasps.length;
    this.origin = origin;
    this.amplitudes = amplitudes;
    this.eigengrasps = eigengrasps;
  }


  public int getNDOF() {
    return NDOF;
  }
  
  
  public int getNumberOfVectors() {
    return N;
}


  public String getName() {
    return name;
  }


  public void setName(String n) {
    this.name = n;
  }


  public String[] getJointNames() {
    return jointNames;
  }


  public double[] getOrigin() {
    return origin;
  }


  public double[] getEigengrasp(int index) {
    assert (index >= 0);
    assert (index < N);
    return eigengrasps[index];
  }


  public double[][] getEigengrasps() {
    return eigengrasps;
  }


  public double[] getAmplitudes() {
    return amplitudes; // might be null!
  }


  public double[] getJointsUsedVector() {
    return jointsUsed;
  }
  
  
  public void setJointsUsedVector( double[] ju ) {
    assert (ju != null);
    assert (ju.length == NDOF);
    jointsUsed = ju;
  }

  /*
   * file format is 
     <code> 
     DIMENSIONS 18
     
     EG 0.1000 0.0 0.0002 0.0016 -0.0044 0.0002 0.0017 -0.0007 -0.0003 0.0005
     -0.0010 0.0043 -0.0066 0.0000 -0.0097 0.0029 0.0013 -0.0004 0.0043
     
     ORIGIN 0.0000 0.0 0.1622 0.3746 0.1597 0.0552 0.3875 0.3380 -0.0305 0.4778
     0.6282 -0.1636 0.4975 0.5830 0.3090 1.0681 0.0729 0.1896 0.3496 
     </code>
   */
  public void parseEigengraspFile(String filename) {
    BufferedReader br = null;
    String line = null;
    String[] tokens = null;

    int NDOF = 0;
    ArrayList<Double> amplitudesList = new ArrayList<Double>();
    ArrayList<double[]> graspsList = new ArrayList<double[]>();

    try {
      br = new BufferedReader(new FileReader(filename));
      while ((line = br.readLine()) != null) {
        if (line.length() == 0) continue;
        if (line.startsWith("#")) continue;

        tokens = line.split("[ \t]+");
        if ("DIMENSIONS".equals(tokens[0])) {
          this.NDOF = Integer.parseInt(tokens[1]);
        }
        if ("ORIGIN".equals(tokens[0])) {
          line = br.readLine(); // throw away

          line = br.readLine().trim();
          tokens = line.split("[ \t]+");
          if (tokens.length != NDOF) {
            throw new RuntimeException("Invalid number of ORIGIN elements: "
                + tokens.length);
          }
          this.origin = new double[NDOF];
          for (int i = 0; i < NDOF; i++) {
            origin[i] = Double.parseDouble(tokens[i]);
          }
        }
        if ("EG".equals(tokens[0])) {
          line = br.readLine().trim();
          amplitudesList.add(Double.parseDouble(line));

          line = br.readLine().trim();
          tokens = line.split("[ \t]+");
          if (tokens.length != (NDOF)) {
            throw new RuntimeException("Invalid number of EG elements: "
                + tokens.length);
          }
          double[] eg = new double[NDOF];
          for (int i = 0; i < NDOF; i++) {
            eg[i] = Double.parseDouble(tokens[i]);
          }
          graspsList.add(eg);
        }
      }

      // convert from ArrayList to arrays
      this.N = graspsList.size();
      this.eigengrasps = graspsList.toArray(new double[][] {});
      this.amplitudes = MathUtils.toArray(amplitudesList);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }


  /**
   * parses the given Matlab -ascii file to read one synergy origin. These files
   * are currently hardcoded to the format used by Alex, including N=NDOF=21.
   */
  public void parseMatlabSynergyMean(InputStream is) throws Exception {
    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(is));
      String line = null;

      int index = 0;
      this.N = 21;
      this.NDOF = 21;
      this.origin = new double[21];
      while ((line = br.readLine()) != null) {
        StringTokenizer st = new StringTokenizer(line.trim(), " \t");
        while (st.hasMoreTokens()) {
          origin[index] = Double.parseDouble(st.nextToken());
          index++;
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }


  /**
   * parses the given Matlab -ascii file to read one synergy origin These files
   * are currently hardcoded to the format used by Alex, including N=NDOF=21.
   */
  public void parseMatlabSynergyVecs(InputStream is) throws Exception {
    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(is));
      String line = null;

      int nvecs = 0;
      int index = 0;
      this.N = 21;
      this.NDOF = 21;
      this.eigengrasps = new double[N][NDOF];
      while ((line = br.readLine()) != null) {
        StringTokenizer st = new StringTokenizer(line.trim(), " \t");
        while (st.hasMoreTokens()) {
// xxxzzz FIXME TODO: which ordering is correct?...
//          
          eigengrasps[index][nvecs] = Double.parseDouble(st.nextToken());
//          eigengrasps[nvecs][index] = Double.parseDouble(st.nextToken());
          index++;
        }
        nvecs++;
        index = 0;
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }

  }





  /*
   * from Graspit 2.1.0, eigenGrasp.cpp:
   * 
   * void EigenGraspInterface::toDOFSpace(const double *amp, double *dof, const
   * double *origin) const { Matrix a(amp, eSize, 1, true); Matrix x(dSize, 1);
   * matrixMultiply(*mPInv, a, x); for(int d=0; d<dSize; d++) { dof[d] =
   * x.elem(d,0) * mNorm->getAxisValue(d) + origin[d]; } }
   * 
   * void EigenGraspInterface::toEigenSpace(double *amp, const double *dof,
   * const double *origin) const { Matrix x(dSize, 1); for (int d=0; d < dSize;
   * d++) { x.elem(d,0) = (dof[d] - origin[d]) / mNorm->getAxisValue(d); }
   * Matrix a(eSize, 1); matrixMultiply(*mP, x, a); for (int e=0; e<eSize; e++)
   * { amp[e] = a.elem(e,0); } }
   */
   
//
//function PARAMS = project_synergies(DATAMATRIX, MEANVAL, EIGENVECS)
//    % DATAMATRIX has as many rows as trials and as many cols as joint angles
//    ZEROMEANDATA = remove_mean(DATAMATRIX,MEANVAL);
//    PARAMS = ZEROMEANDATA*EIGENVECS;
//end
   
   
  /**
   * takes the given joint-angles (in degrees, using Shadow-robot sign
   * conventions, but only including joints represented in the Eigengrasp)
   * and converts this to the corresponding Eigengrasp amplitudes.
   * We first substract the mean-values, then do the projection.
   */
  public double[] toEigenSpace( double[] jointDegrees ) {
    // FIXME: implement, then CHECK that this really works...
    
    assert( jointDegrees.length == NDOF );
    double[] removed_mean = Array.sub( jointDegrees, origin );
    double[] amplitudes = new double[ N ];
    
    for (int i = 0; i < N; i++) {
      for (int j = 0; j < NDOF; j++) {
        amplitudes[i] += removed_mean[j] * eigengrasps[i][j];
      }
    }

    return amplitudes;
  }
  
  
  
  /**
   * takes the given amplitudes in Eigenspace and converts to the per-joint
   * angles in joint-space. It is ok to provide fewer amplitudes than we have
   * eigenvectors, in which case only the first few (0..amplitudes.length-1)
   * eigenvectors are used.
   * 
   * The interpretation of the values depends on the Eigenspace transformation
   * in use; for the postural synergies recorded from our C5 Shadow hand, the
   * output values are in degrees.
   * 
   * We first use the eigenspace transformation with the given amplitudes, and
   * in the second step add the origin (mean-value vector).
   */
  public double[] toJoints(double[] amplitudes) {
    assert (amplitudes != null);
    assert (amplitudes.length <= N);

    double[] jj = new double[NDOF]; // each eigenvector has NDOF entries
    for (int j = 0; j < NDOF; j++) {
      for (int i = 0; i < N; i++) {
        jj[j] += amplitudes[i] * eigengrasps[i][j];
      }
    }

    for (int j = 0; j < NDOF; j++) { // add mean-value/origin
      jj[j] += origin[j];
    }
    return jj;
  }



  
  /**
   * correction of joint-angles to avoid finger-finger collisions.
   * We take the MF abduction as the reference value, and adjust the
   * outer fingers (FF, RF, then LF) to have at least the same abduction
   * values.
   * 
   * Note that negative values imply finger abduction (spread), while
   * positive values are finger adduction (joining) and potentially 
   * dangerous.
   */
  public double[] toSafeAbduction( double[] rawAngles ) {
    int ffj4 = 3;
    int mfj4 = 7;
    int rfj4 = 11;
    int lfj4 = 15;
    
    if (rawAngles[ffj4] >= rawAngles[mfj4]) {
      rawAngles[ffj4] = rawAngles[mfj4];
    }
    if (rawAngles[rfj4] >= -rawAngles[mfj4] ) { // note sign change due to Shadow
      rawAngles[rfj4] = -rawAngles[mfj4];       // finger motion convention
    }
    if (rawAngles[lfj4] >= rawAngles[rfj4]) {
      rawAngles[lfj4] = rawAngles[rfj4]; 
    }
    return rawAngles;
  }

 
  /**
   * uses the given HandProxy to move the fingers to the position corresponding
   * to the given amplitudes, but clipping to joint-angle limits and correcting
   * abduction-angles (J4) to avoid obvious finger-finger collisions.
   * 
   * Note that this method does NOT perform full collision avoidance.
   */
  /*public void moveToJointsClipped( double[] amplitudes, boolean safeAbduction, HandProxy proxy ) {
    if (proxy == null) {
      msg( "-W- moveFingersToJointsClipped: proxy is null, ignored" );
    }
    try {
      double[] jointAngles = toJointsClipped( amplitudes, proxy );
      
      if (safeAbduction) {
        jointAngles = toSafeAbduction( jointAngles ); 
      }
      
      proxy.setJointAngles( 
          " FFJ1 " + jointAngles[0] 
        + " FFJ2 " + jointAngles[1] 
        + " FFJ3 " + jointAngles[2] 
        + " FFJ4 " + jointAngles[3] 
        + " MFJ1 " + jointAngles[4] 
        + " MFJ2 " + jointAngles[5] 
        + " MFJ3 " + jointAngles[6] 
        + " MFJ4 " + jointAngles[7] 
        );
      proxy.setJointAngles(
          " RFJ1 " + jointAngles[8] 
        + " RFJ2 " + jointAngles[9] 
        + " RFJ3 " + jointAngles[10] 
        + " RFJ4 " + jointAngles[11] 
        + " LFJ1 " + jointAngles[12] 
        + " LFJ2 " + jointAngles[13] 
        + " LFJ3 " + jointAngles[14] 
        + " LFJ4 " + jointAngles[15] 
        + " LFJ5 0.0"                                 
        );
      proxy.setJointAngles(
          " THJ1 " + jointAngles[16] 
        + " THJ2 " + jointAngles[17] 
        + " THJ3 " + jointAngles[18] 
        + " THJ4 " + jointAngles[19] 
        + " THJ5 " + jointAngles[20] 
        ); 
    }
    catch( Exception e ) {
      e.printStackTrace();
    } 
  }*/

  
  
  /**
   * set the joint-mapping (=names) used by Alex for his Matlab scripts.
   * Note that LFJ5 and the wrist are missing:
     JOINTS = {...
    'FFJ1_POS', ...
    'FFJ2_POS', ...
    'FFJ3_POS', ...
    'FFJ4_POS', ...
    'MFJ1_POS', ...
    'MFJ2_POS', ...
    'MFJ3_POS', ...
    'MFJ4_POS', ...
    'RFJ1_POS', ...
    'RFJ2_POS', ...
    'RFJ3_POS', ...
    'RFJ4_POS', ...
    'LFJ1_POS', ...
    'LFJ2_POS', ...
    'LFJ3_POS', ...
    'LFJ4_POS', ...
    'THJ1_POS', ...
    'THJ2_POS', ...
    'THJ3_POS', ...
    'THJ4_POS', ...
    'THJ5_POS' };
   */
  public void setMatlabSynergyJointNames() {
    jointNames = new String[] {
      "FFJ1", "FFJ2", "FFJ3", "FFJ4", 
      "MFJ1", "MFJ2", "MFJ3", "MFJ4", 
      "RFJ1", "RFJ2", "RFJ3", "RFJ4", 
      "LFJ1", "LFJ2", "LFJ3", "LFJ4", 
      // "LFJ5" : not included in Alex' analysis!
      "THJ1", "THJ2", "THJ3", "THJ4", "THJ5", 
      // "WRJ1", "WRJ2" : not included in Alex' analysis
    };
  }



  // function JOINTS = reconstruct_synergies(PARAMS, MEANVAL, EIGENVECS)
  // ZEROMEANJOINTS = PARAMS*EIGENVECS';
  // JOINTS = add_mean(ZEROMEANJOINTS, MEANVAL);
  // end


  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("GraspSynergy NDOF=" + NDOF + " N=" + N + " name=" + getName()
        + "\n");
    if (origin != null) {
      sb.append("Origin:\n" + Array.toString(origin) + "\n");
    }
    for (int i = 0; i < N; i++) {
      sb.append("EG<" + i + ">\n" + Array.toString(eigengrasps[i]) + "\n");
    }
    return sb.toString();
  }
}