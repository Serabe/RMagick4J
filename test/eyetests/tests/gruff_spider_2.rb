require 'rubygems'
require 'gruff'
require File.join(File.dirname(__FILE__), 'new_image.rb')


g = Gruff::Spider.new(2)
g.title = "Pie Graph Two One Zero"

g.data(:Bert, [0])
g.data(:Adam, [1])
g.data(:Sam,  [2])

g.theme_37signals

g.write("gruff_spider_2.jpg")