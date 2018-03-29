/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcpclient;


import java.net.*;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class TCPclient implements Runnable
{  
    private static Scanner inp;
    private Socket socket              = null;
   private Thread thread              = null;
   private DataInputStream  console   = null;
   private DataOutputStream streamOut = null;
   private ChatClientThread client    = null;
   public Connection myconObj=null;
   public     Statement mystatObj=null;
   public     ResultSet myResObj=null;
   
   public TCPclient(String serverName, int serverPort,String user)
   {  System.out.println("Establishing connection. Please wait ...");
      try
      {  socket = new Socket(serverName, serverPort);
         System.out.println("Connected: " + socket.getLocalPort());
         try{
            myconObj=DriverManager.getConnection("jdbc:derby://localhost:1527/Accounts", "Abir", "12345");
           PreparedStatement pst=myconObj.prepareStatement("update ABIR.USERS set PORTNUM=? where USERNAME=?");
           pst.setInt(1,socket.getLocalPort());
           pst.setString(2,user);
           pst.executeUpdate();
        }catch(SQLException e){
            System.err.println(e);
        }
         
         //for(int i=0;i<1000;i++) System.out.println("");
             
         start();
      }
      catch(UnknownHostException uhe)
      {  System.out.println("Host unknown: " + uhe.getMessage()); }
      catch(IOException ioe)
      {  System.out.println("Unexpected exception: " + ioe.getMessage()); }
   }
   public void run()
   {  while (thread != null)
      {  try
         {  streamOut.writeUTF(console.readLine());
            streamOut.flush();
         }
         catch(IOException ioe)
         {  System.out.println("Sending error: " + ioe.getMessage());
            stop();
         }
      }
   }
   public void handle(String msg)
   {  if (msg.equals(".bye"))
      {  System.out.println("Good bye. Press RETURN to exit ...");
         stop();
      }
   else if(msg.equals(".list"))
      {
       System.out.println("List of onlines members are:");
       
       
      }
      else
         System.out.println(msg);
   }
   public void start() throws IOException
   {  console   = new DataInputStream(System.in);
      streamOut = new DataOutputStream(socket.getOutputStream());
      if (thread == null)          
      {  
         client = new ChatClientThread(this, socket);
         thread = new Thread(this);
         
         thread.start();
      }
   }
   public void stop()
   {  if (thread != null)
      {  thread.stop();  
         thread = null;
      }
      try
      {  if (console   != null)  console.close();
         if (streamOut != null)  streamOut.close();
         if (socket    != null)  socket.close();
      }
      catch(IOException ioe)
      {  System.out.println("Error closing ..."); }
      client.close();  
      client.stop();
   }
   public static void main(String args[])
   {  
       System.out.println("1.Sign up");
       System.out.println("2.Sign in");
       inp=new Scanner(System.in);
       System.out.println("Choose one option:");
       int option;
       option=inp.nextInt();
       String username=null,pass=null;
       if(option==1){
           System.out.println("Enter user Name:");
           username=inp.next();
           System.out.println("Enter Password:");
           pass=inp.next();
           try{
               Connection myconObj=null;
               Statement mystatObj=null;
               ResultSet myResObj=null;
           myconObj=DriverManager.getConnection("jdbc:derby://localhost:1527/Accounts", "Abir", "12345");
           PreparedStatement pst=myconObj.prepareStatement("insert into USERS values(?,?,?)");
           pst.setString(1,username);
           pst.setString(2,pass);
           pst.setInt(3,0);
           pst.executeUpdate();
           
           TCPclient client = null;
         client = new TCPclient ("localhost", 2000,username); 
         
        }catch(SQLException e){
            System.err.println(e);
        }
         
       }
       if(option==2){
           
           while(true){
               System.out.println("Enter user Name:");
           username=inp.next();
           System.out.println("Enter Password:");
           pass=inp.next();
           String OrgPass=null;
           try{
               Connection myconObj=null;
               Statement mystatObj=null;
               ResultSet myResObj=null;
           myconObj=DriverManager.getConnection("jdbc:derby://localhost:1527/Accounts", "Abir", "12345");
           PreparedStatement pst=myconObj.prepareStatement("select PASSWORD from USERS where USERNAME=?");
           pst.setString(1, username);
           myResObj=pst.executeQuery();
           
           while(myResObj.next()){
               OrgPass=myResObj.getString("PASSWORD");
           }
            System.out.println("port:"+OrgPass);
           if(pass.equals(OrgPass)) break;
           else System.out.println("Enter the correct password");
            
        }catch(SQLException e){
            System.err.println(e);
        }
           
           }
           TCPclient client = null;
         client = new TCPclient ("localhost", 2000,username);
       }
       //TCPclient client = null;
        // client = new TCPclient ("localhost", 2000,username,pass);
         
   }
}