/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet.filters.benchmark;

import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.petra.string.StringBundler;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicInteger;

import org.ehcache.sizeof.SizeOf;

/**
 * @author Tina Tian
 */
public class SessionRecordFilter implements Filter {

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(
			ServletRequest servletRequest, ServletResponse servletResponse,
			FilterChain filterChain)
		throws IOException, ServletException {

		HttpServletRequest httpServletRequest =
			(HttpServletRequest)servletRequest;

		String requestURI = httpServletRequest.getRequestURI();

		if (requestURI.contains("poller")) {
			filterChain.doFilter(servletRequest, servletResponse);

			return;
		}

		HttpSession httpSession = httpServletRequest.getSession(true);

		if (!Files.exists(_BEFORE_PATH)) {
			Files.createDirectories(_BEFORE_PATH);
			Files.createDirectories(_AFTER_PATH);

			_counter = new AtomicInteger();
		}

		String logFileName = String.valueOf(_counter.getAndIncrement());

		logFileName = logFileName.concat(".log");

		Files.write(
			_BEFORE_PATH.resolve(logFileName),
			Arrays.asList(_getSessionAttributes(httpSession, requestURI)));

		try {
			filterChain.doFilter(servletRequest, servletResponse);
		}
		finally {
			Files.write(
				_AFTER_PATH.resolve(logFileName),
				Arrays.asList(_getSessionAttributes(httpSession, requestURI)));
		}
	}

	@Override
	public void init(FilterConfig filterConfig) {
	}

	private String _getSessionAttributes(
		HttpSession httpSession, String requestURI) {

		StringBundler sb = new StringBundler();

		sb.append("####################Session ID: ");
		sb.append(httpSession.getId());
		sb.append("\n####################Request URI: ");
		sb.append(requestURI);

		try {
			Enumeration<String> attributeNamesEnumeration =
				httpSession.getAttributeNames();

			while (attributeNamesEnumeration.hasMoreElements()) {
				String attributeName = attributeNamesEnumeration.nextElement();

				Object attributeValue = httpSession.getAttribute(attributeName);

				sb.append("\nname: ");
				sb.append(attributeName);
				sb.append(", value: ");
				sb.append(attributeValue.toString());
				sb.append(", shallowSize: ");
				sb.append(String.valueOf(_SIZE_OF.sizeOf(attributeValue)));

				try {
					sb.append(", deepSize: ");
					sb.append(
						String.valueOf(_SIZE_OF.deepSizeOf(attributeValue)));
				}
				catch (UnsupportedOperationException
							unsupportedOperationException) {

					sb.append(", deepSize: [Not Available - Hidden Class]");
					sb.append("WARN: Could not get deep size for attribute '");
					sb.append(attributeName);
					sb.append("': ");
					sb.append(unsupportedOperationException.getMessage());
				}
			}
		}
		catch (IllegalStateException illegalStateException) {
			sb.append("\n####################Session is invalidated.\n");
			sb.append(illegalStateException.toString());
		}

		return sb.toString();
	}

	private static final Path _AFTER_PATH;

	private static final Path _BEFORE_PATH;

	private static final SizeOf _SIZE_OF = SizeOf.newInstance();

	static {
		String liferayHome = PropsUtil.get(PropsKeys.LIFERAY_HOME);

		_BEFORE_PATH = Paths.get(liferayHome, "/logs/session/before");

		_AFTER_PATH = Paths.get(liferayHome, "/logs/session/after");
	}

	private AtomicInteger _counter;

}