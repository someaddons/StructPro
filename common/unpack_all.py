import os
import zipfile
from shutil import copyfile
import subprocess
import stat

path = os.path.abspath('')

for e in os.listdir("../zips"):
	to = e.split('-')[1]
	print e + ' -> ' + to
	zip_ref = zipfile.ZipFile('../zips/' + e, 'r')
	zip_ref.extractall('../builds/' + to)
	zip_ref.close()
	copyfile('init.sh', '../builds/' + to +'/init.sh')
	copyfile('build.sh', '../builds/' + to +'/build.sh')

for root, dirs, files in os.walk('../builds/'):
	for currentFile in files:
	    if currentFile.lower().endswith('.gitignore'):
	        print root + '/' + currentFile
	        os.remove(os.path.join(root, currentFile))