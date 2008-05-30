require File.join(File.dirname(__FILE__),'..','..','lib','RMagick')

include Magick

describe Draw do
  
  it "should save the state when calling push and recover it after pop" do
    a = Draw.new
    a.line(5, 5, 10, 10)
    b = a.clone
    b.push
    b.stroke('blue')
    a.inspect.should != b.inspect
    b.pop
    a.inspect.should == b.inspect
  end
end