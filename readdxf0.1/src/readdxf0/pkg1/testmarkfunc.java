/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package readdxf0.pkg1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author User
 */
public class testmarkfunc {

    /**
     * @param args the command line arguments
     */
    public static String fileName="testmark.txt";
    
    public static void main(String[] args) throws FileNotFoundException, IOException {
        // TODO code application logic here
        
      File file=new File(fileName);
      FileReader fr=new FileReader(file);
      BufferedReader bfr= new BufferedReader(fr);
      String s1=null,s2=null;
      int cnt=0;
      while((s1=bfr.readLine())!=null && (s2=bfr.readLine())!=null){
        if(cnt==4){
          bfr.reset();
        }
        bfr.mark(100);
        System.out.println(s1);
        System.out.println(s2);
        cnt=cnt+2;
      }
  }

}


