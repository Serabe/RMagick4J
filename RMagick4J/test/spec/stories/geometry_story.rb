Story: changing geometry
  As a developer
  I want to resize an image using a geometry
  So that I can do some cool stuff.
  
  Scenario: the geometry is a simple one, and the ratios are both >1
    Given a new Image sized 600 x 300
    And a new Geometry from string 300x200
    When changing geometry
    Then the new width should be 300
    And the new height should be 150
    
  Scenario: the geometry is a PercentGeometry, and the ratios are both >1
    Given a new Image sized 600 x 300
    And a new Geometry from string 100x70%
    When changing geometry
    Then the new width should be 600
    And the new height should be 210