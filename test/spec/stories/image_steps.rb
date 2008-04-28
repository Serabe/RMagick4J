steps_for(:image) do
  Given("a new Gradient fill with starting line ($x1 $y1 $x2 y2) with start color: $start_color and end color: $end_color") do |x1, y1, x2, y2, start_color, end_color|
    @fill = GradientFill.new(x1.to_i, y1.to_i, x2.to_i, y2.to_i, start_color, end_color)
  end

  When("filling a new image sized $columns $rows with the GradientFill") do |columns,rows|
    @image = Image.new(columns.to_i, rows.to_i, @fill)
  end

  Then("then the new image should not be nil") do
    !@image.nil?
  end
end
