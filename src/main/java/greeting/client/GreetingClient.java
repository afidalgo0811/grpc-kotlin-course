package greeting.client;

import com.proto.greeting.GreetingRequest;
import com.proto.greeting.GreetingResponse;
import com.proto.greeting.GreetingServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GreetingClient {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Need one argument to work");
            return;
        }
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext()
                .build();
        if ("greet".equals(args[0])) {
            doGreet(channel);
        } else {
            System.out.println("Keyword invalid: " + args[0]);
        }
        System.out.println("Shutting down");
        channel.shutdown();
    }

    private static void doGreet(ManagedChannel channel) {
        System.out.println("Enter doGreet");
        GreetingServiceGrpc.GreetingServiceBlockingStub stub = GreetingServiceGrpc.newBlockingStub(channel);
        GreetingResponse response = stub.greet(GreetingRequest.newBuilder().setFirstName("Clement").build());
        System.out.println("Greeting: " + response.getResult());
    }
}
