package edu.uclm.esi.tysweb2023.model;

import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Email {

    JSONObject configuracion;

    public static void main(String[] args) throws ClientProtocolException, IOException {

        Email e = new Email();

        e.send("jhonnytenazas@gmail.com", "AsuntoDesdeMain", "Buenassss");

    }

    //public void send(String destinatario, String asunto, Token token) throws ClientProtocolException, IOException {
    public void send(String destinatario, String asunto, String body) throws ClientProtocolException, IOException {

        //String body="";
        this.configuracion = read("./parametros.txt");

        JSONObject jEmail = getConfiguration().getJSONObject("email");

        JSONArray jsaHeaders = new JSONArray().
                put("api-key").put(jEmail.getString("api-key")).
                put("accept").put(jEmail.getString("accept")).
                put("content-type").put(jEmail.getString("content-type"));

        JSONObject jsoTo = new JSONObject().
                put("email", destinatario).
                put("name", destinatario);

        JSONObject jsoData = new JSONObject().
                put("sender", jEmail.getJSONObject("sender")).
                put("to", new JSONArray().put(jsoTo)).
                put("subject", asunto).
                put("htmlContent", body);

        JSONObject payload = new JSONObject().
                put("url", jEmail.getJSONObject("endpoint")).
                put("headers", jsaHeaders).
                put("data", jsoData);
        //Formatear JSON para Brevo

        //envio con un post
        //Client client = new Client();
        //client.sendCurlPost(payload, body);

        //Metemos el post en el body
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost("http://www.a-domain.example/foo/");

        // Request parameters and other properties.
        List<NameValuePair> params = new ArrayList<NameValuePair>(2);
        params.add(new BasicNameValuePair("param-1", "12345"));
        params.add(new BasicNameValuePair("param-2", "Hello!"));
        httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

        //Execute and get the response.
        HttpResponse response = httpclient.execute(httppost);
        HttpEntity entity = response.getEntity();

        if (entity != null) {
            try (InputStream instream = entity.getContent()) {
                // do something useful
            }
        }

    }

    private JSONObject getConfiguration() {
        return this.configuracion;
    }

    private JSONObject read(String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();
        try (InputStream fis = classLoader.getResourceAsStream(fileName)) {
            byte[] b = new byte[fis.available()];
            fis.read(b);
            String s = new String(b);
            return new JSONObject(s);
        } catch (IOException e) {
            return null;
        }
    }

    private void setConfiguracion(JSONObject configuracion) {
        this.configuracion = configuracion;
    }

    private String readTextFile(String fileName) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        try (InputStream fis = classLoader.getResourceAsStream(fileName)) {
            byte[] b = new byte[fis.available()];
            fis.read(b);
            return new String(b);
        }
    }

}
