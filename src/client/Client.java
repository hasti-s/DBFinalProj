package client;

import client.ui.first.FirstPage;
import client.ui.first.FirstPageFetcher;
import common.Constants;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by Saeid Dadkhah on 2016-06-27 11:06 AM.
 * Project: DBFinalProject
 */
@SuppressWarnings("unchecked")
public class Client implements FirstPageFetcher {

    private DataInputStream dis;
    private DataOutputStream dos;

    private JSONParser parser;

    private FirstPage firstPage;

    public static void main(String[] args) {
        new Client();
    }

    public Client() {
        for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            if (info.getName().equals("Nimbus")) {
                try {
                    UIManager.setLookAndFeel(info.getClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            Socket socket = new Socket(Constants.SN_NAME, Constants.SN_PORT);
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        parser = new JSONParser();

        firstPage = new FirstPage(this);
    }

    @Override
    public boolean signUp(String username, String password) {
        JSONObject request = new JSONObject();
        try {
            request.put(Constants.F_REQUEST, Constants.RQ_SIGNUP);
            request.put(Constants.F_USERNAME, username);
            request.put(Constants.F_PASSWORD, password);
            System.out.println(request.toJSONString());
            dos.writeUTF(request.toJSONString());

            JSONObject response = (JSONObject) parser.parse(dis.readUTF());
            String responseType = (String) response.get(Constants.F_RESPONSE);
            if (Constants.RS_SUCCESSFUL_SIGNUP.equals(responseType))
                return true;
            else if (Constants.RS_UNSUCCESSFUL_SIGNUP.equals(responseType))
                return false;
            else
                return false;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean logIn(String username, String password) {
        try {
            JSONObject request = new JSONObject();
            request.put(Constants.F_REQUEST, Constants.RQ_LOGIN);
            request.put(Constants.F_USERNAME, username);
            request.put(Constants.F_PASSWORD, password);
            System.out.println(request.toJSONString());
            dos.writeUTF(request.toJSONString());

            JSONObject response = (JSONObject) parser.parse(dis.readUTF());
            if (Constants.RS_SUCCESSFUL_SIGNUP.equals(response.get(Constants.F_RESPONSE))) {
                firstPage.dispose();
                return true;
            } else if (Constants.RS_UNSUCCESSFUL_SIGNUP.equals(response.get(Constants.F_RESPONSE)))
                return false;
            else
                return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void closing() {
        try {
            JSONObject request = new JSONObject();
            request.put(Constants.F_REQUEST, Constants.RQ_DISCONNECT);
            System.out.println(request.toJSONString());
            dos.writeUTF(request.toJSONString());
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
    }
}