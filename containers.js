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
    
    var ll = this;
    var childNodes = $(xmlNode).children();
    var childrenVertical = $(xmlNode).attr('android:orientation') == "vertical" ? 1 : 0;

    var childObjs = new Array();

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
        childObjs.push(child);
    });
    
    this.xmlNode = xmlNode;
    this.parentObj = parentObj;
    
    this.measuredWidth = viewMeasuredWidth;
    this.measuredHeight = viewMeasuredHeight;
    
    this.requestedWidth = viewRequestedWidth;
    this.requestedHeight = viewRequestedHeight;
    
    this.setExtraWidth = function(width, extra, widthAvail) {
        var w = width + (!vertical ? extra*this.weight : 0);
        if (w > widthAvail) w = widthAvail;
        
        if (w > 0) $(this.div).width(w);
        else $(this.div).hide();
        
        if (w < 0) w = 0;
        
        this.width = w;
        
        var childWidths= 0;
        var childWeights = 0;
    
        $.each(childObjs, function(i, child) {
            var cw;
            if ($(xmlNode).attr('android:layout_width').toLowerCase() == "wrap_content")
                cw = child.measuredWidth();
            else
                cw = child.requestedWidth();
        
            if (childrenVertical)
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
        
        $.each(childObjs, function(i, child) {
            var widthToSet = $(xmlNode).attr('android:layout_width').toLowerCase() == "wrap_content" ? child.measuredWidth() : child.requestedWidth();
            
            if (!childrenVertical)
                wa -= child.setExtraWidth(widthToSet, extra, wa);
            else
                child.setExtraWidth(widthToSet, extra, w);
        });
        
        return w;
    }
    
    this.setExtraHeight = function(height, extra, heightAvail) {
        var h = height + (vertical ? extra*this.weight : 0);
        if (h > heightAvail) h = heightAvail;
        
        if (h > 0) $(this.div).height(h);
        else $(this.div).hide();
        
        if (h < 0) h = 0;
        
        this.height = h;
        
        var childHeights= 0;
        var childWeights = 0;
    
        $.each(childObjs, function(i, child) {
            var ch;
            if ($(xmlNode).attr('android:layout_height').toLowerCase() == "wrap_content")
                ch = child.measuredHeight();
            else
                ch = child.requestedHeight();
                        
            if (childrenVertical)
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
        
        $.each(childObjs, function(i, child) {
            var heightToSet;
            if ($(xmlNode).attr('android:layout_height').toLowerCase() == "wrap_content")
                heightToSet = child.measuredHeight();
            else
                heightToSet = child.requestedHeight();
                
            if (childrenVertical)
                ha -= child.setExtraHeight(heightToSet, extra, ha);
            else
                child.setExtraHeight(heightToSet, extra, h);
        });
        
        return h;
    }
}