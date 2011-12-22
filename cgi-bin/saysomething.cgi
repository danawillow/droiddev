#!/usr/bin/python

import subprocess
import os
import cgi
import cgitb
import sys

cgitb.enable()
print "Content-Type: text/html"     # HTML is following
print                               # blank line, end of headers
print "hi there"

sys.stdout.flush()

form = cgi.FieldStorage()
code = form.getfirst('code', 'empty')
code = cgi.escape(code)

print code

#f = open('code2.java', 'w')
#f.write(code)
#f.close()

#subprocess.call("/usr/local/java/jdk1.6.0_11/bin/javac code.java", shell=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
#subprocess.call("/usr/local/java/bin/jdk1.6.0_11/bin/java code " + code, shell=True)
#subprocess.call("ls", shell=True)
#p = subprocess.call("ls", shell=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
#print p.stdout.read()

subprocess.Popen(["javac", "../code.java"])

subprocess.Popen(["java", "../code", "asdf"])

#p = subprocess.Popen("ls")  
#p.wait()  
#if p.returncode:  
#   print "failed with code: %s" % str(p.returncode) 
print "bye there"