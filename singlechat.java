/* complie in Command prompt using command "javac singlechat.java"
and you can open project using command "java singlechat"--->for server application 
"java singlechat local"--->for client chat*/ 
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
class singlechat extends JPanel
{
	Socket sock;
	JTextArea recivedtext;
	private GridBagConstraints c;
	private GridBagLayout gridbag;
	private JFrame frame;
	private JLabel label;
	private int port=6666;
private JTextField sendtext;
private DataOutputStream remoteout;
public static void main(String arg[])
{
	JFrame f=new JFrame("waiting for connection");
	String s=null;
	if(arg.length>0)
	{
		s=arg[0];
		
	}
	singlechat chat=new singlechat(f);
	f.add("Center",chat);
	f.setSize(400,300);
	f.getContentPane().setBackground(Color.BLUE);
	f.setVisible(true);
	if(s==null)
	{
		chat.server();
	}
	else
	{
		chat.client(s);
	}
}
public singlechat(JFrame f)
{
frame=f;
frame.addWindowListener(new windowExitHandler());
Insets insets=new Insets(10,20,5,10);
gridbag=new GridBagLayout();
setLayout(gridbag);
c=new GridBagConstraints();
c.insets=insets;
c.gridy=0;
c.gridx=0;
label=new JLabel("Text to Send");
gridbag.setConstraints(label,c);
add(label);
c.gridx=1;
sendtext=new JTextField(20);
sendtext.addActionListener(new TextActionHandler());
gridbag.setConstraints(sendtext,c);
add(sendtext);
c.gridy=1;
c.gridx=0;
label=new JLabel("Text Recived");
gridbag.setConstraints(label,c);
add(label);
c.gridx=1;
recivedtext=new JTextArea(3,20);
gridbag.setConstraints(recivedtext,c);
add(recivedtext);
}
private void server()
{
	ServerSocket serversock=null;
	try
	{
		InetAddress serverAddr=InetAddress.getByName(null);
		displayMsg("waiting for connection on"+serverAddr.getHostName()+"on port"+port);
		serversock =new ServerSocket(port,1);
		sock=serversock.accept();
		displayMsg("Accepted connection from"+sock.getInetAddress().getHostName());
		remoteout=new DataOutputStream(sock.getOutputStream());
		new singlechatReceive(this).start();
	}catch(IOException e)
	{
		displayMsg(e.getMessage()+"Failed to connect to client");
		
	}finally
	{
		if(serversock!=null)
		{
			try
			{
				serversock.close();
			}catch(IOException x){}
		}
	}
		
	}
	private void client(String serverName)
	{
		try
		{
			if(serverName.equals("local"))
				serverName=null;
			InetAddress serverAddr=InetAddress.getByName(serverName);
			sock=new Socket(serverAddr.getHostName(),port);
			remoteout=new DataOutputStream(sock.getOutputStream());
			displayMsg("connected to server "+serverAddr.getHostName()+"on port"+sock.getPort());
			new singlechatReceive(this).start();
			
		}catch(IOException e)
		{
			displayMsg(e.getMessage()+"Failed to connect to server");
		}
	}
	void displayMsg(String s)
	{
		frame.setTitle(s);
	}
	
	class windowExitHandler extends WindowAdapter
	{
		public void windowClosing(WindowEvent e)
		{
			Window w=e.getWindow();
			w.setVisible(false);
			w.dispose();
			System.exit(0);
		}
	}
	class TextActionHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			try
			{
				remoteout.writeUTF(sendtext.getText());
				sendtext.setText("");
			}catch(IOException x)
			{
				displayMsg(x.getMessage()+"connection to peer lost");
			}
		}
			
	}
}
 class singlechatReceive extends Thread
 {
	 private singlechat chat;
	 private DataInputStream remotein;
	 private boolean listening=true;
	 public singlechatReceive(singlechat chat)
	 {
		 this.chat=chat;
	 }
	 public synchronized void run()
	 {
		 String s;
		 try
		 {
			 remotein=new DataInputStream(chat.sock.getInputStream());
			 while(listening)
			 {
				 s=remotein.readUTF();
				 chat.recivedtext.setText(s);
		 }
		 
	 }catch(IOException e)
	 {
		 chat.displayMsg(e.getMessage()+"connection to peer lost");
		 
	 }finally
	 {
		 try
		 {
			 if(remotein!=null)
			 {
				 remotein.close();
			 }
		 }catch(IOException x){}
 }
	 }
 }
	
	
