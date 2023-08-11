package nl.energydata.api.utils;

import javax.net.ssl.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.ResponseInfo;
import java.nio.charset.StandardCharsets;
import java.net.URI;
import java.io.IOException;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import java.util.Map;

public class Http {

    public static HttpClient createTrustAllHttpClient() throws NoSuchAlgorithmException, KeyManagementException {
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
                public void checkClientTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
                }
                public void checkServerTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
                }
            }
        };
        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        return HttpClient.newBuilder().sslContext(sslContext).build();
    }
    
	
	public static JSONObject callApi(String url) throws IOException, InterruptedException, ParseException {
		 HttpClient client = HttpClient.newHttpClient();
		 HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
		 HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		 JSONParser parser = new JSONParser();
	     return (JSONObject) parser.parse(response.body());
	}
	
    
    public static JSONObject callApi(String url, Map<String, String> headers) throws IOException, InterruptedException, ParseException, NoSuchAlgorithmException, KeyManagementException {
        HttpClient client = createTrustAllHttpClient();

        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create(url));

        for (Map.Entry<String,String> entry : headers.entrySet()) {
            builder.header(entry.getKey(), entry.getValue());
        }

        HttpRequest request = builder.build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JSONParser parser = new JSONParser();
        return (JSONObject) parser.parse(response.body());
    }

    
}
