/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.util;

import com.liferay.dynamic.data.mapping.model.DDMForm;

import java.lang.annotation.Annotation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Marcellus Tavares
 */
public class DDMFormFactory {

	public static DDMForm create(Class<?> clazz) {
		if (!clazz.isAnnotationPresent(_DDM_FORM_ANNOTATION)) {
			throw new IllegalArgumentException(
				"Unsupported class " + clazz.getName());
		}

		return _ddmForms.computeIfAbsent(
			clazz,
			key -> {
				DDMFormFactoryHelper ddmFormFactoryHelper =
					new DDMFormFactoryHelper(clazz);

				return ddmFormFactoryHelper.createDDMForm();
			});
	}

	private static final Class<? extends Annotation> _DDM_FORM_ANNOTATION =
		com.liferay.dynamic.data.mapping.annotations.DDMForm.class;

	private static final Map<Class<?>, DDMForm> _ddmForms =
		new ConcurrentHashMap<>();

}