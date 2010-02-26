require 'rubygems'
require 'gruff'
require File.join(File.dirname(__FILE__), 'new_image.rb')

@datasets = [
  [:large, [100_005, 35_000, 28_000, 27_000]],
  [:large2, [35_000, 28_000, 27_000, 100_005]],
  [:large3, [28_000, 27_000, 100_005, 35_000]],
  [:large4, [1_238, 39_092, 27_938, 48_876]]
  ]

g = Gruff::Line.new
g.title = "Very Large Values Line Graph Test"
g.baseline_value = 50_000
g.baseline_color = 'green'
@datasets.each do |data|
  g.data(data[0], data[1])
end

g.write("gruff_line_2.jpg")
