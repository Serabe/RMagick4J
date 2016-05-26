require 'rubygems'
require 'gruff'
require File.join(File.dirname(__FILE__), 'new_image.rb')

@datasets = [
              [:Darren, [25]],
              [:Chris, [80]],
              [:Egbert, [22]],
              [:Adam, [95]],
              [:Bill, [90]],
              ["Frank", [5]],
              ["Zero", [0]],
            ]
g = Gruff::Pie.new
g.title = "Visual Pie Graph Test"
@datasets.each do |data|
  g.data(data[0], data[1])
end

# Default theme
g.write("gruff_pie_1.jpg")
