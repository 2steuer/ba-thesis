/* PiecewiseLinearFunction
 * 
 * piecewise linearly interpolated function of given control points
 * 
 * 28.03.10 - new class
 * 
 * (C) 2010 fnh
 */

package hdbt.util;





/**
 * constructs and evaluates a piecewise linearly interpolated function
 * y = f(x) of given control points (x0,y0, x1,y1, x2,y2, ..., xN, yN ) 
 * with x0 < x1 < x2 < ... < xN.
 * 
 * For arguments x < x0, we return y0.
 * For arguments x > xN, we return yN.
 */
public class PiecewiseLinearFunction {
  int N;
  double xx[];
  double yy[];

  
  /**
   * constructs the function for the given control points.
   * We need at least two x values (one interval).
   * Also checks that the x arguments are in ascending order.
   */
  public PiecewiseLinearFunction( double x[], double y[] ) {
    if ((x == null) 
        || (y == null) 
        || (x.length < 2)
        || (x.length != y.length))
      throw new UnsupportedOperationException( "invalid arguments" );

    for( int i=1; i<N; i++ ) {
      if (x[i] <= x[i-1]) 
        throw new UnsupportedOperationException( 
                    "x values not in ascending order" );
    }

    N = x.length;
    xx = new double[N];
    yy = new double[N];
    System.arraycopy( x, 0, xx, 0, N );
    System.arraycopy( y, 0, yy, 0, N );
  }
  
  
  public double eval( double x ) {
    if      (x <= xx[0]) return yy[0];
    else if (x >= xx[N-1]) return yy[N-1];
    else {
      // bisection loop to find interval
      //
      int lower = 0;
      int upper = N-1;
      while ((upper - lower) > 1) { // not done yet
        int mid = (upper + lower) / 2;
        
//        System.out.println( " " + lower + " " + upper + " " + mid +
//             "      " + xx[lower] + " " + xx[upper] + " " + xx[mid] );
        
        if (xx[mid] > x) upper = mid;
        else             lower = mid; 

//        System.out.println( "." + lower + " " + upper + " " + mid + 
//             "        " + xx[lower] + " " + xx[upper] );
      }
      double dx = xx[upper] - xx[lower];
      double dy = yy[upper] - yy[lower];
      double y  = yy[lower] + dy * (x - xx[lower])/dx;
      
      //System.out.println( "PLF.eval x= " + x + "   y= " + y );
      return y;
    }
  }
  
}