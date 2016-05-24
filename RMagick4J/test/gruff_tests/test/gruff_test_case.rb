$:.unshift(File.dirname(__FILE__) + "/../lib/")
require 'rubygems'

require 'minitest/autorun'
require 'gruff'
require 'fileutils'
# require 'test_timer'
require 'rmagick'

TEST_ASSETS_DIR = File.expand_path('../assets', File.dirname(__FILE__))
TEST_OUTPUT_DIR = File.expand_path('../output', File.dirname(__FILE__))
FileUtils.mkdir_p(TEST_OUTPUT_DIR)

class GruffTestCase < Minitest::Test

  def setup
    @datasets = [
      [:Jimmy, [25, 36, 86, 39, 25, 31, 79, 88]],
      [:Charles, [80, 54, 67, 54, 68, 70, 90, 95]],
      [:Julie, [22, 29, 35, 38, 36, 40, 46, 57]],
      [:Jane, [95, 95, 95, 90, 85, 80, 88, 100]],
      [:Philip, [90, 34, 23, 12, 78, 89, 98, 88]],
      ["Arthur", [5, 10, 13, 11, 6, 16, 22, 32]],
      ]

    @labels = {
        0 => '5/6',
        1 => '5/15',
        2 => '5/24',
        3 => '5/30',
        4 => '6/4',
        5 => '6/12',
        6 => '6/21',
        7 => '6/28',
      }
  end

  def setup_single_dataset
    @datasets = [
      [:Jimmy, [25, 36, 86]]
      ]

    @labels = {
        0 => 'You',
        1 => 'Average',
        2 => 'Lifetime'
      }
  end

  def setup_wide_dataset
    @datasets = [
      ["Auto", 25],
      ["Food", 5],
      ["Entertainment", 15]
      ]

    @labels = { 0 => 'This Month' }
  end

  def test_dummy
    assert true
  end

protected

  # Generate graphs at several sizes.
  #
  # Also writes the graph to disk.
  #
  #   graph_sized 'bar_basic' do |g|
  #     g.data('students', [1, 2, 3, 4])
  #   end
  #
  def graph_sized(filename, sizes=['', 400])
    class_name = self.class.name.gsub(/^TestGruff/, '')
    Array(sizes).each do |size|
      g = instance_eval("Gruff::#{class_name}.new #{size}")
      g.title = "#{class_name} Graph"
      yield g
      write_test_file g, "#{filename}_#{size}.png"
    end
  end

  def write_test_file(graph, filename)
    graph.write([TEST_OUTPUT_DIR, filename].join("/"))
  end

  ##
  # Example:
  #
  #   setup_basic_graph Gruff::Pie, 400
  #
  def setup_basic_graph(*args)
    klass, size = Gruff::Bar, 400
    # Allow args to be klass, size or just klass or just size.
    #
    # TODO Refactor
    case args.length
    when 1
      case args[0]
      when Fixnum
        size = args[0]
        klass = eval("Gruff::#{self.class.name.gsub(/^TestGruff/, '')}")
      when String
        size = args[0]
        klass = eval("Gruff::#{self.class.name.gsub(/^TestGruff/, '')}")
      else
        klass = args[0]
      end
    when 2
      klass, size = args[0], args[1]
    end

    g = klass.new(size)
    g.title = "My Bar Graph"
    g.labels = @labels


    @datasets.each do |data|
      g.data(data[0], data[1])
    end
    g
  end

end

class Magick::Image
  class << self
    alias_method :old_read, :read

    def read(filename, *args)
      old_read(filename.sub(/^assets/, TEST_ASSETS_DIR), *args)
    end
  end

  alias_method :old_write, :write

  def write(filename)
    engine = (PLATFORM == 'java' ? 'jruby' : 'mri')
    new_filename = filename.sub(/(\.[^\.]*)$/, '.' + engine + '\1')
    # change_geometry('300x300') do |columns, rows, image|
    #   image.resize!(columns, rows)
    # end
    old_write(new_filename.sub(/^test\/output/, TEST_OUTPUT_DIR))
  end
end
