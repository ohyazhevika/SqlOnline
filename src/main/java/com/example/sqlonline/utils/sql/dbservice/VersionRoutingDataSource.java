package com.example.sqlonline.utils.sql.dbservice;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class VersionRoutingDataSource extends AbstractRoutingDataSource  {
    @Override
    protected Object determineCurrentLookupKey() {
        ServletRequestAttributes attr =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attr == null) {
            // when the bean was created, attr is null (startup time)
            return "de";
        }

    // when running without servlet, this is empty (like in CLI)
        if ("".equals(attr.getRequest().getRequestURI())) {
            return "de";
        }
        String pathInfo = attr.getRequest().getRequestURI();
        return pathInfo.substring(1, 3);
    }
}
