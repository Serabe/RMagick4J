Story: filling a background
  As a developer
  I want to fill an empty image with a GradientFill
  So that I can have a beautiful gradient in it.
  
  Scenario: the starting line is the left border
    Given a new Gradient fill with starting line 0 0 0 100 with start color: 900 and end color: 000
    When filling a new image sized 300 100 with the GradientFill
    Then the new image should not be nil