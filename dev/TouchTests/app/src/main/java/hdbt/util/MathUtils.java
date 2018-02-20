/* MathUtils.java
 * 
 * misc. mathematical utilities
 * 
 * 16.03.12 - add drandom( lower, upper )
 * 06.04.10 - new class
 * 
 * (C) 2010 fnh
 */


package hdbt.util;

import java.util.*;



public class MathUtils {
  public final static double LOG2  = Math.log( 2.0 );
  public final static double LOG10 = Math.log( 10.0 );
  
  public final static double DEG2RAD = Math.PI / 180.0;
  public final static double RAD2DEG = 180.0 / Math.PI;

  
  /**
   * clamp the given 'value' to the range [lower..upper].
   * Requires lower <= upper.
   */
  public static int iclamp( int value, int lower, int upper ) {
    if (value <= lower)      return lower;
    else if (value >= upper) return upper;
    else                      return value;
  }

  
  /**
   * clamp the given 'value' to the range [lower..upper].
   * Requires lower <= upper.
   */
  public static double dclamp( double value, double lower, double upper ) {
    if (value <= lower)      return lower;
    else if (value >= upper) return upper;
    else                      return value;
  }
  
  
  /**
   * clamp the given pixel 'value' to the range [0..255].
   */
  public static int pclamp( int value ) {  
    if       (value <= 0)      return 0;
    else if (value >= 255)    return 255;
    else                      return value;
     
  }
  
  
  public static double log2( double v ) {
    return Math.log( v ) / LOG2; 
  }
  
  
  public static double[][] zeroes( int N, int M ) {
    double[][] matrix = new double[N][M];
    for( int i=0; i < N; i++ ) {
      for( int j=0; j < M; j++ ) {
         matrix[i][j] = 0.0;
      }
    }
    return matrix;
  }
  
  
  public static double findMaxElement( double[][] matrix ) {
    int rows = matrix.length;
    int cols = matrix[0].length;
    
    double max = Double.MIN_VALUE;
    for( int r=0; r < rows; r++ ) {
      for( int c=0; c < cols; c++ ) {
        double v = matrix[r][c];
        if (v >= max) max = v;
      }
    }
    return max;
  }
  
  
  /**
   * convert the given ArrayList<Double> to a double[] array.
   */
  public static double[] toArray( ArrayList<Double> ald ) {
    int N = ald.size();
    double[] tmp = new double[N];
    for( int i=0; i < N; i++ ) {
      tmp[i] = ald.get(i).doubleValue();      
    }
    return tmp;
  }
  
  
  /**
   * returns a random number in the given range [lower..upper]
   * and uniform distribution.
   */
  public static double drandom( double lower, double upper ) {
    double range = Math.abs(upper - lower);
    return lower + range*Math.random();
  }
  
  
  
  /**
   * calculate useful lower and upper bounds, and the number of recommended
   * axis labels for plotting data with the given minimum and maximum values.
   * Returns an array with { lower value, upper value, number of ticks }.
   */
  public static double[] autoscale( double minValue, double maxValue ) {
    double m1 = Math.min( minValue, maxValue );
    double m2 = Math.max( minValue, maxValue );
    
    // FIXME
    throw new RuntimeException( "IMPLEMENT ME!" );
    
  }
  

}
