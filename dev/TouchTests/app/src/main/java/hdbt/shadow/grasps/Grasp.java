/* Grasp.java - hdbt.shadow.grasps.Grasp
 * 
 * a generic grasp (for an antropomorphic hand)
 * 
 * 19.09.10 - new class
 * (C) 2010 fnh
 */

package hdbt.shadow.grasps;

public class Grasp {
  
  /** 
   * creates a (generic) grasp for the given hand. 
   * Actual implementation and parameters are provided by subclasses.
   */
  public Grasp() {
    
  }

  public double[] getTargetJointAngles() {
    throw new UnsupportedOperationException( "Must be subclassed" );
  }
  
  public double[] getTargetJointTorques() {
    throw new UnsupportedOperationException( "Must be subclassed" );
  }

  public GraspStatus getStatus() {
    throw new UnsupportedOperationException( "Must be subclassed" );
  }

  public static void main(String[] args) {
    throw new UnsupportedOperationException( "Must be subclassed" );
    
  }
  
}
