/*
 * Copyright 2009 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.jboss.netty.handler.codec.http;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * This DateFormat decodes 3 formats of {@link java.util.Date}, but only encodes the one,
 * the first:
 * <ul>
 * <li>Sun, 06 Nov 1994 08:49:37 GMT: standard specification, the only one with
 * valid generation</li>
 * <li>Sun, 06 Nov 1994 08:49:37 GMT: obsolete specification</li>
 * <li>Sun Nov 6 08:49:37 1994: obsolete specification</li>
 * </ul>
 *
 * @author <a href="http://www.jboss.org/netty/">The Netty Project</a>
 * @author <a href="http://gleamynode.net/">Trustin Lee</a>
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 * @version $Rev$, $Date$
 */
final class HttpHeaderDateFormat extends SimpleDateFormat {
    private static final long serialVersionUID = -925286159755905325L;

    private final SimpleDateFormat format1 = new HttpHeaderDateFormatObsolete1();
    private final SimpleDateFormat format2 = new HttpHeaderDateFormatObsolete2();

    /**
     * Standard date format<p>
     * Sun, 06 Nov 1994 08:49:37 GMT -> E, d MMM yyyy HH:mm:ss z
     */
    HttpHeaderDateFormat() {
        super("E, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
        setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    @Override
    public Date parse(String text, ParsePosition pos) {
        Date date = super.parse(text, pos);
        if (date == null) {
            date = format1.parse(text, pos);
        }
        if (date == null) {
            date = format2.parse(text, pos);
        }
        return date;
    }

    /**
     * First obsolete format<p>
     * Sunday, 06-Nov-94 08:49:37 GMT -> E, d-MMM-y HH:mm:ss z
     */
    private final class HttpHeaderDateFormatObsolete1 extends SimpleDateFormat {
        private static final long serialVersionUID = -3178072504225114298L;

        HttpHeaderDateFormatObsolete1() {
            super("E, dd-MMM-y HH:mm:ss z", Locale.ENGLISH);
            setTimeZone(TimeZone.getTimeZone("GMT"));
        }
    }

    /**
     * Second obsolete format
     * <p>
     * Sun Nov 6 08:49:37 1994 -> EEE, MMM d HH:mm:ss yyyy
     */
    private final class HttpHeaderDateFormatObsolete2 extends SimpleDateFormat {
        private static final long serialVersionUID = 3010674519968303714L;

        HttpHeaderDateFormatObsolete2() {
            super("E MMM d HH:mm:ss yyyy", Locale.ENGLISH);
            setTimeZone(TimeZone.getTimeZone("GMT"));
        }
    }
}
