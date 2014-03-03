/*
 * Copyright 2009, Strategic Gains, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sohu.smc.core.route;

import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;

import java.util.*;

import static org.jboss.netty.handler.codec.http.HttpResponseStatus.OK;

/**
 * @author toddf
 * @since Nov 20, 2009
 */
public class Response
{
	private static final String CONTENT_RANGE_HEADER_NAME = "Content-Range";

	
	// SECTION: INSTANCE VARIABLES

	private HttpResponseStatus responseCode = OK;
	private String contentType = null;
	private Object body;
	private Map<String, List<String>> headers = new HashMap<String, List<String>>();
	private boolean isSerialized = true;
	private Throwable exception = null;

	// SECTION: CONSTRUCTORS
	
	public Response()
	{
		super();
	}


	// SECTION: ACCESSORS/MUTATORS

	public Object getBody()
	{
		return body;
	}

	public boolean hasBody()
	{
		return (getBody() != null);
	}

	public void setBody(Object body)
	{
		this.body = body;
	}

	public void clearHeaders()
	{
		headers.clear();
	}

	public String getHeader(String name)
	{
		List<String> list = headers.get(name);

		if (list != null && !list.isEmpty())
		{
			return list.get(0);
		}
		
		return null;
	}

	public List<String> getHeaders(String name)
	{
		return headers.get(name);
	}
	
	public boolean hasHeader(String name)
	{
		return (getHeader(name) != null);
	}

	public boolean hasHeaders()
	{
		return !headers.isEmpty();
	}

	public Set<String> getHeaderNames()
	{
		return headers.keySet();
	}

	/**
	 * Add a header value to the response.
	 * 
	 * @param name
	 * @param value
	 */
	public void addHeader(String name, String value)
	{
		List<String> list = headers.get(name);
		
		if (list == null)
		{
			list = new ArrayList<String>();
			headers.put(name, list);
		}
		
		list.add(value);
	}


	public void addLocationHeader(String url)
	{
		addHeader(HttpHeaders.Names.LOCATION, url);
	}

	/**
	 * Set the HTTP response status code.
	 * 
	 * @param value
	 */
	public void setResponseCode(int value)
	{
		setResponseStatus(HttpResponseStatus.valueOf(value));
	}
	
	/**
	 * Set the HTTP response status.
	 * 
	 * @param status
	 */
	public void setResponseStatus(HttpResponseStatus status)
	{
		this.responseCode = status;
	}
	
	/**
	 * Sets the HTTP response status code to 201 - created.
	 */
	public void setResponseCreated()
	{
		setResponseStatus(HttpResponseStatus.CREATED);
	}
	
	/**
	 * Get the HTTP Response Status.
	 * 
	 * @return
	 */
	public HttpResponseStatus getResponseStatus()
	{
		return responseCode;
	}

	public String getContentType()
    {
    	return contentType;
    }

	public void setContentType(String contentType)
    {
    	this.contentType = contentType;
    }

	public boolean isSerialized()
	{
		return isSerialized;
	}
	
	public void setIsSerialized(boolean value)
	{
		this.isSerialized = value;
	}

	public void noSerialization()
	{
		setIsSerialized(false);
	}
	
	public void useSerialization()
	{
		setIsSerialized(true);
	}

	public Throwable getException()
    {
    	return exception;
    }
	
	public boolean hasException()
	{
		return (getException() != null);
	}

	public void setException(Throwable exception)
    {
    	this.exception = exception;
    }
}
