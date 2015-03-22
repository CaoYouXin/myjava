package toonly.dbmanager.sqlbuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by caoyouxin on 15-1-22.
 */
public class ExpressionGroup implements Expression {

    private Expression first;
    private List<Boolean> isAndOrOrs;
    private List<Expression> follows;

    public ExpressionGroup(Expression expression) {
        this.first = expression;
    }

    public ExpressionGroup addExpression(boolean isAndOrOr, Expression expression) {
        if (null == expression) {
            throw new NullPointerException("no expression identified.");
        }

        if (null == this.isAndOrOrs) {
            this.isAndOrOrs = new ArrayList<>();
        }
        if (null == this.follows) {
            this.follows = new ArrayList<>();
        }

        this.isAndOrOrs.add(isAndOrOr);
        this.follows.add(expression);

        return this;
    }

    @Override
    public String toSql() {
        if (null == this.follows)
            return String.format("(%s)", this.first.toSql());
        return String.format("(%s%s)", this.first.toSql(), this._toSql());
    }

    private String _toSql() {
        if (this.follows.size() != this.isAndOrOrs.size())
            throw new RuntimeException("inner state error.");

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.follows.size(); i++) {
            sb.append(" ").append(this.isAndOrOrs.get(i) ? "&&" : "||")
                    .append(" ").append(this.follows.get(i).toSql());
        }
        return sb.toString();
    }

    @Override
    public String toPreparedSql() {
        if (null == this.follows)
            return String.format("(%s)", this.first.toPreparedSql());
        return String.format("(%s%s)", this.first.toPreparedSql(), this._toPreparedSql());
    }

    private String _toPreparedSql() {
        if (this.follows.size() != this.isAndOrOrs.size())
            throw new RuntimeException("inner state error.");

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.follows.size(); i++) {
            sb.append(" ").append(this.isAndOrOrs.get(i) ? "&&" : "||")
                    .append(" ").append(this.follows.get(i).toPreparedSql());
        }
        return sb.toString();
    }
}
