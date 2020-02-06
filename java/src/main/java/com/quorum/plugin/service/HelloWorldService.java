package com.quorum.plugin.service;

import com.quorum.plugin.proto.HelloWorld;
import com.quorum.plugin.proto.PluginGreetingGrpc;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;

@Service
public class HelloWorldService extends PluginGreetingGrpc.PluginGreetingImplBase {
    private String currentLanguage;

    @Override
    public void greeting(HelloWorld.PluginHelloWorld.Request request, StreamObserver<HelloWorld.PluginHelloWorld.Response> responseObserver) {
        switch (currentLanguage) {
            case "en":
                responseObserver.onNext(HelloWorld.PluginHelloWorld.Response.newBuilder().setMsg("Hello " + request.getMsg()).build());
                break;
            case "es":
                responseObserver.onNext(HelloWorld.PluginHelloWorld.Response.newBuilder().setMsg("Hola " + request.getMsg()).build());
                break;
            default:
                responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("invalid language").asException());
        }
        responseObserver.onCompleted();
    }

    public void setCurrentLanguage(String currentLanguage) {
        this.currentLanguage = currentLanguage;
    }
}
