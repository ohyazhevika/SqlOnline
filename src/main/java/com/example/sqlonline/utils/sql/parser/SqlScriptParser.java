package com.example.sqlonline.utils.sql.parser;

import java.util.List;

public interface SqlScriptParser {
    List<String> parse(String sql);
}
