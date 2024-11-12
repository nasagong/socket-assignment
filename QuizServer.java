import java.io.*;
import java.net.*;
import java.util.*;

public class QuizServer {
    private static final int PORT = 8080;
    private static final List<Quiz> quizList = new ArrayList<>();

    static class Quiz {
        String question;
        String answer;

        Quiz(String question, String answer) {
            this.question = question;
            this.answer = answer;
        }
    }

    public static void main(String[] args) {
        initializeQuizzes();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Quiz Server started on port " + PORT);

            while (true) {
                System.out.println("Waiting for a client to connect...");
                try (Socket clientSocket = serverSocket.accept();
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                    System.out.println("New client connected: " + clientSocket.getInetAddress());
                    handleClient(in, out);
                } catch (IOException e) {
                    System.out.println("Error handling client: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }

    private static void initializeQuizzes() {
        quizList.add(new Quiz("Q1", "A1"));
        quizList.add(new Quiz("Q2", "A2"));
        quizList.add(new Quiz("Q3", "A3"));
        quizList.add(new Quiz("Q4", "A4"));
        quizList.add(new Quiz("Q5", "A5"));
    }

    private static void handleClient(BufferedReader in, PrintWriter out) throws IOException {
        int currentQuiz = 0;
        int score = 0;

        sendQuiz(currentQuiz, out);

        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            StringTokenizer st = new StringTokenizer(inputLine, ":");
            if (!st.hasMoreTokens()) {
                out.println("Protocol violation: Empty message");
                continue;
            }

            String command = st.nextToken();
            switch (command) {
                case "ANSWER":

                    StringBuilder answer = new StringBuilder();
                    while (st.hasMoreTokens()) {
                        answer.append(st.nextToken());
                        if (st.hasMoreTokens()) {
                            answer.append(":");
                        }
                    }

                    boolean isCorrect = checkAnswer(answer.toString(), currentQuiz);
                    if (isCorrect) {
                        score += 10;
                    }

                    out.println("RESULT:" + (isCorrect ? "correct" : "wrong") + ":" + score);

                    currentQuiz++;
                    if (currentQuiz < quizList.size()) {
                        sendQuiz(currentQuiz, out);
                    } else {
                        out.println("FINAL:" + score);
                        return;
                    }
                    break;

                default:
                    out.println("Protocol violation: Unknown command '" + command + "'");
            }
        }
    }

    private static void sendQuiz(int quizNumber, PrintWriter out) {
        Quiz quiz = quizList.get(quizNumber);
        out.println("QUIZ:" + (quizNumber + 1) + ":" + quiz.question);
    }

    private static boolean checkAnswer(String answer, int quizNumber) {
        Quiz currentQuiz = quizList.get(quizNumber);
        return answer.trim().equalsIgnoreCase(currentQuiz.answer.trim());
    }
}