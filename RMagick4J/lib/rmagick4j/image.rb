module Magick
  class Image
    
    def self.from_blob(blob, &add)
      # TODO multiple images in file
      [Image.from_image(Magick4J.MagickImage.from_blob(blob.to_java_bytes), &add)]
    end

    def self.read(file, &add)
      info = Info.new(&add)
      image = Magick4J.ImageDatabase.createDefault(file.to_s, info._info) || Magick4J.MagickImage.new(java.io.File.new(file.to_s))
      [Image.from_image(image,&add)]
    end
    
    def self.from_image(image, &add)
      raise ArgumentError, 'First parameter must be a MagickImage instance.' unless image.is_a? Magick4J.MagickImage
      magick_image = Image.new(image.getWidth(), image.getHeight(), &add)
      magick_image._image = image
      magick_image
    end
    
    def self.allocate(*args, &add)
      info = Info.new(&add)
      if args.length == 1
        case args[0]
        when String then
          # TODO Respect Dir.getwd
          name = args[0]
          @image = Magick4J.ImageDatabase.createDefault(name, info._info) || Magick4J.MagickImage.new(java.io.File.new(name))
        when Magick4J.MagickImage then
          @image = args[0]
        when Image then
          @image = args[0]._image
        else
          raise ArgumentError, "The argument just can be a String, a MagickImage or an Image instance."
        end
      else
        @image = Magick4J.MagickImage.new(args[0], args[1], info._info)
        if args.length == 3
          args[2].fill(self)
        end
      end
    end

    def background_color
      @image.getBackgroundColor
    end
    
    def background_color=(value)
      raise TypeError, "argument must be color name or pixel (#{value.class} given)" unless value.is_a?(String) || value.is_a?(Pixel)
      value = Pixel.from_color(value) if value.is_a?(String)
      @image.setBackgroundColor(value)
    end

    def blur
      @image.getBlur
    end
    
    def blur=(value)
      raise TypeError, "no implicit conversion to float from #{value.class.to_s.downcase}" unless value.is_a? Numeric
      @image.setBlur(value)
    end

    def blur_image(radius=0.0, sigma=1.0)
      # Swap order on purpose. I wanted them the other way around in Magick4J.
      Image.from_image(Effects.BlurEffect.new(radius,sigma).apply(_image))
    end
    
    def change_geometry(geometry)
      geometry = Geometry.from_s(geometry.to_s) unless geometry.is_a? Geometry
      index = if geometry.flag.nil?
                0
              else
                geometry.flag._val
              end
      geometry = JGeometries[index].new( geometry.width, geometry.height,
                                         geometry.x, geometry.y)
      yield geometry.calculate_width(self._image),
            geometry.calculate_height(self._image),
            self
    end

    def charcoal(radius=0.0, sigma=1.0)
      Image.from_image(Effects.CharcoalEffect.new(radius,sigma).apply(_image))
    end

    def columns
      @image.getWidth
    end

    def composite(*args)
      # image, x, y, composite_op
      args[0] = args[0]._image
      args.map! {|arg| arg.is_a?(Enum) ? arg._val : arg}
      Image.from_image(@image.composited(*args))
    end

    def copy
      Image.from_image(@image.clone)
    end

    def crop(*args)
      copy.crop!(*args)
    end

    def crop!(*args)
      # gravity, x, y, width, height, reset_offset
      # Defaults.
      gravity = nil
      x = y = -1
      reset_offset = false
      # Find available args.
      if args.first.is_a? GravityType
        gravity = args.shift._val
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
      @image =  unless gravity.nil?
                  if x == -1 || y == -1
                    @image.crop(gravity, width, height)
                  else
                    @image.crop(gravity, x, y, width, height)
                  end
                else
                  @image.crop(x,y,width,height)
                end
      self
    end

    def display
      @image.display
      self
    end

    def edge(radius=0.0)
      Image.from_image(Effects.EdgeEffect.new(radius).apply(_image))
    end

    def erase!
      @image.erase
    end

    def format
      @image.getFormat
    end

    def format= format
      @image.setFormat(format)
      self
    end

    def flip
      copy.flip
    end

    def flip!
      @image.flip
      self
    end

    def _image
      @image
    end
    
    def _image=(new_image)
      @image = new_image
    end

    def implode(amount=0.5)
      Image.from_image(Effects.ImplodeEffect.new(amount).apply(_image))
    end
    
    def _info
      @info
    end
    
    def _info=(new_info)
      @info = new_info
    end

    def initialize(columns, rows, fill=nil, &info_block)
      info = Info.new(&info_block)
      @image = Magick4J.MagickImage.new(columns, rows, info._info)
      fill.fill(self) if fill.respond_to? :fill
    end

    def matte
      @image.getMatte
    end

    def matte= matte
      @image.setMatte(matte)
    end

    def negate(grayscale=false)
      Image.from_image(Effects.NegateEffect.new(grayscale).apply(_image))
    end

    def normalize
      Image.from_image(Effects.NormalizeEffect.new().apply(_image))
    end

    def quantize(number_colors=256, colorspace=RGBColorspace, dither=true, tree_depth=0, measure_error=false)
      Image.from_image(@image.quantized(number_colors, colorspace._val, dither, tree_depth, measure_error))
    end

    def raise(width=6, height=6, raise=true)
      Image.from_image(@image.raised(width, height, raise))
    end

		def remap
			# TODO: Implement. Just here to avoid warning in RMagick 2.9 file.
		end

    def resize(*args)
      copy.resize!(*args)
    end

    def resize!(*args)
      @image =  if args.length == 1
                  @image.resized(args[0])
                elsif args.length == 2 # It must be 4 nor 2, but two of them are not yet implemented
                  # TODO  Implement the other two arguments.
                  # arg[0] --> new_width
                  # arg[1] --> new_height
                  # arg[2] --> filter=LanczosFilter
                  # arg[3] --> support=1.0
                  @image.resized(args[0],args[1])
                else
                  Kernel.raise ArgumentError, "wrong number of parameters(#{args.length} for 1 or 4)"
                end
      self
    end

    def rotate(amount, qualifier=nil)
      copy.rotate!(amount,qualifier)
    end

    def rotate!(amount, qualifier=nil)
      if qualifier == '<' && columns < rows
        @image.rotate(amount)
        self
      elsif qualifier == '>' && columns > rows
        @image.rotate(amount)
        self
      elsif qualifier.nil?
        @image.rotate(amount)
        self
      else
        nil
      end
    end

    def rows
      @image.getHeight
    end

    def shade(shading=false,azimuth=30,elevation=30)
      Image.from_image(Effects.ShadeEffect.new(shading,azimuth,elevation).apply(_image))
    end
    
    def solarize(threshold=50)
      Image.from_image(Effects.SolarizeEffect.new(threshold).apply(_image))
    end

    def store_pixels(x, y, columns, rows, pixels)
      ria_size = columns*rows
      raise IndexError, "not enough elements in array - expecting #{ria_size}, got #{pixels.size}" if pixels.size < ria_size
      @image.storePixels(x,y,columns,rows,pixels.to_java)
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

    def wave(amplitude=25.0, wavelength=150.0)
      Image.from_image(Effects.WaveEffect.new(amplitude,wavelength).apply(_image))
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
end
