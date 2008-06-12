require 'rubygems'
require 'gruff'
require File.join(File.dirname(__FILE__), 'new_image.rb')


@datasets = [
              [:Jimmy, [25, 36, 86, 39]],
              [:Charles, [80, 54, 67, 54]],
              [:Julie, [22, 29, 35, 38]],
            ]

g = Gruff::StackedBar.new
g.title = "Visual Stacked Bar Graph Test"
g.labels = {
  0 => '5/6', 
  1 => '5/15', 
  2 => '5/24', 
  3 => '5/30', 
}
@datasets.each do |data|
  g.data(data[0], data[1])
end
g.write 'gruff_stacked_bar_1.jpg'