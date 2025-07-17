/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.settings;

import com.liferay.petra.string.StringBundler;

import java.util.Objects;

/**
 * @author Drew Brokke
 */
public class SystemSettingsLocator implements SettingsLocator {

	public SystemSettingsLocator(String configurationPid) {
		_configurationPid = configurationPid;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof SystemSettingsLocator)) {
			return false;
		}

		SystemSettingsLocator systemSettingsLocator =
			(SystemSettingsLocator)object;

		return Objects.equals(
			_configurationPid, systemSettingsLocator._configurationPid);
	}

	@Override
	public Settings getSettings() throws SettingsException {
		return _settingsLocatorHelper.getConfigurationBeanSettings(
			_configurationPid);
	}

	@Override
	public String getSettingsId() {
		return _configurationPid;
	}

	@Override
	public int hashCode() {
		return _configurationPid.hashCode();
	}

	@Override
	public String toString() {
		return StringBundler.concat(
			"{configurationPid=", _configurationPid, "}");
	}

	private final String _configurationPid;
	private final SettingsLocatorHelper _settingsLocatorHelper =
		SettingsLocatorHelperUtil.getSettingsLocatorHelper();

}