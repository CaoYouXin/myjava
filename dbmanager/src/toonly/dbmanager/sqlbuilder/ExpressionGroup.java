package toonly.dbmanager.sqlbuilder;

import com.sun.istack.internal.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Created by caoyouxin on 15-1-22.
 */
public class ExpressionGroup implements Expression {

    public static final String INNER_STATE_ERROR = "inner state error.";
    public static final String MULTI_EXPRESSIONS = "(%s%s)";
    public static final String SINGLE_EXPRESSION = "(%s)";
    public static final String AND = "&&";
    public static final String OR = "||";
    public static final String BLANK = " ";

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
        return this.sql(SQL::toSql);
    }

    @Override
    public String toPreparedSql() {
        return this.sql(PreparedSQL::toPreparedSql);
    }

    private String sql(Function<Expression, String> toSQL) {
        if (null == this.follows) {
            return String.format(SINGLE_EXPRESSION, toSQL.apply(this.first));
        }
        return String.format(MULTI_EXPRESSIONS, toSQL.apply(this.first), this.append(toSQL));
    }

    private String append(Function<Expression, String> toSQL) {
        if (this.follows.size() != this.isAndOrOrs.size()) {
            throw new BuildException(INNER_STATE_ERROR);
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.follows.size(); i++) {
            sb.append(BLANK).append(this.isAndOrOrs.get(i) ? AND : OR)
                    .append(BLANK).append(toSQL.apply(this.follows.get(i)));
        }
        return sb.toString();
    }
}
