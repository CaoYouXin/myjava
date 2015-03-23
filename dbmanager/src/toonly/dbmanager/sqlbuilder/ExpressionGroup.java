package toonly.dbmanager.sqlbuilder;

import com.sun.istack.internal.NotNull;

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

    public ExpressionGroup addExpression(boolean isAndOrOr, @NotNull Expression expression) {
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
        if (null == this.follows) {
            return String.format("(%s)", this.first.toSql());
        }
        return String.format("(%s%s)", this.first.toSql(), this.appendExpressionsToSql());
    }

    private String appendExpressionsToSql() {
        if (this.follows.size() != this.isAndOrOrs.size()) {
            throw new BuildException("inner state error.");
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.follows.size(); i++) {
            sb.append(" ").append(this.isAndOrOrs.get(i) ? "&&" : "||")
                    .append(" ").append(this.follows.get(i).toSql());
        }
        return sb.toString();
    }

    @Override
    public String toPreparedSql() {
        if (null == this.follows) {
            return String.format("(%s)", this.first.toPreparedSql());
        }
        return String.format("(%s%s)", this.first.toPreparedSql(), this.appendExpressionsToPreparedSql());
    }

    private String appendExpressionsToPreparedSql() {
        if (this.follows.size() != this.isAndOrOrs.size()) {
            throw new BuildException("inner state error.");
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.follows.size(); i++) {
            sb.append(" ").append(this.isAndOrOrs.get(i) ? "&&" : "||")
                    .append(" ").append(this.follows.get(i).toPreparedSql());
        }
        return sb.toString();
    }
}
