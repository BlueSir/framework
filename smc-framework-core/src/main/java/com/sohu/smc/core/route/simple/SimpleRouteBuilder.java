/*
    Copyright 2011, Strategic Gains, Inc.

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
package com.sohu.smc.core.route.simple;

import com.sohu.smc.core.route.Route;
import com.sohu.smc.core.route.RouteBuilder;
import com.sohu.smc.core.route.RouteDefaults;
import com.sohu.smc.core.route.RouteMetadata;
import org.jboss.netty.handler.codec.http.HttpMethod;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author toddf
 * @since Jan 13, 2011
 */
public class SimpleRouteBuilder
extends RouteBuilder
{
	private List<String> aliases = new ArrayList<String>();

	/**
	 * @param uri
	 * @param controller
	 * @param routeType
	 */
	public SimpleRouteBuilder(String uri, Object controller,
                              RouteDefaults defaults)
	{
		super(uri, controller, defaults);
	}

	@Override
	protected Route newRoute(String pattern, Object controller, Method action,
	    HttpMethod method, boolean shouldSerializeResponse, String name,
	    List<String> supportedFormats, String defaultFormat, Set<String> flags,
	    Map<String, Object> parameters, String baseUrl)
	{
		SimpleRoute r = new SimpleRoute(pattern, controller, action, method,
		    shouldSerializeResponse, name, supportedFormats, defaultFormat,
		    flags, parameters, baseUrl);
		r.addAliases(aliases);
		return r;
	}

	/**
	 * Associate another URI pattern to this route, essentially making an alias
	 * for the route. There may be multiple alias URIs for a given route. Note
	 * that new parameter nodes (e.g. {id}) in the URI will be available within
	 * the method. Parameter nodes that are missing from the alias will not be
	 * available in the action method.
	 * 
	 * @param uri the alias URI.
	 * @return the SimpleRouteBuilder instance (this).
	 */
	public SimpleRouteBuilder alias(String uri)
	{
		if (!aliases.contains(uri))
		{
			aliases.add(uri);
		}

		return this;
	}

	@Override
	public RouteMetadata asMetadata()
	{
		RouteMetadata metadata = super.asMetadata();

		for (String alias : aliases)
		{
			metadata.addAlias(alias);
		}

		return metadata;
	}

	protected String toRegexPattern(String uri)
	{
		String pattern = uri;

		if (pattern != null && !pattern.startsWith("/"))
		{
			pattern = "/" + pattern;
		}

		return pattern;
	}

}
