package com.sk.i2i.smsapp.handler;

import com.sk.i2i.smsapp.config.ConfigLoader;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class HttpRequestHandler implements IHttpRequestHandler {
    private static final Logger logger = LogManager.getLogger(HttpRequestHandler.class);
    private String baseUrl;

    public HttpRequestHandler() {
        ConfigLoader configLoader = new ConfigLoader();
        this.baseUrl = configLoader.getProperty("url");
    }

    @Override
    public String sendGetRequest(String phoneNumber) throws Exception {
        String url = baseUrl + phoneNumber;
        logger.debug("Sending GET request to URL: " + url);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            HttpResponse response = httpClient.execute(request);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            logger.debug("Response received: " + result.toString());
            return result.toString();
        }
    }
}
