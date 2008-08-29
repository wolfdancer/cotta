# Site update script
# You need to have your public key uploaded to sourceforge in order for this to wokr
# To update the website content, see sitespec.rb in the website folder
require 'buildmaster/site'
require 'buildmaster/cotta'
require 'buildmaster/auto'

dir = BuildMaster::Cotta.parent_of(__FILE__)
ant = BuildMaster::AntDriver.from_file(dir.file('build.xml'))

load "#{dir.file('website/sitespec.rb').path}"

website_dir = dir.dir('website/htdocs')
website_dir.delete
dir.dir('website/output').move_to(website_dir)
ant.target('report.javadoc')
dir.dir('core/build/report.javadoc').copy_to(website_dir.dir('javadoc'))
dir.dir('build/report').copy_to(website_dir.dir('reports'))
pscp = BuildMaster::PscpDriver.new("wolfdancer@shell.sourceforge.net")
pscp.copy(website_dir.path, '/home/groups/c/co/cotta')

