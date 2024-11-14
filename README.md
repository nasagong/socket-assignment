# Socket-Quiz-Game!

![image](https://github.com/user-attachments/assets/59e88490-ec5e-41ec-b4aa-ec2005222633)

This quiz game has been implemented through Java's socket communication interface. 
You can pre-store questions and answers on the server, and host a quiz game for multiple users using a single server.
You will play the quiz game using a special protocol created specifically for this game! Please refer to the protocol wiki below.

## Protocol Specification
This application implements a text-based protocol for communication between the quiz server and clients. The protocol consists of specific commands, each separated by colons (:), 
to control the flow of questions, answers, and results.

| **Command**           | **Function**                                                                             | **Format**                                  | **Example**                           |
|-----------------------|------------------------------------------------------------------------------------------|---------------------------------------------|---------------------------------------|
| `QUIZ`                | Sends a quiz question from server to client                                              | `QUIZ:<question_number>:<question_content>` | `QUIZ:1:What is the capital of France?` |
| `ANSWER`              | Sends client’s answer to the current question                                            | `ANSWER:<answer_content>`                   | `ANSWER:Paris`                        |
| `RESULT`              | Sends the result of the client’s answer (correct or wrong) and the updated score         | `RESULT:<correct_or_wrong>:<current_score>` | `RESULT:correct:10`                   |
| `FINAL`               | Sends the final score after all questions are answered                                   | `FINAL:<final_score>`                       | `FINAL:50`                            |
| Protocol Violation    | Notifies the client if an unexpected message format is received                          | `Protocol violation !!`                     | `Protocol violation !!`               |

## Protocol Commands
### Quiz Question
The server sends a quiz question to the client using the QUIZ command.

- Format: QUIZ:<question_number>:<question_content>
- Example: QUIZ:1:What is the capital of France?

### Client Answer
The client responds to the question using the ANSWER command.

- Format: ANSWER:<answer_content>
- Example: ANSWER:Paris

### Answer Result
The server evaluates the client’s answer and replies with the RESULT command, 
indicating whether the answer is correct and showing the current score.

- Format: RESULT:<correct_or_wrong>:<current_score>
- Correct or Wrong is indicated by correct or wrong.
- Example: RESULT:correct:10

### Final Score
Once the quiz is complete, the server sends the client’s final score using the FINAL command.

- Format: FINAL:<final_score>
- Example: FINAL:50

### Protocol Violation
If the client sends an unexpected message format, the server responds with a protocol violation notice.

- Message: Protocol violation !!

### Protocol Flow Example
[1] Server sends a question: QUIZ:1:What is the capital of France?
[2] Client answers: ANSWER:Paris
[3] Server responds with result and score: RESULT:correct:10
[4] After all questions, server sends the final score: FINAL:50
