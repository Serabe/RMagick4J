require 'rubygems'
require 'gruff'
require File.join(File.dirname(__FILE__), 'new_image.rb')


@datasets = [
              [:Jimmy, [25, 36, 86, 39]],
              [:Charles, [80, 54, 67, 54]],
              [:Julie, [22, 29, 35, 38]],
              #[:Jane, [95, 95, 95, 90, 85, 80, 88, 100]],
              #[:Philip, [90, 34, 23, 12, 78, 89, 98, 88]],
              #["Arthur", [5, 10, 13, 11, 6, 16, 22, 32]],
            ]    
                  
g = Gruff::SideStackedBar.new
g.title = "Stacked Bar Long Label"
g.labels =  {
              0 => 'September', 
              1 => 'Oct', 
              2 => 'Nov', 
              3 => 'Dec', 
            }
@datasets.each do |data|
  g.data(data[0], data[1])
end
g.write 'gruff_sidestacked_bar_2.jpg'