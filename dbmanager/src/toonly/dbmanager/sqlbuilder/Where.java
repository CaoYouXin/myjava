package toonly.dbmanager.sqlbuilder;

import toonly.wrapper.StringWrapper;

/**
 * Created by caoyouxin on 15-1-22.
 */
public class Where extends ExpressionGroup implements SQL {

    public Where(Expression expression) {
        super(expression);
    }

    @Override
    public Where addExpression(boolean isAndOrOr, Expression expression) {
        return (Where) super.addExpression(isAndOrOr, expression);
    }

    @Override
    public String toSql() {
        return String.format("WHERE %s", new StringWrapper(super.toSql()).unwrap().val());
    }

    @Override
    public String toPreparedSql() {
        return String.format("WHERE %s", new StringWrapper(super.toPreparedSql()).unwrap().val());
    }
}
