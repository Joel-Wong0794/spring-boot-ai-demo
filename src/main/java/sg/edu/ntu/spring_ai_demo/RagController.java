package sg.edu.ntu.spring_ai_demo; // Places this class in the application's Java package.

import org.springframework.ai.chat.client.ChatClient; // Provides a fluent API for sending prompts to the AI model.
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor; // Adds relevant vector-store content to each prompt.
import org.springframework.ai.vectorstore.VectorStore; // Provides access to the embedded FAQ chunks.
import org.springframework.web.bind.annotation.GetMapping; // Maps HTTP GET requests to a controller method.
import org.springframework.web.bind.annotation.RequestParam; // Reads a value from a URL query parameter.
import org.springframework.web.bind.annotation.RestController; // Marks this class as a REST controller whose methods return response data.

@RestController // Registers this class with Spring and writes method results into HTTP
                // responses.
public class RagController { // Declares the controller that answers FAQ questions using RAG.

    private final ChatClient chatClient; // Holds the configured AI client used by every request.

    public RagController(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) { // Spring injects the client
                                                                                          // builder and FAQ vector
                                                                                          // store.
        this.chatClient = chatClientBuilder // Starts configuring the ChatClient and saves the finished client in this
                                            // field.
                .defaultSystem("You are a helpful customer support assistant for ACME CRM. " + // Defines the AI's role.
                        "Answer questions based only on the provided context. " + // Restricts answers to retrieved FAQ
                                                                                  // information.
                        "If the answer is not in the context, say you don't have that information " + // Defines
                                                                                                      // behavior for
                                                                                                      // unknown
                                                                                                      // answers.
                        "and suggest contacting support@acmecrm.com.") // Gives users a fallback support contact.
                .defaultAdvisors(QuestionAnswerAdvisor.builder(vectorStore).build()) // Retrieves relevant FAQ chunks
                                                                                     // and adds them to each prompt.
                .build(); // Creates the fully configured ChatClient.
    } // Ends the constructor.

    @GetMapping("/faq") // Handles GET requests sent to the /faq URL.
    public String faq(@RequestParam String question) { // Reads the required question query parameter, such as
                                                       // /faq?question=How...
        return chatClient.prompt() // Starts building a new prompt for this request.
                .user(question) // Adds the caller's question as the user message.
                .call() // Sends the prompt and retrieved FAQ context to the AI model.
                .content(); // Extracts and returns only the text from the AI response.
    } // Ends the faq endpoint method.
} // Ends the RagController class.