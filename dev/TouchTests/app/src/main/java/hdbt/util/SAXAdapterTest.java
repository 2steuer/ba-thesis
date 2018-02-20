
package hdbt.util;


import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;
import java.io.*;


public class SAXAdapterTest extends DefaultHandler {

  private Writer out;
  
  public SAXAdapterTest(Writer out) {
    this.out = out;   
  }
    
  public void characters(char[] text, int start, int length)
   throws SAXException {
    try {
      out.write(text, start, length); 
    }
    catch (IOException e) {
      throw new SAXException(e);   
    }
  }
  

  public static void main( String args[] ) throws Exception {
   try {

    String dir = "/tmp/HANDLE/coimbra-cube-tekscan-experiments/data acquisitions/";
//  File f = new File( dir + "dataset01/root.xml" );
    File g = new File( dir + "dataset01/StereoCamera/data_Videre.xml" );
 
    XMLReader parser = XMLReaderFactory.createXMLReader();
      
    // Since this just writes onto the console, it's best
    // to use the system default encoding, which is what
    // we get by not specifying an explicit encoding here.
    Writer out = new OutputStreamWriter(System.out);
    ContentHandler handler = new SAXAdapterTest(out);
    parser.setContentHandler(handler);
    parser.parse( g.getAbsolutePath() );
      
    out.flush();
    }
    catch (Exception e) {
      System.err.println(e); 
    }
  }

}