if PLATFORM == 'java'
  require File.join(File.dirname(__FILE__),'..','..','lib','RMagick')
else
  require 'rubygems'
  require 'RMagick'
end

include Magick

describe Draw do
  
  before(:each) do
    @draw = Draw.new
  end
  
  it "should clone correctly the primitives" do
    @draw.line(0, 0, 100, 100)
    b = @draw.clone
    b.push
    
    @draw.inspect.should_not == b.inspect
  end
  
  it "should say that has no primitives defined" do
    @draw.inspect.should == '(no primitives defined)'
  end
  
  it "should add one line per primitive" do
    @draw.path 'M150,150
                l50,50'
    @draw.inspect.split(/\n/).size.should == 1
  end
  
  it "should have the maximum width" do
    string = "get\nmultiline\ntype\nmetrics"
    @draw.get_multiline_type_metrics(string).width.should == string.split(/\n/).map{|x| @draw.get_type_metrics(x).width }.max
  end
  
  it "should have the correct multiline height" do
    string = "get\nmultiline\ntype\nmetrics"
    @draw.get_multiline_type_metrics(string).height.should == string.split(/\n/).map{|x| @draw.get_type_metrics(x).height }.inject{|memo, obj| memo + obj}
  end
end
