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
    
  Scenario: the geometry is a AspectGeometry, and the ratios are both >1
    Given a new Image sized 600 x 300
    And a new Geometry from string 100x70!
    When changing geometry
    Then the new width should be 100
    And the new height should be 70
    
  Scenario: the geometry is a LessGeometry, and the ratios are both >1, (smaller)
    Given a new Image sized 150 x 125
    And a new Geometry from string 300x200<
    When changing geometry
    Then the new width should be 240
    And the new height should be 200
    
  Scenario: the geometry is a LessGeometry, and the ratios are both >1, (bigger)
    Given a new Image sized 600 x 300
    And a new Geometry from string 300x200<
    When changing geometry
    Then the new width should be 600
    And the new height should be 300
    
  Scenario: the geometry is a GreaterGeometry, and the ratios are both >1, (smaller)
    Given a new Image sized 150 x 125
    And a new Geometry from string 300x200>
    When changing geometry
    Then the new width should be 150
    And the new height should be 125
    
  Scenario: the geometry is a GreaterGeometry, and the ratios are both >1, (bigger)
    Given a new Image sized 600 x 300
    And a new Geometry from string 300x200>
    When changing geometry
    Then the new width should be 300
    And the new height should be 150
    
  Scenario: the geometry is a AreaGeometry, and the ratio is >1
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
    
  Scenario: the geometry is a PercentGeometry, and the ratios are mixed
    Given a new Image sized 300 x 600
    And a new Geometry from string 100x70%
    When changing geometry
    Then the new width should be 300
    And the new height should be 420
    
  Scenario: the geometry is a AspectGeometry, and the ratios are mixed
    Given a new Image sized 300 x 600
    And a new Geometry from string 100x70!
    When changing geometry
    Then the new width should be 100
    And the new height should be 70
    
  Scenario: the geometry is a LessGeometry, and the ratios are mixed, (smaller)
    Given a new Image sized 125 x 150
    And a new Geometry from string 300x200<
    When changing geometry
    Then the new width should be 167
    And the new height should be 200
    
  Scenario: the geometry is a LessGeometry, and the ratios are mixed, (bigger)
    Given a new Image sized 400 x 600
    And a new Geometry from string 300x200<
    When changing geometry
    Then the new width should be 400
    And the new height should be 600
    
  Scenario: the geometry is a GreaterGeometry, and the ratios are mixed, (smaller)
    Given a new Image sized 125 x 150
    And a new Geometry from string 300x200>
    When changing geometry
    Then the new width should be 125
    And the new height should be 150
    
  Scenario: the geometry is a GreaterGeometry, and the ratios are mixed, (bigger)
    Given a new Image sized 400 x 600
    And a new Geometry from string 300x200>
    When changing geometry
    Then the new width should be 133
    And the new height should be 200
    
  Scenario: the geometry is a AreaGeometry, and the ratio is <1
    Given a new Image sized 300 x 600
    And a new Geometry from string 300@
    When changing geometry
    Then the new width should be 12
    And the new height should be 24