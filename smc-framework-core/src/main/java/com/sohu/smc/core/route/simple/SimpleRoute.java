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
import com.sohu.smc.core.url.UrlMatch;
import com.sohu.smc.core.url.UrlSimple;
import org.jboss.netty.handler.codec.http.HttpMethod;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * @author toddf
 * @since Jan 7, 2011
 */
public class SimpleRoute
extends Route
{
	private UrlSimple[] aliases;

	/**
     * @param urlMatcher
     * @param controller
     * @param action
     * @param method
     * @param shouldSerializeResponse
     * @param name
     */
    public SimpleRoute(UrlSimple urlMatcher, Object controller, Method action, HttpMethod method, boolean shouldSerializeResponse,
                       String name, List<String> supportedFormats, String defaultFormat, Set<String> flags, Map<String, Object> parameters, String baseUrl)
    {
	    super(urlMatcher, controller, action, method, shouldSerializeResponse, name, supportedFormats, defaultFormat, flags, parameters, baseUrl);
    }

    /**
     * @param UrlSimple
     * @param controller
     * @param action
     * @param method
     * @param shouldSerializeResponse
     * @param name
     */
    public SimpleRoute(String UrlSimple, Object controller, Method action, HttpMethod method, boolean shouldSerializeResponse,
                       String name, List<String> supportedFormats, String defaultFormat, Set<String> flags, Map<String, Object> parameters, String baseUrl)
    {
	    this(new UrlSimple(UrlSimple), controller, action, method, shouldSerializeResponse, name, supportedFormats, defaultFormat, flags, parameters, baseUrl);
    }

    public void addAliases(List<String> uris)
    {
    	if (uris == null) return;
    	
    	aliases = new UrlSimple[uris.size()];
    	int i = 0;

    	for (String uri : uris)
    	{
    		aliases[i++] = new UrlSimple(uri);
    	}
    }

    @Override
    public UrlMatch match(String url)
    {
    	UrlMatch match = super.match(url);
    	
    	if (match == null && aliases != null)
    	{
    		for (UrlSimple alias : aliases)
    		{
    			match = alias.match(url);
    			
    			if (match != null)
    			{
    				break;
    			}
    		}
    	}

    	return match;
    }
}
