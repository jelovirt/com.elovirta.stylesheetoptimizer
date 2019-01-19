/*
 * This file is part of the DITA Open Toolkit project.
 *
 * Copyright 2011 Jarno Elovirta
 *
 * See the accompanying LICENSE file for applicable license.
 */
package org.dita.dost;

import org.dita.dost.log.DITAOTLogger;
import org.slf4j.helpers.MarkerIgnoringBase;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Test utilities.
 *
 * @author Jarno Elovirta
 */
public class TestUtils {

    /**
     * DITA-OT logger that will throw an assertion error for error messages.
     */
    public static class TestLogger extends MarkerIgnoringBase implements DITAOTLogger {

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

    /**
     * DITA-OT logger that will cache messages.
     */
    public static final class CachingLogger extends MarkerIgnoringBase implements DITAOTLogger {

        final boolean strict;

        public CachingLogger() {
            this(false);
        }

        public CachingLogger(final boolean strict) {
            this.strict = strict;
        }

        private List<Message> buf = new ArrayList<Message>();

        public void info(final String msg) {
            buf.add(new Message(Message.Level.INFO, msg, null));
        }

        @Override
        public void info(String format, Object arg) {
            buf.add(new Message(Message.Level.INFO, MessageFormat.format(format, arg), null));
        }

        @Override
        public void info(String format, Object arg1, Object arg2) {
            buf.add(new Message(Message.Level.INFO, MessageFormat.format(format, arg1, arg2), null));
        }

        @Override
        public void info(String format, Object... arguments) {
            buf.add(new Message(Message.Level.INFO, MessageFormat.format(format, arguments), null));
        }

        @Override
        public void info(String msg, Throwable t) {
            buf.add(new Message(Message.Level.INFO, msg, t));
        }

        @Override
        public boolean isWarnEnabled() {
            return true;
        }

        public void warn(final String msg) {
            buf.add(new Message(Message.Level.WARN, msg, null));
        }

        @Override
        public void warn(String format, Object arg) {
            buf.add(new Message(Message.Level.WARN, MessageFormat.format(format, arg), null));
        }

        @Override
        public void warn(String format, Object... arguments) {
            buf.add(new Message(Message.Level.WARN, MessageFormat.format(format, arguments), null));
        }

        @Override
        public void warn(String format, Object arg1, Object arg2) {
            buf.add(new Message(Message.Level.WARN, MessageFormat.format(format, arg1, arg2), null));
        }

        @Override
        public void warn(String msg, Throwable t) {
            buf.add(new Message(Message.Level.WARN, msg, t));
        }

        @Override
        public boolean isErrorEnabled() {
            return true;
        }

        public void error(final String msg) {
            if (strict) {
                throw new RuntimeException();
            } else {
                buf.add(new Message(Message.Level.ERROR, msg, null));
            }
        }

        @Override
        public void error(String format, Object arg) {
            if (strict) {
                throw new RuntimeException();
            } else {
                buf.add(new Message(Message.Level.ERROR, MessageFormat.format(format, arg), null));
            }
        }

        @Override
        public void error(String format, Object arg1, Object arg2) {
            if (strict) {
                throw new RuntimeException();
            } else {
                buf.add(new Message(Message.Level.ERROR, MessageFormat.format(format, arg1, arg2), null));
            }
        }

        @Override
        public void error(String format, Object... arguments) {
            if (strict) {
                throw new RuntimeException();
            } else {
                buf.add(new Message(Message.Level.ERROR, MessageFormat.format(format, arguments), null));
            }
        }

        public void error(final String msg, final Throwable t) {
            if (strict) {
                throw new RuntimeException(t);
            } else {
                buf.add(new Message(Message.Level.ERROR, msg, null));
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
            return false;
        }

        public void debug(final String msg) {
            buf.add(new Message(Message.Level.DEBUG, msg, null));
        }

        @Override
        public void debug(String format, Object arg) {
            buf.add(new Message(Message.Level.DEBUG, MessageFormat.format(format, arg), null));
        }

        @Override
        public void debug(String format, Object arg1, Object arg2) {
            buf.add(new Message(Message.Level.DEBUG, MessageFormat.format(format, arg1, arg2), null));
        }

        @Override
        public void debug(String format, Object... arguments) {
            buf.add(new Message(Message.Level.DEBUG, MessageFormat.format(format, arguments), null));
        }

        @Override
        public void debug(String msg, Throwable t) {
            buf.add(new Message(Message.Level.DEBUG, msg, t));
        }

        @Override
        public boolean isInfoEnabled() {
            return true;
        }

        public static final class Message {
            public enum Level {DEBUG, INFO, WARN, ERROR, FATAL}

            public final Level level;
            public final String message;
            public final Throwable exception;

            public Message(final Level level, final String message, final Throwable exception) {
                this.level = level;
                this.message = message;
                this.exception = exception;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                Message message1 = (Message) o;
                return level == message1.level &&
                        Objects.equals(message, message1.message) &&
                        Objects.equals(exception, message1.exception);
            }

            @Override
            public int hashCode() {

                return Objects.hash(level, message, exception);
            }

            @Override
            public String toString() {
                return "Message{" +
                        "level=" + level +
                        ", message='" + message + '\'' +
                        ", exception=" + exception +
                        '}';
            }
        }

        public List<Message> getMessages() {
            return Collections.unmodifiableList(buf);
        }

    }

}
