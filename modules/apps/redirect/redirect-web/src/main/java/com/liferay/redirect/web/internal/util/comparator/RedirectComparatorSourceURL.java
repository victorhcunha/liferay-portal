/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.redirect.web.internal.util.comparator;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.redirect.model.RedirectEntryModel;
import com.liferay.redirect.model.RedirectEntryModel;


import java.util.function.Function;

/**
 * @author Alejandro Tardín
 */
public class RedirectComparatorSourceURL<T extends BaseModel, V extends Comparable<V>>
	extends OrderByComparator<T> {

	private RedirectComparatorSourceURL(boolean ascending) {
		_ascending = ascending;
	}
	public static RedirectComparatorSourceURL getInstance(boolean ascending) {
		if (ascending) {
			return _INSTANCE_ASCENDING;
		}

		return _INSTANCE_DESCENDING;
	}
	@Override
	public int compare(T baseModel1, T baseModel2) {
		V fieldValue1 = _fieldValueFunction.apply(baseModel1);
		V fieldValue2 = _fieldValueFunction.apply(baseModel2);

		int value = fieldValue1.compareTo(fieldValue2);

		if (_ascending) {
			return value;
		}

		return -value;
	}
	@Override
	public String getOrderBy() {
		return StringBundler.concat(
			"RedirectEntry", StringPool.PERIOD, _fieldName, StringPool.SPACE,
			_ascending ? "DESC" : "ASC");
	}

	@Override
	public String[] getOrderByFields() {
		return new String[] {_fieldName};
	}

	@Override
	public boolean isAscending() {
		return _ascending;
	}

	private static final RedirectComparatorSourceURL _INSTANCE_ASCENDING =
		new RedirectComparatorSourceURL(true);

	private static final RedirectComparatorSourceURL _INSTANCE_DESCENDING =
		new RedirectComparatorSourceURL(false);

	private final boolean _ascending;
	private final String _fieldName = "source-url";
	private final Function<T, V> RedirectEntryModel::getSourceURL;

}