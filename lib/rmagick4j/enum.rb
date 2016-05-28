
module Magick
  
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
