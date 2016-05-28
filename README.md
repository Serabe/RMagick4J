[![Gem Version](https://badge.fury.io/gh/serabe%2Frmagick4j.svg)](https://badge.fury.io/rb/rmagick4j)
[![Build Status](https://travis-ci.org/Serabe/RMagick4J.svg?branch=master)](https://travis-ci.org/Serabe/RMagick4J)

RMagick4J
=========

[RMagick](https://github.com/rmagick/rmagick) is a Ruby binding to ImageMagick
and GraphicsMagick.  **RMagick4J** implements ImageMagick functionality and the
C portions of RMagick for use with JRuby.


## API docs

You can find the generated API documentation here:

http://www.rubydoc.info/gems/rmagick4j


## Authors

This project was written by Thomas Palmer, Sergio Arbeo, Thomas Enebo, and
Uwe Kubosch with lots of help from the JRuby community.

## License

See [LICENSE.md](LICENSE.md)

## Creating

To create a gem you should run rake.
The useful targets are

    :clean, :compile, :gem, :release, :test

To create a new release, you should:

* Add new version entry to History.txt
* Update lib/rmagick4j/version.rb to contain new version
* rake release
