package com.ef;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Service {

    private static Logger LOGGER = LoggerFactory.getLogger(Service.class);

    private Params params;
    private Database database;
    private Connection connection;
    private Repository repository;

    public Service(Params params) throws SQLException {
        this.params = params;
        initDatabase();
    }

    private void initDatabase() throws SQLException {
        database = new Database();
        connection = database.connect();
    }

    public void start() {
        try {
            repository = new Repository(connection);
            Helper helper = new Helper();

            List<Log> logs = readFileLogs();
            saveLogs(logs);

            String endDate = helper.calculateEndDate(params.getStartDate(), params.getDuration());
            List<String> ips = selectIPs(endDate);

            if (ips.size() > 0) {
                saveBlockedIPs(endDate, ips);
            }

            truncateTables();

            database.disconnect();
        } catch (SQLException | IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void truncateTables() throws SQLException {
        long startTimeTruncate = System.currentTimeMillis();
        repository.truncateTable("LOGS");
        long stopTimeTruncate = System.currentTimeMillis();
        long elapsedTime = stopTimeTruncate - startTimeTruncate;
        LOGGER.debug("Time spent to truncate the table: seconds=" + elapsedTime / 1000 + ", milliseconds=" + elapsedTime);
    }

    private void saveBlockedIPs(String endDate, List<String> ips) throws SQLException {
        String reason = generateBlockedMessage(endDate);
        List<BlockedIP> blockedIPS = new ArrayList<>();
        for (String ip : ips) {
            LOGGER.debug(ip);
            System.out.println(ip);
            blockedIPS.add(new BlockedIP(ip, reason));
        }
        repository.saveBlockedIps(blockedIPS);
    }

    private void saveLogs(List<Log> logs) throws SQLException {
        long startTimeSave = System.currentTimeMillis();
        repository.saveLogs(logs);
        long stopTimeSave = System.currentTimeMillis();
        long elapsedTime = stopTimeSave - startTimeSave;
        LOGGER.debug("Time spent to save the logs in database: seconds=" + elapsedTime / 1000 + ", milliseconds=" + elapsedTime);
    }

    private String generateBlockedMessage(String endDate) {
        return "The IP is blocked because more than " + params.getThreshold() + " entries were found between " +
                params.getStartDate() + " and " + endDate;
    }

    private List<Log> readFileLogs() throws IOException {
        long startTimeRead = System.currentTimeMillis();
        FileReader fileReader = new FileReader();
        List<Log> list = fileReader.readFileLogs(params.getPathToFile());
        long stopTimeRead = System.currentTimeMillis();
        long elapsedTime = stopTimeRead - startTimeRead;
        LOGGER.debug("Time spent to read the logs from file: seconds=" + elapsedTime / 1000 + ", milliseconds=" + elapsedTime);
        return list;
    }

    private List<String> selectIPs(String endDate) throws SQLException {
        long startTimeSelect = System.currentTimeMillis();
        List<String> ips = repository.selectIPs(params.getStartDate(), endDate, params.getThreshold());
        long stopTimeSelect = System.currentTimeMillis();
        long elapsedTime = stopTimeSelect - startTimeSelect;
        LOGGER.debug("Time spent to select the IPS from database: seconds=" + elapsedTime / 1000 + ", milliseconds=" + elapsedTime);
        return ips;
    }
}
