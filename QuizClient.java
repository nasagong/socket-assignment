import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.StringTokenizer;
public class QuizClient {
    public static void main(String[] args) {
        String SERVER_IP;
        int SERVER_PORT;

        // 클래스패스를 통해 env.dat의 연결 정포 파싱
        try (InputStream inputStream = QuizClient.class.getResourceAsStream("/env.dat");
             BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            SERVER_IP = br.readLine().trim();
            SERVER_PORT = Integer.parseInt(br.readLine().trim());
        } catch (IOException e) {
            System.out.println("서버 설정을 불러오는 데 실패했습니다: " + e.getMessage());
            return;
        }

        // 파일을 통해 읽어낸 데이터를 기반으로 소켓 연결 시작
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             // out, in 설정
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("퀴즈서버에 연결됐습니다.");
            System.out.println("곧 퀴즈가 시작됩니다.");

            String serverMessage;
            while ((serverMessage = in.readLine()) != null) {
                // StringTokenizer로 서버 메세지의 프로토콜을 분석
                StringTokenizer st = new StringTokenizer(serverMessage, ":");
                String command = st.nextToken();

                // switch문을 통해 프로토콜별 메서드 매칭
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

    // Quiz 프로토콜 수신시 실행되는 메서드
    // 퀴즈 정보를 불러옴
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

    // Result 프로토콜 수신시 실행되는 메서드
    // 정답 여부, 현재 누적 점수 알려줌
    private static void getResult(StringTokenizer st) {
        boolean isCorrect = st.nextToken().equals("correct");
        int currentScore = Integer.parseInt(st.nextToken());

        System.out.println("결과: " + (isCorrect ? "정답입니다!" : "틀렸습니다!"));
        System.out.println("현재 점수: " + currentScore);
    }

    // Final 프로토콜 수신시 실행되는 메서드
    // 최종 점수를 알려줌
    private static void getFinal(StringTokenizer st) {
        int finalScore = Integer.parseInt(st.nextToken());

        System.out.println("\n!! 퀴즈가 종료됐습니다 !!");
        System.out.println("최종 점수: " + finalScore);
    }
}
