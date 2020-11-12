package org.javawebstack.orm.query;

import org.javawebstack.orm.TableInfo;

public interface QueryElement {

    QueryString getQueryString(TableInfo info);

}
