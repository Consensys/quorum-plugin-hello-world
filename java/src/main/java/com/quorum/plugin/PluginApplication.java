package com.quorum.plugin;

import io.grpc.Server;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PreDestroy;
import java.security.Security;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class PluginApplication implements CommandLineRunner {

    @Autowired
    private Server server;

    public static void main(String[] args) {
        Security.addProvider(new BouncyCastleProvider());
        SpringApplication app = new SpringApplication(PluginApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        server.awaitTermination();
    }

    @PreDestroy
    public void onExit() throws Exception {
        System.err.println("plugin shutting down");
        server.shutdownNow();
        server.awaitTermination(10, TimeUnit.SECONDS);
    }
}
