package blog.server

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import io.grpc.Server
import io.grpc.ServerBuilder

class BlogServer constructor(private val port: Int) {
    private val mongodbClient: MongoClient = MongoClients.create("mongodb://root:root@localhost:27017/")
    val server: Server = ServerBuilder.forPort(port).addService(BlogServerImpl(mongodbClient)).build()

    fun start() {
        server.start()
        println("Server started, listening on $port")
        Runtime.getRuntime().addShutdownHook(
            Thread {
                println("*** shutting down gRPC server since JVM is shutting down")
                this@BlogServer.stop()
                mongodbClient.close()
                println("*** server shut down")
            }
        )
    }
    private fun stop() {
        server.shutdown()
    }

    fun blockUntilShutdown() {
        server.awaitTermination()
    }
}
fun main() {
    val port = 50051
    val server = BlogServer(port)
    server.start()
    server.blockUntilShutdown()
}
