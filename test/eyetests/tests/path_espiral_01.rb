require File.join(File.dirname(__FILE__), 'new_image.rb')

include Magick

draw = Draw.new

draw.fill 'red'
draw.fill_rule 'nonzero'
draw.stroke 'blue'
draw.stroke_width(5)
draw.path 'M153 334
C153 334 151 334 151 334
C151 339 153 344 156 344
C164 344 171 339 171 334
C171 322 164 314 156 314
C142 314 131 322 131 334
C131 350 142 364 156 364
C175 364 191 350 191 334
C191 311 175 294 156 294
C131 294 111 311 111 334
C111 361 131 384 156 384
C186 384 211 361 211 334
C211 300 186 274 156 274'

b = Image.new(500, 500, HatchFill.new('white', 'black'))

draw.draw(b)

b.write('path_espiral_01.jpg')
