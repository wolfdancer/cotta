# This rake file uses the working copy of BuildMaster instead of its gem
$:.unshift File.join(File.dirname(__FILE__), '..', 'buildmaster', 'lib', 'buildmaster')
require 'auto'
require 'rake'

root = BuildMaster::Cotta.parent_dir(__FILE__)
build = root.dir('build')
output = build.dir('output')
report = build.dir('report')
dist = build.dir('dist')
junit_file = root.file('lib/junit/junit-4.4.jar')
jmock_dir = root.dir('lib/jmock')

asserts = BuildMaster::JavaProject.new(root.dir('asserts')) do |project|
  project.target_version = '1.5'
  project.src = 'src'
  project.test.src = 'test'
  project.output = output.dir('asserts')
  project.uses(junit_file)
  project.uses_files_in jmock_dir
end

testbase = BuildMaster::JavaProject.new(root.dir('testbase')) do |project|
  project.target_version = '1.5'
  project.src = 'src'
  project.test.src = 'test'
  project.output = output.dir('testbase')
  project.uses(asserts, junit_file)
  project.uses_files_in jmock_dir
end

core = BuildMaster::JavaProject.new(root.dir('core')) do |project|
  project.target_version = '1.5'
  project.output = output.dir('cotta')
  project.src = 'src'
  project.test.src = 'behaviour/src'
  project.test.resource = 'behaviour/resources'
  project.uses(testbase, junit_file) # junit is for TestLoader only
end

ftp = BuildMaster::JavaProject.new(root.dir('ftp')) do |project|
  project.target_version = '1.5'
  project.output = output.dir('ftp')
  project.src = 'src'
  project.test.src = 'behaviour/src'
  project.test.resource = 'behaviour/resources'
  project.uses core
  project.uses_files_in('../lib/commons-io', '../lib/coloradoftp', '../lib/commons-net')
  project.tests_with testbase
end

task :default => [:package, :javadoc]
task :make_testbase => [:make_asserts]
task :make_cotta => [:make_testbase]
task :make_ftp => [:make_cotta]
task :compile => [:make_ftp]

task :make_asserts do
  asserts.make
end

task :make_testbase do
  testbase.make
end

task :make_cotta do
  core.make
end

task :make_ftp do
  ftp.make
end

task :clean do
  build.delete
end

task :package do
  asserts.package(dist, 'cotta-asserts')
  testbase.package(dist, 'cotta-testbase')
  core.package(dist, 'cotta') do |package|
    package.manifest = core.src.file('META-INF/MANIFEST.MF')
    package.add(ftp.prod.output, ftp.prod.src)
  end
end

task :javadoc do
  api = dist.dir('javadoc')
  asserts.javadoc(api.dir('asserts')).run(build.file('javadoc-asserts.xml'))
  core.javadoc(api.dir('core')).run(build.file('javadoc-core.xml'))
end