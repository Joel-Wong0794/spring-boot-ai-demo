package sg.edu.ntu.spring_ai_demo;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
public class AiController {

    private final ChatClient chatClient;

    public AiController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @GetMapping("/chat")
    public String chat(@RequestParam String message) {
        return chatClient
                .prompt()
                .user(message)
                .call()
                .content();
    }

    @GetMapping("/support")
    public String support(@RequestParam String message) {
        return chatClient.prompt()
                .system("You are a friendly and professional customer support assistant for a CRM software company. " +
                        "You help users with questions about managing customers, contacts, and sales pipelines. " +
                        "Keep your answers concise and practical. " +
                        "If a question is not related to CRM or customer management, politely redirect the user.")
                .user(message)
                .call()
                .content();
    }

    @GetMapping("/summarise")
    public String summarise(@RequestParam String text) {
        String summary = chatClient.prompt()
                .system("You are an expert summarizer. Your task is to condense the provided text into exactly five bullet points.\n\nRules:\n- Output exactly five bullet points—no more, no less.\n- Use simple, everyday, plain language; avoid jargon, academic phrasing, and overly complex sentence structures.\n- Do NOT include any introductory greetings, titles, meta-announcements (e.g., 'Here is the summary:'), or conversational filler.\n- Do NOT include any concluding remarks, wrap-up statements, or sign-offs.\n- Begin immediately with the first bullet point and end immediately after the fifth.")
                .user(text)
                .call()
                .content();

        try {
            Files.writeString(Path.of("summary.csv"), summary);
        } catch (IOException e) {
            return "Could not save the file: " + e.getMessage();
        }

        return summary;
    }

    @GetMapping("/analyse-ticket")
    public String analyseTicket(@RequestParam String ticket) {
        TicketAnalysis analysis = chatClient.prompt()
                .user(u -> u.text("Analyse this customer support ticket: {ticket}. " +
                        "Category must be one of: BILLING, DELIVERY, TECHNICAL, OTHER. " +
                        "Urgency must be one of: LOW, MEDIUM, HIGH.")
                        .param("ticket", ticket))
                .call()
                .entity(TicketAnalysis.class);

        if (analysis.refundRequested()) {
            return "Routed to FINANCE team — " + analysis.summary();
        }

        if (analysis.urgency().equalsIgnoreCase("HIGH")) {
            return "Escalated to SENIOR SUPPORT — " + analysis.summary();
        }

        return "Added to standard " + analysis.category() + " queue — " + analysis.summary();
    }
}