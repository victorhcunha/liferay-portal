/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

/**
 * DEMO FILE - live Poshi -> Playwright conversion for a presentation.
 * Not intended to be merged.
 *
 * Original Poshi source (LPS-176844, still present in
 * modules/apps/object/object-test/src/testFunctional/tests/ObjectFields.testcase,
 * never migrated):
 *
 * @description = "Verify that the user is able to add the Formula Field
 * when managing Custom Layout"
 * test CanManageFormulaFieldWithLayout {
 *     task ("Given a custom object with a formula field") {
 *         ObjectAdmin.addObjectViaAPI(...);
 *         ObjectAdmin.addObjectFieldViaAPI(fieldBusinessType = "Decimal", ...);
 *         ObjectAdmin.addObjectFieldViaAPI(
 *             fieldBusinessType = "Formula",
 *             formulaScript = "customDecimalField1 + customDecimalField2",
 *             outputOption = "Decimal", ...);
 *         ObjectAdmin.publishObjectViaAPI(...);
 *     }
 *
 *     task ("Given a layout to the Custom Object") {
 *         ObjectAdmin.addObjectLayoutViaAPI(...);
 *         ObjectAdmin.openObjectAdmin();
 *         ObjectPortlet.selectCustomObject(...);
 *         ObjectAdmin.goToLayoutsTab();
 *         ObjectAdmin.selectKebabMenuOption(kebabOption = "Edit");
 *         ObjectAdmin.markLayoutAsDefault();
 *         ObjectAdmin.goToLayoutTabOnLayouts();
 *         ObjectAdmin.addTabFieldsOnLayout(fieldList = "...", tabName = "Tab Test");
 *         Button.clickSave();
 *     }
 *
 *     task ("When the value of the fields is added") {
 *         ObjectAdmin.goToCustomObject(...);
 *         LexiconEntry.gotoAdd();
 *         ObjectPortlet.typeCustomField(customField = "Custom Decimal Field 1", entry = 1234);
 *         PortletEntry.save();
 *     }
 *
 *     task ("Then assert the value of Formula Field") {
 *         AssertElementPresent(
 *             key_fieldValue = 2468,
 *             locator1 = "ObjectAdmin#VIEW_FIELD_VALUE_DISABLED");
 *     }
 * }
 *
 * Simplified for the demo: a single Decimal input field ("customDecimalField")
 * feeding one Formula field ("customFormulaField" = customDecimalField * 2),
 * instead of the original two-input addition. Same intent: the Formula
 * field's computed value must render, read-only, inside an Object Layout.
 */

import {
	ObjectDefinition,
	ObjectField,
} from '@liferay/object-admin-rest-client-js';
import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import {objectPagesTest} from '../../../fixtures/objectPagesTest';
import getRandomString from '../../../utils/getRandomString';

const test = mergeTests(
	apiHelpersTest,
	dataApiHelpersTest,
	loginTest(),
	objectPagesTest
);

test(
	'formula field value renders inside an object layout',
	{tag: '@LPS-176844'},
	async ({apiHelpers, objectLayoutsPage, page, viewObjectEntriesPage}) => {

		let objectDefinition: ObjectDefinition;

		await test.step(
			'Given a custom object with a decimal field and a formula field',
			async () => {
				objectDefinition =
					await apiHelpers.objectAdmin.postRandomObjectDefinition({
						objectFields: [
							{
								DBType: 'Double',
								businessType: 'Decimal',
								label: {en_US: 'Custom Decimal Field'},
								name: 'customDecimalField',
								required: false,
							},
							{
								DBType: 'String',
								businessType: 'Formula',
								label: {en_US: 'Custom Formula Field'},
								name: 'customFormulaField',
								objectFieldSettings: [
									{name: 'output', value: 'Decimal'} as any,
									{
										name: 'script',
										value: 'customDecimalField * 2',
									} as any,
								],
								required: false,
							},
						] as Partial<ObjectField>[],
						status: {code: 0},
					});

				apiHelpers.data.push({
					id: objectDefinition.id,
					type: 'objectDefinition',
				});
			}
		);

		await test.step(
			'Given an object layout with both fields in a single tab',
			async () => {
				const layoutName = getRandomString();
				const tabName = getRandomString();
				const blockName = getRandomString();

				await objectLayoutsPage.goto(objectDefinition.name);

				await objectLayoutsPage.createObjectLayout(layoutName);

				await objectLayoutsPage.openObjectLayoutConfiguration(
					layoutName
				);

				await objectLayoutsPage.markAsDefaultButton.check();

				await objectLayoutsPage.createObjectLayoutContent({
					objectFieldNames: [
						'Custom Decimal Field',
						'Custom Formula Field',
					],
					objectLayoutName: layoutName,
					objectLayoutRegularBlockName: blockName,
					objectLayoutTabName: tabName,
				});

				await objectLayoutsPage.saveUpdateLayoutButton.click();
			}
		);

		await test.step(
			'When an entry is added with a value for the decimal field',
			async () => {
				await viewObjectEntriesPage.goto(objectDefinition.className);

				await viewObjectEntriesPage.clickAddObjectEntry(
					objectDefinition.label['en_US']
				);

				await page
					.getByLabel('Custom Decimal Field', {exact: true})
					.fill('1234');

				await viewObjectEntriesPage.saveObjectEntryButton.click();
			}
		);

		await test.step(
			'Then the formula field shows the computed value, read-only',
			async () => {
				const formulaFieldInput = page.getByLabel(
					'Custom Formula Field',
					{exact: true}
				);

				await expect(formulaFieldInput).toHaveValue('2468');
				await expect(formulaFieldInput).toBeDisabled();
			}
		);
	}
);
