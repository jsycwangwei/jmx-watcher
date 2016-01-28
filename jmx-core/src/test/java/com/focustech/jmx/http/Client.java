package com.focustech.jmx.http;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class Client {
    public Client() {
    }

    public static void main(String[] args) {
        Socket client = null;
        DataOutputStream out = null;
        BufferedReader reader = null;
        try {

            client = new Socket("192.168.2.57", 8888);
            out = new DataOutputStream((client.getOutputStream()));
            String query = "dump";
            byte[] request = query.getBytes();
            out.write(request);
            out.flush();
            client.shutdownOutput();
            // ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
            // List<Result> list = (List<Result>) ois.readObject();
            // for (Result result : list) {
            // System.out.println(result.getAttributeName());
            // }
            // System.out.println(list.size());
            // reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            // byte[] reply = new byte[1024];
            // StringBuilder sb = new StringBuilder();
            // System.out.println(reader.readLine());
            // in.close();

            DataInputStream dis = new DataInputStream(client.getInputStream());
            byte[] reply = new byte[1024];
            StringBuilder sb = new StringBuilder();
            while (dis.read(reply) > -1) {
                sb.append(new String(reply));
            }
            System.out.println(sb.toString());
            dis.close();

            out.close();
            client.close();

            Thread.currentThread().sleep(1000);

        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
}
