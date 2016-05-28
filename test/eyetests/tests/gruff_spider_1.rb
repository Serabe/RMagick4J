require 'rubygems'
require 'gruff'
require File.join(File.dirname(__FILE__), 'new_image.rb')


@datasets = [
              [:Strength, [10]],
              [:Dexterity, [16]],
              [:Constitution, [12]],
              [:Intelligence, [12]],
              [:Wisdom, [10]],
              ["Charisma", [16]],
            ]

g = Gruff::Spider.new(20)
g.title = "Spider Graph Test"
@datasets.each do |data|
  g.data(data[0], data[1])
end

# Default theme
g.write("gruff_spider_1.jpg")