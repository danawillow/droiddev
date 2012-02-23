function ImageView(xmlNode, parentObj, vertical, tableElement) {
    this.div = tableElement? $("<td />") : $("<div />");
    $(parentObj.div).append($(this.div));
    $(this.div).addClass('element');
    $(this.div).css('text-align', 'center');
    if (vertical) $(this.div).addClass('vertical');
    
    var iv = this;
    $.ajax({
        url: 'findImage.cgi',
        data: {'name': $(xmlNode).attr('android:src')},
        success: function(data) {
            var img = '<img src="' + data + '" />';
            $(iv.div).append(img);
        },
        async: false
    });
    
    this.weight = +$(xmlNode).attr('android:layout_weight') || 0;
    
    this.xmlNode = xmlNode;
    this.parentObj = parentObj;
    
    this.measuredWidth = viewMeasuredWidth;
    this.measuredHeight = viewMeasuredHeight;
    
    this.requestedWidth = viewRequestedWidth;
    this.requestedHeight = viewRequestedHeight;
    
    this.setExtraWidth = viewSetExtraWidth;
    this.setExtraHeight = viewSetExtraHeight;
}

function TextView(xmlNode, parentObj, vertical, tableElement) {
    this.div = tableElement? $("<td />") : $("<div />");
    $(parentObj.div).append($(this.div));
    $(this.div).addClass('element');
    $(this.div).text($(xmlNode).attr('android:text'));
    
    if (vertical) $(this.div).addClass('vertical');
    this.weight = +$(xmlNode).attr('android:layout_weight') || 0;
    if ($(xmlNode).attr('android:id')) {
        ids[$(xmlNode).attr('android:id').replace(/\+/, '')] = this.div;
    }
    
    if (tableElement) {
        var span = +$(xmlNode).attr('android:layout_span') || 1;
        if (span > 1)
            $(this.div).attr('colspan', span);
    }
            
    this.xmlNode = xmlNode;
    this.parentObj = parentObj;
    
    this.measuredWidth = viewMeasuredWidth;
    this.measuredHeight = viewMeasuredHeight;
    
    this.requestedWidth = viewRequestedWidth;
    this.requestedHeight = viewRequestedHeight;
    
    this.setExtraWidth = viewSetExtraWidth;
    this.setExtraHeight = viewSetExtraHeight;
    
    this.position = viewPosition;
}

function Other(xmlNode, parentObj, vertical, tableElement) {
    this.div = tableElement? $("<td />") : $("<div />");
    $(parentObj.div).append($(this.div));
    $(this.div).addClass('element');
    $(this.div).text($(xmlNode)[0].nodeName);
    
    if (vertical) $(this.div).addClass('vertical');
    this.weight = +$(xmlNode).attr('android:layout_weight') || 0;
    if ($(xmlNode).attr('android:id')) {
        ids[$(xmlNode).attr('android:id').replace(/\+/, '')] = this.div;
    }
    
    if (tableElement) {
        var span = +$(xmlNode).attr('android:layout_span') || 1;
        if (span > 1)
            $(this.div).attr('colspan', span);
    }
            
    this.xmlNode = xmlNode;
    this.parentObj = parentObj;
    
    this.measuredWidth = viewMeasuredWidth;
    this.measuredHeight = viewMeasuredHeight;
    
    this.requestedWidth = viewRequestedWidth;
    this.requestedHeight = viewRequestedHeight;
    
    this.setExtraWidth = viewSetExtraWidth;
    this.setExtraHeight = viewSetExtraHeight;
    
    this.position = viewPosition;
}