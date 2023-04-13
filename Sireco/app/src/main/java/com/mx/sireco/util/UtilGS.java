package com.mx.sireco.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/* renamed from: galileosolutions.com.mx.sireco.galileosolutions.com.mx.sireco.utils.UtilGS */
public class UtilGS {
    private final String USER_AGENT = "Mozilla/5.0";

    public String sendPost(String url, String urlParameters) throws Exception {
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();
        int responseCode = con.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuffer response = new StringBuffer();
        while (true) {
            String inputLine = in.readLine();
            if (inputLine != null) {
                response.append(inputLine);
            } else {
                in.close();
                return response.toString();
            }
        }
    }

    public String sendGet(String url) throws Exception {
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        int responseCode = con.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuffer response = new StringBuffer();
        while (true) {
            String inputLine = in.readLine();
            if (inputLine != null) {
                response.append(inputLine);
            } else {
                in.close();
                return response.toString();
            }
        }
    }
}
