package lox;

public class Token {
    public final TokenType type;
    public final String lexeme;
    public final Object literal;
    public final int lineNum;

    public Token(TokenType type, String lexeme, Object literal, int lineNum) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.lineNum = lineNum;
    }

    @Override
    public String toString() {
        return type + " " + lexeme + " " + literal + " " + lineNum;
    }
}
