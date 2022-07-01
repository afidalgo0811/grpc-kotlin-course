package calculator.client;

import com.proto.calculator.Calculator;
import com.proto.calculator.CalculatorServiceGrpc;
import com.proto.calculator.SumRequest;
import com.proto.calculator.SumResponse;
import com.proto.greeting.GreetingRequest;
import com.proto.greeting.GreetingResponse;
import com.proto.greeting.GreetingServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class CalClient {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Need one argument to work");
            return;
        }
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 50052)
                .usePlaintext()
                .build();
        if ("sum".equals(args[0])) {
            doSum(channel);
        } else {
            System.out.println("Keyword invalid: " + args[0]);
        }
        System.out.println("Shutting down");
        channel.shutdown();
    }

    private static void doSum(ManagedChannel channel) {
        System.out.println("Enter doSum");
        CalculatorServiceGrpc.CalculatorServiceBlockingStub stub = CalculatorServiceGrpc.newBlockingStub(channel);
        SumResponse response = stub.sum(SumRequest.newBuilder().setFirstNumber(1).setSecondNumber(2).build());
        System.out.println("Sum of 1 + 2 = " + response.getResult());
    }
}
