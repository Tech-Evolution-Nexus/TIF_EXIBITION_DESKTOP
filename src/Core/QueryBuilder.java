package Core;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import Config.DB;

public class QueryBuilder implements Queryable {
    private StringBuilder query;
    private Connection connection;
    private String tableName;

    public QueryBuilder(String tableName) {
        this.tableName = tableName;
        this.connection = DB.getConnection();
        initQuery();
    }
    
    private void initQuery() {
        if (query !=null) {
            query.setLength(0); 
        }
        this.query = new StringBuilder("SELECT * FROM " + tableName);
    };
    @Override
    public Queryable where(String field, String operator, Object value) {
        query.append(" WHERE ").append(field).append(" ").append(operator).append(" '").append(value).append("'");
        System.out.println("test "+query.toString());
        return this;
    }
    @Override
    public Queryable andWhere(String field, String operator, Object value) {
        query.append(" AND ").append(field).append(" ").append(operator).append(" '").append(value).append("'");
        return this;
    }
    @Override
    public Queryable orWhere(String field, String operator, Object value) {
        query.append(" OR ").append(field).append(" ").append(operator).append(" '").append(value).append("'");
        return this;
    }

    @Override
    public Queryable orderBy(String field, String direction) {
        query.append(" ORDER BY ").append(field).append(" ").append(direction);
        return this;
    }

    @Override
    public Queryable groupBy(String field) {
        query.append(" GROUP BY ").append(field);
        return this;
    }

    @Override
    public ResultSet get() throws SQLException {
        Statement stmt = connection.createStatement();
        String queryStr = query.toString();
        initQuery();
        return stmt.executeQuery(queryStr);
    }

    @Override
    public ResultSet all() throws SQLException{
       Statement stmt = connection.createStatement();
        String queryStr = query.toString();
        initQuery();
        return stmt.executeQuery(queryStr);
    }
    

    @Override
    public QueryBuilder select(String... fields) {
        query.setLength(0);
        query.append("SELECT ");
        if (fields.length == 0) {
            query.append("* ");
        } else {
            for (int i = 0; i < fields.length; i++) {
                query.append(fields[i]);
                if (i < fields.length - 1) {
                    query.append(", ");
                }
            }
        }
        query.append(" FROM ");
        query.append(tableName);
        return this;
    }
 
    public int insert( String[] fields, Object[] values) throws SQLException {
        if (fields.length != values.length) {
            throw new IllegalArgumentException("Jumlah kolom dan nilai harus sama");
        }
        query.setLength(0);

        query.append("INSERT INTO ").append(tableName).append(" (");
        for (int i = 0; i < fields.length; i++) {
            query.append(fields[i]);
            if (i < fields.length - 1) {
                query.append(", ");
            }
        }
        query.append(") VALUES (");
        for (int i = 0; i < values.length; i++) {
            query.append("?");
            if (i < values.length - 1) {
                query.append(", ");
            }
        }
        query.append(")");
        PreparedStatement pstmt = connection.prepareStatement(query.toString());
        for (int i = 0; i < values.length; i++) {
            pstmt.setObject(i + 1, values[i]);
        }
        int rowsAffected = pstmt.executeUpdate();
        pstmt.close();
        initQuery();
        return rowsAffected;
    }
    
    public int update( String[] fields, Object[] values, String condition) throws SQLException {
        if (fields.length != values.length) {
            throw new IllegalArgumentException("Jumlah kolom dan nilai harus sama");
        }

        query.setLength(0);
        query.append("UPDATE ").append(tableName).append(" SET ");

        for (int i = 0; i < fields.length; i++) {
            query.append(fields[i]).append(" = ?");
            if (i < fields.length - 1) {
                query.append(", ");
            }
        }

        query.append(" WHERE ").append(condition);

        PreparedStatement pstmt = connection.prepareStatement(query.toString());

        for (int i = 0; i < values.length; i++) {
            pstmt.setObject(i + 1, values[i]);
        }
System.out.println(query);
        int rowsAffected = pstmt.executeUpdate();
        pstmt.close();
        initQuery();

        return rowsAffected;
    }

    public int delete(String condition) throws SQLException {
        query.setLength(0);
        query.append("DELETE FROM ").append(tableName).append(" WHERE ").append(condition);
        PreparedStatement pstmt = connection.prepareStatement(query.toString());
        int rowsAffected = pstmt.executeUpdate();
        initQuery();
        return rowsAffected;
    }

   
    
}
