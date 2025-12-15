package lox;

import java.util.List;

public abstract class Expr {
    public abstract <R> R accept(Visitor<R> visitor);
    public interface Visitor<T> {
        T visitExprBinary(Binary Expr);
        T visitExprGrouping(Grouping Expr);
        T visitExprLiteral(Literal Expr);
        T visitExprUnary(Unary Expr);
    }
    public static class Binary extends Expr{
        public final Expr left;
        public final Token operator;
        public final Expr right;
        public Binary(Expr left, Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitExprBinary(this);
        }
    }
    public static class Grouping extends Expr{
        public final Expr expression;
        public Grouping(Expr expression) {
            this.expression = expression;
        }
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitExprGrouping(this);
        }
    }
    public static class Literal extends Expr{
        public final Object value;
        public Literal(Object value) {
            this.value = value;
        }
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitExprLiteral(this);
        }
    }
    public static class Unary extends Expr{
        public final Token operator;
        public final Expr right;
        public Unary(Token operator, Expr right) {
            this.operator = operator;
            this.right = right;
        }
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitExprUnary(this);
        }
    }
}
