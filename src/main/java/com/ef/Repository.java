package com.ef;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Repository {

    private Connection connection;

    public Repository(Connection connection) {
        this.connection = connection;
    }

    /**
     * @param list
     * @throws SQLException
     */
    public void saveBlockedIps(List<BlockedIP> list) throws SQLException {
        String query = " insert into BLOCKED_IPS (ip, reason, insert_time) values (?, ?, ?)";

        try (PreparedStatement preparedStmt = connection.prepareStatement(query)){
            int i = 0;
            for(BlockedIP blockedIp: list) {
                preparedStmt.setString(1, blockedIp.getIp());
                preparedStmt.setString(2, blockedIp.getReason());
                preparedStmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));

                preparedStmt.addBatch();
                i++;

                if (i % 50 == 0 || i == list.size()) {
                    preparedStmt.executeBatch();
                }
            }
            preparedStmt.close();
        }
    }

    /**
     * @throws SQLException
     */
    public void truncateTable(String table) throws SQLException {
        String query = "TRUNCATE TABLE " + table;
        Statement statement = connection.createStatement();
        statement.execute(query);
        statement.close();
    }

    public void saveLogs(List<Log> entities) throws SQLException {
        String SQL_INSERT = " insert into LOGS (start_date, IP, request, status, user_agent)"
                + " values (?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(SQL_INSERT)) {
            int i = 0;

            for (Log entity : entities) {
                statement.setTimestamp(1, entity.getStartDate());
                statement.setString(2, entity.getIp());
                statement.setString(3, entity.getRequest());
                statement.setInt(4, entity.getStatus());
                statement.setString(5, entity.getUserAgent());

                statement.addBatch();
                i++;

                if (i % 1000 == 0 || i == entities.size()) {
                    statement.executeBatch();
                }
            }
            statement.close();
        }
    }

    public List<String> selectIPs(String startTime, String endTime, String threshold) throws SQLException {
        List<String> ips = new ArrayList<>();
        String SQL_SELECT = "SELECT  IP, count(IP) as count FROM LOGS where start_date between ? and ? " +
                " GROUP BY IP " +
                " HAVING count > ?" +
                " ORDER by count";

        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT)) {
            statement.setString(1, startTime);
            statement.setString(2, endTime);
            statement.setString(3, threshold);

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                ips.add(rs.getString(1));
            }
            statement.close();
        }

        return ips;
    }
}
