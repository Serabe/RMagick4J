require File.join(File.dirname(__FILE__), 'new_image.rb')

include Magick

clown = Image.load("clown.jpg").first

draw = Draw.new
draw.push
draw.stroke 'green'
draw.fill 'yellow'
draw.circle clown.columns/2, clown.rows/2, clown.columns/4, clown.rows/2
draw.pop

draw.draw clown

clown.write "draw_pop_push.jpg"
