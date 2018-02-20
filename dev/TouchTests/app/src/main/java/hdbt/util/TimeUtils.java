/* TimeUtils.java
 * 
 * utility functions for managing and pretty-printing "time" data
 * 
 * 29.04.2011 - added prettyFilename
 * 11.07.2010 - implement prettyTimestamp and prettySeconds
 * 11.07.2010 - add javadoc comments
 * 05.07.2010 - new class
 * 
 * (C) 2010 fnh
 */


package hdbt.util;

import java.text.SimpleDateFormat;
import java.util.*;


/**
 * utility class to manage, parse, and format timestamps and dates.
 */
public class TimeUtils {
  
  
  /** formats the given value using %.02d format */
  private static String dec2( long value ) {
    if (value < 0)       return ""+value;
    else if (value == 0) return "00";
    else if (value < 10) return "0" + value;
    else                  return ""+value;
  }

  
  /** formats the given value using %.0d3 format */
  private static String dec3( long value ) {
    if (value < 0)       return ""+value;
    else if (value == 0) return "000";
    else if (value < 10) return "00" + value;
    else if (value < 100) return "0" + value;
    else                   return "" + value;
  }
  
  
//  private static String dec4( long value ) {
//    if (value < 0)       return ""+value;
//    else if (value == 0) return "0000";
//    else if (value < 10) return "000" + value;
//    else if (value < 100) return "00" + value;
//    else if (value < 1000) return "0" + value;
//    else                    return "" + value;
//  }
  
  
  public static String prettyMillis( double deltat ) {
    return prettyMillis( (long) deltat );
  }
  
  
  /**
   * pretty-print the given timestamp (assumed to be a difference between 
   * two timestamps, aka. a delta-time)
   * in hh:mm:ss:ddd format or DDd:HHh.SSs.ddd format if days>0.
   * Could be implemented using SimpleDateFormat, but done by hand here...
   */
  public static String prettyMillis( long deltat ) {
    long   days = deltat / (24*60*60*1000L);
    long     d1 = deltat - (days* 24*60*60*1000L);
    long    hrs = d1 / (60*60*1000); // mins*secs*milliseconds
    long     d2 = d1 - (hrs * 60*60*1000);
    long   mins = d2 / (60*1000);
    long     d3 = d2 - (mins * 60*1000);
    long   secs = d3 / (1000);
    long millis = d3 - (secs*1000);
    
    if (days > 0) {
      return "" + days + "d:" 
              + dec2(hrs) + "h:"
              + dec2(mins) + "m:"
              + dec2(secs) + "s."
              + dec3(millis);
    }
    else 
      return dec2(hrs) 
              + ":" + dec2(mins) 
              + ":" + dec2(secs) 
              + "." + dec3(millis );
  }
  
  
  /**
   * print seconds (and optionally, day:hrs:mins), but omit milliseconds.
   */
  public static String prettySeconds( long deltat ) {
    long   days = deltat / (24*60*60*1000L);
    long     d1 = deltat - (days* 24*60*60*1000L);
    long    hrs = d1 / (60*60*1000); // mins*secs*milliseconds
    long     d2 = d1 - (hrs * 60*60*1000);
    long   mins = d2 / (60*1000);
    long     d3 = d2 - (mins * 60*1000);
    long   secs = d3 / (1000);
//  long millis = d3 - (secs*1000);
    
    if ((days > 0) || (hrs > 0)) {
      return prettyMillis( deltat );      
    }
    else if (mins > 0) {
      return dec2(mins) + ":" + dec2(secs) + "\"";
    }
    else {
      return dec2(secs) + "\"";
    } 
  }
  
  
  public static String prettyTimestamp( double millis ) {
    return prettyTimestamp( (long) millis, null ); 
  }
  
  
  /**
   * uses SimpleDateFormat to format the given timestamp in 
   * "yyyy.MM.dd z 'at' HH:mm:ss" format, using "GMT" time zone.
   * Note that milliseconds are dropped.
   */
  public static String prettyTimestamp( long millis ) {
    return prettyTimestamp( millis, null );
  }
  
  
  /**
   * uses SimpleDateformat to format the given timestamp in
   * "yyyy.MM.dd z 'at' HH:mm:ss" format for the given TimeZone.
   * Note that milliseconds are dropped.
   */
  public static String prettyTimestamp( long millis, TimeZone tz ) {
    
    Date date = new Date( millis ); // since 1/1/1970
    SimpleDateFormat sdf = new SimpleDateFormat( "yyyy.MM.dd z 'at' HH:mm:ss" );
    if (tz != null) {
      sdf.setTimeZone( tz );
    }
    else { // default for null tz is GMT
      sdf.setTimeZone( TimeZone.getTimeZone( "GMT" )); 
    }
    return sdf.format( date );
  }

  
  public static String prettyFilename( long millis, TimeZone tz ) {
    Date date = new Date( millis );
    SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd--HH-mm-ss" );
    sdf.setTimeZone( (tz != null) ? tz : TimeZone.getTimeZone( "CET" ));
    return sdf.format( date );
  }

}