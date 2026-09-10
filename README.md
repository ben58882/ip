# BenBot project template

This is a project template for a greenfield Java project. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After Gradle finishes importing the project, run the `run` Gradle task. Alternatively, locate
   `src/main/java/benbot/Launcher.java`, right-click it, and choose `Run Launcher.main()`.
   BenBot's JavaFX chat window should appear.

You can also start the GUI from a macOS or Linux terminal in the project folder:

```bash
./gradlew run
```

## Entity extensions

BenBot can store contacts separately from tasks:

```text
contact NAME /phone PHONE /email EMAIL
contacts
delete-contact CONTACT_NUMBER
```

BenBot can also record short notes:

```text
note TEXT
notes
delete-note NOTE_NUMBER
```

Expenses use positive numeric amounts with at most two decimal places:

```text
expense DESCRIPTION /amount AMOUNT
expenses
delete-expense EXPENSE_NUMBER
```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.
