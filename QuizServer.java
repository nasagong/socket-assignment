import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QuizServer {
    private static final int PORT = 8080;
    private static final List<Quiz> quizList = new ArrayList<>();
    private static final int MAX_THREADS = 10;

    // 퀴즈의 구조를 정의하는 클래스
    static class Quiz {
        String question;
        String answer;

        Quiz(String question, String answer) {
            this.question = question;
            this.answer = answer;
        }
    }

    public static void main(String[] args) {
        // 퀴즈 리스트를 업데이트하는 메서드
        // 퀴즈 문제 변경의 책임을 한 메서드에 위임하기 위함
        initializeQuizzes();

        // 다수의 사용자를 관리하기 위한 스레드풀
        ExecutorService threadPool = Executors.newFixedThreadPool(MAX_THREADS);

        // 소켓 생성
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Quiz Server started on port " + PORT);

            while (true) {
                // 클라이언트 연결시 새로운 연결 소켓 매칭
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());

                // 스레드풀에 클라이언트 핸들러 할당
                threadPool.submit(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        } finally {
            threadPool.shutdown();
        }
    }

    private static void initializeQuizzes() {
        quizList.add(new Quiz("몰도바의 수도는?", "키시너우"));
        quizList.add(new Quiz("부룬디의 수도는?", "기테가"));
        quizList.add(new Quiz("에리트레아의 수도는?", "아스마라"));
        quizList.add(new Quiz("투르크메티스탄의 수도는?", "아시가바트"));
        quizList.add(new Quiz("소말리아의 수도는?", "모가디슈"));
    }

    // 클라이언트별 쓰레드 관리
    static class ClientHandler implements Runnable {

        // 클라이언트 기본 정보
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private int currentQuiz = 0;
        private int score = 0;

        // 소켓 할당 생성자
        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }


        // 쓰레드 실행
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

        // 인덱스 기반으로 퀴즈 전송
        private void sendQuiz() {
            if (currentQuiz < quizList.size()) {
                Quiz quiz = quizList.get(currentQuiz);
                out.println("QUIZ:" + (currentQuiz + 1) + ":" + quiz.question);
            } else {
                out.println("FINAL:" + score);
                closeConnection();
            }
        }

        // 답 확인
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

        // 커넥션 종료
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
