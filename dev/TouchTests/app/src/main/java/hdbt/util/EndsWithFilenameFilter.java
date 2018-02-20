/** EndsWithFilenameFilter.java - FilenameFilter for any filename extension
 * 
 *  07.03.2011 - new class, based on code from jfig.utils.FigFileFilter
 * 
 *  (C) 2011 fnh
 */

package hdbt.util;

import java.io.File;
import java.io.FilenameFilter;


/**
 * FilenameFilter that accepts files with the given extension(s),
 * and directories if this has been activated via setAcceptDirectories(true);
 */
public class EndsWithFilenameFilter implements FilenameFilter {
  boolean acceptDirectories = false;
  String[] knownExtensions = new String[] {};  

  
  /**
   * constructs a FileFilter that accepts all filenames ending
   * with the given extension. The extension should include the dot.
   * 
   * Example: new EndsWithFilenameFilter( ".xml" )
   */
  public EndsWithFilenameFilter( String oneExtension ) {
    knownExtensions = new String[] { oneExtension }; 
  }

  /**
   * overwrites the current set of accepted filename extensions with
   * the new one. The extensions should include the dot.
   * 
   * Example: 
   * EndsWithFilenameFilter f = new EndsWithFilenameFilter( "" );
   * f.setFileExtensions( new String[] { ".xml", ".css" } );
   * 
   */
  public void setFileExtensions( String[] extensions ) {
    knownExtensions = extensions;
  }
  
  /**
   * set whether or not this filter should accept directories.
   */
  public void  setAcceptDirectories( boolean b ) {
    acceptDirectories = b; 
  }
  
  
  public boolean accept( File dir, String name ) {
    //System.out.println( "FigFilenameFilter " + name );
    if (name == null) return false;
    if (dir == null) return false;

    for( int i=0; i < knownExtensions.length; i++ ) {
      if (name.endsWith( knownExtensions[i] )) return true; 
    }
    return acceptDirectories && (new File(dir,name)).isDirectory();
  }
  
}