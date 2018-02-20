/* Logger.java 
 * 
 * 20.04.10 - added padded() and spaces()
 * 07.04.10 - added some printf stuff
 * 25.03.10 - new class, initial documentation
 * 
 * (C) 2010 fnh
 */

package hdbt.util;

import java.util.Formatter;
import java.util.Locale;



public class Logger {
  public static final int DEBUG       = 0;
  public static final int INFOS       = 1;
  public static final int WARNINGS    = 2;
  public static final int ONLY_ERRORS = 3;
  public static final int SILENT      = 4;

  static int level = DEBUG;

  
  
  public static void setLoggingLevel( int l ) {
    level = l;
  }
      
  
  /**
   * formats the given double value using a new Formatter with US locale and the 
   * given format string.
   * 
   * Example: format( "%8.4f", Math.PI )
   */
  public static String format( String form, double d ) {
    return new Formatter( Locale.US ).format( form, d ).toString();
  }
  
  
  /**
   * formats the given integer value using a new Formatter with US locale and
   * the given format string.
   */
  public static String iformat( String form, int i ) {
    return new Formatter( Locale.US ).format( form, i ).toString();
  }
  
  
  /**
   * adds leading zeroes to the given integer 'value' until it is nchars wide.
   */
  public static String leadingZeros( int value, int nchars ) {
    StringBuffer sb = new StringBuffer();
    String svalue = "" + value;
    for( int i=svalue.length(); i < nchars; i++ ) {
      sb.append( "0" );
    }
    sb.append( svalue );
    return sb.toString();
  }
  
  
  /**
   * appends spaces to the given string until it is nchars long.
   */
  public static String padded( String s, int nchars ) {
    if (s == null) return spaces( nchars );
    int n = s.length();
    if (n >= nchars) return s;
    else return s + spaces( nchars-n );
  }
  

  /**
   * creates a string of the given number of space characters.
   */
  public static String spaces( int nspaces ) {
    StringBuffer sb = new StringBuffer();
    for( int i=0; i < nspaces; i++ ) {
      sb.append( " " ); 
    }
    return sb.toString();
    
  }
      
  
  public static void msg( String s ) {
    if (s == null) return;
    System.out.println( s ); 
  }

}
