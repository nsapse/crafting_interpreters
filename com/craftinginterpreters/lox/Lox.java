package com.craftinginterpreters.lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox {
  // variable to track if an error has occured
  static boolean hadError = false;

  public static void main(String[] args) throws IOException {
    if (args.length > 1) {
      System.out.println("Usage: jlox [script]");
      System.exit(64);
    } else if (args.length == 1) {
      runFile(args[0]);
    } else {
      runPrompt();
    }
  }

  // read file from path given by user (or theoertically any path)
  public static void runFile(String path) throws IOException {
    byte[] bytes = Files.readAllBytes(Paths.get(path));
    run(new String(bytes, Charset.defaultCharset()));
  }

  // main REPL loop
  public static void runPrompt() throws IOException {
    InputStreamReader input = new InputStreamReader(System.in);
    BufferedReader reader = new BufferedReader(input);

    for (;;) {
      System.out.println("> ");
      String line = reader.readLine();
      if (line == null) {
        break;
      }
      run(line);
    }
  }

  // primary run loop
  public static void run(String source) {
    // exit if there was an error
    if (hadError) {
      System.exit(65);
    }
    Scanner scanner = new Scanner(source);
    List<Token> tokens = scanner.scanTokens();

    for (Token token : tokens) {
      System.out.println(token);
    }
  }

  // error interface
  static void error(int line, String message) { report(line, "", message); }
  private static void report(int line, String where, String message) {
    System.err.println();
    hadError = true;
  }
}
