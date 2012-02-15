function RelativeLayout(xmlNode, parentObj, vertical, width, height) {
    this.div = $("<div />");
    $(parentObj.div).append($(this.div));
    $(this.div).addClass('element');
    if (vertical) $(this.div).addClass('vertical');
    this.weight = +$(xmlNode).attr('android:layout_weight') || 0;
    
    this.width = width;
    this.height = height;
    
    var ll = this;
    var childNodes = $(xmlNode).children();
    
    this.xmlNode = xmlNode;
    this.parentObj = parentObj;
    
    this.measuredWidth = viewMeasuredWidth;
    this.measuredHeight = viewMeasuredHeight;
    
    this.requestedWidth = viewRequestedWidth;
    this.requestedHeight = viewRequestedHeight;
    
    this.setExtraWidth = viewSetExtraWidth;
    this.setExtraHeight = viewSetExtraHeight;
    
    $(childNodes).each(function() {
        // create child
        var nodeName = $(this)[0].nodeName;
        var child;
        switch(nodeName) {
            case "TextView":
                child = new TextView($(this), ll, 0);
                break;
            case "ImageView":
                child = new ImageView($(this), ll, 0);
                break;
            case "LinearLayout":
                child = new LinearLayout($(this), ll, 0);
                break;
        }
        
        // set child dimensions
        var widthToSet = $(xmlNode).attr('android:layout_width').toLowerCase() == "wrap_content" ? child.measuredWidth() : child.requestedWidth();
        child.setExtraWidth(widthToSet, 0, ll.width);
        
        var heightToSet = $(xmlNode).attr('android:layout_height').toLowerCase() == "wrap_content" ? child.measuredHeight() : child.requestedHeight();
        child.setExtraHeight(heightToSet, 0, ll.height);
        
        // set child position
        $(child.div).css('position', 'absolute');
        child.position();
    });
}


function LinearLayout(xmlNode, parentObj, vertical) {
    this.div = $("<div />");
    $(parentObj.div).append($(this.div));
    $(this.div).addClass('element');
    if (vertical) $(this.div).addClass('vertical');
    this.weight = +$(xmlNode).attr('android:layout_weight') || 0;
    this.vertical = vertical;
    
    var ll = this;
    var childNodes = $(xmlNode).children();
    this.childrenVertical = $(xmlNode).attr('android:orientation') == "vertical" ? 1 : 0;

    this.childObjs = new Array();

    $(childNodes).each(function() {
        var nodeName = $(this)[0].nodeName;
        var child;
        switch(nodeName) {
            case "TextView":
                child = new TextView($(this), ll, childrenVertical);
                break;
            case "ImageView":
                child = new ImageView($(this), ll, childrenVertical);
                break;
            case "LinearLayout":
                child = new LinearLayout($(this), ll, childrenVertical);
                break;
        }
        ll.childObjs.push(child);
    });
    
    this.xmlNode = xmlNode;
    this.parentObj = parentObj;
    
    this.measuredWidth = viewMeasuredWidth;
    this.measuredHeight = viewMeasuredHeight;
    
    this.requestedWidth = viewRequestedWidth;
    this.requestedHeight = viewRequestedHeight;

    this.setExtraWidth = linearExtraWidth;
    this.setExtraHeight = linearExtraHeight;
}

function TableLayout(xmlNode, parentObj, vertical) {
    this.div = $("<table />");
    $(parentObj.div).append($(this.div));
    $(this.div).addClass('element');
    this.weight = +$(xmlNode).attr('android:layout_weight') || 0;
    this.vertical = vertical;
    
    var ll = this;
    var childNodes = $(xmlNode).children();

    this.childObjs = new Array();
    this.childrenVertical = true;

    $(childNodes).each(function() {
        var nodeName = $(this)[0].nodeName;
        var child;
        $(this).attr('android:layout_width', "MATCH_PARENT");
        switch(nodeName) {
            case "TableRow":
                child = new TableRow($(this), ll);
                break;
            case "TextView":
                child = new TextView($(this), ll, true);
                break;
            case "ImageView":
                child = new ImageView($(this), ll, true);
                break;
            case "LinearLayout":
                child = new LinearLayout($(this), ll, true);
                break;
        }
        ll.childObjs.push(child);
    });
    
    this.xmlNode = xmlNode;
    this.parentObj = parentObj;
    
    this.measuredWidth = viewMeasuredWidth;
    this.measuredHeight = viewMeasuredHeight;
    
    this.requestedWidth = viewRequestedWidth;
    this.requestedHeight = viewRequestedHeight;
    
    this.setExtraWidth = linearExtraWidth;
    this.setExtraHeight = linearExtraHeight;
}

function TableRow(xmlNode, parentObj, vertical) {
    this.div = $("<tr />");
    $(parentObj.div).append($(this.div));
    $(this.div).addClass('element');
    this.weight = +$(xmlNode).attr('android:layout_weight') || 0;
    this.vertical = vertical;
    
    var ll = this;
    var childNodes = $(xmlNode).children();

    this.childObjs = new Array();

    $(childNodes).each(function() {
        var nodeName = $(this)[0].nodeName;
        var child;
        $(this).attr('android:layout_width', "MATCH_PARENT");
        $(this).attr('android:layout_height', "WRAP_CONTENT");
        switch(nodeName) {
            case "TextView":
                child = new TextView($(this), ll, false, true);
                break;
            case "ImageView":
                child = new ImageView($(this), ll, false, true);
                break;
            case "LinearLayout":
                child = new LinearLayout($(this), ll, false, true);
                break;
        }
        ll.childObjs.push(child);
    });
    
    this.xmlNode = xmlNode;
    this.parentObj = parentObj;
    
    this.measuredWidth = viewMeasuredWidth;
    this.measuredHeight = viewMeasuredHeight;
    
    this.requestedWidth = viewRequestedWidth;
    this.requestedHeight = viewRequestedHeight;
    
    this.setExtraWidth = linearExtraWidth;
    this.setExtraHeight = linearExtraHeight;
}


linearExtraWidth = function(width, extra, widthAvail) {
    var w = width + (!this.vertical ? extra*this.weight : 0);
    if (w > widthAvail) w = widthAvail;
    
    if (w > 0) $(this.div).width(w);
    else $(this.div).hide();
    
    if (w < 0) w = 0;
    
    this.width = w;
    
    var childWidths= 0;
    var childWeights = 0;
    
    var ll = this;

    $.each(this.childObjs, function(i, child) {
        var cw;
        if ($(ll.xmlNode).attr('android:layout_width').toLowerCase() == "wrap_content")
            cw = child.measuredWidth();
        else
            cw = child.requestedWidth();
    
        if (ll.childrenVertical)
            childWidths = Math.max(childWidths, cw);
        else
            childWidths += cw;
        
        childWeights += child.weight;
    });
    
    var extra = 0;

    if (childWeights > 0) {
        extra = (w - childWidths) / childWeights;
    }
    
    var wa = w;
    
    $.each(this.childObjs, function(i, child) {
        var widthToSet = $(ll.xmlNode).attr('android:layout_width').toLowerCase() == "wrap_content" ? child.measuredWidth() : child.requestedWidth();
        
        if (!ll.childrenVertical)
            wa -= child.setExtraWidth(widthToSet, extra, wa);
        else
            child.setExtraWidth(widthToSet, extra, w);
    });
    
    return w;
}

linearExtraHeight = function(height, extra, heightAvail) {
    var h = height + (this.vertical ? extra*this.weight : 0);
    if (h > heightAvail) h = heightAvail;
    
    if (h > 0) $(this.div).height(h);
    else $(this.div).hide();
    
    if (h < 0) h = 0;
    
    this.height = h;
    
    var childHeights= 0;
    var childWeights = 0;
    
    var ll = this;

    $.each(this.childObjs, function(i, child) {
        var ch;
        if ($(ll.xmlNode).attr('android:layout_height').toLowerCase() == "wrap_content")
            ch = child.measuredHeight();
        else
            ch = child.requestedHeight();
                    
        if (ll.childrenVertical)
            childHeights += ch;
        else
            childHeights = Math.max(childHeights, ch);
        
        childWeights += child.weight;
    });
    
    var extra = 0;

    if (childWeights > 0) {
        extra = (h - childHeights) / childWeights;
    }
    
    var ha = h;
    
    $.each(this.childObjs, function(i, child) {
        var heightToSet;
        if ($(ll.xmlNode).attr('android:layout_height').toLowerCase() == "wrap_content")
            heightToSet = child.measuredHeight();
        else
            heightToSet = child.requestedHeight();
            
        if (ll.childrenVertical)
            ha -= child.setExtraHeight(heightToSet, extra, ha);
        else
            child.setExtraHeight(heightToSet, extra, h);
    });
    
    return h;
}