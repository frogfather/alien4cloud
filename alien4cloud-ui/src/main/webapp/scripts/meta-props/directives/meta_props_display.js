define(function (require) {
  'use strict';

  var modules = require('modules');

  require('scripts/meta-props/controllers/meta_props_edit');

  modules.get('a4c-metas').directive('metaPropertiesDisplay', function () {
    return {
      templateUrl: 'views/meta-props/meta_properties_display.html',
      restrict: 'E',
      scope: {
        'properties': '=',
        'application': '=',
        'cloud': '=',
        'collapse': '=',
      },
      link: {}
    };
  }); // directive
}); // define
