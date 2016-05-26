require 'rubygems'
require 'gruff'
require File.join(File.dirname(__FILE__), 'new_image.rb')

g = Gruff::AccumulatorBar.new 500
g.title = "Your Savings"
g.hide_legend = true

# g.font = File.expand_path(File.dirname(__FILE__) + "/../assets/fonts/ATMA____.TTF")

g.marker_font_size = 18

g.theme = {
  :colors => ['#aedaa9', '#12a702'], # 3077a9 blue, aedaa9 light green
  :marker_color => '#dddddd',
  :font_color => 'black',      
  :background_colors => "white"
  # :background_image => File.expand_path(File.dirname(__FILE__) + "/../assets/backgrounds/43things.png")
}

# Attempt at negative numbers
# g.data 'Savings', (1..20).to_a.map { rand(10) * (rand(2) > 0 ? 1 : -1) }
g.data 'Savings', (1..12).to_a.map { rand(100) }
g.labels = (0..11).to_a.inject({}) {|memo, index| {index => '12-26'}.merge(memo)}

g.maximum_value = 1000
g.minimum_value = 0

g.write("gruff_accumulator_bar.jpg")