/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.solr8.internal.logging;

import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.search.generic.TermQueryImpl;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.search.solr8.internal.SolrIndexSearcher;
import com.liferay.portal.search.solr8.internal.SolrUnitTestRequirements;
import com.liferay.portal.search.solr8.internal.indexing.SolrIndexingFixture;
import com.liferay.portal.search.test.util.indexing.BaseIndexingTestCase;
import com.liferay.portal.search.test.util.indexing.IndexingFixture;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Bryan Engler
 */
public class SolrIndexSearcherLogExceptionsOnlyTest
	extends BaseIndexingTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		Assume.assumeTrue(
			SolrUnitTestRequirements.isSolrExternallyStartedByDeveloper());
	}

	@Test
	public void testExceptionOnlyLoggedWhenQueryMalformedSearch() {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				SolrIndexSearcher.class.getName(), LoggerTestUtil.ERROR)) {

			search(createSearchContext(), getMalformedQuery());

			_assertLogCapture(logCapture, "Cannot parse '+(+f^eld:text)");
		}
	}

	@Test
	public void testExceptionOnlyLoggedWhenQueryMalformedSearchCount() {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				SolrIndexSearcher.class.getName(), LoggerTestUtil.ERROR)) {

			searchCount(createSearchContext(), getMalformedQuery());

			_assertLogCapture(logCapture, "Cannot parse '+(+f^eld:text)");
		}
	}

	@Override
	protected IndexingFixture createIndexingFixture() throws Exception {
		return new SolrIndexingFixture(
			HashMapBuilder.<String, Object>put(
				"logExceptionsOnly", true
			).build());
	}

	protected Query getMalformedQuery() {
		BooleanQueryImpl booleanQueryImpl = new BooleanQueryImpl();

		booleanQueryImpl.add(
			new TermQueryImpl("f^eld", "text"), BooleanClauseOccur.MUST);

		return booleanQueryImpl;
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
