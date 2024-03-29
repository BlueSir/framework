package com.sohu.smc.core.config;

import com.google.common.collect.ImmutableMap;
import com.sohu.smc.common.util.Size;
import org.apache.log4j.Level;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.TimeZone;

@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal"})
public class LoggingConfiguration {
    static final TimeZone UTC = TimeZone.getTimeZone("UTC");

    public static class ConsoleConfiguration {
        @JsonProperty
        private boolean enabled = true;

        @NotNull
        @JsonProperty
        private Level threshold = Level.ALL;

        @NotNull
        @JsonProperty
        private TimeZone timeZone = UTC;

        public boolean isEnabled() {
            return enabled;
        }

        public Level getThreshold() {
            return threshold;
        }

        public TimeZone getTimeZone() {
            return timeZone;
        }
    }

    @SuppressWarnings("CanBeFinal")
    public static class FileConfiguration {
        @JsonProperty
        private boolean enabled = false;

        @NotNull
        @JsonProperty
        private Level threshold = Level.ALL;

        @NotNull
        @JsonProperty
        private String filenamePattern = "./logs/example.log";

        @NotNull
        @JsonProperty
        private Size maxFileSize = Size.megabytes(50);

        @Min(1)
        @Max(50)
        @JsonProperty
        private int retainedFileCount = 5;

        @NotNull
        @JsonProperty
        private TimeZone timeZone = UTC;

        public boolean isEnabled() {
            return enabled;
        }

        public Level getThreshold() {
            return threshold;
        }

        public String getFilenamePattern() {
            return filenamePattern;
        }

        public Size getMaxFileSize() {
            return maxFileSize;
        }

        public int getRetainedFileCount() {
            return retainedFileCount;
        }

        public TimeZone getTimeZone() {
            return timeZone;
        }
    }

    public static class SyslogConfiguration {
        @JsonProperty
        private boolean enabled = true;

        @NotNull
        @JsonProperty
        private Level threshold = Level.ALL;

        @NotNull
        @JsonProperty
        private String host = "localhost";

        @NotNull
        @JsonProperty
        @Pattern(regexp = "(auth|authpriv|daemon|cron|ftp|lpr|kern|mail|news|syslog|user|uucp|local[0-7])")
        private String facility = "local0";

        public boolean isEnabled() {
            return enabled;
        }

        public Level getThreshold() {
            return threshold;
        }

        public String getHost() {
            return host;
        }

        public String getFacility() {
            return facility;
        }
    }

    @NotNull
    @JsonProperty
    private Level level = Level.INFO;

    @NotNull
    @JsonProperty
    private ImmutableMap<String, Level> loggers = ImmutableMap.of();

    @Valid
    @NotNull
    @JsonProperty
    private ConsoleConfiguration console = new ConsoleConfiguration();

    @Valid
    @NotNull
    @JsonProperty
    private FileConfiguration file = new FileConfiguration();

    @Valid
    @NotNull
    @JsonProperty
    private SyslogConfiguration syslog = new SyslogConfiguration();

    public Level getLevel() {
        return level;
    }

    public ImmutableMap<String, Level> getLoggers() {
        return loggers;
    }

    public ConsoleConfiguration getConsoleConfiguration() {
        return console;
    }

    public FileConfiguration getFileConfiguration() {
        return file;
    }

    public SyslogConfiguration getSyslogConfiguration() {
        return syslog;
    }
}
