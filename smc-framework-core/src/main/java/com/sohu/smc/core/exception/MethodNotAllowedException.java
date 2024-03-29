/*
    Copyright 2012, Strategic Gains, Inc.

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

		http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/
package com.sohu.smc.core.exception;

import com.sohu.smc.core.route.Response;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;

import java.util.*;

/**
 * @author toddf
 * @since May 15, 2012
 */
public class MethodNotAllowedException
extends ServiceException
{
    private static final long serialVersionUID = 4116758162425337982L;
	private static final HttpResponseStatus STATUS = HttpResponseStatus.METHOD_NOT_ALLOWED;
	
	private List<HttpMethod> allowedMethods;

	public MethodNotAllowedException(List<HttpMethod> allowed)
	{
		super(STATUS);
		setAllowedMethods(allowed);
	}

	/**
	 * @param message
	 */
	public MethodNotAllowedException(String message, List<HttpMethod> allowed)
	{
		super(STATUS, message);
		setAllowedMethods(allowed);
	}

	/**
	 * @param cause
	 */
	public MethodNotAllowedException(Throwable cause, List<HttpMethod> allowed)
	{
		super(STATUS, cause);
		setAllowedMethods(allowed);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public MethodNotAllowedException(String message, Throwable cause, List<HttpMethod> allowed)
	{
		super(STATUS, message, cause);
		setAllowedMethods(allowed);
	}
	
	public void setAllowedMethods(List<HttpMethod> allowed)
	{
		this.allowedMethods = Collections.unmodifiableList(allowed);
	}
	
	public List<HttpMethod> getAllowedMethods()
	{
		return allowedMethods;
	}
	
	/**
	 * Adds Allow HTTP header to the response with a list of the appropriate HTTP methods for the route.
	 */
	@Override
	public void augmentResponse(Response response)
	{
		response.addHeader(HttpHeaders.Names.ALLOW, join(",", getAllowedMethods()));
	}

    public static final String EMPTY_STRING = "";

    public static String join(String delimiter, Collection<? extends Object> objects)
    {
        if (objects == null || objects.isEmpty())
        {
            return EMPTY_STRING;
        }

        Iterator<? extends Object> iterator = objects.iterator();
        StringBuilder builder = new StringBuilder();
        builder.append(iterator.next());

        while(iterator.hasNext())
        {
            builder.append(delimiter)
                    .append(iterator.next());
        }

        return builder.toString();
    }

    public static String join(String delimiter, Object... objects)
    {
        return join(delimiter, Arrays.asList(objects));
    }
}
