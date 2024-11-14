import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.StringTokenizer;
public class QuizClient {
    public static void main(String[] args) {
        String SERVER_IP;
        int SERVER_PORT;

        // 클래스패스 사용
        try (InputStream inputStream = QuizClient.class.getResourceAsStream("/env.dat");
             BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            SERVER_IP = br.readLine().trim();
            SERVER_PORT = Integer.parseInt(br.readLine().trim());
        } catch (IOException e) {
            System.out.println("서버 설정을 불러오는 데 실패했습니다: " + e.getMessage());
            return;
        }

        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("퀴즈서버에 연결됐습니다.");
            System.out.println("곧 퀴즈가 시작됩니다.");

            String serverMessage;
            while ((serverMessage = in.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(serverMessage, ":");
                String command = st.nextToken();

                switch (command) {
                    case "QUIZ":
                        getQuiz(st, out, scanner);
                        break;

                    case "RESULT":
                        getResult(st);
                        break;

                    case "FINAL":
                        getFinal(st);
                        return;
                }
            }

        } catch (UnknownHostException e) {
            System.out.println("서버를 찾을 수 없습니다: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("I/O 오류: " + e.getMessage());
        }
    }

    private static void getQuiz(StringTokenizer st, PrintWriter out, Scanner scanner) {
        String questionNumber = st.nextToken();

        StringBuilder question = new StringBuilder();
        while (st.hasMoreTokens()) {
            question.append(st.nextToken());
            if (st.hasMoreTokens()) {
                question.append(":");
            }
        }

        System.out.println("\n문제 " + questionNumber + ": " + question.toString());
        System.out.print("답변을 입력해주세요: ");
        String answer = scanner.nextLine();
        out.println("ANSWER:" + answer);
    }

    private static void getResult(StringTokenizer st) {
        boolean isCorrect = st.nextToken().equals("correct");
        int currentScore = Integer.parseInt(st.nextToken());

        System.out.println("결과: " + (isCorrect ? "정답입니다!" : "틀렸습니다!"));
        System.out.println("현재 점수: " + currentScore);
    }

    private static void getFinal(StringTokenizer st) {
        int finalScore = Integer.parseInt(st.nextToken());

        System.out.println("\n!! 퀴즈가 종료됐습니다 !!");
        System.out.println("최종 점수: " + finalScore);
    }
}
