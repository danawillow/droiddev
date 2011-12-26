#!/usr/bin/python

import subprocess
import os
import cgi
import cgitb
import sys
import imghdr

cgitb.enable()
print "Content-Type: text/html"     # HTML is following
print                               # blank line, end of headers
print "hi there"

sys.stdout.flush()

form = cgi.FieldStorage()
##code = form.getfirst('code', 'empty')
fileNames = form.getlist("fileNames[]");
fileContents = form.getlist("fileContents[]");

for i in xrange(len(fileNames)):
    if not imghdr.what(fileNames[i]):
        f = open(fileNames[i], 'w')
        f.write(fileContents[i])
        f.close()

#code = cgi.escape(code)

#print code

##f = open('code2.java', 'w')
##f.write(code)
##f.close()

#subprocess.call("/usr/local/java/jdk1.6.0_11/bin/javac code.java", shell=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
#subprocess.call("/usr/local/java/bin/jdk1.6.0_11/bin/java code " + code, shell=True)
#subprocess.call("ls", shell=True)
#p = subprocess.call("ls", shell=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
#print p.stdout.read()

##p = subprocess.Popen(["javac", "code2.java"])
##p.wait()

##q = subprocess.Popen(["java", "code2"])
##q.wait()

#p = subprocess.Popen("ls")  
#p.wait()  
#if p.returncode:  
#   print "failed with code: %s" % str(p.returncode) 

#f = open('HelloAndroid/res/layout/main.xml', 'r')
#file = f.read()
#toReplace = (file.split('android:text="', 1)[1]).split('"\n')[0]
#newFile = file.replace(toReplace, code)
#f.close()

##
#f = open('HelloAndroid/res/layout/main.xml', 'w')
#f.write(code)
#f.close()

#p = subprocess.Popen(["ant", "debug", "install"], cwd="HelloAndroid")
#p.wait()
##

print "bye there"
