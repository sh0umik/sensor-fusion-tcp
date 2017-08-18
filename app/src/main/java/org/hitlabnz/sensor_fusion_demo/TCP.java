package org.hitlabnz.sensor_fusion_demo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by fahim on 8/18/17.
 */

public class TCP {

    // Server Items
    public static Socket socClient = null;

    public static ServerSocket socServer = null ;

    private static final int SERVER_PORT = 6777;

    public static void sendData(String w, String x, String y, String z) {

        if (socServer == null) {

            new Thread(new Runnable() {

                @Override
                public void run() {

                    try {
                        socServer = new ServerSocket(SERVER_PORT);
                            System.out.println("Init Socket Once and Alive");
                            socClient = socServer.accept();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();


        } else {

            try {

                DataOutputStream responseStream = new DataOutputStream(socClient.getOutputStream());
                String data = w + "|" + x + "|" + y + "|" + z + "\n";
                System.out.println("Sending : " + data);
                responseStream.writeBytes(data);
                responseStream.flush();

                //mySocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}