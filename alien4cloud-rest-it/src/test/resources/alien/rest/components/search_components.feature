Feature: Components Search

Background:
  Given I am authenticated with "COMPONENTS_MANAGER" role
  And There is 10 "node types" with base name "" indexed in ALIEN
  And There is 10 "relationship types" with base name "" indexed in ALIEN
  And There is 10 "capability types" with base name "" indexed in ALIEN

Scenario: Search for All
  When I search for " " from 0 with result size of 30
  Then I should receive a RestResponse with no error
    And The response should contains 30 elements from various types.

Scenario: Search for Nodes should return the expected number of results.
  Given There is 20 "node types" with base name "" indexed in ALIEN
  When I search for "node types" from 0 with result size of 20
  Then I should receive a RestResponse with no error
    And The response should contains 20 "node types".

Scenario: Search for Nodes with filter on capabilities should return only matching data.
  Given There is 20 "node types" indexed in ALIEN with 10 of them having a "test.Capability" "capability"
  When I search for "node types" from 0 with result size of 20 and filter "capabilities.type" set to "test.Capability"
  Then I should receive a RestResponse with no error
    And The response should contains 10 "node types".
    And The "node types" in the response should all have the "test.Capability" "capability"

Scenario: Search for Nodes with filter on requirements should return only matching data.
  Given There is 20 "node types" indexed in ALIEN with 10 of them having a "test.Requirement" "requirement"
  When I search for "node types" from 0 with result size of 20 and filter "requirements.type" set to "test.Requirement"
  Then I should receive a RestResponse with no error
    And The response should contains 10 "node types".
    And The "node types" in the response should all have the "test.Requirement" "requirement"

Scenario: Search for Nodes with filter on a default capability should return only matching data.
  Given There is 20 "node types" indexed in ALIEN with 1 of them having a "test.Capability" "default capability"
  When I search for "node types" from 0 with result size of 20 and filter "defaultCapabilities" set to "test.Capability"
  Then I should receive a RestResponse with no error
    And The response should contains 1 "node types".

Scenario: Search for Relationships should return the expected number of results.
  Given There is 20 "relationship types" with base name "" indexed in ALIEN
  When I search for "relationship types" from 0 with result size of 20
  Then I should receive a RestResponse with no error
    And The response should contains 20 "relationship types".

Scenario: Search for Relationships with filter on a validSources should return only matching data.
  Given There is 20 "relationship types" indexed in ALIEN with 5 of them having a "test.ValidSource" "validSources"
  When I search for "relationship types" from 0 with result size of 20 and filter "validSources" set to "test.ValidSource"
  Then I should receive a RestResponse with no error
    And The response should contains 5 "relationship types".
    And The "relationship types" in the response should all have the "test.ValidSource" "validSource"

Scenario: Search for Capability should return the expected number of results.
  Given There is 20 "capability types" with base name "" indexed in ALIEN
  When I search for "capability types" from 0 with result size of 20
  Then I should receive a RestResponse with no error
    And The response should contains 20 "capability types".

Scenario: Searching for next elements should return other elements than first request.
  Given There is 20 "node types" with base name "" indexed in ALIEN
    And I have already made a query to search the 10 first "node types"
  When I search for "node types" from 10 with result size of 10
  Then I should receive a RestResponse with no error
    And The response should contains 10 other "node types".