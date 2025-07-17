/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.settings;

import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;

import java.util.Objects;

/**
 * @author Iván Zaera
 * @author Jorge Ferrer
 * @author Drew Brokke
 */
public class CompanyServiceSettingsLocator implements SettingsLocator {

	public CompanyServiceSettingsLocator(long companyId, String settingsId) {
		this(companyId, settingsId, settingsId);
	}

	public CompanyServiceSettingsLocator(
		long companyId, String settingsId, String configurationPid) {

		_companyId = companyId;
		_settingsId = settingsId;
		_configurationPid = configurationPid;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof CompanyServiceSettingsLocator)) {
			return false;
		}

		CompanyServiceSettingsLocator companyServiceSettingsLocator =
			(CompanyServiceSettingsLocator)object;

		if (Objects.equals(
				_companyId, companyServiceSettingsLocator._companyId) &&
			Objects.equals(
				_configurationPid,
				companyServiceSettingsLocator._configurationPid) &&
			Objects.equals(
				_settingsId, companyServiceSettingsLocator._settingsId)) {

			return true;
		}

		return false;
	}

	@Override
	public Settings getSettings() throws SettingsException {
		SystemSettingsLocator systemSettingsLocator = new SystemSettingsLocator(
			_configurationPid);

		Settings portalPreferencesSettings =
			_settingsLocatorHelper.getPortalPreferencesSettings(
				_companyId, systemSettingsLocator.getSettings());

		Settings companyConfigurationBeanSettings =
			_settingsLocatorHelper.getCompanyConfigurationBeanSettings(
				_companyId, _configurationPid, portalPreferencesSettings);

		return _settingsLocatorHelper.getCompanyPortletPreferencesSettings(
			_companyId, _settingsId, companyConfigurationBeanSettings);
	}

	@Override
	public String getSettingsId() {
		return _settingsId;
	}

	@Override
	public int hashCode() {
		int hash = HashUtil.hash(0, _companyId);

		hash = HashUtil.hash(hash, _configurationPid);
		hash = HashUtil.hash(hash, _settingsId);

		return hash;
	}

	@Override
	public String toString() {
		return StringBundler.concat(
			"{companyId=", _companyId, ", configurationPid=", _configurationPid,
			", settingsId=", _settingsId, "}");
	}

	private final long _companyId;
	private final String _configurationPid;
	private final String _settingsId;
	private final SettingsLocatorHelper _settingsLocatorHelper =
		SettingsLocatorHelperUtil.getSettingsLocatorHelper();

}