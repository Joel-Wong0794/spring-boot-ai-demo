package sg.edu.ntu.spring_ai_demo; // Places this class in the application's Java package.

import java.util.List; // Imports List so groups of documents and chunks can be stored.

import org.springframework.ai.document.Document; // Represents text and its associated metadata.
import org.springframework.ai.embedding.EmbeddingModel; // Converts text into numerical embedding vectors.
import org.springframework.ai.reader.TextReader; // Reads text content into Document objects.
import org.springframework.ai.transformer.splitter.TokenTextSplitter; // Splits large documents into smaller token-based chunks.
import org.springframework.ai.vectorstore.SimpleVectorStore; // Provides an in-memory VectorStore implementation.
import org.springframework.ai.vectorstore.VectorStore; // Defines operations for storing and searching embeddings.
import org.springframework.context.annotation.Bean; // Marks a method result as an object managed by Spring.
import org.springframework.context.annotation.Configuration; // Marks this class as a source of Spring bean definitions.

@Configuration // Tells Spring to process the bean definitions in this class.
public class RagConfig { // Declares the configuration class for retrieval-augmented generation (RAG).

    @Bean // Registers the returned VectorStore in Spring's application context.
    public VectorStore vectorStore(EmbeddingModel embeddingModel) { // Receives Spring's embedding model and creates the
                                                                    // vector store.
        SimpleVectorStore store = SimpleVectorStore.builder(embeddingModel).build(); // Builds an in-memory store that
                                                                                     // uses the model to create
                                                                                     // embeddings.

        TextReader textReader = new TextReader("classpath:faq.txt"); // Points a text reader at
                                                                     // src/main/resources/faq.txt.
        List<Document> documents = textReader.get(); // Reads the FAQ text and wraps it in Document objects.

        List<Document> chunks = TokenTextSplitter.builder().build().apply(documents); // Splits the documents into
                                                                                      // chunks that are easier to
                                                                                      // retrieve accurately.

        store.add(chunks); // Embeds every chunk and saves the resulting vectors in memory.

        System.out.println("✅ FAQ data loaded into vector store — " + chunks.size() + " chunks"); // Reports how many
                                                                                                  // chunks were indexed
                                                                                                  // during startup.
        return store; // Makes the populated store available to other Spring-managed classes.
    } // Ends the vectorStore bean method.
} // Ends the RagConfig class.