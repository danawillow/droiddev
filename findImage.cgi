#!/usr/bin/python

import fnmatch
import os
import cgi
import cgitb

cgitb.enable()
print "Content-Type: text/html"     # HTML is following
print                               # blank line, end of headers

params = cgi.FieldStorage()

name = params["name"].value.split("@")[1]
filename = name.split("/")

path = 'HelloAndroid/res/' + filename[0]
for file in os.listdir(path):
    if fnmatch.fnmatch(file, filename[1] + '.*'):
        print path + "/" + file,
        break