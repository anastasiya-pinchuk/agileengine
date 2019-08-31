package com.agileengine;

import com.agileengine.analyzer.SmartXmlAnalyzer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;


public class Application {

    private static Logger LOGGER = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        if (args.length < 3) {
            throw new IllegalArgumentException(
                    "Required parameters are absent, should be 'originalFilePath', 'modifiedFilePath', 'targetElementId', " +
                            "please follow the order.");
        }
        LOGGER.info("Next parameters passed: {}, {}, {}", args[0], args[1], args[2]);

        SmartXmlAnalyzer smartXmlAnalyzer = new SmartXmlAnalyzer();
        smartXmlAnalyzer.analyze(new File(args[0]), new File(args[1]), args[2]);
    }
}