/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcpserver;

import java.net.*;
import java.io.*;

public class ChatServerThread extends Thread
{  private TCPServer       server    = null;
   private Socket           socket    = null;
   private int              ID        = -1;
   private String ClientName        = null;
   private DataInputStream  streamIn  =  null;
   private DataOutputStream streamOut = null;
   private int goPortIndex=-1;
   private int GrupChat=-1;
   public ChatServerThread(TCPServer _server, Socket _socket)
   {  super();
      server = _server;
      socket = _socket;
      ID     = socket.getPort();
      goPortIndex=-1;
      GrupChat=-1;
   }

    public String getClientName() {
        return ClientName;
    }

    public void setClientName(String ClientName) {
        this.ClientName = ClientName;
    }

    public int getGoPortIndex() {
        return goPortIndex;
    }

    public void setGoPortIndex(int goPortIndex) {
        this.goPortIndex = goPortIndex;
    }

    public int getGrupChat() {
        return GrupChat;
    }

    public void setGrupChat(int GrupChat) {
        this.GrupChat = GrupChat;
    }

   
   
   public void send(String msg)
   {   try
       {  streamOut.writeUTF(msg);
          streamOut.flush();
       }
       catch(IOException ioe)
       {  System.out.println(ID + " ERROR sending: " + ioe.getMessage());
          server.remove(ID);
          stop();
       }
   }
   public int getID()
   {  return ID;
   }
   public void run()
   {  System.out.println("Server Thread " + ID + " running.");
      while (true)
      {  try
         {  server.handle(ID, streamIn.readUTF());
         }
         catch(IOException ioe)
         {  System.out.println(ID + " ERROR reading: " + ioe.getMessage());
            server.remove(ID);
            stop();
         }
      }
   }
   public void open() throws IOException
   {  streamIn = new DataInputStream(new 
                        BufferedInputStream(socket.getInputStream()));
      streamOut = new DataOutputStream(new
                        BufferedOutputStream(socket.getOutputStream()));
   }
   public void close() throws IOException
   {  if (socket != null)    socket.close();
      if (streamIn != null)  streamIn.close();
      if (streamOut != null) streamOut.close();
   }
}