/* Array.java - Array
 *
 * Matlab-style utility functions to manipulate (1D-) arrays
 *
 * 02.01.13 add toSimpleString() to print an array as one long line
 * 28.12.12 fix broken number format in toString(), default changes to "%9.4"
 * 14.08.12 add linearRegressionEval()
 * 07.06.12 add quantile()
 * 18.01.12 add transpose( double[][] )
 * 27.04.11 add applyZeroThreshold()
 * 21.04.11 add clamp()
 * 22.11.10 add linearRegression()
 * 19.11.10 add getMean() and getMeanAndVariance()
 * 18.05.10 rewrite getMinMax(a,b) and getMinMax(a,b,c)
 * 18.05.10 imported from jfig.utils (make hdbt self-contained)
 * ...
 * 25.06.03 new class
 */

package hdbt.util;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;
import java.util.StringTokenizer;

/**
 * utility functions for double[] arrays, Matlab-style:
 * <ul>
    <li>element-wise array operations: add, sub, mult, div
    <li>element-wise math operations: sin, cos, tan, ..
    <li>scalar operations: add, sub, mult, div 
    <li>array creation, linspace, logspace, ...
    <li>concatenation, subset, ...
    <li>getMin, getMax, ...
   </ul>
 */
public class Array {

static private java.util.Random generator = null;
static private double __log2  = Math.log( 2.0 );
static private double __log10 = Math.log( 10.0 );


public static void help() {
  System.out.println(
  "Class Array provides utility function that operate on double[] arrays.\n"
+ "The main motivation is to provide Matlab-style functionality. However,\n"
+ "without the compact syntax, the resulting code is much less readable.\n"
+ "\nSome methods in this class:\n"
+ "Array         - constructor\n"
+ "constant      - create array with N elements initialized with value\n"
+ "zeros         - create array initialized with N zeroes\n"
+ "ones          - create array initialized with N ones\n"
+ "array(...)    - create array with given values (1..4) elements\n"
+ "parse         - create array from given string\n"
+ "toString      - print string rep. of the given array\n"
+ "\n"
+ "random        - create array with N random elements [0..1)\n"
+ "reverse       - reverse order of elements\n"
+ "add,sub,...   - add, subtract, multiply, divide arrays\n"
+ "cos,exp,...   - calculate elementwise sin,cos,exp,log,... values\n"
+ "ceil, floor   - apply ceil or floor operation to each element\n"
+ "round         - round each element\n"
+ "sum           - sum all elements in this array\n"
+ "toXXX         - convert array to int[], floor[], double[] array\n"
+ "\n"
+ "equals        - calculate elementwise comparison, results are 0 or 1\n"
+ "lessThan...   - calculate elementwise comparison, results are 0 or 1\n"
+ "getMin...     - get minimum, maximum, mean value of array elements\n"
+ "getIndexOfMin - get index of minimum/maximum array element\n"
+ "toMask        - replace each non-0 element with 1\n"
+ "indexArray    - construct array with indices of non-null elements\n"
+ "\n"
+ "clamp         - ensure values are within the allowed range, clamp as necessary\n"
+ "\n"
+ "hanning...    - create window function with N elements\n"
+ "convolve      - convolve two arrays\n"
+ "filter        - digital (IIR/FIR) filter\n"
+ "\n"
+ "transpose     - transposes the given 2D-array\n"
+ "\nExample usage:\n"
+ "from de.mmkh.tams import Array\n"
+ "x = Array.range(0,5)       # [0.0 1.0 2.0 3.0 4.0 5.0]\n"
+ "s = Array.sin( x )                                    \n"
+ "f = Array.parse( '0.1 0.3 0.5 0.3 0.1' )              \n"
+ "z = Array.convolve( s, f )                            \n"
+ "...\n"
  );
}
  
  
/**
 * dummy constructor; you can use this to create an Array instance that
 * allows you to access the static methods of this class from Matlab 5.3
 * (which requires a very ugly syntax to use static methods).
 */
public Array() {
 /* empty, just a placeholder to allow Matlab 5.3 non-static accesses */  
}

public static double[] constant( int N, double value ) {
  return constant( value, N );
}

public static double[] constant( double value, int N ) {
  if (N < 0) throw new RuntimeException( "constant: N too small, must be N>=1");
  double[] result = new double[ N ];
  for( int i=0; i < N; i++ ) {
    result[i] = value;
  }
  return result;
}

/** create an array of N zeroes */
public static double[] zeros( int N ) {
  return constant( 0.0, N );
}

/** create a matrix (M rows, N columns) initialized with all zeros */
public static double[][] zeros( int M, int N ) {
  double[][] matrix = new double[M][N];
  for( int r=0; r < M; r++ ) {
    for( int c=0; c < N; c++) {
      matrix[r][c] = 0.0; 
    } 
  }
  return matrix;
}


/** create a tensor (M rows, N columns, O depths ) initialized with all zeros */
public static double[][][] zeros( int M, int N, int O ) {
  double[][][] tensor = new double[M][N][O];
  for( int r=0; r < M; r++ ) {
    for( int c=0; c < N; c++) {
      for( int d=0; d < O; d++ ) {
        tensor[r][c][d] = 0.0; 
      } 
    } 
  }
  return tensor;
}


/** create an array of N ones */
public static double[] ones( int N ) {
  return constant( 1.0, N );
}

/** create a matrix (M rows, N columns) initialized with all ones */
public static double[][] ones( int M, int N ) {
  double[][] matrix = new double[M][N];
  for( int r=0; r < M; r++ ) {
    for( int c=0; c < N; c++) {
      matrix[r][c] = 1.0; 
    } 
  }
  return matrix;
}

/** create an identity matrix (M rows, N columns). 
 *  That is, the matrix holds zero values
 *  everywhere except for 1's on the main diagonal.
 */
public static double[][] eye( int M, int N ) {
  double[][] matrix = zeros(M,N);
  int NM = Math.min(M,N);
  for( int i=0; i < NM; i++ ) {
    matrix[i][i] = 1.0;
  }
  return matrix;
}



/** create an array [x0] */
public static double[] array( double x0 ) {
  return new double[] { x0 };
}

/** create an array [x0,x1] */
public static double[] array( double x0, double x1 ) {
  return new double[] { x0, x1 };
}

public static double[] array( double x0, double x1, double x2 ) {
  return new double[] { x0, x1, x2 };
}

public static double[] array( double x0, double x1, double x2, double x3 ) {
  return new double[] { x0, x1, x2, x3 };
}

public static double[] array( double x0, double x1, double x2, double x3, double x4 ) {
  return new double[] { x0, x1, x2, x3, x4 };
}

/**
 * read a whitespace, comma, semicolon separated string
 * and create a double array with values corresponding to
 * the tokens in that string.
 */
public static double[] array( String s ) {
  StringTokenizer st = new StringTokenizer( s, " \t\n,;" );
  int N = st.countTokens();
  double[] result = new double[ N ];
  try {
    for( int i=0; i < N; i++ ) {
      result[i] = Double.parseDouble( st.nextToken() );
    }
  }
  catch( Exception e ) {
    System.err.println( "-E- Array.array: Illegal input '"+s+"'");
  }
  return result;  
}


/** 
 * parse the given string and construct an array from it.   
 * See array(String).
 */
public static double[] parse( String s ) {
  return array( s );
}


public static double[] reverse( double[] arg ) {
  double[] result = new double[ arg.length ];
  for( int i=arg.length-1; i >= 0; i-- ) {
    result[i] = arg[ arg.length-1-i ];
  }
  return result;
}



/**
 * generate an array of N random values. 
 * Each random value is taken from the range [0.0 .. 1.0).
 */
public static double[] random( int N ) {
  synchronized( Array.class ) {
    if (generator == null) generator = new Random();
  }
  double[] result = new double[ N ];
  for( int i=0; i < N; i++ ) {
    result[i] = generator.nextDouble();
  }
  return result;
}

/** create a matrix (M rows, N columns) of random values.
 *  Each random value is inside the range [0.0 .. 1.0).
 */
public static double[][] random( int M, int N ) {
  synchronized( Array.class ) {
    if (generator == null) generator = new Random();
  }
  double[][] matrix = new double[M][N];
  for( int r=0; r < M; r++ ) {
    for( int c=0; c < N; c++) {
      matrix[r][c] = generator.nextDouble(); 
    } 
  }
  return matrix;
}





public static double[] toDouble( int[] x ) {
  int N = x.length;
  double[] result = new double[ N ];
  for( int i=0; i < N; i++ ) result[i] = x[i];
  return result;
}


public static double[][] toDouble( int[][] x ) {
  int M = x.length;
  int N = x[0].length;
  double[][] result = new double[M][N];
  for( int r=0; r < M; r++ ) {
    for( int c=0; c < N; c++ ) {
      result[r][c] = x[r][c];
    }
  }
  return result;
}




public static double[] toDouble( float[] x ) {
  int N = x.length;
  double[] result = new double[ N ];
  for( int i=0; i < N; i++ ) result[i] = x[i];
  return result;
}

public static float[] toFloat( double[] x ) {
  int N = x.length;
  float[] result = new float[ N ];
  for( int i=0; i < N; i++ ) result[i] = (float) x[i];
  return result;
}

/** 
 * round the values in array x to the nearest integer;
 * too-large values are clipped to Integer.MAX_VALUE and
 * MIN_VALUE respectively.
 */
public static int[] toInteger( double[] x ) {
  int N = x.length;
  int[] result = new int[ N ];
  for( int i=0; i < N; i++ ) result[i] = (int) Math.round(x[i]);
  return result;
}


public static int[][] toInteger( double[][] x ) {
  int M = x.length;
  int N = x[0].length;
  int[][] result = new int[M][N];
  for( int r=0; r < M; r++ ) {
    for( int c=0; c < N; c++ ) {
      result[r][c] = (int) Math.round( x[r][c] );
    }
  }
  return result;
}


public static int[] izeros( int M ) {
  return new int[M];
}


public static int[][] izeros( int M, int N ) {
  return new int[M][N];
}


public static double[] ceil( double[] x ) {
  int N = x.length;
  double[] result = new double[ N ];
  for( int i=0; i < N; i++ ) result[i] = Math.ceil( x[i] );
  return result;
}


public static double[] floor( double[] x ) {
  int N = x.length;
  double[] result = new double[ N ];
  for( int i=0; i < N; i++ ) result[i] = Math.floor( x[i] );
  return result;
}


public static double[] round( double[] x ) {
  int N = x.length;
  double[] result = new double[ N ];
  for( int i=0; i < N; i++ ) result[i] = Math.round( x[i] );
  return result;
}


public static double[] linspace( double min, double max, int N ) {
  if (N < 1) throw new RuntimeException( "linspace: N too small, must be N>1" );
  if (N == 1) { // special case...
    if (min == max) return new double[] { min };
    else throw new RuntimeException( "linspace: N=1 but min!=max" );
  }
   
  double[] result = new double[ N ];
  double x  = min;
  double dx = (max-min)/(N-1);
  for( int i=0; i < N; i++ ) {
    result[i] = x;
    x += dx; 
  }
  return result; 
}

public static double[] logspace( double exp1, double exp2, int N ) {
  if (N < 2) throw new RuntimeException( "linspace: N too small, must be N>2" );
  double[] result = new double[ N ];

  double  x = exp1;
  double dx = (exp2-exp1)/(N-1);
  for( int i=0; i < N; i++ ) {
    result[i] = Math.pow( 10, x );
    x += dx; 
  }
  return result; 
}


/**
 * create a double array with all integers in the given range.
 * We use Matlab conventions: both start and end values are included.
 * This differs from Python/Jython, where the end value is excluded.
 */ 
public static double[] range( int start, int end ) {
  int     N = Math.abs( end - start ) + 1 ;
  int delta = (end > start) ? +1 : -1;
  double[] result = new double[ N ];
  for( int i = 0, value=start; i < N; i++ ) {
    result[i] = value;
    value     = value + delta;
  }
  return result;
}


/**
 * resample (or 'stretch') the given input vector to the new length.
 * This currently uses simple nearest-neighbor sampling and should 
 * rather use better techniques (e.g. bilinear or fft-based).
 */
public static double[] resample( double[] arg, int new_length ) {
  double    scale = 1.0 * arg.length / new_length;
  double[] result = new double[ new_length ];
  for( int i=0,j=0; i < new_length; i++ ) {
    j         = (int) (i*scale);
    result[i] = arg[j]; 
  }
  return result;
}


public static double[] clone( double[] arg ) {
  double[] result = new double[ arg.length ];
  for( int i=arg.length-1; i >= 0; i-- ) {
    result[i] = arg[i]; 
  }  
  return result;
}



/*
public static double[] copy( double value, int N ) {
  double[] result = new double[ N ];
  for( int i=N-1; i >= 0; i-- ) {
    result[i] = value;
  }
  return result;
}
*/

public static double getMin( double[] arg ) {
  double tmp = Double.MAX_VALUE;
  for( int i=arg.length-1; i >= 0; i-- ) {
    if (arg[i] < tmp) tmp = arg[i];
  }
  return tmp;
}

public static double getMax( double[] arg ) {
  double tmp = Double.MIN_VALUE;
  for( int i=arg.length-1; i >= 0; i-- ) {
    if (arg[i] > tmp) tmp = arg[i];
  }
  return tmp;
}

public static double[] getMinMax( double[] arg ) {
  double min =  Double.MAX_VALUE;
  double max = -Double.MAX_VALUE;
  for( int i=arg.length-1; i >= 0; i-- ) {
    if (arg[i] > max) max = arg[i];
    if (arg[i] < min) min = arg[i];
  }
  return new double[] { min, max };
}


public static double[] getMinMax( double[] a, double[] b ) {
  double[] amm = getMinMax( a );
  double[] bmm = getMinMax( b );
  double[] abmm = new double[] { amm[0], amm[1], bmm[0], bmm[1] };
  return getMinMax( abmm );
}


public static double[] getMinMax( double[] a, double[] b, double[] c ) {
  double[] amm = getMinMax( a );
  double[] bmm = getMinMax( b );
  double[] cmm = getMinMax( c );
  double[] abc = new double[] { amm[0], amm[1], bmm[0], bmm[1], cmm[0], cmm[1] };
  return getMinMax( abc );
}


public static double[] getMinMax( double[] a, double[] b, double[] c, double d[] ) {
  double[] amm = getMinMax( a );
  double[] bmm = getMinMax( b );
  double[] cmm = getMinMax( c );
  double[] dmm = getMinMax( c );
  double[] abcd = new double[] 
                     { amm[0], amm[1], bmm[0], bmm[1], cmm[0], cmm[1], dmm[0], dmm[1] };
  return getMinMax( abcd );
}

public static int getIndexOfMinValue( double arg[] ) {
  double tmp = Double.MAX_VALUE;
  int      N = arg.length;
  int    idx = 0;
  for( int i=0; i < N; i++ ) {
    if (arg[i] < tmp) { tmp = arg[i]; idx = i; }
  }
  return idx;
}


public static int getIndexOfMaxValue( double arg[] ) {
  double tmp = -Double.MAX_VALUE;
  int      N = arg.length;
  int    idx = 0;
  for( int i=0; i < N; i++ ) {
    if (arg[i] > tmp) { tmp = arg[i]; idx = i; }
  }
  return idx;
}


/**
 * return a range of 'clipped'/'rounded' values suitable that bound
 * the minimum and maximum values of the input array.
 * The typical use is to calculate pleasing bounds when plotting the
 * values; 
 * Examples:
 * input bounds:       output array: 
 * [-1.3, 72.5]        [-20, 0, 20, 40, 60, 80]
 * [ 0.0, 3.14]        [0.0, 1.0, 2.0, 3.0, 4.0]
 * [-15001, -11001]    [-16000, -14000, -12000, -10000]
 */ 
public static double[] autoRangeClipped( double arg[] ) {
  double xx[] = getMinMax( arg );
  double xmin = xx[0];
  double xmax = xx[1];
  double delta = xmax - xmin;
  double  expo = Math.floor( log10( delta ));
  double     m = delta / Math.pow( 10, expo );
  
//System.out.println( "-#- autoRangeClipped: " + toString(arg) );
//System.out.println( "-#- autoRangeClipped: " + delta + " " + expo + " " + m );
  
  // select one of a few typical ranges
  //
  double rr[] = null;
  
  if       (m <= 1)     rr = new double[]{ 0.0, 0.2, 0.4, 0.6, 0.8, 1.0 };
  else if  (m <= 1.2)   rr = new double[]{ 0.0, 0.3, 0.6, 0.9, 1.2 };
  else if  (m <= 1.5)   rr = new double[]{ 0.0, 0.5, 1.0, 1.5 };
  else if  (m <= 2)     rr = new double[]{ 0.0, 0.5, 1.0, 1.5, 2.0 };
  else if  (m <= 3)     rr = new double[]{ 0.0, 1.0, 2.0, 3.0 };
  else if  (m <= 4)     rr = new double[]{ 0.0, 1.0, 2.0, 3.0, 4.0 };
  else if  (m <= 5)     rr = new double[]{ 0.0, 1.0, 2.0, 3.0, 4.0, 5.0 };
  else if  (m <= 6)     rr = new double[]{ 0.0, 2.0, 4.0, 6.0 };
  else if  (m <= 8)     rr = new double[]{ 0.0, 2.0, 4.0, 6.0, 8.0 }; 
  else                  rr = new double[]{ 0.0, 2.0, 4.0, 6.0, 8.0, 10.0 };
  
  // scale to required power of ten, calculate difference between values
  //
  rr = mult( rr, Math.pow(10,expo) );
  double drr = rr[1] - rr[0];
  
  // find offset required to translate back (e.g. for negative xmin)
  //
  double offset = drr * Math.ceil( xmin/drr );
  rr = add( rr, offset );
    
  // fixup for a few corner cases :-)
  //
  if (xmin < rr[0] ) {
    rr = concat( array( rr[0]-drr), rr  ); 
  }
  if (xmax > rr[rr.length-1]) {
    rr = concat( rr, array(rr[rr.length-1]+drr) ); 
  }

//System.out.println( "-#- autoRangeClipped: (" + xmin + "," + xmax + ") => "
//  + toString( rr ) );

  // return the final range
  //    
  return rr;
}



/**
 * clamp all values outside of the interval [lower,upper] to this range.
 */
public static double[] clamp( double[] arg, double lower, double upper ) {
  double tmp[] = new double[ arg.length ];
  assert( upper >= lower );
  for( int i=arg.length-1; i >= 0; i-- ) {
    if      (arg[i] < lower) tmp[i] = lower;
    else if (arg[i] > upper) tmp[i] = upper;
    else                     tmp[i] = arg[i];
  }
  return tmp;
}



/**
 * zero all values within the given threshold.
 */
public static double[] applyZeroThreshold( double[] arg, double threshold ) 
{
  double tmp[] = new double[ arg.length ];
  for( int i=arg.length-1; i >= 0; i-- ) {
    if (Math.abs( arg[i] ) < threshold) tmp[i] = 0.0;
    else                                tmp[i] = arg[i];
  }
  return tmp;
}



/** average value of all elements of the given array */
public static double getMean( double[] arg ) {
  double sum = 0.0;
  for( int i=arg.length-1; i >= 0; i-- ) {
    sum += arg[i];
  }
  return (sum / arg.length);
}


/** average value and variance of all elements of the given array.
 * The result is returned as an array with the mean at index 0 and
 * the variance at index 1. 
 */ 
public static double[] getMeanAndVariance( double[] arg ) {
  double mean = getMean( arg );
  double  variance = 0.0;
  for( int i=arg.length-1; i >= 0; i-- ) {
    variance += (arg[i] - mean)*(arg[i] - mean);
  }
  variance /= arg.length;
  return new double[] { mean, variance };
}




public static double[] add( double[] arg, double[] arg2 ) {
  if (arg.length != arg2.length) 
    throw new RuntimeException( "add: vectors must be of same length" );

  double[] result = new double[ arg.length ];
  for( int i=arg.length-1; i >= 0; i-- ) {
    result[i] = arg[i] + arg2[i];
  }
  return result;
}

public static double[] add( double[] arg, double value ) {
  double[] result = new double[ arg.length ];
  for( int i=arg.length-1; i >= 0; i-- ) {
    result[i] = arg[i] + value;
  }
  return result;
}

public static double[] add( double[] x1, double[] x2, double x3[] ) {
  return add( x1, add( x2, x3 ));  
}

public static double[] add( double[] x1, double[] x2, double x3[], double x4[] ) {
  return add( add(x1,x2), add(x3,x4) );  
}


public static double[] mult( double[] arg, double[] arg2 ) {
  if (arg.length != arg2.length) 
    throw new RuntimeException( "mult: vectors must be of same length" );

  double[] result = new double[ arg.length ];
  for( int i=arg.length-1; i >= 0; i-- ) {
    result[i] = arg[i] * arg2[i];
  }
  return result;
}

public static double[] mult( double[] arg, double value ) {
  double[] result = new double[ arg.length ];
  for( int i=arg.length-1; i >= 0; i-- ) {
    result[i] = arg[i] * value;
  }
  return result;
}

public static double[] div( double[] arg, double[] arg2 ) {
  if (arg.length != arg2.length) 
    throw new RuntimeException( "div: vectors must be of same length" );

  double[] result = new double[ arg.length ];
  for( int i=arg.length-1; i >= 0; i-- ) {
    result[i] = arg[i] / arg2[i];
  }
  return result;
}

public static double[] div( double[] arg, double value ) {
  double[] result = new double[ arg.length ];
  for( int i=arg.length-1; i >= 0; i-- ) {
    result[i] = arg[i] / value;
  }
  return result;
}

public static double[] reciprocal( double[] arg ) {
  double[] result = new double[ arg.length ];
  for( int i=arg.length-1; i >= 0; i-- ) {
    try {
      result[i] = 1.0 / arg[i];
    }
    catch( Throwable t ) {
System.out.println( "-E- " + t );
    }
  }
  return result;
}

public static double[] sub( double[] arg, double[] arg2 ) {
  if (arg.length != arg2.length) 
    throw new RuntimeException( "sub: vectors must be of same length" );

  double[] result = new double[ arg.length ];
  for( int i=arg.length-1; i >= 0; i-- ) {
    result[i] = arg[i] - arg2[i];
  }
  return result;
}

public static double[] sub( double[] arg, double value ) {
  double[] result = new double[ arg.length ];
  for( int i=arg.length-1; i >= 0; i-- ) {
    result[i] = arg[i] - value;
  }
  return result;
}

public static double[] min( double[] arg, double[] arg2 ) {
  if (arg.length != arg2.length) 
    throw new RuntimeException( "min: vectors must be of same length" );

  double[] result = new double[ arg.length ];
  for( int i=arg.length-1; i >= 0; i-- ) {
    result[i] = Math.min( arg[i],  arg2[i] );
  }
  return result;
}

public static double[] max( double[] arg, double[] arg2 ) {
  if (arg.length != arg2.length) 
    throw new RuntimeException( "max: vectors must be of same length" );

  double[] result = new double[ arg.length ];
  for( int i=arg.length-1; i >= 0; i-- ) {
    result[i] = Math.max( arg[i],  arg2[i] );
  }
  return result;
}






public static double[] sin( double[] arg ) {
  double[] result = new double[ arg.length ];
  for( int i=arg.length-1; i >= 0; i-- ) {
    result[i] = Math.sin( arg[i] );    
  }
  return result;
}

public static double[] cos( double[] arg ) {
  double[] result = new double[ arg.length ];
  for( int i=arg.length-1; i >= 0; i-- ) {
    result[i] = Math.cos( arg[i] );    
  }
  return result;
}

public static double[] exp( double[] arg ) {
  double[] result = new double[ arg.length ];
  for( int i=arg.length-1; i >= 0; i-- ) {
    result[i] = Math.exp( arg[i] );    
  }
  return result;
}


public static double[] log( double[] arg ) {
  double[] result = new double[ arg.length ];
  for( int i=arg.length-1; i >= 0; i-- ) {
    result[i] = Math.log( arg[i] );    
  }
  return result;
}


public static double log2( double arg ) {
  return Math.log(arg) / __log2;
}


public static double[] log2( double[] arg ) {
  return Array.div( log(arg), __log2 );
}


public static double log10( double arg ) {
  return Math.log(arg) / __log10;  
}


public static double[] log10( double[] arg ) {
  return Array.div( log(arg), __log10 );
}



public static double[] abs( double[] arg ) {
  double[] result = new double[ arg.length ];
  for( int i=arg.length-1; i >= 0; i-- ) {
    result[i] = Math.abs( arg[i] );    
  }
  return result;
}

public static double[] sqrt( double[] arg ) {
  double[] result = new double[ arg.length ];
  for( int i=arg.length-1; i >= 0; i-- ) {
    result[i] = Math.sqrt( arg[i] );    
  }
  return result;
}

public static double[] tan( double[] arg ) {
  double[] result = new double[ arg.length ];
  for( int i=arg.length-1; i >= 0; i-- ) {
    result[i] = Math.tan( arg[i] );    
  }
  return result;
}


public static double[] concat( double[] u, double[] v ) {
  double[] result = new double[ u.length + v.length ];
  int N = u.length;
  for( int i=0; i < N; i++ ) {
    result[i] = u[i];
  }
  int M = u.length + v.length;
  for( int i=N; i < M; i++ ) {
    result[i] = v[i-N];
  }
  return result;
}

public static double[]
concat( double[] u, double[] v, double[] w ) {
  return concat( u, concat( v, w ));
}

public static double[]
concat( double[] u, double[] v, double[] w, double[] x ) {
  return concat( concat(u,v), concat(w,x) );
}




/**
 * insert the elements of y into x, starting at index 'start'
 * For example, for x=[0 1 2 3 4 5 6 7 8 9] and y=[4 3 2],
 * the call insert( x, y, 2 ) yields
 * x = [ 0 1 4 3 2 5 6 7 8 9 ]
 */
public static double[]
insert( double[] x, double[] y, int start ) {
  int NX = x.length;
  int NY = y.length;
  if (NX < (NY + start)) throw new RuntimeException( "target array too small" );
  for (int i=0; i < NY; i++ ) {
    x[i+start] = y[i];  
  }
  return x;  
}


/**
 * return the specified subset of the argument array.
 * Note that the 'end' element is included the result array.
 * Therefore, the indexing of the result array runs from 0 to (end-start).
 */
public static double[] subset( double arg[], int start, int end ) {
  if (end < start) throw new IllegalArgumentException( "-E- subset: end < start index!" );
  double[] result = new double[ end-start+1 ];
  for( int i=0,j=start; j <= end; i++,j++ ) {
    result[i] = arg[j];
  }
  return result;
}

public static double[] subset( double arg[], int start ) {
  return subset( arg, start, arg.length-1 );
}


/**
 * return the specified subset of the argument array, starting at the
 * given 'start' index and stepping by 'step' to 'end'.
 * Note that the 'end' element is included the result array.
 * For example, subset( x, x.length-1, 0, -1 ) reverses the array.
 */
public static double[] subset( double arg[], int start, int end, int step ) {
  int N = (end-start) / step;
  if (N < 0) throw new IllegalArgumentException( "-E- subset: less than zero elements due to illegal arguments" );
  double[] result = new double[N];
  int index = start;
  for( int i=0; i < N; i++ ) {
    result[i] = arg[index];
    index += step;
  }
  return result;
}


/**
 * create a new array containing the indices of all non-zero
 * elements of x[].
 * For example, for x=[ 0 1 0 0 -3 -5 0 0 7 ], this function
 * returns result = [ 1 4 5 8 ]
 */
public static int[] indexArray( double x[] ) {
  // two passes for now
  int N = x.length;
  int M = 0;
  for( int i=0; i < N; i++ ) {
    if (x[i] != 0.0) M++;    
  }
  int result[] = new int[M];
  for( int i=0,j=0; i < N; i++ ) {
    if (x[i] != 0.0) result[j++] = i; 
  }
  return result;                           
}


public static double scalarproduct( double[] arg1, double[] arg2 ) {
  if (arg1.length != arg2.length) 
    throw new RuntimeException( "scalarproduct: vectors must be of same length" );

  double result = 0.0;
  for( int i=arg1.length-1; i >= 0; i-- ) {
    result = result + (arg1[i] * arg2[i]);
  }
  return result;
}



public static String toString( double[] arg ) {
  return toString( arg, "%9.4f" );
  // return String.format( Locale.US, "%8.4f", arg );
}


public static String toString( double[] arg, String format ) {
  if (format == null) format = "%9.4f";

  StringBuffer sb = new StringBuffer();
  int N = arg.length;
  for( int i=0; i < N; i+=8 ) {
    sb.append( String.format( Locale.US, "%-4d", i )); 
    sb.append( ":  " );
    for( int j=0; j < 8; j++ ) {
      if (i+j >= N) break;
 
      String tmp = String.format( Locale.US, format, arg[i+j] );
      sb.append( tmp );
      sb.append( " " );
    }
    sb.append( "\n" );
  }
  return sb.toString();
}


public static String toSimpleString( double[] arg, String format ) {
  if (format == null) format = "%9.4f";

  StringBuffer sb = new StringBuffer();
  int N = arg.length;
  for( int i=0; i < N; i++ ) {
    String tmp = String.format( Locale.US, format, arg[i] );
    sb.append( tmp );
    sb.append( " " );
  }
  return sb.toString();
}


public static String print( double[] arg ) {
  return toString( arg ); 
}



 public static double[] normalize( double[] s ) {
    int N = s.length;
    double[] z = new double[N];

    // phase 1: find max and min sample values, collect average
    //
    double xmax = -1.0E10F;
    double xmin =  1.0E10F;
    double avg  =  0.0F;
    for( int i=0; i < N; i++ ) {
      double x = s[i];
      if (x > xmax) xmax = x;
      if (x < xmin) xmin = x;
      avg = avg + x;
    }
    if ((xmax == 0.0) && (xmin == 0.0)) {
      System.err.println( "Cannot normalize all-null signal, sorry!" );
      return z;
    }

    double scale = (double) (1.0 / Math.max( xmax, -xmin ));
    for( int i=0; i < N; i++ ) {
      z[i] = s[i]*scale;
    }
    return z;
}
  


public static double[] hamming( int N ) {
  if (N < 1) throw new RuntimeException( "hamming: N >= 1 required." ); 
  if (N == 1) return new double[] { 1.0 }; // special case

  double[] result = new double[N];
  double    scale = 2*Math.PI / (N-1);
  for( int i=0; i < N; i++ ) {
    result[i] = 0.54 - 0.46 * Math.cos( scale * i );
  }
  return result;
}

public static double[] hanning( int N ) {
  if (N < 1) throw new RuntimeException( "hanning: N >= 1 required." ); 
  if (N == 1) return new double[] { 1.0 }; // special case

  double[] result = new double[N];
  double    scale = 2*Math.PI / (N-1);
  for( int i=0; i < N; i++ ) {
    result[i] = 0.5 * (1.0 - Math.cos( scale * i ));
  }
  return result;
}


public static double sum( double x[] ) {
  double result = 0.0;
  int N = x.length;
  for( int i=0; i < N; i++ ) {
    result += x[i];
  }
  return result;
}



/**
 * find the p-quantile of the data, that is the value of all elements in x[]
 * so that p*100% percent of the data elements are smaller (left)  
 * and (1-p)*100% of the data elements are larger (right) than the p-quantile.
 * For example, 
 * quantile( x, 0.5 ) calculates the median,
 * quantile( 0.25 ) the first quartile, and 
 * quantile( 0.025 ) the 2.5% quantile.
 * 
 * Empirische Quantile teilen die Daten einer Messreihe prozentual in zwei Teile, sodass p \cdot 100% der Daten links vom Quantil und (1 - p) \cdot 100% der Daten rechts vom Quantil liegen. Angenommen die Messdaten sind geordnet in Form einer Rangliste gegeben: x_1, x_2,...,x_n. Sei weiter 0 < p < 1. Die Formel für die Berechnung eines p-Quantils ist dann wie folgt:
    \tilde x_p = \begin{cases} \frac{1}{2}(x_{n \cdot p} + x_{n \cdot p + 1}), & \text{wenn }n \cdot p\text{ ganzzahlig,}\\ x_{\lceil n \cdot p \rceil}, & \text{wenn }n \cdot p\text{ nicht ganzzahlig.} \end{cases}
Dabei ist für eine reelle Zahl x der Wert \lceil x \rceil die kleinste ganze Zahl, die größer oder gleich x ist (siehe auch: Gaußklammer#Aufrundungsfunktion). [2][3][4]
    * Beispiel 1:
\begin{align} & x_1,...,x_{10} = (1,1,1,3,4,7,9,11,13,13), ~ p = 0,3 \\ & n \cdot p = 10 \cdot 0,3 = 3 \text{ ist ganzzahlig} \rightarrow \tilde x_{0,3} = \frac{1}{2}(x_{n \cdot p} + x_{n \cdot p + 1}) = \frac{1}{2}(x_3 + x_4) = \frac{1}{2}(1 + 3) = 2 \end{align}
    * Beispiel 2:
\begin{align} & x_1,...,x_{10} = (1,1,1,3,4,7,9,11,13,13), ~ p = 0,75 \\ & n \cdot p = 10 \cdot 0,75 = 7,5 \text{ ist nicht ganzzahlig} \rightarrow \tilde x_{0,75} = x_{\lceil n \cdot p \rceil} = x_{\lceil 7,5 \rceil} = x_8 = 11 \end{align}
 * 
 * FIXME: check this method
 */
public static double quantile( double x[], double p ) {
  double[] xx = Array.clone( x );
  Arrays.sort( xx );
  double NP = x.length * p;
  int    INP = (int) Math.floor( NP );
  if (NP == INP) { // integer
    if (INP == 0) return xx[0];
    if (INP == (x.length)) return xx[x.length-1];
    return 0.5*(xx[INP-1] + xx[INP]);
  }
  else {
    return xx[INP];
  }
}



/* *********************************************************** */
/* comparisons (equals, not equals, greater, ...)              */
/* *********************************************************** */

/**
 * create a mask array where each non-zero value in x is replaced
 * by a value of one, while zeroes are kept.
 */ 
public static double[] toMask( double x[] ) {
  int N = x.length;
  double[] result = new double[N];
  for( int i=0; i < N; i++ ) {
    if (x[i] == 0.0) result[i] = 0.0;
    else             result[i] = 1.0;  
  } 
  return result;
}


/**
 * create a mask array from the equality test.
 * Therefore, result[i] = 1.0 if x[i] == y[i],
 * but result[i] = 0.0 otherwise.
 */
public static double[] equals( double x[], double y[] ) {
  if (x.length != y.length) 
    throw new RuntimeException( "add: vectors must be of same length" );

  int N = x.length;
  double[] result = new double[N];
  for( int i=0; i < N; i++ ) {
    if (x[i] == y[i]) result[i] = 1.0;
    else              result[i] = 0.0;  
  } 
  return result;
}


/**
 * create a mask array from the not-equals test.
 * Therefore, result[i] = 1.0 if x[i] != y[i],
 * but result[i] = 0.0 if x[i] == y[i].
 */
public static double[] neq( double x[], double y[] ) {
  if (x.length != y.length) 
    throw new RuntimeException( "add: vectors must be of same length" );

  int N = x.length;
  double[] result = new double[N];
  for( int i=0; i < N; i++ ) {
    if (x[i] == y[i]) result[i] = 0.0;
    else              result[i] = 1.0;  
  } 
  return result;
}


/**
 * create a mask array from the greaterThan test.
 * Therefore, result[i] = 1.0 if x[i] > y[i],
 * but result[i] = 0.0 otherwise.
 */
public static double[] greaterThan( double x[], double y[] ) {
  if (x.length != y.length) 
    throw new RuntimeException( "add: vectors must be of same length" );

  int N = x.length;
  double[] result = new double[N];
  for( int i=0; i < N; i++ ) {
    if (x[i] > y[i]) result[i] = 1.0;
    else             result[i] = 0.0;  
  } 
  return result;
}


/**
 * create a mask array from the greaterEquals test.
 * Therefore, result[i] = 1.0 if x[i] >= y[i],
 * but result[i] = 0.0 otherwise.
 */
public static double[] greaterEquals( double x[], double y[] ) {
  if (x.length != y.length) 
    throw new RuntimeException( "add: vectors must be of same length" );

  int N = x.length;
  double[] result = new double[N];
  for( int i=0; i < N; i++ ) {
    if (x[i] >= y[i]) result[i] = 1.0;
    else              result[i] = 0.0;  
  } 
  return result;
}


/**
 * create a mask array from the lessThan test.
 * Therefore, result[i] = 1.0 if x[i] < y[i],
 * but result[i] = 0.0 otherwise.
 */
public static double[] lessThan( double x[], double y[] ) {
  if (x.length != y.length) 
    throw new RuntimeException( "add: vectors must be of same length" );

  int N = x.length;
  double[] result = new double[N];
  for( int i=0; i < N; i++ ) {
    if (x[i] < y[i]) result[i] = 1.0;
    else             result[i] = 0.0;  
  } 
  return result;
}



/**
 * create a mask array from the lessEquals test.
 * Therefore, result[i] = 1.0 if x[i] <= y[i],
 * but result[i] = 0.0 otherwise.
 */
public static double[] lessEquals( double x[], double y[] ) {
  if (x.length != y.length) 
    throw new RuntimeException( "add: vectors must be of same length" );

  int N = x.length;
  double[] result = new double[N];
  for( int i=0; i < N; i++ ) {
    if (x[i] <= y[i]) result[i] = 1.0;
    else              result[i] = 0.0;  
  } 
  return result;
}

/**
 * return 0 if all elements of x[] are zero, otherwise return 1
 */
public static int any( double x[] ) {
  int N = x.length;
  for( int i=0; i < N; i++ ) {
    if (x[i] != 0.0) return 1; 
  }
  return 0;
}


public static boolean hasNaN( double x[] ) {
  int N = x.length;
  for( int i=0; i < N; i++ ) {
    if (Double.isNaN( x[i] )) return true;
  }
  return false;
}


/* *********************************************************** */
/* (linear) regression                                         */
/* *********************************************************** */


/**
 * calculates the (linear) least-squares regression (fit) for the
 * given data-points y(x), with indices i=0,(n-1).
 * The formula is z(x) = z0 + z1*x with
 * 
 * z0 = y_mean - z1*x_mean
 * 
 * z1 = \frac{ \sum_i=0^{n-1} (x_i - x_mean)*(y_i - y_main}
 *           { \sum_i=0^{n-1} (x_i - x_mean)^2}
 *           
 * x_mean = 1/n \sum_i={0}^{n-1} x_i
 * y_mean = 1/n \sum_i={0}^{n-1} y_i
 * 
 * The result is returned as an array [z0 z1].
 * See: http://de.wikipedia.org/wiki/Methode_der_kleinsten_Quadrate
 */
public static double[] linearRegression( double[]x, double[] y ) {
  if (x.length != y.length) {
    throw new RuntimeException( 
      "x and y must be non-null, and their lengths must match " 
      + x.length + " " + y.length );
  }
  double x_mean = getMean( x );
  double y_mean = getMean( y );
  
  double numerator   = 0.0;
  double denumerator = 0.0;
  for( int i=0; i < x.length; i++ ) {
    numerator   += (x[i] - x_mean) * (y[i] - y_mean);
    denumerator += (x[i] - x_mean) * (x[i] - x_mean);
  }
  double z1 = numerator / denumerator;
  double z0 = y_mean - z1*x_mean;

  return new double[] { z0, z1 };
}


/**
 * evaluates the linear-regression specified by the coefficients 
 * at all given arguments and returns a double[] array with the results.
 * f(x) = coefficients[0] + x*coefficients[1].
 */
public static double[] linearRegressionEval( double[] coefficients, 
                                                double[] args ) 
{
  int N = args.length;
  double[] results = new double[ args.length ];
  for( int i=0; i < N; i++ ) {
    results[i] = coefficients[0] + args[i] * coefficients[1];    
  }
  return results;
}




/**
 * transposes the given (non-null) NxM matrix
 */
public static double[][] transpose( double[][] matrix ) {
  if (matrix == null) return null;
  
  int N = matrix.length;    // rows
  int M = matrix[0].length; // columns per row
  
  double[][] x = new double[M][N];
  for( int i=0; i < M; i++ ) {
    for( int j=0; j < N; j++ ) {
      x[i][j] = matrix[j][i];
    }
  }
  
  return x;
}


/* *********************************************************** */
/* convolution and filter                                     */
/* *********************************************************** */



public static double[] convolve( double target[], double[] kernel ) {
  int K  = kernel.length;
  int NK = target.length - kernel.length + 1;
  // int NKK= NK - K;
  double[] result = new double[ NK ];
  
  double norm = sum( kernel );
  
  for( int i=0; i < NK; i++ ) {
    result[i] = 0.0;
    for( int k=0; k < K; k++ ) {
      // System.out.println( "" + i + " " + k + " " + NK );
      result[i] += target[i+k] * kernel[k];
    }  
    if (norm != 0.0) result[i] /= norm;
  }
  return result;  
}


/**
 * one-dimensional digital filter.
 * Y = filter( B, A, X ) filters the data in vector X with the filter
 * described by vectors A and B to create the filtered data Y. The filter
 * is the "Direct From II Transposed" implementation of the standard 
 * difference equation:
 * 
 * a(0)*y(n-1) = b(0)*x(n-1) + b(1)*x(n-2) + ... + b(nb)*x(n-nb-1)
 *                           - a(1)*y(n-1) - ... - a(na)*y(n-na-1)
 * 
 * If a(0) is not equal to 1, filter normalizes the filter coefficients
 * by a(0).
 * 
 */
public static double[] filter( double B[], double A[], double X[] ) {
  double a[] = A;
  double b[] = B;
  int NA = A.length;
  int NB = B.length;
  int NX = X.length;
  
  // prolog: checks and normalization
  //
  if (A[0] == 0.0) throw new RuntimeException( "A[0] may not be zero." );
  
  if (A[0] != 1.0) { // normalize
    a = new double[ A.length ];
    for( int i=1; i < NA; i++ ) {
       a[i] = a[i] / a[0];  
    }
    b = new double[ B.length ];
    for( int i=0; i < NB; i++ ) {
       b[i] = b[i] / a[0];  
    }
  }
  
  // now do the filtering
  //
  double[] Y = new double[ NX ];

  for( int n=0; n < NX; n++ ) {
    Y[n] = 0.0;

    int NN = Math.max( NB, n );
    for( int k=0; k < NN; k++ ) {
      Y[n] += b[k] * X[n-k];  
    }
    
    int MM = Math.max( NA, n );
    for (int k=1; k < MM; k++ ) {
      Y[n] -= a[k] * Y[n-k]; 
    }
  }

  return Y;  
}

/**
 * calculate the FFT of arrays ar and ai (real and imaginary parts of the
 * input values). 
 * Returns two vectors; the real and imaginary parts of the transform.
 * Matlab-compatible scaling (1.0 for FFT, 1/N for IFFT), i.e.
 * Matlab:  four = fft( real(data), imag(data))
 * Java:    fft[0] = real(fft(data))
 *          fft[1] = imag(fft(data))
 */
public static double[][] fft( double ar[], double ai[] ) {
  double xr[] = clone(ar);
  double xi[] = clone(ai);
  double scale = 1.0;
  
  fft_core( -1, ar.length, xr, xi, scale );
  return new double[][]{ xr, xi };  
}


public static double[][] ifft( double ar[], double ai[] ) {
  double xr[] = clone(ar);
  double xi[] = clone(ai);
  double scale = 1.0/ar.length;
  
  fft_core( 1, ar.length, xr, xi, scale );
  return new double[][]{ xr, xi };  
}


/** 
 * calculate in-place FFT for input arrays ar and ai of length 2**n.
 * sign=-1: FFT  sign=+1: iFFT
 */
public static void fft_core(int sign, int n, double ar[], double ai[], double scale) 
{
  if (ar.length != ai.length)
    throw new RuntimeException( "input array mismatch!" );
  if (ar.length != n)
    throw new RuntimeException( "input array length not the expected power of 2!" );


  // double scale = Math.sqrt(1.0/n);

  int i,j;
  for (i=j=0; i<n; ++i) {
    if (j>=i) {
              double tempr = ar[j]*scale;
              double tempi = ai[j]*scale;
              ar[j] = ar[i]*scale;
              ai[j] = ai[i]*scale;
              ar[i] = tempr;
              ai[i] = tempi;
    }       
    int m = n/2;
    while (m>=1 && j>=m) { 
              j -= m; 
              m /= 2; 
    }       
    j += m; 
  }     
    
  int mmax,istep;
  for (mmax=1,istep=2*mmax; mmax<n; mmax=istep,istep=2*mmax) {
    double delta = sign*Math.PI/mmax;
    for (int m=0; m<mmax; ++m) {
      double w = m*delta;
      double wr = Math.cos(w);
      double wi = Math.sin(w);
      for (i=m; i<n; i+=istep) {
        j = i+mmax; 
        double tr = wr*ar[j]-wi*ai[j];
        double ti = wr*ai[j]+wi*ar[j];
        ar[j] = ar[i]-tr; 
        ai[j] = ai[i]-ti; 
        ar[i] += tr;
        ai[i] += ti;
      }       
    }       
    mmax = istep;
  }     
}







  public static void main( String argv[] ) {
    double[] v = linspace( 0.0, 4*Math.PI, 31 );
    System.out.println( toString(v) );
    double[] s = sin( v );
    double[] c = cos( v );
    System.out.println( toString(s) );

    double[] sc = add( s, c );
    System.out.println( toString(sc) );

    double[] o    = constant( -1.0, c.length );
    double[] sscc = add( add( mult(s,s), mult(c,c) ), o );
    System.out.println( toString(sscc) );

    double[] t = linspace( 0.0, 10.0, 25 );
    System.out.println( toString(t) );

    double[] ls = logspace(0,2,11);
    System.out.println( toString( ls ));
    System.out.println( ls[ls.length-1] );

    double[] oo = ones( 101 );
    double[] x  = linspace( 0.0, 100.0, 101 );
    System.out.println( toString( x ));

    System.out.println( toString( reciprocal(x) ));

    System.out.println( toString( div( oo, x)));

    System.out.println( toString( subset( x, 4, 8 )));
    System.out.println( toString( subset( x, 98)  ));

    double[] u = linspace(0,3,4);
    System.out.println( toString( concat( u,u,u, subset(reciprocal(x),3,6))));

    System.out.println( toString( array( 0.2, 3.8, 4.2, 5.1 )));

    for( int i=1; i <= 100; i++ ) { // sum of numbers up to limit
      double[] a1 = ones( i );
      double[] a2 = linspace( 1, i, i );
      System.out.println( i + " " + scalarproduct( a1, a2 ));
    }
    
    System.out.println( "\n\nConvolving:" );

    double[] yy     = linspace( 0.0, 10.0, 50 ); 
    double[] kernel = constant( 3.0, 4 );
    double[] cv     = convolve( yy, kernel );
    System.out.println( toString( cv ) );
    
    double[] k2     = array( 1.0, 2.0, 3.0, 2.0, 1.0 );
    double[] cv2    = convolve( yy, k2 );
    System.out.println( "\n\n" + toString( cv2 ));
    
    double[] yy3     = s;
    double[] k3      = constant( 1.0, 7 );
    double[] cv3     = convolve( yy3, k3 );
    System.out.println( "\n\n" + toString( s ) );
    System.out.println( toString( cv3 ));


    System.out.println( "\n\nHanning:" );
    System.out.println( toString( hanning( 6 )));

    System.out.println( "\n\nRange:" );
    System.out.println( toString( range( 1, 5 ) ));
    System.out.println( toString( range( 5, 1 ) ));
    System.out.println( toString( range( 5, -5 ) ));
    System.out.println( toString( range( 17, 17 ) ));

    for( int i=0; i < 15; i++ ) {
      System.out.println( "" + i + " " + ((i*(i+1))/2) + " " 
                          + sum(range(0,i))  );
    }
    
    System.out.println( "any: " + any(zeros(115)) 
                        + " " + any(sin(range(1,20)) ));
     
    
    System.out.println( "\n\nRandom:" );
    for( int i=0; i < 10; i++ ) {
      System.out.println( toString( random( 7 ) ) ); 
    }
    for( int i=0; i < 10; i++ ) {
      double tmp[] = random( 100000 );
      System.out.println( "" + getMin(tmp) + " " + getMax(tmp) 
                           + " " + sum(tmp)/tmp.length );
    }
    
    System.out.println( "\n\nIndexArray." );
    double[] ttt = mult( ones(1000), 0.95 );
    for( int i=0; i < 10; i++ ) {
      System.out.println( 
        toString( toDouble(indexArray( greaterThan( random( 1000 ), ttt )))));
    }
    System.out.println( 
      sum(toDouble(indexArray(greaterThan(random(100000),constant(0.95,100000))))));
    
    System.out.println( "\n\n transpose:" );
    double[][] m = new double[][] { { 0, 1, 2, 3, 4 }, { 5, 6, 7, 8, 9 }};
    double[][] mt = Array.transpose( m );
    double[][] mx = new double[][] { { 0 }, { 1}, {2 }};
    double[][] my = Array.transpose( mx );
    double[][] mz = Array.transpose( my );
    System.out.println( mt + " " + mt.length );
    System.out.println( mz + " " + mz.length );
  }

}
