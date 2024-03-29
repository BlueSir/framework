/*
    Copyright 2010, Strategic Gains, Inc.

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

import org.jboss.netty.handler.codec.http.HttpResponseStatus;

/**
 * @author toddf
 * @since Jun 28, 2010
 */
public class NotFoundException
extends ServiceException
{
    private static final long serialVersionUID = -8484662487466021563L;
	private static final HttpResponseStatus STATUS = HttpResponseStatus.NOT_FOUND;

	public NotFoundException()
	{
		super(STATUS);
	}

	/**
	 * @param message
	 */
	public NotFoundException(String message)
	{
		super(STATUS, message);
	}

	/**
	 * @param cause
	 */
	public NotFoundException(Throwable cause)
	{
		super(STATUS, cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public NotFoundException(String message, Throwable cause)
	{
		super(STATUS, message, cause);
	}
}
