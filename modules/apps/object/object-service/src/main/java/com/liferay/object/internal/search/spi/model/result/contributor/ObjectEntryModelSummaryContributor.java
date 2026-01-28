/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.search.spi.model.result.contributor;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.spi.model.result.contributor.ModelSummaryContributor;

import java.util.Locale;

/**
 * @author Bryan Engler
 */
public class ObjectEntryModelSummaryContributor
	implements ModelSummaryContributor {

	@Override
	public Summary getSummary(
		Document document, Locale locale, String snippet) {

		return new Summary(
			_getTitle(document, locale), _getContent(document, locale));
	}

	private String _getContent(Document document, Locale locale) {
		String languageId = LanguageUtil.getLanguageId(locale);

		String content = document.get(
			"snippet_objectEntryContent_" + languageId);

		if (Validator.isBlank(content)) {
			String localizedContent = document.get(
				"objectEntryContent_" + languageId);

			if (Validator.isNotNull(localizedContent)) {
				content = StringUtil.shorten(
					localizedContent, 300, StringPool.TRIPLE_PERIOD);
			}
		}

		if (Validator.isBlank(content)) {
			content = document.get("snippet_objectEntryContent");
		}

		if (Validator.isBlank(content)) {
			String nonlocalizedContent = document.get("objectEntryContent");

			if (Validator.isNotNull(nonlocalizedContent)) {
				content = StringUtil.shorten(
					nonlocalizedContent, 300, StringPool.TRIPLE_PERIOD);
			}
		}

		return content;
	}

	private String _getTitle(Document document, Locale locale) {
		String title = document.get(
			"snippet_objectEntryTitle_" + LanguageUtil.getLanguageId(locale));

		if (Validator.isBlank(title)) {
			title = document.get(
				"objectEntryTitle_" + LanguageUtil.getLanguageId(locale));
		}

		if (Validator.isBlank(title)) {
			title = document.get("snippet_objectEntryTitle");
		}

		if (Validator.isBlank(title)) {
			title = document.get("objectEntryTitle");
		}

		if (Validator.isBlank(title)) {
			title = document.get("snippet_" + Field.ENTRY_CLASS_PK);
		}

		if (Validator.isBlank(title)) {
			title = document.get(Field.ENTRY_CLASS_PK);
		}

		return title;
	}

}