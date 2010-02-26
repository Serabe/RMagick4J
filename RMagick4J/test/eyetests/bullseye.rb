TEST_ROOT = File.expand_path(File.dirname(__FILE__))

require 'java'
require 'rubygems'

#require 'RMagick'
require 'profligacy/swing'
require 'profligacy/lel'
require  TEST_ROOT + '/tests/new_image.rb'
require 'tmpdir'
Kernel.srand(Time.now.to_i)
OUTPUT_DIR = File.join(Dir.tmpdir,Kernel.rand(100).to_s)
Dir.mkdir(OUTPUT_DIR)

module Bullseye
  include Profligacy
  
  DefaultListModel = javax.swing.DefaultListModel
  ImageIO = javax.imageio.ImageIO
  JButton = javax.swing.JButton
  JFile = java.io.File
  JFrame = javax.swing.JFrame
  JLabel = javax.swing.JLabel
  JList = javax.swing.JList
  JPanel = javax.swing.JPanel
  JScrollPane = javax.swing.JScrollPane
  JTextField = javax.swing.JTextField
  
  class ImagePanel < JPanel
    attr_accessor :image_1, :image_2
    
    def paint(graphics)
      
      unless image_1.nil?
        begin
          pic = load_image(image_1)
          graphics.drawImage(pic, 0, 0, nil)
        rescue
          pic = Magick::Image.new(300, 300)
          pic.background_color = 'red'
          pic.erase!
          graphics.drawImage(pic._image.getImage, 0, 0, nil)
        end
      end
      
      unless image_2.nil?
        begin
          pic = load_image(image_2)
          graphics.drawImage(pic, 310, 0, nil)
        rescue
          pic = Magick::Image.new(300, 300)
          pic.background_color = 'red'
          pic.erase!
          graphics.drawImage(pic._image.getImage, 310, 0, nil)
        end
      end
    end

    def load_image(filename)
      file = JFile.new(filename)
      raise ArgumentError.new("File #{filename} does not exist") unless file.exists
      ImageIO.read(file)
    end
  end
  
  class ScriptRunner < Struct.new(:picture_panel, :source)
    def initialize(picture_panel, source)
      super(picture_panel, source)
      @running = false
    end
    
    def run_script
      @running = true
      script_name = source.selected_value.downcase.gsub(' ','_')
      selected_script = File.join(TEST_ROOT, 'tests', script_name + '.rb')
      NOTIFIER.notify 'Running script in MRI.'
      output_commands = `sh -c 'ruby #{selected_script} #{OUTPUT_DIR}'`
      NOTIFIER.notify 'Running in JRuby.'
      output_commands += `jruby #{selected_script} #{OUTPUT_DIR}`
      if output_commands == ''
        NOTIFIER.notify 'Done'
        picture_panel.image_1 = File.join(OUTPUT_DIR, script_name + '.jruby.jpg')
        picture_panel.image_2 = File.join(OUTPUT_DIR, script_name + '.mri.jpg')
        picture_panel.repaint
      else
        puts output_commands
        NOTIFIER.notify 'Error'
      end
      @running = false
    end
  end
  
  class Notifier < Struct.new(:output)
    def notify(string)
      output.text = string.to_s
    end
  end
  
  class ScriptList < JList
    def initialize
      @default_model = DefaultListModel.new
      super(@default_model)
      load_test_scripts

      self.setSelectedIndex(0)
    end
   
    def load_test_scripts
      Dir[File.dirname(__FILE__) + '/tests/*.rb'].sort.each do |script|
        script = script.gsub(/(^.*tests\/|.rb$)/,'').split('_').map{|x| x.capitalize}.join(' ')
        @default_model.addElement(script) unless script == 'New Image'
      end
    end
  end
  
  def self.scrollable(component)
    JScrollPane.new component, 
       JScrollPane::VERTICAL_SCROLLBAR_AS_NEEDED, 
       JScrollPane::HORIZONTAL_SCROLLBAR_NEVER
  end
  
  lel = '[ <label_results       | <label_scripts   ]
         [ (610,300)image_panel | <scripts_list    ]
         [ >status              | >run_button      ]'

  ui = Swing::LEL.new(JFrame, lel) do |c, i|
    c.label_scripts = JLabel.new('Scripts')
    c.scripts_list = scrollable(scripts = ScriptList.new)
    c.run_button = JButton.new('Run')
    c.label_results = JLabel.new('Results')
    c.image_panel = ImagePanel.new
    c.status = JLabel.new ""

    Bullseye::NOTIFIER = Notifier.new(c.status)
    script_runner = ScriptRunner.new(c.image_panel, scripts)
    run_script = proc {
        scripts.enabled = c.run_button.enabled = false
        Thread.new do
          NOTIFIER.notify 'Running scripts...'
          script_runner.run_script
          scripts.enabled = c.run_button.enabled = true
        end
    }

    scripts.addMouseListener { |event| run_script.call if event.click_count == 2 }
    i.run_button = {:action => proc { |t,e| run_script.call } }
  end.build :args => 'Bullseye'
   
  ui.default_close_operation = JFrame::EXIT_ON_CLOSE

end
