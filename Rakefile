require 'rake'

BUILD_FILE = File.join(File.dirname(__FILE__), 'Magick4J', 'build.xml')
JAR_SRC_FILE = File.join(File.dirname(__FILE__), 'Magick4J', 'dist', 'Magick4J.jar')
JAR_DEST_FILE = File.join(File.dirname(__FILE__), 'RMagick4J', 'lib', 'magick4j.jar')

task :default => [:compile, :move, :clean]

task :compile do
  `ant -f #{BUILD_FILE} jar`
  puts 'Compiled magick4j.jar'
end

task :clean do
  `ant -f #{BUILD_FILE} clean`
end

task :move do
  rm_f(JAR_DEST_FILE) 
  mv(JAR_SRC_FILE, JAR_DEST_FILE)
  puts 'Moved jar file to lib folder.'
end
