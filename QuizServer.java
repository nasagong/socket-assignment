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
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                new Thread(clientHandler).start();
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

    static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private int currentQuiz = 0;
        private int score = 0;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                sendQuiz();

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    if (inputLine.startsWith("ANSWER:")) {
                        String answer = inputLine.substring(7);
                        checkAnswer(answer);
                    }
                    else{
                        out.println("Protocol violation !!");
                    }
                }
            } catch (IOException e) {
                System.out.println("Error handling client: " + e.getMessage());
            } finally {
                closeConnection();
            }
        }

        private void sendQuiz() {
            if (currentQuiz < quizList.size()) {
                Quiz quiz = quizList.get(currentQuiz);
                out.println("QUIZ:" + (currentQuiz + 1) + ":" + quiz.question);
            } else {
                out.println("FINAL:" + score);
                closeConnection();
            }
        }

        private void checkAnswer(String answer) {
            Quiz currentQuizObj = quizList.get(currentQuiz);
            boolean isCorrect = answer.trim().equalsIgnoreCase(currentQuizObj.answer.trim());

            if (isCorrect) {
                score += 10;
            }

            out.println("RESULT:" + (isCorrect ? "correct" : "wrong") + ":" + score);

            currentQuiz++;
            sendQuiz();
        }

        private void closeConnection() {
            try {
                if (out != null) out.close();
                if (in != null) in.close();
                if (clientSocket != null) clientSocket.close();
            } catch (IOException e) {
                System.out.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}
