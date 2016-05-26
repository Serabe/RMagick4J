Feature: changing geometry
  As a developer
  I want to resize an image using a geometry
  So that I can do some cool stuff.
  
  Scenario: the geometry is a simple one, and the ratios are both >1
    Given a new Image sized 600 x 300
    And a new Geometry from string 300x200
    When changing geometry
    Then the new width should be 300
    And the new height should be 150
    
  Scenario: the geometry has a percentage flag, and the ratios are both >1
    Given a new Image sized 600 x 300
    And a new Geometry from string 100x70%
    When changing geometry
    Then the new width should be 600
    And the new height should be 210
    
  Scenario: the geometry has an aspect flag, and the ratios are both >1
    Given a new Image sized 600 x 300
    And a new Geometry from string 100x70!
    When changing geometry
    Then the new width should be 100
    And the new height should be 70
    
  Scenario: the geometry has a less flag, and the ratios are both >1, (smaller)
    Given a new Image sized 150 x 125
    And a new Geometry from string 300x200<
    When changing geometry
    Then the new width should be 240
    And the new height should be 200
    
  Scenario: the geometry has a less flag, and the ratios are both >1, (bigger)
    Given a new Image sized 600 x 300
    And a new Geometry from string 300x200<
    When changing geometry
    Then the new width should be 600
    And the new height should be 300
    
  Scenario: the geometry has a greater flag, and the ratios are both >1, (smaller)
    Given a new Image sized 150 x 125
    And a new Geometry from string 300x200>
    When changing geometry
    Then the new width should be 150
    And the new height should be 125
    
  Scenario: the geometry has a greater flag, and the ratios are both >1, (bigger)
    Given a new Image sized 600 x 300
    And a new Geometry from string 300x200>
    When changing geometry
    Then the new width should be 300
    And the new height should be 150
    
  Scenario: the geometry has an area flag, and the ratio is >1
    Given a new Image sized 600 x 300
    And a new Geometry from string 300@
    When changing geometry
    Then the new width should be 24
    And the new height should be 12
    
  Scenario: the geometry is a simple one, and the ratios are mixed
    Given a new Image sized 300 x 600
    And a new Geometry from string 300x200
    When changing geometry
    Then the new width should be 100
    And the new height should be 200
    
  Scenario: the geometry has a percentage flag, and the ratios are mixed
    Given a new Image sized 300 x 600
    And a new Geometry from string 100x70%
    When changing geometry
    Then the new width should be 300
    And the new height should be 420
    
  Scenario: the geometry has an aspect flag, and the ratios are mixed
    Given a new Image sized 300 x 600
    And a new Geometry from string 100x70!
    When changing geometry
    Then the new width should be 100
    And the new height should be 70
    
  Scenario: the geometry has a less flag, and the ratios are mixed, (smaller)
    Given a new Image sized 125 x 150
    And a new Geometry from string 300x200<
    When changing geometry
    Then the new width should be 167
    And the new height should be 200
    
  Scenario: the geometry has a less flag, and the ratios are mixed, (bigger)
    Given a new Image sized 400 x 600
    And a new Geometry from string 300x200<
    When changing geometry
    Then the new width should be 400
    And the new height should be 600
    
  Scenario: the geometry has a greater flag, and the ratios are mixed, (smaller)
    Given a new Image sized 125 x 150
    And a new Geometry from string 300x200>
    When changing geometry
    Then the new width should be 125
    And the new height should be 150
    
  Scenario: the geometry has a greater flag, and the ratios are mixed, (bigger)
    Given a new Image sized 400 x 600
    And a new Geometry from string 300x200>
    When changing geometry
    Then the new width should be 133
    And the new height should be 200
    
  Scenario: the geometry has an area flag, and the ratio is <1
    Given a new Image sized 300 x 600
    And a new Geometry from string 300@
    When changing geometry
    Then the new width should be 12
    And the new height should be 24

  Scenario: the geometry is a simple one, and the ratios are both >1
    Given a new Image sized 600 x 300
    And a new Geometry from string 300
    When changing geometry
    Then the new width should be 300
    And the new height should be 150

  Scenario: the geometry is a simple one, and the ratios are both >1
    Given a new Image sized 600 x 300
    And a new Geometry from string x150
    When changing geometry
    Then the new width should be 300
    And the new height should be 150

  Scenario: the geometry has a less flag, and the ratios are both >1
    Given a new Image sized 600 x 300
    And a new Geometry from string 700x<
    When changing geometry
    Then the new width should be 700
    And the new height should be 350

  Scenario: the geometry has a less flag, and the ratios are both >1
    Given a new Image sized 600 x 300
    And a new Geometry from string 500x<
    When changing geometry
    Then the new width should be 600
    And the new height should be 300

  Scenario: the geometry has a less flag, and the ratios are both >1
    Given a new Image sized 600 x 300
    And a new Geometry from string x350<
    When changing geometry
    Then the new width should be 700
    And the new height should be 350

  Scenario: the geometry has a less flag, and the ratios are both >1
    Given a new Image sized 600 x 300
    And a new Geometry from string x200<
    When changing geometry
    Then the new width should be 600
    And the new height should be 300

  Scenario: the geometry has a percentage flag, and the ratios are both >1
    Given a new Image sized 600 x 300
    And a new Geometry from string 50%
    When changing geometry
    Then the new width should be 300
    And the new height should be 150
  
  Scenario: the geometry has a percentage flag, and the ratios are both >1
    Given a new Image sized 600 x 300
    And a new Geometry from string x50%
    When changing geometry
    Then the new width should be 300
    And the new height should be 150

  Scenario: the geometry has a greater flag, and the ratios are both >1
    Given a new Image sized 600 x 300
    And a new Geometry from string 500x>
    When changing geometry
    Then the new width should be 500
    And the new height should be 250

  Scenario: the geometry has a greater flag, and the ratios are both >1
    Given a new Image sized 600 x 300
    And a new Geometry from string 700x>
    When changing geometry
    Then the new width should be 600
    And the new height should be 300

  Scenario: the geometry has a greater flag, and the ratios are both >1
    Given a new Image sized 600 x 300
    And a new Geometry from string x250>
    When changing geometry
    Then the new width should be 500
    And the new height should be 250

  Scenario: the geometry has a greater flag, and the ratios are both >1
    Given a new Image sized 600 x 300
    And a new Geometry from string x400>
    When changing geometry
    Then the new width should be 600
    And the new height should be 300

  Scenario: the geometry has an aspect flag, and the ratios are both >1
    Given a new Image sized 600 x 300
    And a new Geometry from string 300!
    When changing geometry
    Then the new width should be 300
    And the new height should be 300

  Scenario: the geometry has an aspect flag, and the ratios are both >1
    Given a new Image sized 600 x 300
    And a new Geometry from string x200!
    When changing geometry
    Then the new width should be 0
    And the new height should be 200

  Scenario: the geometry has an aspect flag, and the ratios are both >1
    Given a new Image sized 600 x 300
    And a new Geometry from string x300@
    When changing geometry
    Then the new width should be 0
    And the new height should be 0
