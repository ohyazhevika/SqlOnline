package com.example.sqlonline.utils.sql.parser;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SqlScriptParserImpl implements SqlScriptParser {
    private final static String DEFAULT_DELIMITER = ";";
    @Override
    public List<String> parse(String sql) {
        if (sql == null) throw new RuntimeException("Cannot parse null script");
        return List.of(sql.split(DEFAULT_DELIMITER));
    }
}
