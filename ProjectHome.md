Programming project
Instructor: Dr. Fawaz Bokhari Course: CMP-330 (Computer Networks)
UDP Ping
Ping is a utility for network performance measurement used to test the reachability of a host on
an Internet Protocol (IP) network and to measure the round-trip time for messages sent from the
originating host to a destination computer.
The goal of this project is to implement a simple Internet ping server, and implement a
corresponding client in java language. The functionality provided by these programs are similar
to the standard ping programs available in modern operating systems, except that they use UDP
rather than Internet Control Message Protocol (ICMP) to communicate with each other.
The ping protocol allows a client machine to send a packet of data to a remote machine, and have
the remote machine return the data back to the client unchanged (an action referred to as
echoing). Among other uses, the ping protocol allows hosts to determine round-trip times to
other machines.
You are given the starting code for the Ping server in java language below. Your job is to write
the Ping client and extend the Ping server.
Server Code
The following code implements a ping server. You need to compile and run this code. You
should study this code carefully, as it will help you write your Ping client and extend the server
code.
// PingServer.java
import java.io.**;
import java.net.**;
import java.util.**;
/**
**Server to process ping requests over UDP.**/
public class PingServer
{
private static final double LOSS\_RATE = 0.3;
private static final int AVERAGE\_DELAY = 100; // milliseconds
public static void main(String[.md](.md) args) throws Exception
{
// Get command line argument.
if (args.length != 1) {
System.out.println("Required arguments: port");
return;
}
int port = Integer.parseInt(args[0](0.md));
// Create random number generator for use in simulating
// packet loss and network delay.
Random random = new Random();
// Create a datagram socket for receiving and sending UDP packets
// through the port specified on the command line.
DatagramSocket socket = new DatagramSocket(port);
// Processing loop.
while (true) {
// Create a datagram packet to hold incomming UDP packet.
DatagramPacket request = new DatagramPacket(new byte[1024](1024.md), 1024);
// Block until the host receives a UDP packet.
socket.receive(request);
// Print the recieved data.
printData(request);
// Decide whether to reply, or simulate packet loss.
if (random.nextDouble() < LOSS\_RATE) {
System.out.println(" Reply not sent.");
continue;
}
// Simulate network delay.
Thread.sleep((int) (random.nextDouble() **2** AVERAGE\_DELAY));
// Send reply.
InetAddress clientHost = request.getAddress();
int clientPort = request.getPort();
byte[.md](.md) buf = request.getData();
DatagramPacket reply = new DatagramPacket(buf, buf.length, clientHost, clientPort);
socket.send(reply);
System.out.println(" Reply sent.");
}
}
/ Print ping data to the standard output stream.
**/
private static void printData(DatagramPacket request) throws Exception
{
// Obtain references to the packet's array of bytes.
byte[.md](.md) buf = request.getData();
// Wrap the bytes in a byte array input stream,
// so that you can read the data as a stream of bytes.
ByteArrayInputStream bais = new ByteArrayInputStream(buf);
// Wrap the byte array output stream in an input stream reader,
// so you can read the data as a stream of characters.
InputStreamReader isr = new InputStreamReader(bais);
// Wrap the input stream reader in a bufferred reader,
// so you can read the character data a line at a time.
// (A line is a sequence of chars terminated by any combination of \r and \n.)
BufferedReader br = new BufferedReader(isr);
// The message data is contained in a single line, so read this line.
String line = br.readLine();
// Print host address and data received from it.
System.out.println(
"Received from " +
request.getAddress().getHostAddress() +
": " +
new String(line) );
}
}
The server sits in an infinite loop listening for incoming UDP packets. When a packet comes in,
the server simply sends the encapsulated data back to the client.
Packet Loss
UDP provides applications with an unreliable transport service, because messages may get lost in
the network due to router queue overflows or other reasons. In contrast, TCP provides
applications with a reliable transport service and takes care of any lost packets by retransmitting
them until they are successfully received. Applications using UDP for communication must
therefore implement any reliability they need separately in the application level (each application
can implement a different policy, according to its specific needs).
Because packet loss is rare or even non-existent in typical campus networks, the server in this lab
injects artificial loss to simulate the effects of network packet loss. The server has a parameter
LOSS\_RATE that determines which percentage of packets should be lost.
The server also has another parameter AVERAGE\_DELAY that is used to simulate transmission
delay from sending a packet across the Internet. You should set AVERAGE\_DELAY to a
positive value when testing your client and server on the same machine, or when machines are
close by on the network. You can set AVERAGE\_DELAY to 0 to find out the true round trip
times of your packets.
Compiling and Running Server
To compile the server, do the following:
javac PingServer.java
To run the server, do the following:
java PingServer port
where port is the port number the server listens on. Remember that you have to pick a port
number greater than 1024, because only processes running with root (administrator) privilege
can bind to ports less than 1024.
Note: if you get a class not found error when running the above command, then you may need to
tell Java to look in the current directory in order to resolve class references. In this case, the
commands will be as follows:
java -classpath . PingServer port
Your Job
This project is divided into two parts for the ease of students.
Part I
• You should write the client so that it sends 10 ping requests to the server, separated by
exactly one second. Each message contains a payload of data that includes the keyword
PING, a sequence number, and a timestamp. After sending each packet, the client waits up to
one second to receive a reply. If one seconds goes by without a reply from the server, then
the client assumes that its packet or the server's reply packet has been lost in the network.
• You should write the client so that it starts with the following command:
java PingClient host port
where host is the name of the computer the server is running on and port is the port number it
is listening to. When developing your code, you should run the ping server on your machine,
and test your client by sending packets to localhost (or, 127.0.0.1). After you have fully
debugged your code, you should see how your application communicates across the network
with a ping server run by another member of the class.
• The ping message is formatted in a simple way. Each message contains a sequence of
characters and it should contain the following string:
PING sequence\_number time CRLF
where sequence\_number starts at 0 and progresses to 9 for each successive ping message sent
by the client, time is the time when the client sent the message, and CRLF represent the
carriage return and line feed characters that terminate the line.
• For each message returned within its one second window, print its sequence number and the
RTT. The client should be able to calculate the round trip time of each Ping message that it
has transmitted and the output of each Ping message can be shown like this
e.g PING 0 {the time it was sent} {successfully received/sent or not} {RTT}
For each timed-out packet, prints its sequence number and the string Timed out. For each
packet received out of sequence, print the sequence number of the received and expected
messages and the string Message received out of order.
• Before terminating, your program should also report the minimum, maximum, and average
RTTs of all the messages that were sent. It should also report the loss rate.
Part II
• In the second part of your project, you are required to convert your ping program (both the
server and the client code) in part I into a reliable UDP ping application so that the data can
be sent and received reliably over UDP. To do this, you will need to devise a protocol
(similar to TCP) in which the recipient of data sends acknowledgements back to the sender to
indicate that the data has arrived. You can simplify the problem by only providing one-way
transport of application data from sender to recipient.
• You also have to make sure that in case of Ping Packet loss, how would you recover from
that situation avoiding any deadlocks at the same time, meaning what are the steps that your
protocol would take in order to ensure reliability and avoiding any deadlock situation.
• You are free to use any message format as long as the output messages on the screens are
meaningful and comprehensible.
• You are required to provide a flow diagram/state machine diagram or step-by-step sequence
of operations of your proposed reliable protocol (sender-receiver) in order to show that your
proposed reliability protocol does not end up in any cycles or deadlock.
• The server program must be able to accept LOSS\_RATE as an argument from the
command prompt in addition to the port number as follows:
java PingServer port LOSS\_RATE
where loss\_rate is the rate at which the server would drop the packets. This is for the TA to
evaluate the program for different test cases as mentioned in the evaluation methodology
section. Please make sure that you do make this change in the server code, otherwise your
points will be deducted accordingly.
• Please don't try to over complicate this part by implementing extra functionality that is not
required. The reliability can be one-way in a sense that the ack will be sent from the server to
the client for each sent ping packet. Remember, the purpose of this part of the project is to let
the student understand and appreciate the challenges of introducing reliability in transport
layer services. (Note: Please read section 3.4 of the book for more understanding).
Deliverables
• The program consisting of the server and client code of both part I and II as well as any
additional programs as required.
• The code must be well documented.
• A readme file containing the instructions on how to run the program.
• A reliability document containing an explanation of how you implemented the reliability in
UDP ping application for part II, including any flow/state machine diagrams if necessary.
• All the deliverables must be e-mailed to ONLY (NOT BOTH) your respective lab
instructor's email address ("SYED ADNAN UL HASSAN" <bcsf10m046@pucit.edu.pk>,
"Saad Ahmed" <bcsf10m003@pucit.edu.pk>) as a zip, tar or gz file with the following
naming convention**

&lt;your-pucit-id&gt;



&lt;your-firstname&gt;

.tar
• The deadline for submission of the project is Wednesday, June 4, 2014, 11:59 PM
Evaluation Methodology
• Part I (30)
o Basic Client server communication (20)
o Correct calculation of RTT (5)
o Correct calculation of minimum, maximum, and average RTTs (5)
• Part II (50)
o Reliability implementation (20)
o No cycles/starvation/deadlocks during the execution of the program (10)
o Test cases: with no packet loss, with moderate or 100% packet loss including loss of
ping packets as well as ack packets (10)
o Document explaining the logic of reliable UDP (10)
• Display of proper and informative output messages (10 marks)
• Code Documentation/Comments (5 marks)
• Readme file (5 marks)