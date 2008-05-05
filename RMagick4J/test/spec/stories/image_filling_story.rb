Story: filling a background
  As a developer
  I want to fill an empty image with a GradientFill
  So that I can have a beautiful gradient in it.
  
  # Vertical line (GradientFill)
  Scenario: the starting line is the left border
    Given a new GradientFill with starting line 0 0 0 100 with start color: 900 and end color: 000
    When filling a new image sized 300 100 with the Fill object
    Then the new image should not be nil

  # Radial gradient (GradientFill)
  Scenario: the starting point is the 0 0
    Given a new GradientFill with starting point 0 0 with start color: 900 and end color: 000
    When filling a new image sized 300 100 with the Fill object
    Then the new image should not be nil
  
  # Horizontal line (GradientFill)
  Scenario: the starting line is an horizontal one
    Given a new GradientFill with starting line 0 150 200 150 with start color: 900 and end color: 000
    When filling a new image sized 300 300 with the Fill object
    Then the new image should not be nil
  
  # Horizontal_diagonal line (GradientFill)
  Scenario: the starting line is a horizontal_diagonal one.
    Given a new GradientFill with starting line 100 0 200 300 with start color: 900 and end color: 000
    When filling a new image sized 300 300 with the Fill object
    Then the new image should not be nil
    
  # Vertical_diagonal line (GradientFill)
  Scenario: the starting line is a vertical_diagonal one.
    Given a new GradientFill with starting line 0 100 300 200 with start color: 900 and end color: 000
    When filling a new image sized 300 300 with the Fill object
    Then the new image should not be nil
    
  # Texture filling (TextureFill)
  Scenario: filling an image with a texture.
    Given a new Image texture with extension jpg
    And a new TextureFill from image
    When filling a new image sized 300 300 with the Fill object
    Then the new image should not be nil