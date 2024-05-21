package Core;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface Queryable {
    ResultSet all() throws SQLException;
    Queryable select(String... fields);
    Queryable where(String field, String operator, Object value);
    Queryable andWhere(String field, String operator, Object value);
    Queryable orWhere(String field, String operator, Object value);
    Queryable orderBy(String field, String direction);
    Queryable groupBy(String field);
    Queryable groupByAnd(String field);
    ResultSet get() throws SQLException;
    int update( String[] fields, Object[] values, String condition) throws SQLException ;
    int insert( String[] fields, Object[] values) throws SQLException ;
    int delete( String condition) throws SQLException ;
}
