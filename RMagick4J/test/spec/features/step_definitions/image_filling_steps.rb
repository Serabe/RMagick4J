Given /a new GradientFill with starting line (\d+) (\d+) (\d+) (\d+) with start color: (.*) and end color: (.*)/ do |x1, y1, x2, y2, start_color, end_color|
  @fill = GradientFill.new(x1.to_i, y1.to_i, x2.to_i, y2.to_i, '#'+start_color, '#'+end_color)
end

Given /a new GradientFill with starting point (\d+) (\d+) with start color: (.*) and end color: (.*)/ do |x, y, start_color, end_color|
  @fill = GradientFill.new(x.to_i, y.to_i, x.to_i, y.to_i, '#'+start_color, '#'+end_color)
end

Given /a new Image (.*) with extension (.*)/ do |image_name, extension|
  @image = Image.read(File.join(File.dirname(__FILE__), '..', '..', '..', 'images', image_name+'.'+extension))
end

Given /a new TextureFill from image/ do
  @fill = TextureFill.new(@image.first)
end

When /filling a new image sized (\d+) (\d+) with the Fill object/ do |columns, rows|
  begin
    @image = Image.new(columns.to_i, rows.to_i, @fill)
  rescue
    @image = nil
  end
end

Then /the new image should not be (.*)/ do |value|
  expected_value =  case value
                    when 'nil': nil
                    when 'false': false
                    when 'true': true
                    when value.to_i.to_s: value.to_i
                    when value.to_f.to_s: value.to_f
                    else
                      value
                    end
  @image.should_not be expected_value
end

Then /the new image should be (.*)/ do |value|
  expected_value =  case value
                    when 'nil': nil
                    when 'false': false
                    when 'true': true
                    when value.to_i.to_s: value.to_i
                    when value.to_f.to_s: value.to_f
                    else
                      value
                    end
  @image.should be expected_value
end
