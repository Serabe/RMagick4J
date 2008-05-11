RMagick is a Ruby binding to ImageMagick and GraphicsMagick. RMagick4J implements ImageMagick functionality and the C portions of RMagick for use with JRuby. 

== Authors

This project was written by Thomas Palmer, Sergio Rodríguez Arbeo, and Thomas Enebo with lots of help from the JRuby community.

== License

The file observer.rb comes from JRuby's distribution of standard Ruby libraries. The file jruby.jar also comes from the JRuby project.

RMagick.rb, clown.jpg, most of rmagick.gemspec, and the contents of rvg come from the RMagick project. Much of the content of the test demos (currently in "RMagickTestSuite.rb") come from the RMagick web site.

See these respective projects for the licenses and ownership of these files.

Other files in RMagickJr are hereby placed in the public domain.

== Creating

To create a gem you should run rake.  The useful targets are :clean, :compile, :gem, :release, :test (hoe has more -- see hoe docs).  In order to run rake you must have hoe installed.

To create a new release, you should:

* Add new version entry to History.txt
* Update lib/rmagick4j/version.rb to contain new version
* rake release VERSION={{{{YOUR NEW VERSION (e.g. 1.1.1)}}}}