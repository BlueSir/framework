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

import java.nio.charset.Charset;

/**
 * Default FileUpload implementation that stores file into memory.<br><br>
 *
 * Warning: be aware of the memory limitation.
 *
 * @author <a href="http://www.jboss.org/netty/">The Netty Project</a>
 * @author Andy Taylor (andy.taylor@jboss.org)
 * @author <a href="http://gleamynode.net/">Trustin Lee</a>
 * @author <a href="http://openr66.free.fr/">Frederic Bregier</a>
 *
 */
public class MemoryFileUpload extends AbstractMemoryHttpData implements FileUpload {

    private String filename = null;

    private String contentType = null;

    private String contentTransferEncoding = null;

    public MemoryFileUpload(String name, String filename, String contentType,
            String contentTransferEncoding, Charset charset, long size)
            throws NullPointerException, IllegalArgumentException {
        super(name, charset, size);
        setFilename(filename);
        setContentType(contentType);
        setContentTransferEncoding(contentTransferEncoding);
    }

    @Override
    public HttpDataType getHttpDataType() {
        return HttpDataType.FileUpload;
    }

    @Override
    public String getFilename() {
        return filename;
    }

    @Override
    public void setFilename(String filename) {
        if (filename == null) {
            throw new NullPointerException("filename");
        }
        this.filename = filename;
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Attribute)) {
            return false;
        }
        Attribute attribute = (Attribute) o;
        return getName().equalsIgnoreCase(attribute.getName());
    }

    @Override
    public int compareTo(InterfaceHttpData arg0) {
        if (!(arg0 instanceof FileUpload)) {
            throw new ClassCastException("Cannot compare " + getHttpDataType() +
                    " with " + arg0.getHttpDataType());
        }
        return compareTo((FileUpload) arg0);
    }

    public int compareTo(FileUpload o) {
        int v;
        v = getName().compareToIgnoreCase(o.getName());
        if (v != 0) {
            return v;
        }
        // TODO should we compare size for instance ?
        return v;
    }

    @Override
    public void setContentType(String contentType) {
        if (contentType == null) {
            throw new NullPointerException("contentType");
        }
        this.contentType = contentType;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public String getContentTransferEncoding() {
        return contentTransferEncoding;
    }

    @Override
    public void setContentTransferEncoding(String contentTransferEncoding) {
        this.contentTransferEncoding = contentTransferEncoding;
    }

    @Override
    public String toString() {
        return HttpPostBodyUtil.CONTENT_DISPOSITION+": "+
            HttpPostBodyUtil.FORM_DATA+"; "+HttpPostBodyUtil.NAME+"=\"" + getName() +
            "\"; "+HttpPostBodyUtil.FILENAME+"=\"" + filename + "\"\r\n" +
            HttpHeaders.Names.CONTENT_TYPE+": " + contentType +
            (charset != null? "; "+ HttpHeaders.Values.CHARSET+"=" + charset + "\r\n" : "\r\n") +
            HttpHeaders.Names.CONTENT_LENGTH+": " + length() + "\r\n" +
            "Completed: " + isCompleted() +
            "\r\nIsInMemory: " + isInMemory();
    }
}
