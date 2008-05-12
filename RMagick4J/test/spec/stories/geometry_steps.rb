steps_for(:geometry) do
  Given("a new Image sized $width x $height") do |width, height|
    @image = Image.new(width.to_i, height.to_i)
  end
  
  Given("a new Geometry from string $geometry") do |geometry|
    @geometry = Geometry.from_s(geometry)
  end

  When("changing geometry") do
    @image.change_geometry(@geometry) do |cols, rows, image|
      @new_width  = cols
      @new_height = rows
    end
  end

  Then("the new width should be $value") do |value|
    @new_width.should eql(value.to_f)
  end
  
  Then("the new height should be $value") do |value|
    @new_height.should eql(value.to_f)
  end
end