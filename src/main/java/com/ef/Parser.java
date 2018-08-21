package com.ef;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class Parser {

    static Logger LOGGER = LoggerFactory.getLogger(Parser.class);

    public static void main(String[] args) throws SQLException {
        Helper helper = new Helper();
        Params params;
        try {
            params = helper.readParams(args);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return;
        }
        Service service = new Service(params);
        service.start();
    }
}
