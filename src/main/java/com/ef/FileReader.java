package com.ef;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FileReader {

    private static Logger LOGGER = LoggerFactory.getLogger(FileReader.class);

    public List<Log> readFileLogs(String inputFilePath) throws IOException {
        List<Log> inputList;
        try{
            File inputF = new File(inputFilePath);
            InputStream inputFS = new FileInputStream(inputF);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputFS));
            inputList = br.lines().map(mapToItem).collect(Collectors.toList());
            br.close();
        } catch (IOException e) {
            LOGGER.error("File not found: " + inputFilePath);
            throw e;
        }
        return inputList ;
    }

    private Function<String, Log> mapToItem = (line) -> {
        String[] p = line.split("\\|");
        Log log = new Log();
        log.setStartDate(Timestamp.valueOf(p[0]));
        log.setIp(p[1]);
        log.setRequest(p[2]);
        log.setStatus(Integer.valueOf(p[3]));
        log.setUserAgent(p[4]);
        return log;
    };
}
