/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.logging;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.DocumentImpl;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.IndexWriter;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.search.opensearch2.internal.OpenSearchIndexWriter;
import com.liferay.portal.search.opensearch2.internal.OpenSearchTestRule;
import com.liferay.portal.search.opensearch2.internal.connection.TestOpenSearchConnectionManager;
import com.liferay.portal.search.opensearch2.internal.indexing.LiferayOpenSearchIndexingFixtureFactory;
import com.liferay.portal.search.opensearch2.internal.search.engine.adapter.document.BulkDocumentRequestExecutor;
import com.liferay.portal.search.test.util.indexing.BaseIndexingTestCase;
import com.liferay.portal.search.test.util.indexing.DocumentCreationHelpers;
import com.liferay.portal.search.test.util.indexing.IndexingFixture;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Bryan Engler
 */
public class OpenSearchIndexWriterLogExceptionsOnlyTest
	extends BaseIndexingTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@ClassRule
	public static OpenSearchTestRule openSearchTestRule =
		OpenSearchTestRule.INSTANCE;

	@Test
	public void testAddDocument() throws Exception {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				OpenSearchIndexWriter.class.getName(), LoggerTestUtil.ERROR)) {

			addDocument(
				DocumentCreationHelpers.singleKeyword(
					Field.EXPIRATION_DATE, "text"));

			_assertLogCapture(
				logCapture,
				"failed to parse field [expirationDate] of type [date]");
		}
	}

	@Test
	public void testAddDocuments() {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				OpenSearchIndexWriter.class.getName(), LoggerTestUtil.ERROR)) {

			List<Document> documents = new ArrayList<>();

			Document document = new DocumentImpl();

			document.addKeyword(Field.EXPIRATION_DATE, "text");

			documents.add(document);

			IndexWriter indexWriter = getIndexWriter();

			try {
				indexWriter.addDocuments(createSearchContext(), documents);
			}
			catch (SearchException searchException) {
				if (_log.isDebugEnabled()) {
					_log.debug(searchException);
				}
			}

			_assertLogCapture(logCapture, "Bulk add failed");
		}
	}

	@Test
	public void testAddDocumentsBulkExecutor() {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				BulkDocumentRequestExecutor.class.getName(),
				LoggerTestUtil.ERROR)) {

			List<Document> documents = new ArrayList<>();

			Document document = new DocumentImpl();

			document.addKeyword(Field.EXPIRATION_DATE, "text");

			documents.add(document);

			IndexWriter indexWriter = getIndexWriter();

			try {
				indexWriter.addDocuments(createSearchContext(), documents);
			}
			catch (SearchException searchException) {
				if (_log.isDebugEnabled()) {
					_log.debug(searchException);
				}
			}

			_assertLogCapture(
				logCapture,
				"failed to parse field [expirationDate] of type [date]");
		}
	}

	@Test
	public void testCommit() {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				OpenSearchIndexWriter.class.getName(), LoggerTestUtil.ERROR)) {

			SearchContext searchContext = new SearchContext();

			searchContext.setCompanyId(1);

			IndexWriter indexWriter = getIndexWriter();

			try {
				indexWriter.commit(searchContext);
			}
			catch (SearchException searchException) {
				if (_log.isDebugEnabled()) {
					_log.debug(searchException);
				}
			}

			_assertLogCapture(logCapture, "no such index");
		}
	}

	@Test
	public void testDeleteDocument() {
		SearchContext searchContext = new SearchContext();

		searchContext.setCompanyId(1);

		IndexWriter indexWriter = getIndexWriter();

		try {
			indexWriter.deleteDocument(searchContext, "1");
		}
		catch (SearchException searchException) {
			if (_log.isDebugEnabled()) {
				_log.debug(searchException);
			}
		}
	}

	@Test
	public void testDeleteDocumentInfoLevel() {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				OpenSearchIndexWriter.class.getName(), LoggerTestUtil.INFO)) {

			SearchContext searchContext = new SearchContext();

			searchContext.setCompanyId(1);

			IndexWriter indexWriter = getIndexWriter();

			try {
				indexWriter.deleteDocument(searchContext, "1");
			}
			catch (SearchException searchException) {
				if (_log.isDebugEnabled()) {
					_log.debug(searchException);
				}
			}

			_assertLogCapture(logCapture, "no such index");
		}
	}

	@Test
	public void testDeleteDocuments() {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				OpenSearchIndexWriter.class.getName(), LoggerTestUtil.ERROR)) {

			SearchContext searchContext = new SearchContext();

			searchContext.setCompanyId(1);

			List<String> uids = new ArrayList<>();

			uids.add("1");

			IndexWriter indexWriter = getIndexWriter();

			try {
				indexWriter.deleteDocuments(searchContext, uids);
			}
			catch (SearchException searchException) {
				if (_log.isDebugEnabled()) {
					_log.debug(searchException);
				}
			}

			_assertLogCapture(logCapture, "Bulk delete failed");
		}
	}

	@Test
	public void testDeleteDocumentsBulkExecutor() {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				BulkDocumentRequestExecutor.class.getName(),
				LoggerTestUtil.ERROR)) {

			SearchContext searchContext = new SearchContext();

			searchContext.setCompanyId(1);

			List<String> uids = new ArrayList<>();

			uids.add("1");

			IndexWriter indexWriter = getIndexWriter();

			try {
				indexWriter.deleteDocuments(searchContext, uids);
			}
			catch (SearchException searchException) {
				if (_log.isDebugEnabled()) {
					_log.debug(searchException);
				}
			}

			_assertLogCapture(logCapture, "no such index");
		}
	}

	@Test
	public void testDeleteEntityDocuments() {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				OpenSearchIndexWriter.class.getName(), LoggerTestUtil.ERROR)) {

			SearchContext searchContext = new SearchContext();

			searchContext.setCompanyId(1);

			IndexWriter indexWriter = getIndexWriter();

			try {
				indexWriter.deleteEntityDocuments(searchContext, "test");
			}
			catch (SearchException searchException) {
				if (_log.isDebugEnabled()) {
					_log.debug(searchException);
				}
			}

			_assertLogCapture(logCapture, "no such index");
		}
	}

	@Test
	public void testPartiallyUpdateDocument() {
		Document document = new DocumentImpl();

		document.addKeyword(Field.UID, "1");

		IndexWriter indexWriter = getIndexWriter();

		try {
			indexWriter.partiallyUpdateDocument(createSearchContext(), document);
		}
		catch (SearchException searchException) {
			if (_log.isDebugEnabled()) {
				_log.debug(searchException);
			}
		}
	}

	@Test
	public void testPartiallyUpdateDocuments() {
		Document document = new DocumentImpl();

		List<Document> documents = new ArrayList<>();

		document.addKeyword(Field.UID, "1");

		documents.add(document);

		IndexWriter indexWriter = getIndexWriter();

		try {
			indexWriter.partiallyUpdateDocuments(
				createSearchContext(), documents);
		}
		catch (SearchException searchException) {
			if (_log.isDebugEnabled()) {
				_log.debug(searchException);
			}
		}
	}

	@Test
	public void testPartiallyUpdateDocumentsBulkExecutor() {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				BulkDocumentRequestExecutor.class.getName(),
				LoggerTestUtil.ERROR)) {

			Document document = new DocumentImpl();

			List<Document> documents = new ArrayList<>();

			document.addKeyword(Field.UID, "1");

			documents.add(document);

			IndexWriter indexWriter = getIndexWriter();

			try {
				indexWriter.partiallyUpdateDocuments(
					createSearchContext(), documents);
			}
			catch (SearchException searchException) {
				if (_log.isDebugEnabled()) {
					_log.debug(searchException);
				}
			}

			_assertLogCapture(logCapture, "document missing");
		}
	}

	@Test
	public void testUpdateDocument() {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				OpenSearchIndexWriter.class.getName(), LoggerTestUtil.ERROR)) {

			Document document = new DocumentImpl();

			document.addKeyword(Field.EXPIRATION_DATE, "text");
			document.addKeyword(Field.UID, "1");

			IndexWriter indexWriter = getIndexWriter();

			try {
				indexWriter.updateDocument(createSearchContext(), document);
			}
			catch (SearchException searchException) {
				if (_log.isDebugEnabled()) {
					_log.debug(searchException);
				}
			}

			_assertLogCapture(logCapture, "Update failed");
		}
	}

	@Test
	public void testUpdateDocumentBulkExecutor() {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				BulkDocumentRequestExecutor.class.getName(),
				LoggerTestUtil.ERROR)) {

			Document document = new DocumentImpl();

			document.addKeyword(Field.EXPIRATION_DATE, "text");
			document.addKeyword(Field.UID, "1");

			IndexWriter indexWriter = getIndexWriter();

			try {
				indexWriter.updateDocument(createSearchContext(), document);
			}
			catch (SearchException searchException) {
				if (_log.isDebugEnabled()) {
					_log.debug(searchException);
				}
			}

			_assertLogCapture(
				logCapture,
				"failed to parse field [expirationDate] of type [date]");
		}
	}

	@Test
	public void testUpdateDocuments() {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				OpenSearchIndexWriter.class.getName(), LoggerTestUtil.ERROR)) {

			List<Document> documents = new ArrayList<>();

			Document document = new DocumentImpl();

			document.addKeyword(Field.EXPIRATION_DATE, "text");
			document.addKeyword(Field.UID, "1");

			documents.add(document);

			IndexWriter indexWriter = getIndexWriter();

			try {
				indexWriter.updateDocuments(createSearchContext(), documents);
			}
			catch (SearchException searchException) {
				if (_log.isDebugEnabled()) {
					_log.debug(searchException);
				}
			}

			_assertLogCapture(logCapture, "Bulk update failed");
		}
	}

	@Test
	public void testUpdateDocumentsBulkExecutor() {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				BulkDocumentRequestExecutor.class.getName(),
				LoggerTestUtil.ERROR)) {

			List<Document> documents = new ArrayList<>();

			Document document = new DocumentImpl();

			document.addKeyword(Field.EXPIRATION_DATE, "text");
			document.addKeyword(Field.UID, "1");

			documents.add(document);

			IndexWriter indexWriter = getIndexWriter();

			try {
				indexWriter.updateDocuments(createSearchContext(), documents);
			}
			catch (SearchException searchException) {
				if (_log.isDebugEnabled()) {
					_log.debug(searchException);
				}
			}

			_assertLogCapture(
				logCapture,
				"failed to parse field [expirationDate] of type [date]");
		}
	}

	@Override
	protected IndexingFixture createIndexingFixture() {
		return LiferayOpenSearchIndexingFixtureFactory.builder(
		).testOpenSearchConnectionManager(
			new TestOpenSearchConnectionManager(
				HashMapBuilder.<String, Object>put(
					"logExceptionsOnly", true
				).build())
		).build();
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

	private static final Log _log = LogFactoryUtil.getLog(
		OpenSearchIndexWriterLogExceptionsOnlyTest.class);

}
