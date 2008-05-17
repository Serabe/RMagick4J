require File.join(File.dirname(__FILE__),'..','..','lib','RMagick')

include Magick

class IntConverter
  def to_int
    1
  end
end

describe Pixel do
  Red     = 233
  Green   = 234
  Blue    = 235
  Opacity = 151
  Color = ['AliceBlue', Pixel.new(240, 248, 255, 0)]

  it "should allow limited or no parameters without error" do
    Pixel.new
    Pixel.new(1)
    Pixel.new(1, 1)
    Pixel.new(1, 1, 1)
    Pixel.new(1, 1, 1, 1)
  end

  it "should accept float values and round down" do
    Pixel.new(1.1).red.should == 1
    Pixel.new(1.9).red.should == 1
  end

  it "should call to_int on pixel values" do
    Pixel.new(IntConverter.new).red.should == 1
  end
  
  before(:each) do
    @pixel = Pixel.new(Red, Green, Blue, Opacity)
  end

  it "should return the red value" do
    @pixel.red.should eql(Red)
  end
  
  it "should return the green value" do
    @pixel.green.should eql(Green)
  end
  
  it "should return the blue value" do
    @pixel.blue.should eql(Blue)
  end
  
  it "should return the opacity value" do
    @pixel.opacity.should eql(Opacity)
  end
  
  it "should not find the value and raise and exception" do
    lambda {Pixel.from_color('mathematica')}.should raise_error(ArgumentError)
  end
  
  it "should find the right value for #{Color[0]}" do
    lambda {
      Pixel.from_color(Color[0]).should eql(Color[1])
    }.should_not raise_error(ArgumentError)
  end

  it "should type error on invalud types to fcmp" do
    lambda { @pixel.fcmp('a') }.should raise_error(TypeError)
  end
  
  it "should return the color name (#{Color[0]})" do
    Color[1].to_color.should eql(Color[0])
  end
  
end

