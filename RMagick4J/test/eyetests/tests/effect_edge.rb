require File.join(File.dirname(__FILE__), 'new_image.rb')

include Magick

clown = Image.load("clown.jpg").first

clown = clown.edge 5

clown.write "effect_edge.jpg"
