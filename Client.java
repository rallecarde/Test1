
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class Client {
    private static DatagramSocket clientSocket;
    private static InetAddress ipAdd;
    private static String name = "";
    private static int port;
    private static long time;
    public static boolean fl;
    public static void main(String[] args) throws SocketException, UnknownHostException {
        port = Integer.parseInt(args[1]);
        clientSocket = new DatagramSocket();
        ipAdd = InetAddress.getByName(args[0]);
        time = System.currentTimeMillis();
        ThreadOut t1 = new ThreadOut(); //поток отправки
        Threadin t2 = new Threadin(); // поток принятия
        t2.setDaemon(true);
        t1.start();
        t2.start();
    }

    static class ThreadS extends Thread{
        private long time1;
        public ThreadS(long time2){
            time1= time2;
        }
        public void run() {
            try {
                sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (((System.currentTimeMillis()-time) >=60000)& (!fl)){
                System.out.println("the server is not responding");
            fl=true;
			}
        }
    }
    static class ThreadOut extends Thread {
        @Override
        public void run() {
            try {
                byte[] outData = "Start".getBytes(StandardCharsets.UTF_8);
                DatagramPacket packet1 = new DatagramPacket(outData,outData.length, ipAdd, port);
                clientSocket.send(packet1);
                while (true) {
                    BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
                    String str = userInput.readLine();
                    if(str.contains("@ping")) {
                        fl= false;
                        time = System.currentTimeMillis();
                        outData = str.getBytes(StandardCharsets.UTF_8);
                        packet1 = new DatagramPacket(outData, outData.length, ipAdd, port);
                        clientSocket.send(packet1);
                        ThreadS t4 = new ThreadS(time);
                        t4.start();
                    }
                }
            }catch (Exception e){
                System.out.println(e.toString());
            }
        }
    }

    static class Threadin extends Thread {
        @Override
        public void run() {
            try {
                while (true) {
                    byte[] inData = new byte[128];
                    DatagramPacket packet4 = new DatagramPacket(inData, inData.length);
                    clientSocket.receive(packet4);
                    String res = new String(packet4.getData());
                    if(res.contains("@ping")) {
                        System.out.println(System.currentTimeMillis()-time);
                        fl = true;
                    }
                }
            }catch (Exception e){
                System.out.println(e.toString());
            }
        }
    }
}