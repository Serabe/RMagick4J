
module Magick
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
      @draw = Magick4J::DrawInfo.new
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
end
