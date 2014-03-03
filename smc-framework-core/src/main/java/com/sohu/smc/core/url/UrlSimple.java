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
package com.sohu.smc.core.url;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author toddf
 * @since Apr 28, 2010
 * @see http://www.ietf.org/rfc/rfc3986.txt
 */
public class UrlSimple
implements UrlMatcher
{

	/**
	 * The URL pattern describing the URL layout and any parameters.
	 */
	private String urlPattern;


	/**
	 * An ordered list of parameter names found in the urlPattern, above.
	 */
	private List<String> parameterNames = new ArrayList<String>();


	// SECTION: CONSTRUCTOR

	/**
	 * @param pattern
	 */
	public UrlSimple(String pattern)
	{
		super();
		setUrlPattern(pattern);
	}

	
	// SECTION: ACCESSORS/MUTATORS - PRIVATE

	/**
     * @return the pattern
     */
    private String getUrlPattern()
    {
    	return urlPattern;
    }
    
    public String getPattern()
    {
    	return getUrlPattern();
    }

	/**
     * @param pattern the pattern to set
     */
    private void setUrlPattern(String pattern)
    {
    	this.urlPattern = pattern;
    }
    
    public List<String> getParameterNames()
    {
    	return Collections.unmodifiableList(parameterNames);
    }
    
    
    // SECTION: URL MATCHING

    /**
     * Test the given URL against the underlying pattern to determine if it matches, returning the
     * results in a UrlMatch instance.  If the URL matches, parse any applicable parameters from it,
     * placing those also in the UrlMatch instance accessible by their parameter names.
     * 
     * @param url an URL string with or without query string.
     * @return a UrlMatch instance reflecting the outcome of the comparison, if matched. Otherwise, null.
     */
	@Override
    public UrlMatch match(String url)
	{
		if (getUrlPattern().equals(url))
		{
			return new UrlMatch(null);
		}

		return null;
	}
	
	/**
	 * Test the given URL against the underlying pattern to determine if it matches, returning a boolean
	 * to reflect the outcome.
	 * 
	 * @param url an URL string with or without query string.
	 * @return true if the given URL matches the underlying pattern.  Otherwise false.
	 */
	@Override
    public boolean matches(String url)
	{
		return (match(url) != null);
	}
	
	

}
