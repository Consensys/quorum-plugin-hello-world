package com.quorum.plugin;

import com.quorum.plugin.service.HelloWorldService;
import com.quorum.plugin.service.InitializationService;
import com.quorum.plugin.util.CertHolder;
import io.grpc.Server;
import io.grpc.health.v1.HealthCheckResponse;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyServerBuilder;
import io.grpc.services.HealthStatusManager;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContextBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;

@Configuration
public class PluginConfiguration {
    // This must be agreed with the host program so this plugin is supported
    private static final String AppProtocolVersion = "1";
    // The env variable to read value to verify compatibility with the host program
    private static final String MagicCookieKey = "QUORUM_PLUGIN_MAGIC_COOKIE";
    // This value must be the same as host program
    private static final String MagicCookieValue = "CB9F51969613126D93468868990F77A8470EB9177503C5A38D437FEFF7786E0941152E05C06A9A3313391059132A7F9CED86C0783FE63A8B38F01623C8257664";

    /**
     * Create a gRPC server bean that is compatible with go-plugin protocol
     *
     * @return
     * @throws Exception
     * @see <a href="https://github.com/hashicorp/go-plugin/blob/master/docs/internals.md">go-plugins internal</a>
     */
    @Bean
    public Server server(InitializationService initializationService,
                         HelloWorldService service) throws Exception {
        // check for the magic cookie
        if (!MagicCookieValue.equals(System.getenv(MagicCookieKey))) {
            String msg = "This binary is a plugin. These are not meant to be executed directly.\n" +
                    "Please execute the compatible program that consumes this plugin therefore will load it automatically.\n";
            System.err.println(msg);
            throw new IllegalCallerException(msg);
        }

        // healthcheck service for the plugin
        HealthStatusManager healthStatusManager = new HealthStatusManager();
        healthStatusManager.setStatus("plugin", HealthCheckResponse.ServingStatus.SERVING);

        NettyServerBuilder serverBuilder = NettyServerBuilder.forPort(0)
                .addService(healthStatusManager.getHealthService())
                .addService(initializationService)
                .addService(service);

        // check if mTLS is asked
        String certPemB64 = "";
        String clientCert = System.getenv("PLUGIN_CLIENT_CERT");
        if (!StringUtils.isEmpty(clientCert)) {
            System.err.println("configuring server automatic mTLS");
            CertHolder certHolder = CertHolder.generate();
            SslContextBuilder sslContextBuilder = SslContextBuilder
                    .forServer(new ByteArrayInputStream(certHolder.certPem()), new ByteArrayInputStream(certHolder.keyPem()))
                    .trustManager(new ByteArrayInputStream(clientCert.getBytes()))
                    .clientAuth(ClientAuth.REQUIRE);
            serverBuilder.sslContext(GrpcSslContexts.configure(sslContextBuilder).build());
            certPemB64 = Base64Utils.encodeToString(certHolder.certDer());
            System.err.println(certPemB64);
        }

        Server server = serverBuilder.build().start();

        // write a message to start handshake with the host program
        System.out.println("1|" + AppProtocolVersion + "|tcp|localhost:" + server.getPort() + "|grpc|" + certPemB64);
        System.out.flush();

        return server;
    }
}
