require File.join(File.dirname(__FILE__), 'new_image.rb')

include Magick

points = [145, 65, 174,151, 264,151, 192,205,
          218,291, 145,240,  72,291,  98,205,
           26,151, 116,151]

pr = Draw.new

# Define a clip-path.
# The name of the clip-path is "example"
pr.define_clip_path('example') do
    pr.polygon(*points)
end

# Enable the clip-path
pr.push
pr.clip_path('example')

pr.stroke 'none'

pr.fill 'green'
pr.circle(150, 0, 150, 150)

pr.fill 'red'
pr.circle(0, 150, 150, 150)

pr.fill 'blue'
pr.circle(300, 150, 150, 150)

pr.fill 'orange'
pr.circle(150, 300, 150, 150)

pr.pop

# Create a canvas to draw on, a bit bigger than the star.
canvas = Image.new(300, 300, HatchFill.new('white', 'black'))

pr.draw(canvas)

canvas.write("draw_clip_path.jpg")


