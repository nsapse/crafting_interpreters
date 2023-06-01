package com.craftinginterpreters.lox;

import static com.craftinginterpreters.lox.TokenType.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scanner {
  private final String source;
  private final List<Token> tokens = new ArrayList<>();
  private int start = 0;
  private int current = 0;
  private int line = 1;

  Scanner(String source) { this.source = source; }

  List<Token> scanTokens() {
    while (!isAtEnd()) {
      start = current;
      scanToken();
    }
    tokens.add(new Token(EOF, "", null, line));
    return tokens;
  }

  private boolean isAtEnd() { return current >= source.length(); }

  private void scanToken() {
    char c = advance();
    switch (c) {

    // single character operators
    case '(':
      addToken(LEFT_PAREN);
      break;
    case ')':
      addToken(RIGHT_PAREN);
      break;
    case '{':
      addToken(LEFT_BRACE);
      break;
    case '}':
      addToken(RIGHT_BRACE);
      break;
    case ',':
      addToken(COMMA);
      break;
    case '.':
      addToken(DOT);
      break;
    case '-':
      addToken(MINUS);
      break;
    case '+':
      addToken(PLUS);
      break;
    case ';':
      addToken(SEMICOLON);
      break;
    case '*':
      addToken(STAR);
      break;

    // Multi Character Operators
    case '!':
      addToken(match('=') ? BANG_EQUAL : BANG);
      break;
    case '=':
      addToken(match('=') ? EQUAL_EQUAL : EQUAL);
      break;
    case '<':
      addToken(match('=') ? LESS_EQUAL : LESS);
      break;
    case '>':
      addToken(match('=') ? GREATER_EQUAL : GREATER);
      break;

    // Long Lexemes

    // checking for  comments
    case '/':
      if (match('/')) {
        while (peek() != '\n' && !isAtEnd()) {
          advance();
        }
      } else {
        addToken(SLASH);
      }
      break;
    // strings
    case '"':
      string();
      break;

    // whitespace
    case ' ':
      break;
    case '\r':
      break;
    case '\t':
      break;
    case '\n':
      line++;
      break;

    // default - unrecognized character
    default:
      Lox.error(line, "Unexpected Character");
      break;
    }
  }

  // ~~~ helper functions ~~~

  // looking forward / advancing
  private char advance() { return source.charAt(current++); }

  private char peek() {
    if (isAtEnd())
      return '\0';
    return source.charAt(current);
  }

  private boolean match(char expected) {
    if (isAtEnd()) {
      return false;
    }

    if (source.charAt(current) != expected) {
      return false;
    }
    current++;
    return true;
  }

  // adding tokens
  private void addToken(TokenType type) { addToken(type, null); }

  private void addToken(TokenType type, Object literal) {
    String text = source.substring(start, current);
    tokens.add(new Token(type, text, literal, line));
  }

  // parse strings
  private void string() {
    while (peek() != '"' && !isAtEnd()) {
      // if we've reached a newline character
      if (peek() == '\n') {
        line++;
      }
      advance();
    }

    // if we've reached the end and not found a closing quote
    if (isAtEnd()) {
      Lox.error(line, "Unterminated string");
    }
    // if a proper string was successfully parsed
    advance();

    // trim the surrounding quotes before storing the string
    String value = source.substring(start + 1, current - 1);
    addToken(STRING, value);
  }
}