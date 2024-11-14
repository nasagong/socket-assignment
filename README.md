

# Protocol Specification
This application implements a text-based protocol for communication between the quiz server and clients. The protocol consists of specific commands, each separated by colons (:), 
to control the flow of questions, answers, and results.

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
