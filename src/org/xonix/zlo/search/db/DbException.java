package org.xonix.zlo.search.db;

import java.io.IOException;

/**
 * Author: gubarkov
 * Date: 14.09.2007
 * Time: 14:32:12
 */
public class DbException extends IOException {
    public DbException(String message, Throwable cause) {
        super(message, cause);
    }

    public DbException(Throwable cause) {
        super(cause);
    }
}