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

function TextView(xmlNode, parentObj, vertical, tableElement, tableRow) {
    this.init = init;
    this.init(xmlNode, parentObj, vertical, tableElement, tableRow);
    
    $(this.div).text($(xmlNode).attr('android:text'));
    
    this.measuredWidth = viewMeasuredWidth;
    this.measuredHeight = viewMeasuredHeight;
    
    this.requestedWidth = viewRequestedWidth;
    this.requestedHeight = viewRequestedHeight;
    
    this.setExtraWidth = viewSetExtraWidth;
    this.setExtraHeight = viewSetExtraHeight;
    
    this.position = viewPosition;
}

function Other(xmlNode, parentObj, vertical, tableElement, tableRow) {
    this.init = init;
    this.init(xmlNode, parentObj, vertical, tableElement, tableRow);
    
    $(this.div).text($(xmlNode)[0].nodeName);
    
    this.measuredWidth = viewMeasuredWidth;
    this.measuredHeight = viewMeasuredHeight;
    
    this.requestedWidth = viewRequestedWidth;
    this.requestedHeight = viewRequestedHeight;
    
    this.setExtraWidth = viewSetExtraWidth;
    this.setExtraHeight = viewSetExtraHeight;
    
    this.position = viewPosition;
}

init = function(xmlNode, parentObj, vertical, tableElement, tableRow) {
    this.div = tableElement? $("<td />") : tableRow? $("<tr><td /></tr>") : $("<div />");
    this.xmlNode = xmlNode;
    this.parentObj = parentObj;
    
    $(parentObj.div).append($(this.div));
    $(this.div).addClass('element');
    if (tableElement) $(this.div).css('float', 'none');
    
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
    
    $(this.div).hover(
        function() {
            $(this).addClass('bordered');
        },
        function() {
            $(this).removeClass('bordered');
        }
    );
    
    if ($(xmlNode).attr('android:id')) {
        $(this.div).draggable({
            helper: 'clone',
            revert: true,
            revertDuration: 50,
            appendTo: 'body',
            start: function() {
                codeMirror.focus();
            },
            drag: function(event, ui) {
                var cm = codeMirror.getWrapperElement();
                if (event.pageX > $(cm).offset().left && event.pageX < $(cm).offset().left + $(cm).width() &&
                    event.pageY > $(cm).offset().top  && event.pageY < $(cm).offset().top  + $(cm).height()) {
                    var coordsChar = codeMirror.coordsChar({x: event.pageX, y: event.pageY});
                    codeMirror.setCursor(coordsChar);
                }
            },
            stop: function(event, ui) {
                var cm = codeMirror.getWrapperElement();
                if (event.pageX > $(cm).offset().left && event.pageX < $(cm).offset().left + $(cm).width() &&
                    event.pageY > $(cm).offset().top  && event.pageY < $(cm).offset().top  + $(cm).height()) {
                    var cursor = codeMirror.coordsChar({x: event.pageX, y: event.pageY});
                    codeMirror.setCursor(cursor);
                    var lineContent = codeMirror.getLine(cursor.line);
                    var id = $(xmlNode).attr('android:id').split("/")[1];
                    var nodeName = $(xmlNode)[0].nodeName;
                    var nextLine = nodeName + " " + id + " = (" + nodeName + ")findViewById(R.id." + id + ");";
                    codeMirror.setLine(cursor.line, lineContent + '\n' + nextLine);
                    codeMirror.setSelection({line: cursor.line+1, ch: nodeName.length+1},
                                            {line: cursor.line+1, ch: (nodeName + " " + id).length});
                    codeMirror.indentLine(cursor.line+1);
                }
            }
        });
    }
    
    $(this.div).contextMenu({
        menu: $(xmlNode)[0].nodeName + "Menu",
        inSpeed: 0,
        outSpeed: 0
        },
        function(action, el, pos) {
            if ($(xmlNode).attr('android:id')) {
                var varName = findViewVarName($(xmlNode).attr('android:id').split("/")[1]);
                if (varName != -1) {
                    var cursor = codeMirror.getCursor();
                    var lineContent = codeMirror.getLine(cursor.line);
                    var nextLine = varName + "." + action + ";";
                    codeMirror.setLine(cursor.line, lineContent + '\n' + nextLine);
                    codeMirror.indentLine(cursor.line+1);
                    codeMirror.focus();
                }
            }
        }
    );
}