package org.xonix.zlo.search;

import org.apache.lucene.analysis.Analyzer;

import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Author: gubarkov
 * Date: 30.05.2007
 * Time: 18:06:03
 */
public class Config {
    private static Properties props = new Properties();

    static {
        try {
            props.load(Thread.currentThread()
                    .getContextClassLoader()
                    .getResourceAsStream("org/xonix/zlo/search/config.properties"));
        } catch (IOException e) {
            System.out.println("Can't load config!");
            e.printStackTrace();
        }
    }

    public static String URL = props.getProperty("url");
    public static String READ_QUERY = props.getProperty("query.read");
    public static final int BUFFER = Integer.parseInt(props.getProperty("buffer", "512"));

    public static final String CHARSET_NAME = "windows-1251";
    public static final String END_MSG_MARK = "<BIG>��������� � ���� ������</BIG>";
    public static final String INDEX_DIR = props.getProperty("index.dir");
    public static final Analyzer ANALYZER;
    static {
        Analyzer _a = null;
        try {
            _a = (Analyzer) Class.forName(props.getProperty("analyzer")).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ANALYZER = _a;
    }
}
