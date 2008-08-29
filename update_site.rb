# Site update script
# You need to have your public key uploaded to sourceforge in order for this to wokr
# To update the website content, see sitespec.rb in the website folder
require 'buildmaster/site'
require 'buildmaster/cotta'
require 'buildmaster/project'

cotta = BuildMaster::Cotta.new
dir = cotta.file(__FILE__).parent
svn = BuildMaster::SvnDriver.from_path(dir)
ant = BuildMaster::AntDriver.from_file(dir.file('build.xml'))

load "#{dir.file('website/sitespec.rb').path}"

website_dir = dir.dir('website/htdocs')
website_dir.delete
dir.dir('website/output').move_to(website_dir)
ant.target('report.javadoc')
dir.dir('core/build/report.javadoc').copy_to(website_dir.dir('javadoc'))
dir.dir('core/build/report.coverage.summary').copy_to(website_dir.dir('reports/emma'))
pscp = BuildMaster::PscpDriver.new("#{svn.user}@shell.sourceforge.net")
pscp.copy(website_dir.path, '/home/groups/c/co/cotta')

