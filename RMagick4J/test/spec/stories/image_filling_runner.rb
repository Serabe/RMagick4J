require "rubygems"
require 'spec/story'

require File.join(File.dirname(__FILE__), '..', '..', '..','lib','rmagick4j','rmagick4j')

include Magick

require File.join(File.dirname(__FILE__), 'image_filling_steps.rb')


with_steps_for(:image) do
  run File.join(File.dirname(__FILE__), 'image_filling_story.rb')
end