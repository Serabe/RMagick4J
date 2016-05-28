require 'java'

rmagick4j_dir = File.dirname(__FILE__)

# TODO See about using Raven to get gems of jhlabs and svgsalamander?
require File.join(rmagick4j_dir, '..', 'magick4j.jar')
require File.join(rmagick4j_dir, '..', 'svgsalamander.jar')

#RMagick classes, keep in alphabetical order, please.
require File.join(rmagick4j_dir, 'constants')
require File.join(rmagick4j_dir, 'draw')
require File.join(rmagick4j_dir, 'enum')
require File.join(rmagick4j_dir, 'gradient_fill')
require File.join(rmagick4j_dir, 'image')
require File.join(rmagick4j_dir, 'image_list')
require File.join(rmagick4j_dir, 'pixel')
require File.join(rmagick4j_dir, 'texture_fill')
require File.join(rmagick4j_dir, 'type_metric')
