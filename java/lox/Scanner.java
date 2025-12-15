package lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static lox.TokenType.*;

public class Scanner {
    private List<Token> tokens;
    private String source;
    private int lineNumber = 1;
    private int start = 0;
    private int current = 0;
    private static final Map<String, TokenType> keywords;
    public Scanner(TokenType type, String lexeme) {}
    public Scanner(String source) {
        this.source = source;
        this.tokens = new ArrayList<>();

    }

    static {
        keywords = new HashMap<>();
        keywords.put("and",    AND);
        keywords.put("class",  CLASS);
        keywords.put("else",   ELSE);
        keywords.put("false",  FALSE);
        keywords.put("for",    FOR);
        keywords.put("fun",    FUN);
        keywords.put("if",     IF);
        keywords.put("nil",    NIL);
        keywords.put("or",     OR);
        keywords.put("print",  PRINT);
        keywords.put("return", RETURN);
        keywords.put("super",  SUPER);
        keywords.put("this",   THIS);
        keywords.put("true",   TRUE);
        keywords.put("var",    VAR);
        keywords.put("while",  WHILE);
    }

    public List<Token> parseTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }
        return  tokens;
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(': addToken(TokenType.LEFT_PAREN); break;
            case ')': addToken(TokenType.RIGHT_PAREN); break;
            case '{': addToken(TokenType.LEFT_BRACE); break;
            case '}': addToken(TokenType.RIGHT_BRACE); break;
            case ',': addToken(TokenType.COMMA); break;
            case ';': addToken(TokenType.SEMICOLON); break;
            case '.': addToken(TokenType.DOT); break;

            // handling operators
            case '+': addToken(TokenType.PLUS); break;
            case '-': addToken(TokenType.MINUS); break;
            case '*': addToken(TokenType.STAR); break;
            case '=': {
                if  (match('=')) {
                    addToken(TokenType.EQUAL_EQUAL);
                } else {
                    addToken(TokenType.EQUAL);
                }
                break;
            }
            case '!': {
                if (match('=')) {
                    addToken(TokenType.BANG_EQUAL);
                } else {
                    addToken(TokenType.BANG);
                }
                break;
            }
            case '<': {
                if (match('=')) {
                    addToken(TokenType.LESS_EQUAL);
                } else {
                    addToken(TokenType.LESS);
                }
                break;
            }
            case '>': {
                if (match('=')) {
                    addToken(TokenType.GREATER_EQUAL);
                } else {
                    addToken(TokenType.GREATER);
                }
                break;
            }
            // handling comments
            case '/': {
                if (match('/')) {
                    while (!isAtEnd() && peek() != '\n') {
                        advance();
                    }
                } else {
                    addToken(TokenType.SLASH);
                }
                break;
            }
            // handling longer lexemes
            case '"': {
                parseString();
                break;
            }
            case ' ', '\r', '\t':
                break;
            case '\n':
                lineNumber++;
                break;
            default: {
                if (isDigit(c)) {
                    parseNumber();
                } else if (isAlphaNum(c)) {
                    parseKeyword();
                } else {
                    Lox.error(lineNumber, "Unexpected character");
                }
            }
        }
    }

    private boolean isAlpha(char c) {
        return 'a' <= c && c <= 'z' || 'A' <= c && c <= 'Z';
    }

    private boolean isAlphaNum(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private char advance() {
        return source.charAt(current++);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String lexeme = source.substring(start, current);
        tokens.add(new Token(type, lexeme, literal, lineNumber));
    }

    private boolean match(char expectedChar) {
        if (isAtEnd()) {
            return false;
        }
        if (source.charAt(current) == expectedChar) {
            return true;
        }
        current++;
        return false;
    }

    private char peek() {
        return source.charAt(current);
    }

    private void parseString() {
        while (!isAtEnd() && peek() != '"') {
            if (peek() == '\n') {
                lineNumber++;
            }
            advance();
        }
        if (isAtEnd() || peek() != '"') {
            Lox.error(lineNumber, "Unterminated string");
            return;
        }
        advance();
        String lexeme = source.substring(start, current);
        addToken(TokenType.STRING, lexeme);
    }

    private boolean isDigit(char c) {
        return '0' <= c && c <= '9';
    }

    private char nextPeek() {
        if (isAtEnd() || current + 1 == source.length()) {
            return '\0';
        }
        return source.charAt(current + 1);
    }

    private void parseNumber () {
        while (!isAtEnd() && isDigit(peek())) {
            advance();
        }
        if (peek() == '.' && isDigit(nextPeek())) {
            do {
                advance();
            } while (isDigit(peek()));
        }
        addToken(TokenType.NUMBER, source.substring(start, current));
    }

    private void parseKeyword() {
        while (!isAtEnd() && isAlphaNum(peek())) {
            advance();
        }
        String ss =  source.substring(start, current);
        if (keywords.containsKey(ss)) {
            addToken(keywords.get(ss));
            return;
        }
        addToken(IDENTIFIER);
    }
}
