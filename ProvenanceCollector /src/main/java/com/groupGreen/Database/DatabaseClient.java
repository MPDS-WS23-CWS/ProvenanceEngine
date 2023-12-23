package com.groupGreen.Database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

@Component

public class DatabaseClient {

    @Value("${database.userName}")
    private String userName;
    @Value("${database.password}")
    private String password;
    @Value("${database.serverName}")
    private String serverName;
    @Value("${database.portNumber}")
    private String portNumber;
    @Value("${database.dbName}")
    private String dbName;
    @Value("${database.dbms}")
    private String dbms;

    public DatabaseClient() throws URISyntaxException, IOException, InterruptedException, SQLException {





        this.userName="testUser";
        this.password="testPassword";
        this.serverName="localhost";
        this.portNumber="5432";
        this.dbName="testDB";
        this.dbms="postgresql";

        this.getConnection();

        this.IsConnectionTopgRESTSuccessful();

    }

    @PostConstruct
    public void init()
    {
        System.out.println(this.dbms);


    }






    public boolean IsConnectionTopgRESTSuccessful() throws URISyntaxException, IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:3000/"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if(response.statusCode() == 200)
        {
            System.out.println("pgREST instance up and running!");
            return true;
        }
        else
        {
            System.out.println("ERROR: pgREST instance doesn't seem to run properly! ERROR CODE: "+response.body());
        }
        return false;
    }

    public Connection getConnection() throws SQLException {

        Connection conn = null;



        conn = DriverManager.getConnection("jdbc:"+this.dbms+"://"+this.serverName+":"+this.portNumber+"/"+this.dbName, this.userName, this.password);

        System.out.println("Connected to database");
        return conn;
    }










}
