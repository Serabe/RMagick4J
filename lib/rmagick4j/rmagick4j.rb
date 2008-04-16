require 'java'
# TODO See about using Raven to get gems of jhlabs and svgsalamander?
require 'jhlabs-filters.jar'
require 'magick4j.jar'
require 'svgsalamander.jar'
require 'observer'

module Magick

Magick4J = Java::magick4j
RMagick4J = Java::rmagick4j

class Draw

	def annotate(img, width, height, x, y, text, &add)
		instance_eval &add if add
		@draw.annotate(img._image, width, height, x, y, text)
		self
	end

	def draw(image)
		@draw.clone.draw(image._image, Magick4J.CommandParser.parse(@primitives))
		self
	end

	def fill= fill
		@draw.fill = Magick4J.ColorDatabase.query_default(fill)
		self
	end

	def font_family= font_family
		@draw.font_family = font_family
		self
	end

	def font_weight= font_weight
		font_weight = {BoldWeight => 700, NormalWeight => 400}[font_weight]
		@draw.font_weight = font_weight
	end

	def get_type_metrics(*args)
		raise ArgumentError.new('wrong number of arguments (#{args.length})') if not (1..2) === args.length
		string = args.last
		image = args.first._image if args.length == 2
		jmetrics = @draw.getTypeMetrics(string, image)
		metrics = TypeMetric.new
		metrics.ascent = jmetrics.getAscent
		metrics.descent = jmetrics.getDescent
		metrics.height = jmetrics.getHeight
		metrics.max_advance = jmetrics.getMaxAdvance
		metrics.width = jmetrics.getWidth
		metrics
	end

	def font= font
		# TODO
	end

	def gravity= gravity
		@draw.setGravity(gravity._val)
	end

	def initialize
		# Docs say that you can initialize with a block, but it doesn't really work because it inits an ImageInfo not a DrawInfo.
		# instance_eval &add if add
		@draw = Magick4J.DrawInfo.new
		@primitives = ''
	end

	def inspect
		@primitives
	end

	def pointsize= pointsize
		@draw.setPointSize(pointsize)
	end

	def primitive primitive
		# TODO Concat in a string like they do, then use helper to parse later
		@primitives << "\n" unless @primitives.empty?
		@primitives << primitive
		self
	end

	def rotation= rotation
		@draw.rotate(rotation)
		self
	end

	def stroke= stroke
		@draw.setStroke(Magick4J.ColorDatabase.queryDefault(stroke))
		self
	end

end

class Enum

	def self.def_val(name, val)
		enum = new(name, val)
		Magick.const_set(name, enum)
	end

	def initialize(name, val)
		@name = name
		@val = val
	end

	def to_i
		@val.ordinal
	end

	def _val
		@val
	end

end

class GradientFill

	def fill(image)
		@fill.fill(image._image)
	end

	def initialize(x1, y1, x2, y2, start_color, end_color)
		@fill = Magick4J.GradientFill.new(x1, y1, x2, y2, Magick4J.ColorDatabase.queryDefault(start_color), Magick4J.ColorDatabase.queryDefault(end_color))
	end

end

class Image

	def self.from_blob(blob, &add)
		# TODO Use info somehow
		info = Info.new(&add)
		# TODO multiple images in file
		[Image.new(Magick4J.MagickImage.from_blob(blob.to_java_bytes))]
	end

	def self.read(file, &add)
		[Image.new(file, &add)]
	end

	def blur_image(radius=0.0, sigma=1.0)
		# Swap order on purpose. I wanted them the other way around in Magick4J.
		Image.new(@image.blurred(sigma, radius))
	end

	def columns
		@image.width
	end

	def composite(*args)
		# image, x, y, composite_op
		args[0] = args[0]._image
		args.map! {|arg| arg.is_a?(Enum) ? arg._val : arg}
		Image.new(@image.composited(*args))
	end

  def copy
    Image.new(@image.clone)
  end

  def crop(*args)
    copy.crop!(*args)
  end

  def crop!(*args)
    # gravity, x, y, width, height, reset_offset
    # Defaults.
    gravity = nil
    x = y = 0
    reset_offset = false
    # Find available args.
    if args.first.is_a? Magick4J::Gravity
      gravity = args.shift
    end
    if [FalseClass, TrueClass].member? args.last.class
      reset = args.pop
    end
    if args.length == 4
      x, y = args[0..1]
    end
    width, height = args[-2..-1]
    # Call Java.
    # TODO Why wouldn't we reset offset information? Do we need to use that?
    @image.cropped(gravity, x, y, width, height)
    self
  end

	def display
		@image.display
		self
	end

	def format
		@image.getFormat
	end

	def format= format
		@image.setFormat(format)
		self
	end

	def flip!
		@image.flip
		self
	end

	def _image
		@image
	end

	def initialize(*args, &add)
		# TODO Only use new as defined in the RMagick docs. Use allocate and other methods otherwise?
		info = Info.new(&add)
		if args.length == 1
			if args[0].is_a? String
				# TODO Respect Dir.getwd
				name = args[0]
				@image = Magick4J.ImageDatabase.createDefault(name, info._info) || Magick4J.MagickImage.new(java.io.File.new(name))
			else
				@image = args[0]
			end
		else
			@image = Magick4J.MagickImage.new(args[0], args[1], info._info)
			if args.length == 3
				args[2].fill(self)
			end
		end
	end

	def matte= matte
		@image.setMatte(matte)
	end

	def quantize(number_colors=256, colorspace=RGBColorspace, dither=true, tree_depth=0, measure_error=false)
		Image.new(@image.quantized(number_colors, colorspace._val, dither, tree_depth, measure_error))
	end

	def raise(width=6, height=6, raise=true)
		Image.new(@image.raised(width, height, raise))
	end

	def resize(scale_factor)
		Image.new(@image.resized(scale_factor))
	end

  def resize!(scale_factor)
    @image.resize(scale_factor)
    self
  end

	def rotate(amount)
		Image.new(@image.rotated(amount))
	end

	def rotate!(amount)
		@image.rotate(amount)
		self
	end

	def rows
		@image.getHeight
	end

	def to_blob(&add)
		info = Info.new(&add)
		@image.setFormat(info.format) if info.format
		String.from_java_bytes(@image.toBlob)
	end

    def watermark(mark, lightness=1.0, saturation=1.0, gravity=nil, x_offset=0, y_offset=0)
      if gravity.is_a? Numeric
        # gravity is technically an optional argument in the middle.
        gravity = nil
        y_offset = x_offset
        x_offset = gravity
      end
      # TODO Perform watermark.
      self
    end

	def write(file, &add)
		# TODO I'm having trouble finding out how this info is used, so I'll skip using it for now.
		info = Info.new(&add)
		# TODO Resolve pwd as needed
		@image.write(file)
		self
	end

	class Info

		# TODO Replace with call to Java, or is this the better way? Should it be converted to the Java version only later?
		def background_color= background_color
			@info.setBackgroundColor(Magick4J.ColorDatabase.queryDefault(background_color))
		end

		attr_accessor :format

		def _info
			@info
		end

		def initialize(&add)
			@info = Magick4J.ImageInfo.new
			instance_eval &add if add
		end

		def size= size
			size = Geometry.from_s(size) if size.is_a? String
			geometry = Magick4J.Geometry.new
			geometry.setWidth(size.width)
			geometry.setHeight(size.height)
			@info.setSize(geometry)
		end

	end

end

class ImageList < Array

	def to_blob(&add)
		# TODO Support lists.
		first.to_blob(&add)
	end

end

class TypeMetric

	attr_accessor :ascent, :descent, :height, :max_advance, :width

end


# Enums

class CompositeOperator < Enum
	def_val :CopyOpacityCompositeOp, Magick4J.CompositeOperator::COPY_OPACITY
	def_val :OverCompositeOp, Magick4J.CompositeOperator::OVER
end

class GravityType < Enum
	def_val :CenterGravity, Magick4J.Gravity::CENTER
	def_val :EastGravity, Magick4J.Gravity::EAST
	def_val :ForgetGravity, Magick4J.Gravity::FORGET
	def_val :NorthEastGravity, Magick4J.Gravity::NORTH_EAST
	def_val :NorthGravity, Magick4J.Gravity::NORTH
	def_val :NorthWestGravity, Magick4J.Gravity::NORTH_WEST
	def_val :SouthEastGravity, Magick4J.Gravity::SOUTH_EAST
	def_val :SouthGravity, Magick4J.Gravity::SOUTH
	def_val :SouthWestGravity, Magick4J.Gravity::SOUTH_WEST
	def_val :WestGravity, Magick4J.Gravity::WEST
end

class ColorspaceType < Enum
	def_val :GRAYColorspace, Magick4J.Colorspace::GRAY
	def_val :RGBColorspace, Magick4J.Colorspace::RGB
	def_val :UndefinedColorspace, Magick4J.Colorspace::Undefined
end


# Simple hack Enums.
# TODO All these need changed to the official way used above.

@@enumVal = 1
def self.nextVal
	@@enumVal = @@enumVal + 1
end

LeftAlign = nextVal
RightAlign = nextVal
CenterAlign = nextVal
StartAnchor = nextVal
MiddleAnchor = nextVal
EndAnchor = nextVal
NoDecoration = nextVal
UnderlineDecoration = nextVal
OverlineDecoration = nextVal
LineThroughDecoration = nextVal

AnyWeight = nextVal
NormalWeight = nextVal
BoldWeight = nextVal
BolderWeight = nextVal
LighterWeight = nextVal

PointMethod = nextVal
ReplaceMethod = nextVal
FloodfillMethod = nextVal
FillToBorderMethod = nextVal
ResetMethod = nextVal
NormalStretch = nextVal
UltraCondensedStretch = nextVal
ExtraCondensedStretch = nextVal
CondensedStretch = nextVal
SemiCondensedStretch = nextVal
SemiExpandedStretch = nextVal
ExpandedStretch = nextVal
ExtraExpandedStretch = nextVal
UltraExpandedStretch = nextVal
AnyStretch = nextVal
NormalStyle = nextVal
ItalicStyle = nextVal
ObliqueStyle = nextVal
AnyStyle = nextVal

# ColorspaceType constants
# DEF_ENUM(ColorspaceType)
TransparentColorspace = nextVal
OHTAColorspace = nextVal
XYZColorspace = nextVal
YCbCrColorspace = nextVal
YCCColorspace = nextVal
YIQColorspace = nextVal
YPbPrColorspace = nextVal
YUVColorspace = nextVal
CMYKColorspace = nextVal
SRGBColorspace = nextVal
HSLColorspace = nextVal
HWBColorspace = nextVal
HSBColorspace = nextVal           # IM 6.0.0
CineonLogRGBColorspace = nextVal  # GM 1.2
LABColorspace = nextVal           # GM 1.2
Rec601LumaColorspace = nextVal    # GM 1.2 && IM 6.2.2
Rec601YCbCrColorspace = nextVal   # GM 1.2 && IM 6.2.2
Rec709LumaColorspace = nextVal    # GM 1.2 && IM 6.2.2
Rec709YCbCrColorspace = nextVal   # GM 1.2 && IM 6.2.2
LogColorspace = nextVal           # IM 6.2.3

end
