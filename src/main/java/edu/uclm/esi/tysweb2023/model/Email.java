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
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Email {

    JSONObject configuracion;

    public static void main(String[] args) throws ClientProtocolException, IOException {

        Email e = new Email();
    }

    public void send(String destinatario, String asunto, Token token) throws ClientProtocolException, IOException {
    	String body = "#URL_CONFIRMACION_REGISTRO#";
    	this.setConfiguracion(read("./parametros.txt"));

    	// Imprimir el valor del token
    	System.out.println("Token ID: " + token.getId());

    	// Reemplazar la URL en el body
    	body = body.replace("#URL_REGISTRO#", getConfiguration().getString("URL_REGISTRO") + token.getId());
    	System.out.println("URL después de la primera replace: " + body);

    	setConfiguracion(read("./parametros.txt"));

    	// Reemplazar la URL nuevamente en el body
    	body = body.replace("#URL_REGISTRO#", "parteEstaticaDeURL" + token);
    	System.out.println("URL después de la segunda replace: " + body);

    	this.configuracion = read("./parametros.txt");

        JSONObject jEmail = getConfiguration().getJSONObject("email");

        JSONArray jsaHeaders = new JSONArray()
            .put("api-key").put(jEmail.getString("api-key"))
            .put("content-type").put(jEmail.getString("content-type"));

        JSONObject jsoTo = new JSONObject()
            .put("email", destinatario)
            .put("name", destinatario);

        JSONObject jsoData = new JSONObject()
            .put("sender", jEmail.getJSONObject("sender"))
            .put("to", new JSONArray().put(jsoTo))
            .put("subject", asunto)
            .put("htmlContent", body);

        JSONObject payload = new JSONObject()
            .put("url", jEmail.getString("endpoint"))
            .put("headers", jsaHeaders)
            .put("data", jsoData);
        System.out.println(payload);
        // Enviar la solicitud POST con HttpClient
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(payload.getString("url"));

        StringEntity stringEntity = new StringEntity(payload.getJSONObject("data").toString());
        stringEntity.setContentType("application/json");
        httppost.setEntity(stringEntity);

        // Establecer encabezados
        for (int i = 0; i < jsaHeaders.length(); i += 2) {
            httppost.setHeader(jsaHeaders.getString(i), jsaHeaders.getString(i + 1));
        }

        // Ejecutar la solicitud y obtener la respuesta
        try {
            HttpResponse response = httpclient.execute(httppost);
            String responseContent = EntityUtils.toString(response.getEntity());
            System.out.println("Response Content: " + responseContent);
            // Resto del código...
        } catch (IOException e) {
            e.printStackTrace();
            // Manejar la excepción según sea necesario
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