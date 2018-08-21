package com.ef;

import com.mysql.cj.core.util.StringUtils;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 */
public class Helper {

    public static final String START_DATE_SHORT = "sd";
    public static final String DURATION_SHORT = "d";
    public static final String THRESHOLD_SHORT = "t";
    public static final String ACCESS_LOG_SHORT = "a";
    public static final String START_DATE_LONG = "startDate";
    public static final String DURATION_LONG = "duration";
    public static final String THRESHOLD_LONG = "threshold";
    public static final String ACCESS_LOG_LONG = "accesslog";
    public static final String HOURLY = "hourly";
    public static final String DAILY = "daily";
    public static final String DATE_FORMAT = "yyyy-MM-dd.HH:mm:ss";
    private static Logger LOGGER = LoggerFactory.getLogger(Helper.class);

    /**
     * @param args
     * @return
     * @throws Exception
     */
    public Params readParams(String[] args) throws Exception {
        final CommandLineParser parser = new DefaultParser();
        final Options options = new Options();
        options.addOption(START_DATE_SHORT, START_DATE_LONG, true, START_DATE_LONG);
        options.addOption(DURATION_SHORT, DURATION_LONG, true, DURATION_LONG);
        options.addOption(THRESHOLD_SHORT, THRESHOLD_LONG, true, THRESHOLD_LONG);
        options.addOption(ACCESS_LOG_SHORT, ACCESS_LOG_LONG, true, ACCESS_LOG_LONG);

        final CommandLine commandLine = parser.parse(options, args);

        final String startDate = getOption(START_DATE_SHORT, commandLine, START_DATE_LONG);
        final String duration = getOption(DURATION_SHORT, commandLine, DURATION_LONG);
        final String threshold = getOption(THRESHOLD_SHORT, commandLine, THRESHOLD_LONG);
        final String pathToFile = getOption(ACCESS_LOG_SHORT, commandLine, ACCESS_LOG_LONG);

        Params params = new Params(startDate, duration, threshold, pathToFile);
        validateParams(params);
        return params;
    }

    private String getOption(final String opt, final CommandLine commandLine, final String longOpt) throws Exception {

        if (commandLine.hasOption(opt)) {
            String value = commandLine.getOptionValue(opt);
            if (StringUtils.isNullOrEmpty(value)) {
                throw new Exception("Mandatory value for " + longOpt + " is missing.");
            }
            return value;
        }
        throw new Exception("Mandatory parameter missing: " + longOpt);
    }

    private void validateParams(Params params) throws Exception {
        if (!isValidFormat(DATE_FORMAT, params.getStartDate())) {
            throw new Exception("Invalid format for parameter startDate");
        }
        if (!(HOURLY.equals(params.getDuration()) || DAILY.equals(params.getDuration()))) {
            throw new Exception("Invalid value for parameter duration. Accepted values: " + HOURLY + " and " +
                    DAILY);
        }
        try {
            Integer threshold = Integer.valueOf(params.getThreshold());
            if (threshold.intValue() < 0) {
                throw new Exception("Invalid value for parameter threshold: " + params.getThreshold());
            }
        } catch (NumberFormatException e) {
            throw new Exception("Invalid value for parameter threshold: " + params.getThreshold());
        }
    }

    public boolean isValidFormat(String format, String value) throws Exception {
        Date date;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            date = sdf.parse(value);
            if (!value.equals(sdf.format(date))) {
                date = null;
            }
        } catch (ParseException ex) {
            throw new Exception("Invalid format for: " + value);
        }
        return date != null;
    }

    public String calculateEndDate(String startDate, String duration) {
        DateFormat format = new SimpleDateFormat(DATE_FORMAT);
        String endDate = null;

        try {
            Date dateStart = format.parse(startDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateStart);

            switch (duration) {
                case HOURLY: {
                    cal.add(Calendar.HOUR_OF_DAY, 1);
                    Date newDate = cal.getTime();
                    endDate = format.format(newDate);
                    break;
                }
                case DAILY: {
                    cal.add(Calendar.DATE, 1);
                    Date newDate = cal.getTime();
                    endDate = format.format(newDate);
                    break;
                }
            }
        } catch (ParseException e1) {
            LOGGER.error(e1.getMessage());
        }

        return endDate;
    }
}
