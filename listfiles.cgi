#!/usr/bin/python

import cgitb
import os, os.path
import simplejson as json

cgitb.enable()
print "Content-Type: text/html"     # HTML is following
print                               # blank line, end of headers
"""
def listthings(path):
    print "<li>", path, "</li>"
    print "<ul>"
    for entry in os.listdir(path):
        epath = os.path.join(path, entry)
        if os.path.isfile(epath):
            if entry[len(entry)-1] != '~':
                print "<li>",entry,"</li>"
        elif os.path.isdir(epath):
            listthings(epath)
    print "</ul>"

print "<ul>"
listthings('HelloAndroid/res/')
listthings('HelloAndroid/src/com/example/helloandroid')
print "</ul>"
"""

def listthings(path):
    dict = {'dir': path}
    children = []
    for entry in os.listdir(path):
        epath = os.path.join(path, entry)
        if os.path.isfile(epath):
            if entry[len(entry)-1] != '~' and entry[0] != '.':
                children.append(entry)
        elif os.path.isdir(epath):
            children.append(listthings(epath))
    dict['children'] = children
    return dict

paths = ['HelloAndroid/res/', 'HelloAndroid/src/com/example/helloandroid']
obj = []
for path in paths:
    obj.append(listthings(path))
objdict = {'results': obj}
print json.dumps(objdict)
