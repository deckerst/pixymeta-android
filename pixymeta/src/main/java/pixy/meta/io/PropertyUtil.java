package pixy.meta.io;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class PropertyUtil
{
  private static ResourceBundle b;
  
  public static String getString( String key )
  {
	  if (b == null) {
		  b = getBundle();
	  }
      assert b != null;
      return b.getString(key);
  }
  
  /** Get bundle from .properties files in the current dir. */
  private static ResourceBundle getBundle()
  {   
     ResourceBundle bundle;
     
     InputStream in = null;
     
     try {
    	 try {
    		 in = PropertyUtil.class.getResourceAsStream("properties");
    	 } catch(Exception ignored) {}
     
    	 if(in == null) {
    		 in = new FileInputStream("properties");
    	 }
		 bundle = new PropertyResourceBundle(in);
		 return bundle;
     } catch (Exception e) {
    	 e.printStackTrace();
     }
     
     return null;
  }
}
