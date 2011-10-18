/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package program4;

/**
 *
 * @author Administrator
 */
import com.jcraft.jsch.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.*;
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Tunnel t = new Tunnel();
        t.go();
        t.delete();
        t.done();
    }

}
class Tunnel {
Connection con = null;
 public void go() {

      int aport=0;
     try{
 String host="gwdspace.wrlc.org";
 String user="dspace";
 String password="GwDSpace3918";
 int port=9999;

 int tunnelLocalPort=8440;
 String tunnelRemoteHost="gwdspace.wrlc.org";
 int tunnelRemotePort=3306;

 JSch jsch=new JSch();
 Session session=jsch.getSession(user, host, port);
 java.util.Properties config = new java.util.Properties();
config.put("StrictHostKeyChecking", "no");
session.setConfig(config);
System.out.println(session.getUserName());
 session.setPassword(password);
 //localUserInfo lui=new localUserInfo();
 //session.setUserInfo(lui);
 session.connect();
 aport=session.setPortForwardingL(tunnelLocalPort,host,tunnelRemotePort);
 System.out.println(session.getPortForwardingL());

 System.out.println("Connected "+aport);
 Class.forName("com.mysql.jdbc.Driver");
   String url = "jdbc:mysql://127.0.0.1:8440/bagdb";

   // Get a connection to the database for a
   // user named auser with the password
   // drowssap, which is password spelled
   // backwards.


  // DriverManager.setLoginTimeout(1);
    System.out.println(DriverManager.getLoginTimeout());
  con = DriverManager.getConnection(url, "bagit","GWbaGit");
  
  
 //Thread.sleep(10000);

     }
     catch(Exception e)
     {
         System.out.println(e);
     }
    }
public void delete()
    {
try {


   Statement st = con.createStatement();
   Statement st1=con.createStatement();
   System.out.println("result set aquired");
  ResultSet rs = st.executeQuery("select *  from bag_record where (bag_status like 'validated' AND source_deleted = 0) OR (bag_status like 'imported' AND source_deleted=0)" );
int size =0;
if (rs != null)
{
  rs.beforeFirst();
  rs.last();
  size = rs.getRow();
}

System.out.println("Record set size "+size);
rs.first();
int count =1;
  while(rs.next())
  {

    String p_name=rs.getString(4);
    String barcode=rs.getString(2);
    String bag_name= rs.getString(5);
    System.out.println(p_name+ " "+barcode+ " "+bag_name);
    
    File dest=new File("u:\\assetstore-ro\\"+barcode+"\\"+bag_name);
    boolean test = deleteDirectory(dest);
    if(test)
    {
        System.out.println(dest.getAbsolutePath()+"  is the "+count+" file to be deleted");
        int val = st1.executeUpdate("update bag_record set source_deleted = 1 where record_id = "+rs.getString(1));
        }
    else
        System.out.println(dest.getAbsolutePath()+"  cant be deleted");
      
    
        count++;

  }


  } catch (Exception e) {
   e.printStackTrace();
   System.out.println("Exception: " + e.getMessage());
  }
}
public void done()
    {
    try
    {
    con.close();
        }
    catch(Exception e)
    {
        System.out.println(e);
        e.printStackTrace();
    }
}
boolean deleteDirectory(java.io.File dir)
        {
           try{
            if (dir.isDirectory()) {
        String[] children = dir.list();
        for (int i=0; i<children.length; i++) {
            boolean success = deleteDirectory(new java.io.File(dir, children[i]));
            if (!success) {
                return false;
            }
        }
    }

    // The directory is now empty so delete it
    return dir.delete();

           }
           catch(Exception e)
           {
               System.out.println(e);
               e.printStackTrace();
           }
            return false;


    }
}
