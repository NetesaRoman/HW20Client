package view;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

/*
 *
 * @author Roman Netesa
 *
 */
public class Client {

    private static DataOutputStream dataOutputStream = null;
    private static DataInputStream dataInputStream = null;

    public static void main(String[] args) throws InterruptedException {

        try (
                Socket socket = new Socket("localhost", 8080);
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

                PrintWriter oos = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader ois = new BufferedReader(new InputStreamReader(socket.getInputStream()));) {

            Runnable runnable = () -> {
                while (true) {
                    try {
                        update(socket);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            };
            Thread readThread = new Thread(runnable);
            readThread.start();
            System.out.println("Client connected to socket.");
            System.out.println();
            System.out.println("Client writing channel = oos & reading channel = ois initialized.");


            while (!socket.isOutputShutdown()) {


                if (br.ready()) {


                    System.out.println("Client start writing in channel...");
                    Thread.sleep(1000);
                    String clientCommand = br.readLine();


                    oos.println(clientCommand);
                    System.out.println("Clien sent message " + clientCommand + " to server.");
                    Thread.sleep(1000);


                    if (clientCommand.split(" ")[0].equalsIgnoreCase("file")) {

                        System.out.println("SEND FILE");
                        dataInputStream = new DataInputStream(
                                socket.getInputStream());
                        dataOutputStream = new DataOutputStream(
                                socket.getOutputStream());
                        sendFile(clientCommand.split(" ")[1]);
                        dataInputStream.close();
                        dataInputStream.close();

                    }


                    if (clientCommand.equalsIgnoreCase("exit")) {


                        System.out.println("Client kill connections");
                        Thread.sleep(2000);


                        break;
                    }


                }
            }

            System.out.println("Closing connections & channels on clentSide - DONE.");

        } catch (
                UnknownHostException e) {

            e.printStackTrace();
        } catch (
                IOException e) {

            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void update(Socket socket) throws IOException {
        BufferedReader ois = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        System.out.println("reading...");
        String in = ois.readLine();
        System.out.println(in);
    }

    private static void sendFile(String path)
            throws Exception {


        int bytes = 0;

        File file = new File(path);
        FileInputStream fileInputStream
                = new FileInputStream(file);


        dataOutputStream.writeLong(file.length());

        byte[] buffer = new byte[4 * 1024];
        while ((bytes = fileInputStream.read(buffer))
                != -1) {

            dataOutputStream.write(buffer, 0, bytes);
            dataOutputStream.flush();
        }


    }
}
