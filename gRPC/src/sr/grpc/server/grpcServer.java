package sr.grpc.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;

import java.io.IOException;

public class grpcServer {
	private static final int port = 8080;
	private static Server server = null;

	public static void main(String[] args) throws IOException, InterruptedException {
		final grpcServer server = new grpcServer();
		server.start();
		server.blockUntilShutdown();
	}

	private void start() throws IOException {
		server = ServerBuilder
				.forPort(port)
				.addService(ProtoReflectionService.newInstance())
				.addService(new AdvancedCalculatorImpl())
				.build()
				.start();

		System.out.println("Server is running...");

		Runtime.getRuntime().addShutdownHook(
				new Thread(() -> {
					// Use stderr here since the logger may have been reset by its JVM shutdown hook.
					System.err.println("*** shutting down gRPC server since JVM is shutting down");
					grpcServer.this.stop();
					System.err.println("*** server shut down");
				})
		);
	}

	private void stop() {
		if (server != null) {
			server.shutdown();
		}
	}

	private void blockUntilShutdown() throws InterruptedException {
		if (server != null) {
			server.awaitTermination();
		}
	}

}
