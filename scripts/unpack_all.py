import os
import zipfile
from shutil import copyfile
import subprocess
import stat
import shutil


def skip(path):
	return ('src' in path.lower() or 'bin' in path.lower())

def clean(path):
	return path.lower().endswith('.gitignore')

archSrc = '../zips'
buildDst = '../builds'

print '--------------------------------'
print 'CLEANING BUILDS'
print '--------------------------------'
for root, dirs, files in os.walk('../builds/'):
	for file in files:
		filepath = os.path.join(root, file);
		if not skip(filepath):
			print 'REMOVE FILE: ' + filepath 
			os.remove(filepath)
	for dir in dirs:
		dirpath = os.path.join(root, dir);
		if not (skip(dirpath) or os.path.abspath(os.path.join(dirpath, os.pardir)) == os.path.abspath(buildDst)):
			print 'REMOVE DIR: ' + dirpath 
			shutil.rmtree(dirpath)


print '--------------------------------'
print 'UNPACK ARCHIVES'
print '--------------------------------'
for archive in os.listdir(archSrc):
	# Archive template forge-version-subversion-etc.zip 
	version = archive.split('-')[1]
	src = os.path.join(archSrc, archive)
	dst = os.path.join(buildDst, version)
	print 'EXTRACT: ' + src + ' -> ' + dst
	zip_ref = zipfile.ZipFile(src, 'r')
	for name in zip_ref.namelist():
		if not skip(name):
			zip_ref.extract(name, dst)
	zip_ref.close()
	print 'COPY: init.sh to ' + dst
	copyfile('init.sh', os.path.join(dst, 'init.sh'))
	print 'COPY: build.sh to ' + dst
	copyfile('build.sh', os.path.join(dst, 'build.sh'))


print '--------------------------------'
print 'REMOVEING SCRAP FILES'
print '--------------------------------'
for root, dirs, files in os.walk('../builds/'):
	for currentFile in files:
	    if clean(currentFile):
	    	fullpath = os.path.join(root, currentFile)
	        print 'REMOVE: ' + fullpath
	        os.remove(fullpath)
