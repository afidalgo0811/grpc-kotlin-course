package blog.server

import com.mongodb.MongoException
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.result.InsertOneResult
import com.proto.blog.Blog
import com.proto.blog.BlogId
import com.proto.blog.BlogServiceGrpc.BlogServiceImplBase
import io.grpc.Status
import io.grpc.stub.StreamObserver
import org.bson.Document
import org.bson.types.ObjectId

class BlogServerImpl constructor(client: MongoClient) : BlogServiceImplBase() {
    private val db: MongoDatabase = client.getDatabase("blogdb")
    private val mongodbCollection: MongoCollection<Document> = db.getCollection("blog")

    override fun createBlog(request: Blog, streamObserver: StreamObserver<BlogId>) {
        val doc: Document = Document("author", request.author)
            .append("title", request.title)
            .append("content", request.content)

        val result: InsertOneResult

        try {
            result = mongodbCollection.insertOne(doc)
        } catch (ex: MongoException) {
            print(ex.message)
            streamObserver.onError(
                Status.INTERNAL
                    .withDescription(ex.message)
                    .asRuntimeException()
            )
            return
        }
        if (!result.wasAcknowledged()) {
            streamObserver.onError(
                Status.INTERNAL
                    .withDescription("Blog could not be created")
                    .asRuntimeException()
            )
        }

        val id = result.insertedId?.asObjectId()?.value.toString()
        streamObserver.onNext(BlogId.newBuilder().setId(id).build())
        streamObserver.onCompleted()
    }

    override fun readBlog(request: BlogId?, responseObserver: StreamObserver<Blog>?) {
        println("is it valid : ${ObjectId.isValid(request.toString())}")
        if (request != null) {
            val doc: Document? = mongodbCollection.find(eq("_id", ObjectId(request.id.toString()))).first()
            if (doc == null) {
                responseObserver?.onError(
                    Status.NOT_FOUND
                        .withDescription("Blog with id $request was not found")
                        .asRuntimeException()
                )
                return
            } else {
                responseObserver?.onNext(
                    Blog.newBuilder()
                        .setId(doc["_id"].toString())
                        .setAuthor(doc.getString("author"))
                        .setTitle(doc.getString("title"))
                        .setContent(doc.getString("content"))
                        .build()
                )
                responseObserver?.onCompleted()
            }
        } else {
            responseObserver?.onError(
                Status.INVALID_ARGUMENT
                    .asRuntimeException()
            )
            return
        }
    }
}
