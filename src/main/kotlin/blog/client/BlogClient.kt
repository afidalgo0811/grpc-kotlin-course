package blog.client

import com.proto.blog.Blog
import com.proto.blog.BlogId
import com.proto.blog.BlogServiceGrpc
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.StatusRuntimeException

class BlogClient constructor(private val channel: ManagedChannel) {

    fun run() {
        val blogServiceGrpc: BlogServiceGrpc.BlogServiceBlockingStub = BlogServiceGrpc.newBlockingStub(channel)

        val blogId: BlogId? = createBlog(blogServiceGrpc)
        if (blogId != null) {
            readBlog(blogServiceGrpc, blogId)
        }
    }

    private fun createBlog(stub: BlogServiceGrpc.BlogServiceBlockingStub): BlogId? {
        try {
            val requestBlog: Blog = Blog.newBuilder()
                .setAuthor("Alberto")
                .setTitle("new Blog")
                .setContent("Hello world from gRPC")
                .build()

            val responseBlogId: BlogId = stub.createBlog(
                requestBlog
            )
            println("Blog created with id: " + responseBlogId.id)
            return responseBlogId
        } catch (ex: StatusRuntimeException) {
            println("Couldn't create a blog")
            ex.printStackTrace()
            return null
        }
    }

    private fun readBlog(stub: BlogServiceGrpc.BlogServiceBlockingStub, blogId: BlogId) {
        try {
            val responseBlog: Blog = stub.readBlog(blogId)
            println("Blog read: $responseBlog")
        } catch (ex: StatusRuntimeException) {
            println("Couldn't read a blog")
            ex.printStackTrace()
        }
    }
}

fun main() {
    val channel = ManagedChannelBuilder
        .forAddress("localhost", 50051)
        .usePlaintext()
        .build()

    val blogClient = BlogClient(channel)
    blogClient.run()

    println("Shutting down")
    channel.shutdown()
}
