Story: filling a background
  As a developer
  I want to fill an empty image with a GradientFill
  So that I can have a beautiful gradient in it.
  
  # Vertical line
  Scenario: the starting line is the left border
    Given a new Gradient fill with starting line 0 0 0 100 with start color: 900 and end color: 000
    When filling a new image sized 300 100 with the GradientFill
    Then the new image should not be nil

  # Radial gradient
  Scenario: the starting point is the 0 0
    Given a new Gradient fill with starting point 0 0 with start color: 900 and end color: 000
    When filling a new image sized 300 100 with the GradientFill
    Then the new image should not be nil
  
  # Horizontal line
  Scenario: the starting line is an horizontal one
    Given a new Gradient fill with starting line 0 150 200 150 with start color: 900 and end color: 000
    When filling a new image sized 300 300 with the GradientFill
    Then the new image should not be nil
  
  # Diagonal line
  # TODO: Split in two: diagonal vertical & diagonal horizontal
  Scenario: the starting line is a diagonal one.
    Given a new Gradient fill with starting line 0 0 300 200 with start color: 900 and end color: 000
    When filling a new image sized 300 300 with the GradientFill
    Then the new image should not be nil