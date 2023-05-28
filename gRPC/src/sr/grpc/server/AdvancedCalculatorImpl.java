package sr.grpc.server;

import sr.grpc.gen.AdvancedCalculatorGrpc.AdvancedCalculatorImplBase;
import sr.grpc.gen.ComplexArithmeticOpResult;

import java.util.Comparator;
import java.util.NoSuchElementException;

public class AdvancedCalculatorImpl extends AdvancedCalculatorImplBase {
    @Override
    public void complexOperation(sr.grpc.gen.ComplexArithmeticOpArguments request,
                                 io.grpc.stub.StreamObserver<sr.grpc.gen.ComplexArithmeticOpResult> responseObserver)
    {
        System.out.println("multipleArgumentsRequest (" + request.getOptypeValue() + ", #" + request.getArgsCount() +")");

        if(request.getArgsCount() == 0) {
            System.out.println("No agruments");
        }

        double res = 0;
        switch (request.getOptype()) {
            case SUM:
                for (Double d : request.getArgsList()) res += d;
                break;
            case AVG:
                for (Double d : request.getArgsList()) res += d;
                res /= request.getArgsCount();
                break;
            case MIN:
                res = request.getArgsList().stream().min(Comparator.naturalOrder()).orElseThrow(NoSuchElementException::new);
                break;
            case MAX:
                res = request.getArgsList().stream().max(Comparator.naturalOrder()).orElseThrow(NoSuchElementException::new);
                break;
            case UNRECOGNIZED:
                throw new RuntimeException("Unknown operation");
        }

        ComplexArithmeticOpResult result = ComplexArithmeticOpResult.newBuilder().setRes(res).build();
        System.out.println(result);
        responseObserver.onNext(result);
        responseObserver.onCompleted();
    }
}
