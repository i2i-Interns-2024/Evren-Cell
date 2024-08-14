//package com.i2i.evrencell.aom.helper;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//import org.voltdb.client.Client;
//import org.voltdb.client.ClientConfig;
//import org.voltdb.client.ClientFactory;
//
//import java.io.IOException;
//
//@Component
//public class VoltDBConnection {
//
//    @Value("${spring.datasource.voltdb.dbUrl}")
//    private String dbUrl;
//
//    @Value("${spring.datasource.voltdb.port}")
//    private int port;
//
//    public Client getClient() throws IOException {
//        ClientConfig config = new ClientConfig();
//        Client client = ClientFactory.createClient(config);
//        client.createConnection(dbUrl, port);
//        return client;
//    }
//}