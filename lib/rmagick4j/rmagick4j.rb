require 'java'
# TODO See about using Raven to get gems of jhlabs and svgsalamander?
require File.join(File.dirname(__FILE__), '..', 'jhlabs-filters.jar')
require File.join(File.dirname(__FILE__), '..', 'magick4j.jar')
require File.join(File.dirname(__FILE__), '..', 'svgsalamander.jar')

#RMagick classes, keep in alphabetical order, please.
require 'rmagick4j/constants'
require 'rmagick4j/draw'
require 'rmagick4j/enum'
require 'rmagick4j/gradient_fill'
require 'rmagick4j/image'
require 'rmagick4j/image_list'
require 'rmagick4j/type_metric'
