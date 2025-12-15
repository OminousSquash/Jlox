package lox;

import java.text.ParseException;
import java.util.List;

public class Parser {

    private static class ParseError extends RuntimeException {
    }

    private final List<Token> tokens;
    private int current = 0;
    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public Expr parse() {
        try {
            return expression();
        } catch (ParseError pe) {
            return  null;
        }
    }

    private Expr expression() {
        return equality();
    }

    private Expr equality() {
        Expr expr = comparison();
        while (!isAtEnd() && match(TokenType.BANG_EQUAL, TokenType.EQUAL)) {
            Token operator = previous();
            expr = new Expr.Binary(expr, operator, comparison());
        }
        return expr;
    }

    private Expr comparison() {
        Expr expr = term();
        while (!isAtEnd() && match(TokenType.GREATER,
                TokenType.GREATER_EQUAL,
                TokenType.LESS,
                TokenType.LESS_EQUAL)) {
            Token operator = previous();
            expr = new Expr.Binary(expr, operator, term());
        }
        return expr;
    }

    private Expr term() {
        Expr expr = factor();
        while (!isAtEnd() && match(TokenType.PLUS, TokenType.MINUS)) {
            Token operator = previous();
            expr = new Expr.Binary(expr, operator, factor());
        }
        return expr;
    }

    private Expr factor() {
        Expr expr = unary();
        while (!isAtEnd() && match(TokenType.STAR, TokenType.SLASH)) {
            Token operator = previous();
            expr = new Expr.Binary(expr, operator, unary());
        }
        return expr;
    }

    private Expr unary() {
        while (!isAtEnd() && match(TokenType.BANG, TokenType.MINUS)) {
            Token operator = previous();
            return  new Expr.Unary(operator, unary());
        }
        return primary();
    }

    private Expr primary() {
        if (match(TokenType.FALSE)) {
            return (new Expr.Literal(false));
        }
        if (match(TokenType.TRUE)) {
            return (new Expr.Literal(true));
        }
        if (match(TokenType.NIL)) {
            return (new Expr.Literal(null));
        }
        if (match(TokenType.NUMBER, TokenType.STRING)) {
            return new Expr.Literal(previous().literal);
        }
        if (match(TokenType.LEFT_PAREN)) {
            Expr expr = expression();
            consume(TokenType.RIGHT_PAREN, " ')' expected");
            return new Expr.Grouping(expr);
        }
        throw error(peek(), "Expected expression");
    }

    private void synchronize() {
        advance();
        while (!isAtEnd()) {
            Token prevToken = previous();
            if (prevToken.type == TokenType.SEMICOLON) {
                return;
            }
            switch (peek().type) {
                case CLASS:
                case FUN:
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN:
                    return;
            }
            advance();
        }
    }

    private Token consume(TokenType type, String errorMessage) {
        if (match(type)) {
            return advance();
        }
        throw error(peek(), errorMessage);
    }

    private ParseError error(Token token, String errorMessage) {
        Lox.error(token, errorMessage);
        return new ParseError();
    }

    private boolean isAtEnd() {
        return current == tokens.size();
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token advance() {
        return tokens.get(current++);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private boolean match(TokenType... types) {
        Token token = peek();
        for (TokenType type : types) {
            if (token.type == type) {
                advance();
                return true;
            }
        }
        return false;
    }
}
