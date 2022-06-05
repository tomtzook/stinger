package com.stinger.framework.db;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JdbcDatabase implements Database {

    private final Connection mConnection;

    public JdbcDatabase(Connection connection) {
        mConnection = connection;
    }

    public static JdbcDatabase open(Path path) throws ClassNotFoundException, SQLException {
        if (!Files.exists(path)) {
            throw new SQLException("missing db file");
        }

        Class.forName("org.sqlite.JDBC");
        Connection connection = DriverManager.getConnection(String.format(
                "jdbc:sqlite:%s",
                path.toAbsolutePath().toString()));
        return new JdbcDatabase(connection);
    }

    @Override
    public List<Map<String, Object>> query(String query, Object... args) throws DatabaseException {
        try (PreparedStatement statement = mConnection.prepareStatement(query)) {
            for (int i = 0; i < args.length; i++) {
                statement.setObject(i + 1, args[i], getObjectType(args[i]));
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                List<Map<String, Object>> dataList = new ArrayList<>();

                while (resultSet.next()) {
                    Map<String, Object> data = new HashMap<>();

                    ResultSetMetaData metaData = resultSet.getMetaData();
                    for (int i = 1; i <= metaData.getColumnCount(); i++) {
                        String name = metaData.getColumnName(i);
                        Object value = resultSet.getObject(i);

                        data.put(name, value);
                    }

                    dataList.add(data);
                }

                return dataList;
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public void update(String query, Object... args) throws DatabaseException {
        try (PreparedStatement statement = mConnection.prepareStatement(query)) {
            for (int i = 0; i < args.length; i++) {
                statement.setObject(i + 1, args[i], getObjectType(args[i]));
            }

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public void close() throws IOException {
        try {
            mConnection.close();
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }

    private int getObjectType(Object object) {
        if (object instanceof String) {
            return Types.NVARCHAR;
        } else if (object instanceof Integer) {
            return Types.INTEGER;
        } else if (object instanceof Double) {
            return Types.DOUBLE;
        }

        throw new AssertionError("Unsupported type: " + object.getClass());
    }
}
