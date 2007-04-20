puts Time.new
begin
  require 'rubygems'
  gem PLATFORM == 'java' ? 'rmagick4j' : 'rmagick'
rescue Exception
  puts 'No gems!'
end
puts Time.new
require 'RMagick'
puts Time.new

# TODO I know this isn't a real test, but it's what I've got for now.
# TODO Probably eventually want to hook into RMagick's existing tests.

def main
  if ARGV.length > 0
    ARGV.each {|arg| send(arg).display}
  else
    image = addTransparencyWithAMask.raise
    image = openClown.blur_image(0, 5)
    # TODO Support to_blob block.
    open('test.jpg', 'w') do |file|
      file.write(image.to_blob {self.format = 'JPEG'})
    end
  end
  puts Time.new
end

def addRaisedEdges
  openClown.raise
end

def addText
  clown = openClown
  text = Magick::Draw.new
  text.annotate(clown, 0, 0, 0, 60, "My friend!") {
    self.gravity = Magick::SouthGravity
    self.pointsize = 48
    self.stroke = 'transparent'
    self.fill = '#0000A9'
    self.font_weight = Magick::BoldWeight
  }
  clown.first
end

def addTransparencyWithAMask
  clown = openClown.first
  mask = Magick::Image.new(clown.columns, clown.rows) {
    self.background_color = 'black'
  }
  bg = Magick::Image.read("pattern:checkerboard") {
    self.size = "#{clown.columns}x#{clown.rows}"
  }
  bg = bg.first

  height = clown.rows * 0.8
  width = clown.columns * 0.7
  x = (clown.columns - width) / 2
  y = (clown.rows - height) / 2

  gc = Magick::Draw.new
  gc.stroke('white').fill('white')
  gc.roundrectangle(x, y, x+width, y+height, 24, 24)
  gc.draw(mask)

  mask = mask.blur_image(0, 20)

  mask.matte = false
  clown.matte = true

  clown = clown.composite(mask, Magick::CenterGravity, Magick::CopyOpacityCompositeOp)
  bg.composite(clown, Magick::CenterGravity, Magick::OverCompositeOp)
end

def cropToANewSize
  clown = openClown
  face = clown.crop(50, 15, 150, 165)
  white_bg = Magick::Image.new(clown.columns, clown.rows)
  white_bg.composite(face, 50, 15, Magick::OverCompositeOp)
end

def flipIt
  openClown.flip!
end

def makeAThumbnail
  clown = openClown
  tiny = clown.resize(0.25)
  white_bg = Magick::Image.new(clown.columns, clown.rows)
  white_bg.composite(tiny, Magick::CenterGravity, Magick::OverCompositeOp)
end

def openClown
  #Magick::ImageList.new("clown.jpg")
  open("clown.jpg") {|file| Magick::ImageList.new.from_blob(file.read)}
end

def rotateToAnyAngle
  clown = openClown
  clown.rotate(130)
end

def turnItIntoBlackAndWhite
  clown = Magick::ImageList.new("clown.jpg")
  clown.quantize(256, Magick::GRAYColorspace)
end

main
