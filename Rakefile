require 'rake'
require 'rake/testtask'
require 'bundler/gem_tasks'

BUILD_FILE = File.join(File.dirname(__FILE__), 'Magick4J', 'build.xml')
JAR_SRC_FILE = File.join(File.dirname(__FILE__), 'Magick4J', 'dist', 'Magick4J.jar')
JAR_DEST_FILE = File.join(File.dirname(__FILE__), 'lib', 'magick4j.jar')

task :default => [:compile, :move, :test, :gem, :clean]
task :gem => :build

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

task :work do
  puts `git diff --shortstat 538db96f13ba63fc057b81f9f710554d8f48d84b..HEAD`
end

jar_file = File.join(%w(lib magick4j.jar))

desc 'Clean up any generated file.'
task :clean do
  rm_rf 'pkg'
end

task :test => [:compile, :move, :eyetests, :gruff_test]

Rake::TestTask.new :eyetests do |t|
  t.libs << 'test'
  t.test_files = FileList['test/eyetests/tests/**/*.rb']
end

desc 'Run gruff unit tests.'
Rake::TestTask.new :gruff_test do |t|
  t.test_files = FileList['test/gruff_tests/test/test_*.rb']
end

desc 'Run a live sample using RMagick4j.'
task :sample do
  load_paths = '-Ijruby -Ilib -Ipkg'
  sh "java #{classpath(jar_file)} org.jruby.Main #{load_paths} test/RMagickTestSuite.rb addWatermark"
end

task :spec do
  require 'spec/rake/spectask'
  desc 'Runs Java Integration Specs'
  
  Spec::Rake::SpecTask.new do |t|
    t.spec_opts ||= []
    t.spec_files =  if ENV['class'].nil?
                      FileList['test/spec/**']
                    else
                      File.join('test', 'spec', ENV['class']+'_spec.rb')
                    end
  end

end

task :stories do
  if ENV['file'].nil?
    FileList['test/spec/stories/**/*_runner.rb'].each do |runner|
      require runner
    end
  else
    require File.join('test', 'spec', 'stories', ENV['file']+'_runner.rb')
  end
end

%w(package install_gem debug_gem).each { |t| task t => :compile }

# helper methods below

def classpath(extra_jars=nil)
  jruby_cpath = Java::java.lang.System.getProperty 'java.class.path'
  path = jruby_cpath ? jruby_cpath.split(File::PATH_SEPARATOR) : []
  path << FileList['lib/*.jar']
  path << extra_jars.split(File::PATH_SEPARATOR) if extra_jars
  "-cp #{path.flatten.join(File::PATH_SEPARATOR)}"
end

