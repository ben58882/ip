# BenBot

BenBot is a JavaFX personal-assistant chatbot that keeps track of tasks. It
supports to-dos, deadlines, and events through a compact command interface and
saves the task list between sessions.

## Features

- Create to-dos, deadlines, and events.
- Parse and display dates and optional times.
- List all tasks and find tasks by a case-insensitive keyword.
- Mark tasks as done or not done.
- Delete tasks by their displayed number.
- Save tasks automatically when the user exits or closes the application.
- Reload saved tasks when the application starts.
- Use the chatbot through a JavaFX conversation window.
- Check the project automatically on Linux, macOS, and Windows with GitHub
  Actions.

## Commands

| Command | Purpose | Example |
| --- | --- | --- |
| `todo DESCRIPTION` | Add a to-do | `todo read book` |
| `deadline DESCRIPTION /by DATE [TIME]` | Add a deadline | `deadline submit report /by 15/9/2026 1800` |
| `event DESCRIPTION /from START /to END` | Add an event | `event team meeting /from 15/9/2026 1400 /to 15/9/2026 1600` |
| `list` | Show every task | `list` |
| `find KEYWORD` | Find tasks by description | `find book` |
| `mark TASK_NUMBER` | Mark a task as done | `mark 2` |
| `unmark TASK_NUMBER` | Mark a task as not done | `unmark 2` |
| `delete TASK_NUMBER` | Delete a task | `delete 2` |
| `bye` | Save tasks and exit | `bye` |

Dates use the `day/month/year` format, such as `15/9/2026`. Times are optional
where shown and use the 24-hour `HHmm` format, such as `1800`.

Task numbers are the one-based numbers shown by `list`. BenBot stores up to 100
tasks.

## Requirements

- JDK 25
- A recent IntelliJ IDEA version, or a terminal on macOS, Linux, or Windows

## Run BenBot

From the project root, run:

```bash
./gradlew run
```

On Windows Command Prompt, use:

```bat
gradlew.bat run
```

The Gradle task opens BenBot's JavaFX window. Enter a command in the text field
and press Enter or select the send button.

Saved tasks are written to `data/stored-task` relative to the directory from
which BenBot is launched.

## Set up IntelliJ IDEA

1. Update IntelliJ IDEA to a recent version.
2. Open the repository as a project.
3. Configure the project SDK as JDK 25 and leave the language level as
   `SDK default`.
4. Allow Gradle to finish importing the project.
5. Run the `run` Gradle task, or run `benbot.Launcher.main()`.

Keep `src/main/java` as the source root so Gradle and IntelliJ IDEA can locate
the Java source files.

## Test and check the project

Run the tests and Checkstyle checks with:

```bash
./gradlew check
```

GitHub Actions runs the same command with JDK 25 on Linux, macOS, and Windows
for every push and pull request.

## Build the executable JAR

Create the JAR containing BenBot and its dependencies with:

```bash
./gradlew shadowJar
```

The generated file is `build/libs/benbot.jar`.
