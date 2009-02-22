# SiteSpec for Cotta website to be built by BuildMaster
# To start the local server against the content, type
#     sitespec.rb server
# and point your browser to port 2000
# See BuildMaster website (http://buildmaster.rubyforge.org) for details
require 'buildmaster/site'
require 'buildmaster/cotta'

class MySiteSpec < BuildMaster::SiteSpec
  def initialize
    super
    cotta = BuildMaster::Cotta.new
    root = cotta.file(__FILE__).parent
    @output_dir = root.dir('output')
    @content_dir = root.dir('content')
    @template_file = root.file('template.html')
    add_property('release', '1.3.1')
    add_property('prerelease', 'n/a')
    add_property('snapshot', 'n/a')
  end

  def center_class(content_path)
    if index_file? content_path
      return 'Content3Column'
    else
      return 'Content2Column'
    end
  end

  def history(content_path)
    return content_path
  end

  def news_rss2
    return IO.read(File.join(@content_dir, 'news-rss2.xml'))
  end
end

site = BuildMaster::Site.new(MySiteSpec.new)
site.execute(ARGV)
