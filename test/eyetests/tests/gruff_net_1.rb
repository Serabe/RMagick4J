require 'rubygems'
require 'gruff'
require File.join(File.dirname(__FILE__), 'new_image.rb')


@datasets = [
              [:small, [0.1, 0.14356, 0.0, 0.5674839, 0.456]],
              [:small2, [0.2, 0.3, 0.1, 0.05, 0.9]]
            ]

g = Gruff::Net.new
g.title = "Small Values Net Graph Test"
@datasets.each do |data|
g.data(data[0], data[1])
end
g.write("gruff_net_1.jpg")