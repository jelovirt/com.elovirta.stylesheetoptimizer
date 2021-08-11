package org.dita.dost;

import org.dita.dost.log.DITAOTLogger;
import org.slf4j.helpers.MarkerIgnoringBase;

import java.text.MessageFormat;

/**
 * DITA-OT logger that will throw an assertion error for error messages.
 */
public class TestLogger extends MarkerIgnoringBase implements DITAOTLogger {

    private boolean failOnError;

    public TestLogger() {
        this.failOnError = true;
    }

    public TestLogger(final boolean failOnError) {
        this.failOnError = failOnError;
    }

    public void info(final String msg) {
        //System.out.println(msg);
    }

    @Override
    public void info(String format, Object arg) {
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
    }

    @Override
    public void info(String format, Object... arguments) {
    }

    @Override
    public void info(String msg, Throwable t) {
    }

    @Override
    public boolean isWarnEnabled() {
        return true;
    }

    public void warn(final String msg) {
        //System.err.println(msg);
    }

    @Override
    public void warn(String format, Object arg) {
    }

    @Override
    public void warn(String format, Object... arguments) {
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
    }

    @Override
    public void warn(String msg, Throwable t) {
    }

    @Override
    public boolean isErrorEnabled() {
        return true;
    }

    public void error(final String msg) {
        if (failOnError) {
            throw new AssertionError("Error message was thrown: " + msg);
        }
    }

    @Override
    public void error(String format, Object arg) {
        if (failOnError) {
            throw new AssertionError("Error message was thrown: " + MessageFormat.format(format, arg));
        }
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        if (failOnError) {
            throw new AssertionError("Error message was thrown: " + MessageFormat.format(format, arg1, arg2));
        }
    }

    @Override
    public void error(String format, Object... arguments) {
        if (failOnError) {
            throw new AssertionError("Error message was thrown: " + MessageFormat.format(format, arguments));
        }
    }

    public void error(final String msg, final Throwable t) {
        if (failOnError) {
            t.printStackTrace();
            throw new AssertionError("Error message was thrown: " + msg, t);
        }
    }

    @Override
    public boolean isTraceEnabled() {
        return false;
    }

    @Override
    public void trace(String msg) {
    }

    @Override
    public void trace(String format, Object arg) {
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
    }

    @Override
    public void trace(String format, Object... arguments) {
    }

    @Override
    public void trace(String msg, Throwable t) {
    }

    @Override
    public boolean isDebugEnabled() {
        return true;
    }

    public void debug(final String msg) {
        //System.out.println(msg);
    }

    @Override
    public void debug(String format, Object arg) {
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
    }

    @Override
    public void debug(String format, Object... arguments) {
    }

    @Override
    public void debug(String msg, Throwable t) {
    }

    @Override
    public boolean isInfoEnabled() {
        return true;
    }

}
