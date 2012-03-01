viewMeasuredWidth = function() {
    return $(this.div).width();
}

viewMeasuredHeight = function() {
    return $(this.div).height();
}

viewRequestedWidth = function() {
    switch($(this.xmlNode).attr('android:layout_width').toLowerCase()) {
        case 'fill_parent':
        case 'match_parent':
            return this.parentObj.width;
        case 'wrap_content':
            return $(this.div).width();
        default:
            return +$(this.xmlNode).attr('android:layout_width').match(/\d+/)[0];
    }
}

viewRequestedHeight = function() {
    switch($(this.xmlNode).attr('android:layout_height').toLowerCase()) {
        case 'fill_parent':
        case 'match_parent':
            return this.parentObj.height;
        case 'wrap_content':
            return $(this.div).height();
        default:
            return +$(this.xmlNode).attr('android:layout_height').match(/\d+/)[0];
    }
}

viewSetExtraWidth = function(width, extra, widthAvail) {
    var w = width + extra*this.weight;
    if (w > widthAvail) w = widthAvail;
    
    if (w > 0) $(this.div).width(w);
    else $(this.div).hide();
    
    return w > 0 ? w : 0;
}

viewSetExtraHeight = function(height, extra, heightAvail) {
    var h = height + extra*this.weight;
    if (h > heightAvail) h = heightAvail;
    
    if (h > 0) $(this.div).height(h);
    else $(this.div).hide();
    
    return h > 0 ? h : 0;
}

viewPosition = function() {
    var h = false;
    var v = false;
    var t = false, b = false, l = false, r = false;
    
    /* Position center relative to parent */
    if ($(this.xmlNode).attr('android:layout_centerHorizontal') == "true" ||
        $(this.xmlNode).attr('android:layout_centerInParent') == "true") {
        $(this.div).css('left', this.parentObj.requestedWidth()/2 - $(this.div).width()/2);
        h = true;
        l = true;
    }
    if ($(this.xmlNode).attr('android:layout_centerVertical') == "true" ||
        $(this.xmlNode).attr('android:layout_centerInParent') == "true") {
        $(this.div).css('top', this.parentObj.requestedHeight()/2 - $(this.div).height()/2);
        v = true;
        t = true;
    }
    
    /* Position relative to other element */
    if ($(this.xmlNode).attr('android:layout_toRightOf')) {
        var other = ids[$(this.xmlNode).attr('android:layout_toRightOf')];
        if (other.css('right') == "auto") {
            $(this.div).css('left', +$(other).css('left').replace(/px/, '') + $(other).width());
            h = true;
            l = true;
        }
        else if (other) {
            $(this.div).css('left', this.parentObj.requestedWidth() - +$(other).css('right').replace(/px/, ''));
            h = true;
            l = true;
        }
    }
    if ($(this.xmlNode).attr('android:layout_toLeftOf')) {
        var other = ids[$(this.xmlNode).attr('android:layout_toLeftOf')];
        if (other.css('left') == "auto") {
            $(this.div).css('right', +$(other).css('right').replace(/px/, '') + $(other).width());
            h = true;
            r = true;
        }
        else if (other) {
            $(this.div).css('right', this.parentObj.requestedWidth() - +$(other).css('left').replace(/px/, ''));
            h = true;
            r = true;
        }
    }
    if ($(this.xmlNode).attr('android:layout_above')) {
        var other = ids[$(this.xmlNode).attr('android:layout_above')];
        if (other.css('top') == "auto") {
            $(this.div).css('bottom', +$(other).css('bottom').replace(/px/, '') + $(other).height());
            v = true;
            b = true;
        }
        else if (other) {
            $(this.div).css('bottom', this.parentObj.requestedHeight() - +$(other).css('top').replace(/px/, ''));
            v = true;
            b = true;
        }
    }
    if ($(this.xmlNode).attr('android:layout_below')) {
        var other = ids[$(this.xmlNode).attr('android:layout_below')];
        if (other.css('bottom') == "auto") {
            $(this.div).css('top', +$(other).css('top').replace(/px/, '') + $(other).height());
            v = true;
            t = true;
        }
        else if (other) {
            $(this.div).css('top', this.parentObj.requestedHeight() - +$(other).css('bottom').replace(/px/, ''));
            v = true;
            t = true;
        }
    }
    
    /* Align relative to other element */
    if ($(this.xmlNode).attr('android:layout_alignRight')) {
        var other = ids[$(this.xmlNode).attr('android:layout_alignRight')];
        if (other.css('right') == "auto") {
            $(this.div).css('right', this.parentObj.requestedWidth() - (+$(other).css('left').replace(/px/, '') + $(other).width()));
            h = true;
            r = true;
        }
        else if (other) {
            $(this.div).css('right', +$(other).css('right').replace(/px/, ''));
            h = true;
            r = true;
        }
    }
    if ($(this.xmlNode).attr('android:layout_alignLeft')) {
        var other = ids[$(this.xmlNode).attr('android:layout_alignLeft')];
        if (other.css('left') == "auto") {
            $(this.div).css('left', this.parentObj.requestedWidth() - (+$(other).css('right').replace(/px/, '') + $(other).width()));
            h = true;
            l = true;
        }
        else if (other) {
            $(this.div).css('left', +$(other).css('left').replace(/px/, ''));
            h = true;
            l = true;
        }
    }
    if ($(this.xmlNode).attr('android:layout_alignTop')) {
        var other = ids[$(this.xmlNode).attr('android:layout_alignTop')];
        if (other.css('top') == "auto") {
            $(this.div).css('top', this.parentObj.requestedHeight() - (+$(other).css('bottom').replace(/px/, '') + $(other).height()));
            v = true;
            t = true;
        }
        else if (other) {
            $(this.div).css('top', +$(other).css('top').replace(/px/, ''));
            v = true;
            t = true;
        }
    }
    if ($(this.xmlNode).attr('android:layout_alignBottom')) {
        var other = ids[$(this.xmlNode).attr('android:layout_alignBottom')];
        if (other.css('bottom') == "auto") {
            $(this.div).css('bottom', this.parentObj.requestedHeight() - (+$(other).css('top').replace(/px/, '') + $(other).height()));
            v = true;
            b = true;
        }
        else if (other) {
            $(this.div).css('bottom', +$(other).css('bottom').replace(/px/, ''));
            v = true;
            b = true;
        }
    }
    if ($(this.xmlNode).attr('android:layout_alignBaseline')) { // note: wrong.
        var other = ids[$(this.xmlNode).attr('android:layout_alignBaseline')];
        if (other.css('bottom') == "auto") {
            $(this.div).css('bottom', this.parentObj.requestedHeight() - (+$(other).css('top').replace(/px/, '') + $(other).height()));
            v = true;
            b = true;
        }
        else if (other) {
            $(this.div).css('bottom', +$(other).css('bottom').replace(/px/, ''));
            v = true;
            b = true;
        }
    }
    
    /* Position relative to parent */
    if ($(this.xmlNode).attr('android:layout_alignParentLeft') == "true") {
        $(this.div).css('left', '0px');
        h = true;
        l = true;
    }
    if ($(this.xmlNode).attr('android:layout_alignParentRight') == "true") {
        $(this.div).css('right', '0px');
        h = true;
        r = true;
    }
    if ($(this.xmlNode).attr('android:layout_alignParentTop') == "true") {
        $(this.div).css('top', '0px');
        v = true;
        t = true;
    }
    if ($(this.xmlNode).attr('android:layout_alignParentBottom') == "true") {
        $(this.div).css('bottom', '0px');
        v = true;
        b = true;
    }
    
    if (!h)
        $(this.div).css('left', '0px');
    if (!v)
        $(this.div).css('top', '0px');
    
    return [l && r, t && b];
}

findViewVarName = function(id) {
    var re = new RegExp("\\b\\w+(?=\\s*=.*R.id." + id + ")");
    //var index = fileContents[currFile].search(re);
    var m = re.exec(fileContents[currFile]);
    if (m == null) return -1;
    return m[0];
}
