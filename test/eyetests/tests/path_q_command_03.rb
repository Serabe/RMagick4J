require File.join(File.dirname(__FILE__), 'new_image.rb')

include Magick

draw = Draw.new

draw.pattern('green', 0, 0, 16, 16) do
  draw.stroke('none')
  draw.fill('DarkBlue')
  draw.rectangle(0,0, 16,16)
  draw.fill('yellow')
  draw.stroke('red')
  draw.polygon(0,0, 8,16, 16,0, 0,0)
end

draw.fill 'green'
draw.stroke 'blue'
draw.stroke_width(5)
draw.path 'M50,50 Q-30,100 50,150 100,230 150,150 230,100 150,50 100,-30 50,50'

b = Image.new(300, 300, HatchFill.new('white', 'black'))

draw.draw(b)

b.write('path_q_command_03.jpg')