// PingClient.java
import java.io.*;             // To use BufferedReader class
import java.net.*;	//To use DatagramPacket, DatagramSocket classes
import java.util.*;     //To use Random class
//********************************************************************************************************************************************************
public class PingClient
{
              	static long []  rtt= new long[10];		//Used to store the RTTs of packets 0-9
	static long []  pingSendTime=new long[10];
	
        public static void main(String Args[]) throws Exception	// main program
        {
	if (Args.length != 2)                                                                 //Vlidating CMD Arguments          
                   {
                       System.out.println("Invalid arguments!!!  :  Required host-Name &  port #");
                       System.exit(1);				// Terminate program
                    
                    }
	for(int i=0;i<10;i++)
	{
		rtt[i]=-1;				// Initializing rtt array with initial values
	}
	String server=Args[0];			// Extract server name
                    int portNO=Integer.parseInt(Args[1]);		//Extract port number
 	DatagramSocket socket=new DatagramSocket();	          //Creating Socket for sending & receiving Packets	
 	InetAddress  ipAddress =InetAddress.getByName(server);   //Getting ip address of server
	for(int sequenceNO=0;sequenceNO<10;sequenceNO++)	//Iterations to send packets 0-9
	{
		Calendar calendar = Calendar.getInstance();	//To use time of calendar in hh:mm:ss formate
		pingSendTime[sequenceNO]=System.currentTimeMillis();           //To use time in milliseconds formate
		String pingMessage ="Ping:"+sequenceNO+ " " +"Time="+ new Formatter().format("%02d:%02d:%02d",(calendar.get(Calendar.HOUR)),calendar.get(Calendar.MINUTE),calendar.get(Calendar.SECOND))+"\n";     //Making  message
		DatagramPacket clientRequest=new DatagramPacket(pingMessage.getBytes(), pingMessage.length(),ipAddress,portNO );    //Making Request Packet
		socket.send(clientRequest);			//Sending client request
 		DatagramPacket serverResponse =new DatagramPacket(new byte[100], 100); 	//Create packet to store server response
 		socket.setSoTimeout(1000);			// Max time out of socket, i.e wait for packet at socket for 1 second
 		try
 		{
 		           socket.receive(serverResponse);		// if packet not received in 1000ms then exception occours and catch block will execute
		           long  pingReceiveTime =System.currentTimeMillis();
		           printData(serverResponse,pingReceiveTime,sequenceNO);          //Printing  Server Response
 		}
		catch(IOException E)
		{
 			System.out.println("Timeout for  Packet "+sequenceNO);
		}
 		Thread.sleep(1000);			//wait for 1 second to send next request
	}
		 printStats();			//functioin to print min , max, avg ,loss rate of rtt
        }
//********************************************************************************************************************************************************************************************************
        public static void printData(DatagramPacket packet, long receivedTime,int sequenceNO) throws  Exception
        {
	byte[] data = packet.getData();			//Getting bytes of data in udp packet
	ByteArrayInputStream byteArray = new ByteArrayInputStream(data);   //Wrap in stream to read as stream of bytes
	InputStreamReader reader = new InputStreamReader(byteArray);        //Wrap in stream to read as stream of characters
	BufferedReader br = new BufferedReader(reader);		                 //to read data as whole terminated ny \n
	String line = br.readLine();				 //Reading data
	int  i= (int)line.charAt(5);				//getting its sequence number
	i=i-48;						//Conversion to integer
	rtt[i]= receivedTime-pingSendTime[i];					//Save RTT of packet i in rtt array;
	if(i==sequenceNO)
	{
									//Here packet is received in correct order
	line=line+"  Successfully Received"+"  RTT = "+new Formatter().format("%03d",rtt[i])+"ms";    
	}
	else
		line=line+"  Received Out of Order"+"  RTT = "+new Formatter().format("%03d",rtt[i]);	//Here packet is Not received  in correct order
	System.out.println( line);   
       }
//*********************************************************************************************************************************************
        public static void printStats()
        {
                         System.out.println("\n********************* Ping Stats ********************\n");
	     long min=100000;
                         long max=rtt[0];
	     long avg=0;
	     int count=0;		
	     for(int i=0;i<10;i++)
                         {
                                                if(rtt[i]>max)
                                                          max=rtt[i];
		        if(rtt[i]<min && rtt[i]!=-1)			//Find min , max of RTT
		                  min=rtt[i];
		         if(rtt[i]!=-1)
		          {							
			avg=avg+rtt[i];
			count++;
	                 	         } 
                         }
	     if(count==0)
                                 min=max=0;
	     double lost=(10-count);		
	     double lostRate =(lost/10)*100;			//Calculate lost rate
                         avg = avg /10;		    
	     System.out.println("Minimum = "+ min +"ms        Maximum  = "+max+"ms");
                         System.out.println("Avg Rtt = " +avg+"ms        LostRate = "+lostRate+" %");	      	
         }
//*********************************************************************************************************************************************
}