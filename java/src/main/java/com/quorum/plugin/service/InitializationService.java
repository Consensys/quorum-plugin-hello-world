package com.quorum.plugin.service;

import com.google.gson.Gson;
import com.quorum.plugin.proto.Initializer;
import com.quorum.plugin.proto.PluginInitializerGrpc;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;
import java.util.Map;

@Service
public class InitializationService extends PluginInitializerGrpc.PluginInitializerImplBase {
    private Logger logger = LoggerFactory.getLogger(InitializationService.class);

    @Autowired
    private HelloWorldService service;

    @Override
    public void init(Initializer.PluginInitialization.Request request, StreamObserver<Initializer.PluginInitialization.Response> responseObserver) {
        String rawConfig = request.getRawConfiguration().toString(Charset.defaultCharset());
        logger.info("received initialization request: nodeName={}, config={}",
                request.getHostIdentity(),
                rawConfig);
        Gson g = new Gson();
        Map<String, String> config = g.fromJson(rawConfig, Map.class);
        String language = config.get("language");
        if (!"en".equals(language) && !"es".equals(language)) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("unsupported language").asException());
        }
        service.setCurrentLanguage(language);
        responseObserver.onNext(Initializer.PluginInitialization.Response.newBuilder().build());
        responseObserver.onCompleted();
    }
}
