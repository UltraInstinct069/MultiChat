package tcpserver;
import java.net.*;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TCPServer implements Runnable
{  private ChatServerThread clients[] = new ChatServerThread[50];
   private ServerSocket server = null;
   private Thread       thread = null;
   private int clientCount = 0;
   int goPortIndex=-1;
   Connection myconObj=null;
        Statement mystatObj=null;
        ResultSet myResObj=null;

   public TCPServer (int port)
   {  try
      {  System.out.println("Binding to port " + port + ", please wait  ...");
         server = new ServerSocket(port);  
         System.out.println("Server started: " + server);
         start(); }
      catch(IOException ioe)
      {  System.out.println("Can not bind to port " + port + ": " + ioe.getMessage()); }
   }
@Override
   public void run()
   {  while (thread != null)
      {  try
         {  System.out.println("Waiting for a client ...");
             
                            
            addThread(server.accept()); }
         catch(IOException ioe)
         {  System.out.println("Server accept error: " + ioe); stop(); }
      }
   }
   public void start()  { if (thread == null)
      {  thread = new Thread(this); 
         thread.start();
      }
   }
   public void stop()   { 
   if (thread != null)
      {  thread.stop(); 
         thread = null;
      }
   }
   private int findClient(int ID)
   {  for (int i = 0; i < clientCount; i++)
         if (clients[i].getID() == ID)
            return i;
      return -1;
   }
   public synchronized void handle(int ID, String input)
   {  if (input.equals(".bye"))
      {  clients[findClient(ID)].send(".bye");
         remove(ID); 
      }
      else if(input.equals(".list"))
      {
          System.out.println("Server Onlines");
       for (int i = 0; i < clientCount; i++){
           //clients[findClient(ID)].send(".list");
           clients[findClient(ID)].send(": " + clients[i].getClientName());
           System.out.println(": " + clients[i].getID());
       }
      }
      else if(input.equals(".PM"))
      {
          clients[findClient(ID)].setGoPortIndex(-1);
          
       
      }
      else if(input.startsWith("PM"))
      {
          String data[]=input.split(" ");
          //int goID=Integer.parseInt(data[1]);
          int goPortIndex=-1;
          System.out.println("Private message entering....");
          /*for(int i = 0; i < clientCount; i++)
          {
              if(clients[i].getID()==goID) {
                  goPortIndex=i;
                  break;
              }
              
          }*/
          for(int i = 0; i < clientCount; i++)
          {
              if(clients[i].getClientName().equals(data[1])) {
                  goPortIndex=i;
                  System.out.println("Found: "+goPortIndex);
                  break;
              }
              
          }
          
          if(goPortIndex==-1)
          {
              clients[findClient(ID)].send(data[1]+" is offline now.To send offilne msg write .off_name_message");
          }
          else{
          clients[findClient(ID)].setGoPortIndex(goPortIndex);
          clients[goPortIndex].send(clients[findClient(ID)].getClientName()+ ": " + input);
          }
      }
      else if(input.startsWith(".off"))
      {
       String data[]=input.split("_");
       String sender=clients[findClient(ID)].getClientName();
       String Rec=data[1];
       String offMsg=data[2];
          
       try{
               Connection myconObj=null;
               Statement mystatObj=null;
               ResultSet myResObj=null;
               myconObj=DriverManager.getConnection("jdbc:derby://localhost:1527/Accounts", "Abir", "12345");
               PreparedStatement pst=myconObj.prepareStatement("insert into OFFLINEMSG values(?,?,?)");
               pst.setString(1,sender);
               pst.setString(2,Rec);
               pst.setString(3,offMsg);
               
               pst.executeUpdate();
           
        }catch(SQLException e){
            System.err.println(e);
        }
          
      }
      else if(input.startsWith(".add"))
      {
       String data[]=input.split(" ");
          //int addID=Integer.parseInt(data[1]);
          for(int i = 0; i < clientCount; i++)
          {
              if(clients[i].getClientName().equals(data[1])) {
                  goPortIndex=i;
                  break;
              }
          }
          clients[findClient(ID)].setGrupChat(1);
          clients[goPortIndex].setGrupChat(1);
          clients[findClient(ID)].setGoPortIndex(-1);
          clients[goPortIndex].setGoPortIndex(-1);
      }
      else if(input.startsWith(".LeaveGrp"))
      {
       
          clients[findClient(ID)].setGrupChat(-1);
          for (int i = 0; i < clientCount; i++) { 
                if(clients[i].getGrupChat()==1)  
                clients[i].send("[Group]: "+clients[findClient(ID)].getClientName() + " leaves the group" );
              }
          
      }
      else if(input.startsWith(".sendReq"))
      {
       
          String data[]=input.split(" ");
          //int addID=Integer.parseInt(data[1]);
          for(int i = 0; i < clientCount; i++)
          {
              if(clients[i].getClientName().equals(data[1])) {
                  goPortIndex=i;
                  break;
              }
          }
          
          clients[goPortIndex].send(clients[findClient(ID)].getClientName()+" Wants to be your friend.Write .acc_space_name to accept or .dec to decline");
          
      }
      else if(input.startsWith(".acc"))
      {
       
          String data[]=input.split(" ");
          //int addID=Integer.parseInt(data[1]);
         String F1=data[1];
         String F2=clients[findClient(ID)].getClientName();
         for(int i = 0; i < clientCount; i++)
          {
              if(clients[i].getClientName().equals(data[1])) {
                  goPortIndex=i;
                  break;
              }
          }
         try{
               Connection myconObj=null;
               Statement mystatObj=null;
               ResultSet myResObj=null;
               myconObj=DriverManager.getConnection("jdbc:derby://localhost:1527/Accounts", "Abir", "12345");
               PreparedStatement pst=myconObj.prepareStatement("insert into FRIENDS values(?,?),(?,?)");
               pst.setString(1,F1);
               pst.setString(2,F2);
               pst.setString(3,F2);
               pst.setString(4,F1);
               pst.executeUpdate();
           
        }catch(SQLException e){
            System.err.println(e);
        }
          clients[findClient(ID)].send("You and "+F1+" become friends");
          clients[goPortIndex].send("You and "+F2+" become friends");
          
      }
       else if(input.equals(".dec"))
      {
          System.out.println("Friend request not accepted.");
      }
       else if(input.equals(".showMsg"))
      {
          try{
              Connection myconObj=null;
           Statement mystatObj=null;
           ResultSet myResObj=null;
           String onlineName=clients[findClient(ID)].getClientName();
           myconObj=DriverManager.getConnection("jdbc:derby://localhost:1527/Accounts", "Abir", "12345");
           PreparedStatement pst=myconObj.prepareStatement("select SENDER,MSG from OFFLINEMSG where RECEIVER=?");
           pst.setString(1,onlineName);
           myResObj=pst.executeQuery();
           String OffMSG=null;
           String Sender=null;
           while(myResObj.next()){
               OffMSG=myResObj.getString("MSG");
               Sender=myResObj.getString("SENDER");
           }
           clients[findClient(ID)].send(Sender+"(OfflineMSG): "+OffMSG);
           
           pst=myconObj.prepareStatement("delete from OFFLINEMSG where RECEIVER=? and SENDER=?");
           pst.setString(1,onlineName);
           pst.setString(2,Sender);
           pst.executeUpdate();
           
            //System.out.println("port:"+name+" "+port);
        }catch(SQLException e){
            System.err.println(e);
        }
          
          
      }
       else if(input.equals(".frnds"))
      {
          clients[findClient(ID)].send("Friend List :");
          try{
              Connection myconObj=null;
           Statement mystatObj=null;
           ResultSet myResObj=null;
           String name=clients[findClient(ID)].getClientName();
           String FriendName;
           myconObj=DriverManager.getConnection("jdbc:derby://localhost:1527/Accounts", "Abir", "12345");
           PreparedStatement pst=myconObj.prepareStatement("select FRIEND2 from FRIENDS where FRIEND1=?");
           pst.setString(1,name);
           myResObj=pst.executeQuery();
           
           while(myResObj.next()){
               FriendName=myResObj.getString("FRIEND2");
               clients[findClient(ID)].send(FriendName);
           }
            
        }catch(SQLException e){
            System.err.println(e);
        }
      }
      else{
               System.out.println("GoInd :"+goPortIndex);
          if(clients[findClient(ID)].getGoPortIndex()==-1 && clients[findClient(ID)].getGrupChat()==-1){  
         for (int i = 0; i < clientCount; i++)             
                clients[i].send(clients[findClient(ID)].getClientName() + ": " + input);
         }
          else if(clients[findClient(ID)].getGrupChat()==1 && clients[findClient(ID)].getGoPortIndex()==-1){
              for (int i = 0; i < clientCount; i++) { 
                if(clients[i].getGrupChat()==1)  
                clients[i].send("[Group]"+clients[+findClient(ID)].getClientName() + ": " + input);
              }
          }
          else{
               clients[findClient(ID)].send("[PM]"+clients[findClient(ID)].getClientName()+": " + input);
               clients[clients[findClient(ID)].getGoPortIndex()].send("[PM]"+clients[findClient(ID)].getClientName() + ": " + input);
          }
          }
       
         
   }
   public synchronized void remove(int ID)
   {  int pos = findClient(ID);
      if (pos >= 0)
      {  ChatServerThread toTerminate = clients[pos];
         System.out.println("Removing client thread " + ID + " at " + pos);
         if (pos < clientCount-1)
            for (int i = pos+1; i < clientCount; i++)
               clients[i-1] = clients[i];
         clientCount--;
         try
         {  toTerminate.close(); }
         catch(IOException ioe)
         {  System.out.println("Error closing thread: " + ioe); }
         toTerminate.stop(); }
   }
   private void addThread(Socket socket) 
   {  if (clientCount < clients.length)
      {  
          
          System.out.println("Client accepted: " + socket.getPort());
          String name=null;
          int port=socket.getPort();
          try{
          Thread.sleep(100);
          }catch(Exception e){
              System.err.print(e);
          }
              
          try{
              
              Connection myconObj=null;
           Statement mystatObj=null;
           ResultSet myResObj=null;
           myconObj=DriverManager.getConnection("jdbc:derby://localhost:1527/Accounts", "Abir", "12345");
           PreparedStatement pst=myconObj.prepareStatement("select USERNAME from USERS where PORTNUM=?");
           pst.setInt(1,port);
           myResObj=pst.executeQuery();
           
           while(myResObj.next()){
               name=myResObj.getString("USERNAME");
           }
            System.out.println("port:"+name+" "+port);
        }catch(SQLException e){
            System.err.println(e);
        }
          
         clients[clientCount] = new ChatServerThread(this, socket);
         try
         {  clients[clientCount].open(); 
            clients[clientCount].start();
            clients[clientCount].setClientName(name);
            clientCount++; 
         }
                  
         catch(IOException ioe)
         {  System.out.println("Error opening thread: " + ioe); } 
      
         try{
              Connection myconObj=null;
           Statement mystatObj=null;
           ResultSet myResObj=null;
           String onlineName=clients[clientCount-1].getClientName();
           myconObj=DriverManager.getConnection("jdbc:derby://localhost:1527/Accounts", "Abir", "12345");
           PreparedStatement pst=myconObj.prepareStatement("select SENDER from OFFLINEMSG where RECEIVER=?");
           pst.setString(1,onlineName);
           myResObj=pst.executeQuery();
           String senderName=null;
           while(myResObj.next()){
               senderName=myResObj.getString("SENDER");
               clients[clientCount-1].send("You have a Offline message from "+senderName+".Write .showMsg to see.");
           }
            //System.out.println("port:"+name+" "+port);
        }catch(SQLException e){
            System.err.println(e);
        }
         
      }
      else
         System.out.println("Client refused: maximum " + clients.length + " reached.");
   
         
   }
   public static void main(String args[]) { TCPServer server = null;
         server = new TCPServer(2000); }
}