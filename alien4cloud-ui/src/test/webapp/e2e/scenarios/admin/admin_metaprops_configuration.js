/* global element, by */
'use strict';

var common = require('../../common/common');
var authentication = require('../../authentication/authentication');
var genericForm = require('../../generic_form/generic_form');
var tagConfigCommon = require('../../admin/metaprops_configuration_common');
var cloudsCommon = require('../../admin/clouds_common');

describe('Tag configuration CRUD', function() {
  beforeEach(function() {
    common.before();
    authentication.login('admin');
  });

  afterEach(function() {
    // Logout action
    authentication.logout();
  });

  it('should be able to add/edit/delete a tag configuration', function() {
    console.log('################# should be able to add/edit/delete a tag configuration');
    tagConfigCommon.addTagConfiguration(tagConfigCommon.maturityTag, tagConfigCommon.tagMaturityValidValuesConstraint);

    var firstTag = tagConfigCommon.getFirstElementInTagList(tagConfigCommon.maturityTag.name.value);
    firstTag.click();

    element(by.id('breadcrumbrootlabel')).click();

    // rename
    genericForm.sendValueToPrimitive('default', 'Bad', false, 'xeditable');
    browser.element(by.id('closeGenericFormButton')).click();

    firstTag = tagConfigCommon.getFirstElementInTagList(tagConfigCommon.maturityTag.name.value);
    firstTag.click();

    genericForm.expectValueFromPrimitive('default', 'Bad', 'xeditable');
    browser.element(by.id('deleteGenericFormButton')).click();

    // count tags array
    var tagConfigurationsTable = element(by.id('tagConfigurationsTable'));
    expect(tagConfigurationsTable.all(by.tagName('tr')).count()).toEqual(0);

  });

  it('should add 2 configuration tags and check tag configurations table', function() {
    console.log('################# should add 2 configuration tags and check tag configurations table');
    // add tags
    tagConfigCommon.createConfigurationTags();

    // count tags array
    var tagConfigurationsTable = element(by.id('tagConfigurationsTable'));
    expect(tagConfigurationsTable.all(by.tagName('tr')).count()).toEqual(2);
  });

  it('should add a cloud meta-property and set <success> as value', function() {
    console.log('################# should add a cloud meta-property and set <success> as value');
    cloudsCommon.goToCloudList();
    cloudsCommon.createNewCloud('testcloud');
    cloudsCommon.goToCloudDetail('testcloud');
    cloudsCommon.goToCloudConfiguration();
    expect(element(by.id('cloudMetaPropertiesDisplay')).isPresent()).toBe(false);

    tagConfigCommon.addTagConfiguration(tagConfigCommon.defaultCloudProperty, null);
    cloudsCommon.goToCloudDetail('testcloud');
    cloudsCommon.goToCloudConfiguration();
    cloudsCommon.showMetaProperties();
    expect(element(by.id('cloudMetaPropertiesDisplay')).isDisplayed()).toBe(true);
    tagConfigCommon.editTagConfiguration('distribution', 'success');
  });

});
