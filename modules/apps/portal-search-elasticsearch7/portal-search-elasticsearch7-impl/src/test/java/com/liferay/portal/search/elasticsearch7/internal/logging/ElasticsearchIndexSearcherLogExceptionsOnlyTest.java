/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.logging;

import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.search.generic.TermQueryImpl;
import com.liferay.portal.search.elasticsearch7.internal.ElasticsearchIndexSearcher;
import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchConnectionFixture;
import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchFixture;
import com.liferay.portal.search.elasticsearch7.internal.indexing.LiferayElasticsearchIndexingFixtureFactory;
import com.liferay.portal.search.test.util.indexing.BaseIndexingTestCase;
import com.liferay.portal.search.test.util.indexing.IndexingFixture;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collections;
import java.util.List;

import org.elasticsearch.ElasticsearchStatusException;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Bryan Engler
 */
public class ElasticsearchIndexSearcherLogExceptionsOnlyTest
	extends BaseIndexingTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testExceptionOnlyLoggedWhenQueryMalformedSearch() {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				ElasticsearchIndexSearcher.class.getName(),
				LoggerTestUtil.ERROR)) {

			search(createSearchContext(), getMalformedQuery());

			_assertLogCapture(logCapture);
		}
	}

	@Test
	public void testExceptionOnlyLoggedWhenQueryMalformedSearchCount() {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				ElasticsearchIndexSearcher.class.getName(),
				LoggerTestUtil.ERROR)) {

			searchCount(createSearchContext(), getMalformedQuery());

			_assertLogCapture(logCapture);
		}
	}

	protected ElasticsearchConnectionFixture
		createElasticsearchConnectionFixture() {

		return ElasticsearchConnectionFixture.builder(
		).clusterName(
			ElasticsearchIndexWriterLogExceptionsOnlyTest.class.getSimpleName()
		).elasticsearchConfigurationProperties(
			Collections.singletonMap("logExceptionsOnly", true)
		).build();
	}

	@Override
	protected IndexingFixture createIndexingFixture() {
		return LiferayElasticsearchIndexingFixtureFactory.builder(
		).elasticsearchFixture(
			new ElasticsearchFixture(createElasticsearchConnectionFixture())
		).build();
	}

	protected Query getMalformedQuery() {
		BooleanQueryImpl booleanQueryImpl = new BooleanQueryImpl();

		booleanQueryImpl.add(
			new TermQueryImpl(Field.EXPIRATION_DATE, "text"),
			BooleanClauseOccur.MUST);

		return booleanQueryImpl;
	}

	private void _assertLogCapture(LogCapture logCapture) {
		List<LogEntry> logEntries = logCapture.getLogEntries();

		Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

		LogEntry logEntry = logEntries.get(0);

		Assert.assertEquals(LoggerTestUtil.ERROR, logEntry.getPriority());

		Throwable throwable = logEntry.getThrowable();

		Assert.assertSame(
			ElasticsearchStatusException.class, throwable.getClass());
		Assert.assertEquals(
			"Elasticsearch exception [type=search_phase_execution_exception, " +
				"reason=all shards failed]",
			throwable.getMessage());
	}

}