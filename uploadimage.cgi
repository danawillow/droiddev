#!/usr/bin/python

import cgi, cgitb
import os, os.path
import simplejson as json

cgitb.enable()
print "Content-Type: text/html"     # HTML is following
print                               # blank line, end of headers

form = cgi.FieldStorage()

fileitem = form['file']

if fileitem.filename:
    fn = os.path.basename(fileitem.filename)
    open('HelloAndroid/res/drawable/' + fn, 'wb').write(fileitem.file.read())