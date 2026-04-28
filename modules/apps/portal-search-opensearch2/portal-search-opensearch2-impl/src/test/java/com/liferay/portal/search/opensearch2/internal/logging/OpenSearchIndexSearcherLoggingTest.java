/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.logging;

import com.liferay.portal.kernel.search.generic.MatchAllQuery;
import com.liferay.portal.search.opensearch2.internal.OpenSearchIndexSearcher;
import com.liferay.portal.search.opensearch2.internal.OpenSearchTestRule;
import com.liferay.portal.search.opensearch2.internal.indexing.LiferayOpenSearchIndexingFixtureFactory;
import com.liferay.portal.search.opensearch2.internal.search.engine.adapter.search.CountSearchRequestExecutor;
import com.liferay.portal.search.opensearch2.internal.search.engine.adapter.search.SearchSearchRequestExecutor;
import com.liferay.portal.search.test.util.indexing.BaseIndexingTestCase;
import com.liferay.portal.search.test.util.indexing.IndexingFixture;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Bryan Engler
 * @author André de Oliveira
 */
public class OpenSearchIndexSearcherLoggingTest extends BaseIndexingTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@ClassRule
	public static OpenSearchTestRule openSearchTestRule =
		OpenSearchTestRule.INSTANCE;

	@Test
	public void testCountSearchRequestExecutorLogsViaIndexer() {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				CountSearchRequestExecutor.class.getName(),
				LoggerTestUtil.DEBUG)) {

			searchCount(createSearchContext(), new MatchAllQuery());

			_assertLogCapture(logCapture, "The search engine processed");
		}
	}

	@Test
	public void testIndexerSearchCountLogs() {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				OpenSearchIndexSearcher.class.getName(), LoggerTestUtil.INFO)) {

			searchCount(createSearchContext(), new MatchAllQuery());

			_assertLogCapture(logCapture, "The search engine processed");
		}
	}

	@Test
	public void testIndexerSearchLogs() {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				OpenSearchIndexSearcher.class.getName(), LoggerTestUtil.INFO)) {

			search(createSearchContext());

			_assertLogCapture(logCapture, "The search engine processed");
		}
	}

	@Test
	public void testSearchSearchRequestExecutorLogsPrettyPrintedString() {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				SearchSearchRequestExecutor.class.getName(),
				LoggerTestUtil.TRACE)) {

			search(createSearchContext());

			_assertLogCapture(logCapture, "Search query:");
		}
	}

	@Test
	public void testSearchSearchRequestExecutorLogsViaIndexer() {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				SearchSearchRequestExecutor.class.getName(),
				LoggerTestUtil.DEBUG)) {

			search(createSearchContext());

			_assertLogCapture(logCapture, "The search engine processed");
		}
	}

	@Override
	protected IndexingFixture createIndexingFixture() {
		return LiferayOpenSearchIndexingFixtureFactory.getInstance();
	}

	private void _assertLogCapture(
		LogCapture logCapture, String expectedMessage) {

		List<LogEntry> logEntries = logCapture.getLogEntries();

		Assert.assertFalse(logEntries.toString(), logEntries.isEmpty());

		StringBuilder sb = new StringBuilder();

		for (LogEntry logEntry : logEntries) {
			sb.append(logEntry.getMessage());
		}

		String messages = sb.toString();

		Assert.assertTrue(messages, messages.contains(expectedMessage));
	}

}
