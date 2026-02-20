/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.search.spi.model.result.contributor;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.spi.model.result.contributor.ModelSummaryContributor;

import java.util.Locale;

/**
 * @author Bryan Engler
 * @author Joshua Cords
 */
public class ObjectEntryModelSummaryContributor
	implements ModelSummaryContributor {

	@Override
	public Summary getSummary(
		Document document, Locale locale, String snippet) {

		String defaultLanguageId = document.get("defaultLanguageId");

		return new Summary(
			_getTitle(defaultLanguageId, document, locale),
			_getContent(defaultLanguageId, document, locale));
	}

	private String _getContent(
		String defaultLanguageId, Document document, Locale locale) {

		String localizedFieldName = Field.getLocalizedName(
			locale, _OBJECT_ENTRY_CONTENT);

		String localizedSnippetFieldName = StringBundler.concat(
			Field.SNIPPET, StringPool.UNDERLINE, localizedFieldName);

		String content = document.get(localizedSnippetFieldName);

		if (Validator.isBlank(content)) {
			String localizedContent = document.get(localizedFieldName);

			if (Validator.isNotNull(localizedContent)) {
				content = StringUtil.shorten(
					localizedContent, 300, StringPool.TRIPLE_PERIOD);
			}
		}

		if (Validator.isBlank(content) &&
			!Validator.isBlank(defaultLanguageId)) {

			String defaultLocalizedFieldName = Field.getLocalizedName(
				defaultLanguageId, _OBJECT_ENTRY_CONTENT);

			String defaultLocalizedSnippetFieldName = StringBundler.concat(
				Field.SNIPPET, StringPool.UNDERLINE, defaultLocalizedFieldName);

			content = document.get(defaultLocalizedSnippetFieldName);

			if (Validator.isBlank(content)) {
				String localizedContent = document.get(
					defaultLocalizedFieldName);

				if (Validator.isNotNull(localizedContent)) {
					content = StringUtil.shorten(
						localizedContent, 300, StringPool.TRIPLE_PERIOD);
				}
			}
		}

		if (Validator.isBlank(content)) {
			content = document.get(
				StringBundler.concat(
					Field.SNIPPET, StringPool.UNDERLINE,
					_OBJECT_ENTRY_CONTENT));
		}

		if (Validator.isBlank(content)) {
			String nonlocalizedContent = document.get(_OBJECT_ENTRY_CONTENT);

			if (Validator.isNotNull(nonlocalizedContent)) {
				content = StringUtil.shorten(
					nonlocalizedContent, 300, StringPool.TRIPLE_PERIOD);
			}
		}

		return content;
	}

	private String _getTitle(
		String defaultLanguageId, Document document, Locale locale) {

		String localizedFieldName = Field.getLocalizedName(
			locale, _OBJECT_ENTRY_TITLE);

		String localizedSnippetFieldName = StringBundler.concat(
			Field.SNIPPET, StringPool.UNDERLINE, localizedFieldName);

		String title = document.get(localizedSnippetFieldName);

		if (Validator.isBlank(title)) {
			title = document.get(localizedFieldName);
		}

		if (Validator.isBlank(title) && !Validator.isBlank(defaultLanguageId)) {
			String defaultLocalizedFieldName = Field.getLocalizedName(
				defaultLanguageId, _OBJECT_ENTRY_TITLE);

			String defaultLocalizedSnippetFieldName = StringBundler.concat(
				Field.SNIPPET, StringPool.UNDERLINE, defaultLocalizedFieldName);

			title = document.get(defaultLocalizedSnippetFieldName);

			if (Validator.isBlank(title)) {
				title = document.get(defaultLocalizedFieldName);
			}
		}

		if (Validator.isBlank(title)) {
			title = document.get(
				StringBundler.concat(
					Field.SNIPPET, StringPool.UNDERLINE, _OBJECT_ENTRY_TITLE));
		}

		if (Validator.isBlank(title)) {
			title = document.get(_OBJECT_ENTRY_TITLE);
		}

		if (Validator.isBlank(title)) {
			title = document.get(Field.SNIPPET + Field.ENTRY_CLASS_PK);
		}

		if (Validator.isBlank(title)) {
			title = document.get(Field.ENTRY_CLASS_PK);
		}

		return title;
	}

	private static final String _OBJECT_ENTRY_CONTENT = "objectEntryContent";

	private static final String _OBJECT_ENTRY_TITLE = "objectEntryTitle";

}