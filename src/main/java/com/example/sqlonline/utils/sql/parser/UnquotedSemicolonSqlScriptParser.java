package com.example.sqlonline.utils.sql.parser;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class UnquotedSemicolonSqlScriptParser implements SqlScriptParser {
    private final static Character QUOTE_CHAR = '\'';
    private final static Character SEMICOLON_CHAR = ';';

    private enum State {
        QUOTED,
        UNQUOTED
    }
    @Override
    public List<String> parse(String sql) {
        List<String> statements = new ArrayList<>();
        StringBuilder currentStatement = new StringBuilder();
        State currentState = State.UNQUOTED;
        for (char c: sql.trim().toCharArray()) {
            if (currentState == State.UNQUOTED) {
                if ( c == SEMICOLON_CHAR) {
                    statements.add(currentStatement.toString());
                    currentStatement = new StringBuilder();
                    continue;
                }
                if (c == QUOTE_CHAR) {
                    currentState = State.QUOTED;
                }
            }
            // QUOTED_STATE
            else if (c == QUOTE_CHAR) {
                currentState = State.UNQUOTED;
            }
            currentStatement.append(c);
        }
        return statements;
    }
}
